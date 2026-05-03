"""
Virtual Try-On Processor using OpenCV and MediaPipe
Implements pose detection and clothing overlay for virtual try-on.
"""

import io
import logging
from typing import Optional, Tuple
import numpy as np
from PIL import Image
import cv2
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision

logger = logging.getLogger(__name__)

# MediaPipe Pose landmarks (relevant indices)
# See: https://developers.google.com/mediapipe/solutions/vision/pose_landmarker
NOSE = 0
LEFT_SHOULDER = 11
RIGHT_SHOULDER = 12
LEFT_ELBOW = 13
RIGHT_ELBOW = 14
LEFT_WRIST = 15
RIGHT_WRIST = 16
LEFT_HIP = 23
RIGHT_HIP = 24
LEFT_KNEE = 25
RIGHT_KNEE = 26
LEFT_ANKLE = 27
RIGHT_ANKLE = 28


class TryOnProcessor:
    """Processes virtual try-on using pose detection and image overlay."""
    
    def __init__(self):
        """Initialize MediaPipe Pose detector."""
        try:
            # Initialize pose detection
            self.mp_pose = mp.solutions.pose
            self.pose = self.mp_pose.Pose(
                static_image_mode=True,
                model_complexity=2,
                enable_segmentation=False,
                min_detection_confidence=0.5
            )
            self.mp_drawing = mp.solutions.drawing_utils
            logger.info("MediaPipe Pose initialized successfully")
        except Exception as e:
            logger.error(f"Failed to initialize MediaPipe: {e}")
            raise
    
    def download_image(self, url: str) -> np.ndarray:
        """
        Download image from URL and convert to OpenCV format.
        
        Args:
            url: Image URL
            
        Returns:
            NumPy array in BGR format (OpenCV standard)
        """
        try:
            import httpx
            response = httpx.get(url, timeout=30.0)
            response.raise_for_status()
            
            # Convert to PIL Image
            pil_image = Image.open(io.BytesIO(response.content))
            
            # Convert to RGB then BGR (OpenCV format)
            rgb_image = np.array(pil_image.convert('RGB'))
            bgr_image = cv2.cvtColor(rgb_image, cv2.COLOR_RGB2BGR)
            
            logger.info(f"Downloaded image from {url}: shape={bgr_image.shape}")
            return bgr_image
        except Exception as e:
            logger.error(f"Failed to download image from {url}: {e}")
            raise
    
    def detect_pose(self, image: np.ndarray) -> Optional[object]:
        """
        Detect pose landmarks in image.
        
        Args:
            image: Input image in BGR format
            
        Returns:
            MediaPipe pose landmarks or None if detection fails
        """
        try:
            # Convert BGR to RGB for MediaPipe
            rgb_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            
            # Process with MediaPipe
            results = self.pose.process(rgb_image)
            
            if results.pose_landmarks:
                logger.info("Pose detected successfully")
                return results.pose_landmarks
            else:
                logger.warning("No pose detected in image")
                return None
        except Exception as e:
            logger.error(f"Pose detection failed: {e}")
            return None
    
    def get_body_measurements(
        self, 
        landmarks, 
        image_shape: Tuple[int, int, int]
    ) -> dict:
        """
        Extract body measurements from pose landmarks.
        
        Args:
            landmarks: MediaPipe pose landmarks
            image_shape: Image shape (height, width, channels)
            
        Returns:
            Dictionary with body measurements and key points
        """
        height, width, _ = image_shape
        
        # Get key points (convert normalized coordinates to pixel coordinates)
        left_shoulder = (
            int(landmarks.landmark[LEFT_SHOULDER].x * width),
            int(landmarks.landmark[LEFT_SHOULDER].y * height)
        )
        right_shoulder = (
            int(landmarks.landmark[RIGHT_SHOULDER].x * width),
            int(landmarks.landmark[RIGHT_SHOULDER].y * height)
        )
        left_hip = (
            int(landmarks.landmark[LEFT_HIP].x * width),
            int(landmarks.landmark[LEFT_HIP].y * height)
        )
        right_hip = (
            int(landmarks.landmark[RIGHT_HIP].x * width),
            int(landmarks.landmark[RIGHT_HIP].y * height)
        )
        nose = (
            int(landmarks.landmark[NOSE].x * width),
            int(landmarks.landmark[NOSE].y * height)
        )
        
        # Calculate shoulder width
        shoulder_width = np.sqrt(
            (right_shoulder[0] - left_shoulder[0]) ** 2 +
            (right_shoulder[1] - left_shoulder[1]) ** 2
        )
        
        # Calculate torso height (nose to average hip)
        avg_hip_y = (left_hip[1] + right_hip[1]) / 2
        torso_height = avg_hip_y - nose[1]
        
        # Calculate hip width
        hip_width = np.sqrt(
            (right_hip[0] - left_hip[0]) ** 2 +
            (right_hip[1] - left_hip[1]) ** 2
        )
        
        return {
            "shoulder_width": shoulder_width,
            "torso_height": torso_height,
            "hip_width": hip_width,
            "left_shoulder": left_shoulder,
            "right_shoulder": right_shoulder,
            "left_hip": left_hip,
            "right_hip": right_hip,
            "nose": nose,
        }
    
    def overlay_clothing(
        self,
        user_image: np.ndarray,
        clothing_image: np.ndarray,
        measurements: dict,
        clothing_type: str = "tops"
    ) -> np.ndarray:
        """
        Overlay clothing image onto user image using pose measurements.
        
        Args:
            user_image: User's photo (BGR)
            clothing_image: Clothing item image (BGR with alpha)
            measurements: Body measurements from pose detection
            clothing_type: "tops", "bottoms", or "dresses"
            
        Returns:
            Image with clothing overlaid
        """
        result = user_image.copy()
        
        try:
            # Convert clothing image to RGBA if not already
            if clothing_image.shape[2] == 3:
                # Add alpha channel
                b, g, r = cv2.split(clothing_image)
                alpha = np.ones(b.shape, dtype=b.dtype) * 255
                clothing_rgba = cv2.merge([clothing_image, alpha])
            else:
                clothing_rgba = clothing_image
            
            # Calculate target region based on clothing type
            if clothing_type == "tops":
                # Top region: from shoulders to hips
                top_left = (
                    int(measurements["left_shoulder"][0] - measurements["shoulder_width"] * 0.1),
                    int(measurements["left_shoulder"][1] - measurements["shoulder_width"] * 0.1)
                )
                bottom_right = (
                    int(measurements["right_hip"][0] + measurements["shoulder_width"] * 0.1),
                    int(measurements["left_hip"][1] + measurements["torso_height"] * 0.2)
                )
            elif clothing_type == "bottoms":
                # Bottom region: from hips to knees
                top_left = (
                    int(measurements["left_hip"][0] - measurements["hip_width"] * 0.1),
                    int(measurements["left_hip"][1] - 10)
                )
                left_knee_y = int(measurements.get("left_hip", (0, 0))[1] + measurements["torso_height"] * 0.8)
                bottom_right = (
                    int(measurements["right_hip"][0] + measurements["hip_width"] * 0.1),
                    int(left_knee_y)
                )
            else:  # dresses
                # Full body: from shoulders to knees
                top_left = (
                    int(measurements["left_shoulder"][0] - measurements["shoulder_width"] * 0.15),
                    int(measurements["left_shoulder"][1] - measurements["shoulder_width"] * 0.1)
                )
                left_knee_y = int(measurements.get("left_hip", (0, 0))[1] + measurements["torso_height"] * 1.2)
                bottom_right = (
                    int(measurements["right_hip"][0] + measurements["shoulder_width"] * 0.15),
                    int(left_knee_y)
                )
            
            # Ensure coordinates are within image bounds
            top_left = (max(0, top_left[0]), max(0, top_left[1]))
            bottom_right = (min(result.shape[1], bottom_right[0]), min(result.shape[0], bottom_right[1]))
            
            # Calculate target size
            target_width = bottom_right[0] - top_left[0]
            target_height = bottom_right[1] - top_left[1]
            
            if target_width <= 0 or target_height <= 0:
                logger.warning("Invalid target region for clothing overlay")
                return result
            
            # Resize clothing to fit target region
            clothing_resized = cv2.resize(clothing_rgba, (target_width, target_height))
            
            # Extract region from user image
            roi = result[top_left[1]:bottom_right[1], top_left[0]:bottom_right[0]]
            
            # Blend clothing with user image using alpha channel
            if clothing_resized.shape[2] == 4:
                # Use alpha channel for blending
                alpha = clothing_resized[:, :, 3] / 255.0
                alpha = np.stack([alpha] * 3, axis=2)
                
                # Blend
                for c in range(3):
                    roi[:, :, c] = roi[:, :, c] * (1 - alpha[:, :, 0]) + clothing_resized[:, :, c] * alpha[:, :, 0]
            else:
                # No alpha channel, just overlay
                roi = clothing_resized[:, :, :3]
            
            # Place blended region back
            result[top_left[1]:bottom_right[1], top_left[0]:bottom_right[0]] = roi
            
            logger.info(f"Clothing overlay completed for {clothing_type}")
            return result
            
        except Exception as e:
            logger.error(f"Clothing overlay failed: {e}")
            return user_image
    
    def process_tryon(
        self,
        user_image_url: str,
        clothing_image_url: str,
        clothing_type: str = "tops"
    ) -> bytes:
        """
        Process virtual try-on from start to finish.
        
        Args:
            user_image_url: URL to user's photo
            clothing_image_url: URL to clothing image
            clothing_type: Type of clothing
            
        Returns:
            Processed image as bytes (JPEG)
        """
        try:
            # Download images
            logger.info(f"Downloading user image: {user_image_url}")
            user_image = self.download_image(user_image_url)
            
            logger.info(f"Downloading clothing image: {clothing_image_url}")
            clothing_image = self.download_image(clothing_image_url)
            
            # Detect pose
            logger.info("Detecting pose...")
            landmarks = self.detect_pose(user_image)
            
            if landmarks is None:
                logger.warning("No pose detected, returning original image")
                # Return original image with a warning overlay
                result_image = user_image.copy()
                cv2.putText(
                    result_image,
                    "Pose not detected",
                    (50, 50),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    1,
                    (0, 0, 255),
                    2
                )
            else:
                # Get measurements
                measurements = self.get_body_measurements(landmarks, user_image.shape)
                
                # Overlay clothing
                result_image = self.overlay_clothing(
                    user_image,
                    clothing_image,
                    measurements,
                    clothing_type
                )
            
            # Convert result to bytes
            _, encoded_image = cv2.imencode('.jpg', result_image, [cv2.IMWRITE_JPEG_QUALITY, 90])
            
            logger.info("Try-on processing completed successfully")
            return encoded_image.tobytes()
            
        except Exception as e:
            logger.error(f"Try-on processing failed: {e}")
            raise


# Global processor instance
_processor_instance: Optional[TryOnProcessor] = None


def get_processor() -> TryOnProcessor:
    """Get or create the singleton processor instance."""
    global _processor_instance
    if _processor_instance is None:
        _processor_instance = TryOnProcessor()
    return _processor_instance