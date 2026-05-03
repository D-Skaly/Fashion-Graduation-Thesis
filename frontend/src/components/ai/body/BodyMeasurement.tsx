"use client";

import { useEffect, useRef, useState } from "react";
import * as pose from "@mediapipe/pose";
import { Camera } from "@mediapipe/camera_utils";
import { Button } from "@/components/ui/button";
import { Loader2, Camera as CameraIcon, ScanLine, Ruler, CheckCircle2 } from "lucide-react";
import api from "@/lib/axios";

interface MeasurementResult {
  height: number;
  weight: number;
  chest: number;
  waist: number;
}

export function BodyMeasurement() {
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isMeasuring, setIsMeasuring] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [measurementResult, setMeasurementResult] = useState<MeasurementResult | null>(null);

  useEffect(() => {
    if (!isMeasuring) return;

    const bodyPose = new pose.Pose({
      locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/pose/${file}`,
    });

    bodyPose.setOptions({
      modelComplexity: 1,
      smoothLandmarks: true,
      enableSegmentation: false,
      minDetectionConfidence: 0.5,
      minTrackingConfidence: 0.5,
    });

    bodyPose.onResults((results) => {
      if (!canvasRef.current || !videoRef.current) return;
      
      const canvasCtx = canvasRef.current.getContext("2d")!;
      canvasCtx.save();
      canvasCtx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
      canvasCtx.drawImage(results.image, 0, 0, canvasRef.current.width, canvasRef.current.height);
      
      if (results.poseLandmarks) {
        // Drawing landmarks with a premium cyan/primary color
        for (const landmark of results.poseLandmarks) {
          canvasCtx.beginPath();
          canvasCtx.arc(landmark.x * canvasRef.current.width, landmark.y * canvasRef.current.height, 3, 0, 2 * Math.PI);
          canvasCtx.fillStyle = "hsl(var(--primary))";
          canvasCtx.fill();
          canvasCtx.lineWidth = 1;
          canvasCtx.strokeStyle = "rgba(255,255,255,0.5)";
          canvasCtx.stroke();
        }
      }
      canvasCtx.restore();
    });

    if (videoRef.current) {
      const camera = new Camera(videoRef.current, {
        onFrame: async () => {
          if (videoRef.current) await bodyPose.send({ image: videoRef.current });
        },
        width: 640,
        height: 480,
      });
      camera.start();

      return () => {
        camera.stop();
        bodyPose.close();
      };
    }
  }, [isMeasuring]);

  const handleCapture = async () => {
    setIsProcessing(true);
    
    try {
      // Get the canvas with pose landmarks
      if (!canvasRef.current) {
        throw new Error("Canvas not found");
      }

      // Convert canvas to blob
      const canvas = canvasRef.current;
      const blob = await new Promise<Blob>((resolve) => {
        canvas.toBlob((blob) => resolve(blob!), "image/jpeg", 0.8);
      });

      // Upload image to get URL
      const formData = new FormData();
      formData.append("file", blob, "body-measurement.jpg");
      
      const uploadResponse = await api.post("/images/upload?folder=body-profiles", formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
      
      const imageUrl = uploadResponse.data.data.url;

      // Send to backend for processing
      const response = await api.post("/users/body-profile", {
        imageUrl: imageUrl,
      });

      setIsMeasuring(false);
      setIsProcessing(false);
      setMeasurementResult(response.data.data);
      
     
    } catch (error) {
      console.error("Failed to process body measurement", error);
      setIsProcessing(false);
      alert("Failed to process body measurement. Please try again.");
    }
  };

  return (
    <div className="flex flex-col gap-6 border border-border/50 p-6 sm:p-8 rounded-2xl bg-secondary/10 backdrop-blur-sm relative overflow-hidden">
      {/* Background glow */}
      <div className="absolute -top-24 -right-24 w-48 h-48 bg-primary/10 rounded-full blur-[60px] pointer-events-none" />
      
      <div className="flex flex-col items-center text-center gap-2">
          <div className="h-12 w-12 rounded-full bg-primary/10 flex items-center justify-center mb-2">
              <ScanLine className="h-6 w-6 text-primary" />
          </div>
          <h3 className="font-bold text-xl tracking-tight">AI Body Measurement</h3>
          <p className="text-sm text-muted-foreground font-light max-w-[280px]">
              Tự động đo kích thước cơ thể qua camera để gợi ý size đồ chính xác nhất.
          </p>
      </div>

      {!isMeasuring && !measurementResult && (
        <div className="flex justify-center mt-2">
            <Button onClick={() => setIsMeasuring(true)} className="rounded-full px-8 shadow-md hover:shadow-lg transition-all h-12">
            <CameraIcon className="mr-2 h-4 w-4" /> Bắt đầu đo
            </Button>
        </div>
      )}
      
      {isMeasuring && (
        <div className="relative w-full aspect-[4/3] sm:aspect-video bg-black rounded-xl overflow-hidden border border-border/50 shadow-inner group">
          <video ref={videoRef} className="hidden" />
          <canvas ref={canvasRef} width={640} height={480} className="w-full h-full object-cover opacity-80" />
          
          {/* Scanning Overlay Effect */}
          <div className="absolute inset-0 bg-gradient-to-b from-transparent via-primary/20 to-transparent w-full h-[20%] animate-scan pointer-events-none" />
          
          <div className="absolute bottom-6 left-0 right-0 flex justify-center z-10">
            <Button 
                onClick={handleCapture} 
                disabled={isProcessing}
                className="rounded-full px-8 h-12 shadow-2xl bg-primary hover:bg-primary/90 text-primary-foreground border-2 border-primary-foreground/20 backdrop-blur-md"
            >
              {isProcessing ? (
                  <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Đang phân tích...
                  </>
              ) : (
                  <>
                      <Ruler className="mr-2 h-4 w-4" /> Lấy số đo
                  </>
              )}
            </Button>
          </div>
        </div>
      )}

      {measurementResult && (
        <div className="w-full space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
          <div className="flex items-center justify-center gap-2 text-green-500 mb-4">
              <CheckCircle2 className="h-5 w-5" />
              <span className="font-medium">Phân tích thành công!</span>
          </div>
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div className="p-4 border border-border/50 rounded-xl bg-background/50 flex flex-col items-center gap-1">
                <span className="text-muted-foreground text-xs uppercase tracking-wider">Chiều cao</span>
                <span className="font-bold text-lg">{measurementResult.height} <span className="text-xs font-normal text-muted-foreground">cm</span></span>
            </div>
            <div className="p-4 border border-border/50 rounded-xl bg-background/50 flex flex-col items-center gap-1">
                <span className="text-muted-foreground text-xs uppercase tracking-wider">Cân nặng</span>
                <span className="font-bold text-lg">{measurementResult.weight} <span className="text-xs font-normal text-muted-foreground">kg</span></span>
            </div>
            <div className="p-4 border border-border/50 rounded-xl bg-background/50 flex flex-col items-center gap-1">
                <span className="text-muted-foreground text-xs uppercase tracking-wider">Vòng ngực</span>
                <span className="font-bold text-lg">{measurementResult.chest} <span className="text-xs font-normal text-muted-foreground">cm</span></span>
            </div>
            <div className="p-4 border border-border/50 rounded-xl bg-background/50 flex flex-col items-center gap-1">
                <span className="text-muted-foreground text-xs uppercase tracking-wider">Vòng eo</span>
                <span className="font-bold text-lg">{measurementResult.waist} <span className="text-xs font-normal text-muted-foreground">cm</span></span>
            </div>
          </div>
          <div className="flex justify-center pt-2">
            <Button variant="ghost" className="rounded-full px-8 text-muted-foreground hover:text-foreground hover:bg-secondary" onClick={() => setMeasurementResult(null)}>
                Đo lại
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
