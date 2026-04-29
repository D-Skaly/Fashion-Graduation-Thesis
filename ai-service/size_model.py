"""
Size Recommendation Model for Fashion E-Commerce
Uses body measurements to predict appropriate clothing sizes.
Implements a simple but effective rule-based + statistical approach.
"""

from typing import Dict, Optional, Tuple
from enum import Enum


class Size(str, Enum):
    XS = "XS"
    S = "S"
    M = "M"
    L = "L"
    XL = "XL"
    XXL = "XXL"


class Gender(str, Enum):
    MALE = "male"
    FEMALE = "female"
    UNISEX = "unisex"


# Size charts based on standard measurements (in cm)
# These are reference values for different clothing types
SIZE_CHARTS = {
    "tops": {
        "male": {
            Size.XS: {"chest": (0, 86), "waist": (0, 71), "shoulder": (0, 38)},
            Size.S: {"chest": (86, 94), "waist": (71, 79), "shoulder": (38, 41)},
            Size.M: {"chest": (94, 102), "waist": (79, 87), "shoulder": (41, 44)},
            Size.L: {"chest": (102, 110), "waist": (87, 95), "shoulder": (44, 47)},
            Size.XL: {"chest": (110, 118), "waist": (95, 103), "shoulder": (47, 50)},
            Size.XXL: {"chest": (118, 130), "waist": (103, 115), "shoulder": (50, 54)},
        },
        "female": {
            Size.XS: {"chest": (0, 80), "waist": (0, 62), "hips": (0, 86)},
            Size.S: {"chest": (80, 88), "waist": (62, 70), "hips": (86, 94)},
            Size.M: {"chest": (88, 96), "waist": (70, 78), "hips": (94, 102)},
            Size.L: {"chest": (96, 104), "waist": (78, 86), "hips": (102, 110)},
            Size.XL: {"chest": (104, 112), "waist": (86, 94), "hips": (110, 118)},
            Size.XXL: {"chest": (112, 124), "waist": (94, 106), "hips": (118, 130)},
        },
    },
    "bottoms": {
        "male": {
            Size.XS: {"waist": (0, 71), "hips": (0, 86), "inseam": (0, 76)},
            Size.S: {"waist": (71, 79), "hips": (86, 94), "inseam": (76, 79)},
            Size.M: {"waist": (79, 87), "hips": (94, 102), "inseam": (79, 82)},
            Size.L: {"waist": (87, 95), "hips": (102, 110), "inseam": (82, 85)},
            Size.XL: {"waist": (95, 103), "hips": (110, 118), "inseam": (85, 88)},
            Size.XXL: {"waist": (103, 115), "hips": (118, 130), "inseam": (88, 91)},
        },
        "female": {
            Size.XS: {"waist": (0, 62), "hips": (0, 86), "inseam": (0, 71)},
            Size.S: {"waist": (62, 70), "hips": (86, 94), "inseam": (71, 74)},
            Size.M: {"waist": (70, 78), "hips": (94, 102), "inseam": (74, 77)},
            Size.L: {"waist": (78, 86), "hips": (102, 110), "inseam": (77, 80)},
            Size.XL: {"waist": (86, 94), "hips": (110, 118), "inseam": (80, 83)},
            Size.XXL: {"waist": (94, 106), "hips": (118, 130), "inseam": (83, 86)},
        },
    },
    "dresses": {
        "female": {
            Size.XS: {"chest": (0, 80), "waist": (0, 62), "hips": (0, 86)},
            Size.S: {"chest": (80, 88), "waist": (62, 70), "hips": (86, 94)},
            Size.M: {"chest": (88, 96), "waist": (70, 78), "hips": (94, 102)},
            Size.L: {"chest": (96, 104), "waist": (78, 86), "hips": (102, 110)},
            Size.XL: {"chest": (104, 112), "waist": (86, 94), "hips": (110, 118)},
            Size.XXL: {"chest": (112, 124), "waist": (94, 106), "hips": (118, 130)},
        },
    },
}


