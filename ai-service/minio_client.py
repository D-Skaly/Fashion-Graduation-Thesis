"""
MinIO Client for presigned URL generation and privacy-first storage.
Implements data-privacy rules: no permanent storage, auto-delete after processing.
"""
import os
import logging
from datetime import timedelta
from typing import Optional

logger = logging.getLogger(__name__)

try:
    from minio import Minio
    from minio.error import S3Error
    MINIO_AVAILABLE = True
except ImportError:
    logger.warning("MinIO not installed. Install with: pip install minio")
    MINIO_AVAILABLE = False


class MinioClient:
    """MinIO client wrapper for presigned URLs and temporary storage."""
    
    def __init__(self):
        self.endpoint = os.getenv('MINIO_ENDPOINT', 'minio:9000')
        self.access_key = os.getenv('MINIO_ACCESS_KEY', 'minioadmin')
        self.secret_key = os.getenv('MINIO_SECRET_KEY', 'minioadmin')
        self.bucket_name = os.getenv('MINIO_BUCKET', 'fashion-ai')
        self.secure = os.getenv('MINIO_SECURE', 'false').lower() == 'true'
        
        if MINIO_AVAILABLE:
            try:
                self.client = Minio(
                    self.endpoint,
                    access_key=self.access_key,
                    secret_key=self.secret_key,
                    secure=self.secure
                )
                # Ensure bucket exists
                if not self.client.bucket_exists(self.bucket_name):
                    self.client.make_bucket(self.bucket_name)
                    logger.info(f"Created bucket: {self.bucket_name}")
                logger.info("MinIO client initialized successfully")
            except Exception as e:
                logger.error(f"Failed to initialize MinIO: {e}")
                self.client = None
        else:
            self.client = None
    
    def get_presigned_upload_url(self, object_name: str, expires_in_seconds: int = 3600) -> Optional[str]:
        """
        Generate a presigned URL for uploading an object.
        Privacy: URL expires automatically after TTL.
        """
        if not self.client:
            return None
        
        try:
            url = self.client.presigned_put_object(
                self.bucket_name,
                object_name,
                expires=timedelta(seconds=expires_in_seconds)
            )
            logger.info(f"Generated upload URL for {object_name} (expires in {expires_in_seconds}s)")
            return url
        except S3Error as e:
            logger.error(f"Failed to generate upload URL: {e}")
            return None
    
    def get_presigned_download_url(self, object_name: str, expires_in_seconds: int = 3600) -> Optional[str]:
        """
        Generate a presigned URL for downloading an object.
        Privacy: URL expires automatically after TTL.
        """
        if not self.client:
            return None
        
        try:
            url = self.client.presigned_get_object(
                self.bucket_name,
                object_name,
                expires=timedelta(seconds=expires_in_seconds)
            )
            logger.info(f"Generated download URL for {object_name} (expires in {expires_in_seconds}s)")
            return url
        except S3Error as e:
            logger.error(f"Failed to generate download URL: {e}")
            return None
    
    def upload_bytes(self, data: bytes, object_name: str, content_type: str = 'image/jpeg') -> bool:
        """Upload bytes data to MinIO."""
        if not self.client:
            return False
        
        try:
            from io import BytesIO
            data_stream = BytesIO(data)
            self.client.put_object(
                self.bucket_name,
                object_name,
                data_stream,
                length=len(data),
                content_type=content_type
            )
            logger.info(f"Uploaded {object_name} to MinIO")
            return True
        except S3Error as e:
            logger.error(f"Failed to upload: {e}")
            return False
    
    def delete_object(self, object_name: str) -> bool:
        """Delete an object from MinIO. Privacy enforcement."""
        if not self.client:
            return False
        
        try:
            self.client.remove_object(self.bucket_name, object_name)
            logger.info(f"Deleted {object_name} from MinIO (privacy enforcement)")
            return True
        except S3Error as e:
            logger.error(f"Failed to delete: {e}")
            return False
    
    def download_bytes(self, object_name: str) -> Optional[bytes]:
        """Download object as bytes."""
        if not self.client:
            return None
        
        try:
            response = self.client.get_object(self.bucket_name, object_name)
            data = response.read()
            response.close()
            response.release_conn()
            return data
        except S3Error as e:
            logger.error(f"Failed to download: {e}")
            return None


# Global instance
_minio_client: Optional[MinioClient] = None


def get_minio_client() -> Optional[MinioClient]:
    """Get or create MinIO client singleton."""
    global _minio_client
    if _minio_client is None:
        _minio_client = MinioClient()
    return _minio_client