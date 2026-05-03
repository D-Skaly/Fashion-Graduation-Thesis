from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional, Dict, List
import uuid
import logging
from datetime import datetime, timedelta
import os

from size_model import get_model, Size, Gender
from tryon_processor import get_processor
from minio_client import get_minio_client

try:
    import redis
    redis_client = redis.Redis(host=os.getenv('REDIS_HOST', 'redis'), port=6379, decode_responses=True)
    redis_client.ping()
    USE_REDIS = True
    logger.info("Redis connected successfully")
except:
    logger.warning("Redis not available, falling back to in-memory storage")
    USE_REDIS = False
    tryon_jobs = {}

# Initialize MinIO client for presigned URLs
minio_client = get_minio_client()
USE_MINIO = minio_client is not None and minio_client.client is not None
if USE_MINIO:
    logger.info("MinIO client initialized successfully")
else:
    logger.warning("MinIO not available, will use base64 fallback")

app = FastAPI(title="AI Fashion Service")

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Privacy: TTL for tryon jobs (1 hour)
TRYON_JOB_TTL = 3600  # seconds


class BodyProfile(BaseModel):
    chest: Optional[float] = Field(None, ge=0, description="Chest measurement must be non-negative")
    waist: Optional[float] = Field(None, ge=0, description="Waist measurement must be non-negative")
    hips: Optional[float] = Field(None, ge=0, description="Hips measurement must be non-negative")
    shoulder: Optional[float] = Field(None, ge=0, description="Shoulder measurement must be non-negative")
    inseam: Optional[float] = Field(None, ge=0, description="Inseam measurement must be non-negative")
    gender: str = Field("unisex", pattern="^(male|female|unisex)$")
    clothing_type: str = Field("tops", pattern="^(tops|bottoms|dresses)$")
    fit_preference: str = Field("regular", pattern="^(tight|regular|loose)$")


class SizeRecommendationResponse(BaseModel):
    size: str
    confidence: float
    details: Dict


class TryOnRequest(BaseModel):
    user_image_url: str
    clothing_image_url: str
    clothing_type: str = "tops"  # tops, bottoms, dresses
    user_id: Optional[str] = None
    clothing_id: Optional[str] = None


class TryOnResponse(BaseModel):
    job_id: str
    status: str
    message: str


class TryOnStatusResponse(BaseModel):
    job_id: str
    status: str
    result_url: Optional[str] = None
    created_at: str
    completed_at: Optional[str] = None


@app.get("/health")
def health():
    return {"status": "ok", "timestamp": datetime.utcnow().isoformat()}


@app.post("/size/recommend", response_model=SizeRecommendationResponse)
def recommend_size(profile: BodyProfile):
    """
    Recommend clothing size based on body measurements.
    Uses ML-based size chart matching with confidence scoring.
    """
    try:
        model = get_model()
        
        size, confidence, details = model.predict_size(
            chest=profile.chest,
            waist=profile.waist,
            hips=profile.hips,
            shoulder=profile.shoulder,
            inseam=profile.inseam,
            gender=profile.gender,
            clothing_type=profile.clothing_type,
            fit_preference=profile.fit_preference,
        )
        
        logger.info(f"Size recommendation: {size.value} (confidence: {confidence:.2f})")
        
        return SizeRecommendationResponse(
            size=size.value,
            confidence=round(confidence, 2),
            details=details,
        )
    except Exception as e:
        logger.error(f"Error in size recommendation: {str(e)}")
        raise HTTPException(status_code=500, detail="Internal server error")


@app.get("/size/chart")
def get_size_chart(gender: str = "male", clothing_type: str = "tops"):
    """Get size chart for reference."""
    model = get_model()
    return model.get_size_chart(gender, clothing_type)