class SizeRecommendationModel:
    """
    Size recommendation model that uses body measurements
    to predict the best fitting size.
    """
    
    def __init__(self):
        self.size_charts = SIZE_CHARTS
    
    def predict_size(
        self,
        chest: Optional[float] = None,
        waist: Optional[float] = None,
        hips: Optional[float] = None,
        shoulder: Optional[float] = None,
        inseam: Optional[float] = None,
        gender: str = "unisex",
        clothing_type: str = "tops",
        fit_preference: str = "regular"  # regular, slim, loose
    ) -> Tuple[Size, float, Dict]:
        """
        Predict the best size based on body measurements.
        
        Args:
            chest: Chest circumference in cm
            waist: Waist circumference in cm
            hips: Hips circumference in cm
            shoulder: Shoulder width in cm
            inseam: Inseam length in cm
            gender: 'male', 'female', or 'unisex'
            clothing_type: 'tops', 'bottoms', or 'dresses'
            fit_preference: 'regular', 'slim', or 'loose'
        
        Returns:
            Tuple of (recommended_size, confidence_score, details)
        """
        # Normalize gender
        gender = gender.lower()
        if gender not in ["male", "female"]:
            gender = "unisex"
        
        # Get appropriate size chart
        chart_key = clothing_type.lower()
        if chart_key not in self.size_charts:
            chart_key = "tops"
        
        if gender == "unisex":
            # Default to male chart for unisex
            gender_key = "male"
        else:
            gender_key = gender
        
        if gender_key not in self.size_charts[chart_key]:
            # Fallback to available gender
            gender_key = list(self.size_charts[chart_key].keys())[0]
        
        size_chart = self.size_charts[chart_key][gender_key]
        
        # Calculate match scores for each size
        size_scores = {}
        for size, measurements in size_chart.items():
            score = 0.0
            total_metrics = 0
            
            for metric, value in [
                ("chest", chest),
                ("waist", waist),
                ("hips", hips),
                ("shoulder", shoulder),
                ("inseam", inseam),
            ]:
                if value is not None and metric in measurements:
                    min_val, max_val = measurements[metric]
                    if min_val <= value <= max_val:
                        # Perfect fit
                        score += 1.0
                    elif value < min_val:
                        # Too small - calculate how far off
                        score += max(0, 1 - (min_val - value) / min_val)
                    else:
                        # Too large
                        score += max(0, 1 - (value - max_val) / max_val)
                    total_metrics += 1
            
            if total_metrics > 0:
                size_scores[size] = score / total_metrics
            else:
                size_scores[size] = 0.0
        
        # Adjust for fit preference
        if fit_preference == "slim" and size_scores:
            # Prefer smaller sizes
            sizes = sorted(size_scores.keys(), key=lambda s: list(size_chart.keys()).index(s))
            for i, size in enumerate(sizes):
                if i > 0:
                    size_scores[size] *= 0.9  # Reduce score of larger sizes
        elif fit_preference == "loose" and size_scores:
            # Prefer larger sizes
            sizes = sorted(size_scores.keys(), key=lambda s: list(size_chart.keys()).index(s))
            for i, size in enumerate(sizes):
                if i < len(sizes) - 1:
                    size_scores[size] *= 0.9  # Reduce score of smaller sizes
        
        # Find best match
        if not size_scores:
            return Size.M, 0.5, {"note": "No measurements provided, defaulting to M"}
        
        best_size = max(size_scores, key=size_scores.get)
        confidence = size_scores[best_size]
        
        # Prepare details
        details = {
            "all_scores": {s.value: round(score, 2) for s, score in size_scores.items()},
            "measurements_provided": {
                "chest": chest,
                "waist": waist,
                "hips": hips,
                "shoulder": shoulder,
                "inseam": inseam,
            },
            "clothing_type": clothing_type,
            "gender": gender_key,
            "fit_preference": fit_preference,
        }
        
        return best_size, confidence, details
    
    def get_size_chart(
        self,
        gender: str = "male",
        clothing_type: str = "tops"
    ) -> Dict:
        """Return the size chart for reference."""
        chart_key = clothing_type.lower()
        if chart_key not in self.size_charts:
            chart_key = "tops"
        
        gender_key = gender.lower()
        if gender_key not in self.size_charts[chart_key]:
            gender_key = list(self.size_charts[chart_key].keys())[0]
        
        return {
            "clothing_type": clothing_type,
            "gender": gender_key,
            "sizes": {
                size.value: measurements
                for size, measurements in self.size_charts[chart_key][gender_key].items()
            }
        }


# Global model instance
_model_instance: Optional[SizeRecommendationModel] = None


def get_model() -> SizeRecommendationModel:
    """Get or create the singleton model instance."""
    global _model_instance
    if _model_instance is None:
        _model_instance = SizeRecommendationModel()
    return _model_instance