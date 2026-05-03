# AI Fashion Service

FastAPI-based AI service for the Fashion E-Commerce system, providing size recommendation and virtual try-on capabilities.

## Features

1. **Size Recommendation** - ML-based clothing size prediction using body measurements
2. **Virtual Try-On** - **Real Computer Vision processing** using OpenCV + MediaPipe Pose Detection
3. **Size Charts** - Reference data for different clothing types and genders
4. **Pose Detection** - Automatic body key-point detection for accurate clothing overlay
5. **Privacy-First Design** - Presigned URLs, auto-deletion, no permanent storage

## API Endpoints

### Health Check
```
GET /health
```
Returns service health status.

**Response:**
```json
{
  "status": "ok",
  "timestamp": "2026-04-28T08:46:32.123456"
}
```

---

### Size Recommendation
```
POST /size/recommend
```

Predicts the best-fitting clothing size based on body measurements.

**Request Body:**
```json
{
  "chest": 95.5,
  "waist": 82.0,
  "hips": 98.0,
  "shoulder": 42.0,
  "inseam": 81.0,
  "gender": "male",
  "clothing_type": "tops",
  "fit_preference": "regular"
}
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `chest` | float | No | Chest circumference in cm |
| `waist` | float | No | Waist circumference in cm |
| `hips` | float | No | Hips circumference in cm |
| `shoulder` | float | No | Shoulder width in cm |
| `inseam` | float | No | Inseam length in cm |
| `gender` | string | No | "male", "female", or "unisex" (default: "unisex") |
| `clothing_type` | string | No | "tops", "bottoms", or "dresses" (default: "tops") |
| `fit_preference` | string | No | "regular", "slim", or "loose" (default: "regular") |

**Response:**
```json
{
  "size": "M",
  "confidence": 0.95,
  "details": {
    "all_scores": {"XS": 0.0, "S": 0.3, "M": 0.95, "L": 0.6, "XL": 0.1, "XXL": 0.0},
    "measurements_provided": {
      "chest": 95.5,
      "waist": 82.0,
      "hips": 98.0,
      "shoulder": 42.0,
      "inseam": 81.0
    },
    "clothing_type": "tops",
    "gender": "male",
    "fit_preference": "regular"
  }
}
```

---

### Get Size Chart
```
GET /size/chart?gender=male&clothing_type=tops
```

Returns the size chart reference for a specific gender and clothing type.

**Query Parameters:**
- `gender` (optional): "male" or "female" (default: "male")
- `clothing_type` (optional): "tops", "bottoms", or "dresses" (default: "tops")

**Response:**
```json
{
  "clothing_type": "tops",
  "gender": "male",
  "sizes": {
    "XS": {"chest": [0, 86], "waist": [0, 71], "shoulder": [0, 38]},
    "S": {"chest": [86, 94], "waist": [71, 79], "shoulder": [38, 41]},
    "M": {"chest": [94, 102], "waist": [79, 87], "shoulder": [41, 44]},
    "L": {"chest": [102, 110], "waist": [87, 95], "shoulder": [44, 47]},
    "XL": {"chest": [110, 118], "waist": [95, 103], "shoulder": [47, 50]},
    "XXL": {"chest": [118, 130], "waist": [103, 115], "shoulder": [50, 54]}
  }
}
```

---

### Virtual Try-On - Submit Job (Computer Vision Powered)
```
POST /tryon/process
```

Submits a virtual try-on job for processing using **OpenCV + MediaPipe Pose Detection**.

**Processing Pipeline:**
1. Download user and clothing images from URLs
2. Detect body pose using MediaPipe (33 key points)
3. Calculate body measurements (shoulder width, torso height, hip width)
4. Resize and overlay clothing onto user image
5. Apply alpha blending for natural look
6. Return base64-encoded result image

**Request Body:**
```json
{
  "user_image_url": "https://storage.example.com/user_123_photo.jpg",
  "clothing_image_url": "https://storage.example.com/dress_456.jpg",
  "clothing_type": "dresses",
  "user_id": "user_123",
  "clothing_id": "cloth_456"
}
```

**Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `user_image_url` | string | Yes | Presigned URL to user's photo (standing straight, front-facing) |
| `clothing_image_url` | string | Yes | URL to clothing item image (with alpha channel preferred) |
| `clothing_type` | string | No | "tops", "bottoms", or "dresses" (default: "tops") |
| `user_id` | string | No | User identifier for tracking |
| `clothing_id` | string | No | Clothing item identifier |

**Response:**
```json
{
  "job_id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING",
  "message": "Try-on job accepted. Poll /tryon/status/{job_id} for updates."
}
```

---

### Get Try-On Job Status
```
GET /tryon/status/{job_id}
```

Checks the status of a try-on job.

**Response:**
```json
{
  "job_id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "result_url": "https://cdn.example.com/results/tryon_550e8400-e29b-41d4-a716-446655440000.jpg",
  "created_at": "2026-04-28T08:46:32.123456",
  "completed_at": "2026-04-28T08:46:34.123456"
}
```

**Status Values:**
- `PROCESSING` - Job is being processed
- `COMPLETED` - Processing complete, result available
- `FAILED` - Processing failed

---

### Get Try-On History
```
GET /tryon/history/{user_id}?limit=10
```

Retrieves try-on job history for a specific user.

**Query Parameters:**
- `limit` (optional): Maximum number of records to return (default: 10)

**Response:**
```json
[
  {
    "job_id": "550e8400-e29b-41d4-a716-446655440000",
    "status": "COMPLETED",
    "result_url": "https://cdn.example.com/results/tryon_550e8400.jpg",
    "created_at": "2026-04-28T08:46:32.123456",
    "completed_at": "2026-04-28T08:46:34.123456"
  }
]
```

---

## Privacy Features

The virtual try-on service implements privacy-first design:

1. **Presigned URLs** - User images accessed via temporary, expiring URLs
2. **Auto-deletion** - User images scheduled for deletion after processing
3. **No permanent storage** - Sensitive data not stored beyond processing window
4. **Logging** - All access logged for audit trails

---

## Model Details

### Size Recommendation Model (`size_model.py`)

The size recommendation uses a rule-based + statistical approach:

- **Size Charts**: Pre-defined measurement ranges for XS to XXL across different clothing types and genders
- **Confidence Scoring**: Calculates match percentage for each size based on how well measurements fit within size ranges
- **Fit Preference**: Adjusts recommendations based on user's fit preference (slim/regular/loose)
- **Multi-metric Matching**: Considers chest, waist, hips, shoulder, and inseam measurements

**Supported Clothing Types:**
- Tops (shirts, jackets, etc.)
- Bottoms (pants, skirts, etc.)
- Dresses

**Supported Genders:**
- Male
- Female
- Unisex (defaults to male chart)

---

## Running the Service

### Local Development
```bash
cd ai-service
pip install -r requirements.txt
uvicorn main:app --host 0.0.0.0 --port 8001 --reload
```

### Docker (Recommended)
```bash
docker-compose up ai-service
```

The service will be available at `http://localhost:8001`

---

## Integration

### From ai-orchestrator (NestJS)
The ai-orchestrator can call this service via HTTP:
```typescript
const response = await axios.post('http://ai-service:8001/size/recommend', {
  chest: 95,
  waist: 82,
  gender: 'male',
  clothing_type: 'tops'
});
```

### From backend (Spring Boot)
The backend can integrate via Spring's `RestTemplate` or `WebClient` through the AI port/adapter pattern as defined in `.junie/AGENTS.md`.

---

## Future Enhancements

1. **Real ML Models** - Integrate PyTorch/TensorFlow for more accurate predictions
2. **Image Processing** - Actual virtual try-on using computer vision models
3. **Queue System** - Use Redis/BullMQ for production job processing
4. **Database Persistence** - Store job history in PostgreSQL
5. **Advanced Privacy** - Implement automatic image deletion with MinIO lifecycle policies