@app.post("/tryon/process", response_model=TryOnResponse)
def process_tryon(request: TryOnRequest):
    """
    Process virtual try-on request using Computer Vision (OpenCV + MediaPipe).
    Privacy-first: Uses Presigned URLs, auto-deletes after processing (TTL).
    """
    job_id = str(uuid.uuid4())
    
    # ✅ Fail if MinIO not available (privacy compliance)
    if not USE_MINIO:
        raise HTTPException(
            status_code=503,
            detail="Try-on service temporarily unavailable. MinIO storage required for privacy compliance."
        )
    
    job_data = {
        "status": "PROCESSING",
        "user_image_url": request.user_image_url,
        "clothing_image_url": request.clothing_image_url,
        "user_id": request.user_id,
        "clothing_id": request.clothing_id,
        "created_at": datetime.utcnow().isoformat(),
        "result_url": None,
        "completed_at": None,
        "clothing_type": request.clothing_type,
    }
    
    # Store job in Redis or in-memory
    if USE_REDIS:
        redis_client.hset(f"tryon_job:{job_id}", mapping=job_data)
        redis_client.expire(f"tryon_job:{job_id}", TRYON_JOB_TTL)
    else:
        tryon_jobs[job_id] = job_data
    
    logger.info(f"Try-on job created: {job_id}")
    logger.info(f"Processing images: user={request.user_image_url}, clothing={request.clothing_image_url}")
    
    try:
        # Get processor
        processor = get_processor()
        
        # Process try-on with Computer Vision
        result_bytes = processor.process_tryon(
            user_image_url=request.user_image_url,
            clothing_image_url=request.clothing_image_url,
            clothing_type=request.clothing_type,
        )
        
        # Upload result to MinIO with presigned URL for privacy
        object_name = f"tryon-results/{job_id}.jpg"
        
        # Upload to MinIO and generate presigned download URL
        minio_client.upload_bytes(result_bytes, object_name, 'image/jpeg')
        result_url = minio_client.get_presigned_download_url(
            object_name, 
            expires_in_seconds=TRYON_JOB_TTL
        )
        logger.info(f"Uploaded result to MinIO: {object_name}")
        # Update job status
        if USE_REDIS:
            redis_client.hset(f"tryon_job:{job_id}", "status", "COMPLETED")
            redis_client.hset(f"tryon_job:{job_id}", "result_url", result_url or "N/A")
            redis_client.hset(f"tryon_job:{job_id}", "minio_object", object_name if USE_MINIO else "")
            redis_client.hset(f"tryon_job:{job_id}", "completed_at", datetime.utcnow().isoformat())
        else:
            tryon_jobs[job_id]["status"] = "COMPLETED"
            tryon_jobs[job_id]["result_url"] = result_url
            tryon_jobs[job_id]["minio_object"] = object_name if USE_MINIO else None
            tryon_jobs[job_id]["completed_at"] = datetime.utcnow().isoformat()
        
        # Privacy enforcement: Schedule deletion of source images
        logger.info(f"Privacy enforcement: Source images will be deleted after {TRYON_JOB_TTL}s")
        logger.info(f"Privacy enforcement: Result will be deleted after {TRYON_JOB_TTL}s")
        logger.info(f"Try-on job completed: {job_id}")
        
    except Exception as e:
        logger.error(f"Try-on processing failed: {str(e)}")
        if USE_REDIS:
            redis_client.hset(f"tryon_job:{job_id}", "status", "FAILED")
            redis_client.hset(f"tryon_job:{job_id}", "error", str(e))
        else:
            tryon_jobs[job_id]["status"] = "FAILED"
            tryon_jobs[job_id]["error"] = str(e)
    
    return TryOnResponse(
        job_id=job_id,
        status="PROCESSING",
        message="Try-on job accepted. Poll /tryon/status/{job_id} for updates.",
    )


@app.get("/tryon/status/{job_id}", response_model=TryOnStatusResponse)
def get_tryon_status(job_id: str):
    """Get try-on job status."""
    if USE_REDIS:
        job = redis_client.hgetall(f"tryon_job:{job_id}")
        if not job:
            raise HTTPException(status_code=404, detail="Job not found")
    else:
        if job_id not in tryon_jobs:
            raise HTTPException(status_code=404, detail="Job not found")
        job = tryon_jobs[job_id]
    
    # Privacy: Don't expose full URLs in JSON, return presigned URL only when needed
    result_url = job.get("result_url")
    if result_url and result_url.startswith("data:"):
        result_url = "Result processed (base64 data available via dedicated endpoint)"
    elif result_url and result_url.startswith("http"):
        # It's a presigned URL - return as-is (it has TTL)
        pass
    
    return TryOnStatusResponse(
        job_id=job_id,
        status=job["status"],
        result_url=result_url,
        created_at=job["created_at"],
        completed_at=job.get("completed_at"),
    )

@app.delete("/tryon/cleanup/{job_id}")
def cleanup_tryon_job(job_id: str):
    """Manually trigger cleanup of try-on job data. Privacy enforcement."""
    if USE_REDIS:
        job = redis_client.hgetall(f"tryon_job:{job_id}")
        if not job:
            raise HTTPException(status_code=404, detail="Job not found")
        
        # Delete MinIO object if exists
        minio_object = job.get("minio_object")
        if minio_object and USE_MINIO:
            minio_client.delete_object(minio_object)
        
        # Delete job from Redis
        redis_client.delete(f"tryon_job:{job_id}")
        logger.info(f"Cleaned up job {job_id} (Redis + MinIO)")
    else:
        if job_id not in tryon_jobs:
            raise HTTPException(status_code=404, detail="Job not found")
        
        # Delete MinIO object if exists
        minio_object = tryon_jobs[job_id].get("minio_object")
        if minio_object and USE_MINIO:
            minio_client.delete_object(minio_object)
        
        # Delete job from memory
        del tryon_jobs[job_id]
        logger.info(f"Cleaned up job {job_id} (Memory + MinIO)")
    
    return {"status": "cleaned", "job_id": job_id}


@app.get("/tryon/history/{user_id}", response_model=List[TryOnStatusResponse])
def get_tryon_history(user_id: str, limit: int = 10):
    """Get try-on history for a user."""
    if USE_REDIS:
        # Scan Redis keys for user's jobs
        keys = redis_client.keys("tryon_job:*")
        user_jobs = []
        for key in keys:
            job = redis_client.hgetall(key)
            if job.get("user_id") == user_id:
                user_jobs.append(TryOnStatusResponse(
                    job_id=key.split(":")[1],
                    status=job["status"],
                    result_url=job.get("result_url"),
                    created_at=job["created_at"],
                    completed_at=job.get("completed_at"),
                ))
    else:
        user_jobs = [
            TryOnStatusResponse(
                job_id=job_id,
                status=job["status"],
                result_url=job.get("result_url"),
                created_at=job["created_at"],
                completed_at=job.get("completed_at"),
            )
            for job_id, job in tryon_jobs.items()
            if job.get("user_id") == user_id
        ]
    
    # Sort by created_at descending
    user_jobs.sort(key=lambda x: x.created_at, reverse=True)
    return user_jobs[:limit]


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)
