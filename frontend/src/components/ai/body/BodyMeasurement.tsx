"use client";

import { useEffect, useRef, useState } from "@react";
import * as pose from "@mediapipe/pose";
import { Camera } from "@mediapipe/camera_utils";
import { Button } from "@/components/ui/button";
import { Loader2, Camera as CameraIcon } from "lucide-react";
import api from "@/lib/axios";

export function BodyMeasurement() {
  const videoRef = useRef<HTMLVideoElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [isMeasuring, setIsMeasuring] = useState(false);
  const [measurementResult, setMeasurementResult] = useState<any>(null);

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
        // Drawing landmarks (simplified)
        for (const landmark of results.poseLandmarks) {
          canvasCtx.beginPath();
          canvasCtx.arc(landmark.x * canvasRef.current.width, landmark.y * canvasRef.current.height, 2, 0, 2 * Math.PI);
          canvasCtx.fillStyle = "#00FF00";
          canvasCtx.fill();
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
    // Logic to calculate dimensions from landmarks and send to backend
    setIsMeasuring(false);
    // Simulation for now
    const dummyProfile = {
      height: 175,
      weight: 70,
      chest: 95,
      waist: 80,
      hips: 98
    };
    
    try {
      await api.post("/users/profile/body", dummyProfile);
      setMeasurementResult(dummyProfile);
    } catch (error) {
      console.error("Failed to save body profile", error);
    }
  };

  return (
    <div className="flex flex-col items-center gap-4 border p-4 rounded-xl bg-card">
      <h3 className="font-semibold text-lg">AI Body Measurement</h3>
      {!isMeasuring && !measurementResult && (
        <Button onClick={() => setIsMeasuring(true)}>
          <CameraIcon className="mr-2 h-4 w-4" /> Start Measurement
        </Button>
      )}
      
      {isMeasuring && (
        <div className="relative w-full aspect-video bg-black rounded-lg overflow-hidden">
          <video ref={videoRef} className="hidden" />
          <canvas ref={canvasRef} width={640} height={480} className="w-full h-full object-cover" />
          <div className="absolute bottom-4 left-0 right-0 flex justify-center">
            <Button onClick={handleCapture}>Capture & Calculate</Button>
          </div>
        </div>
      )}

      {measurementResult && (
        <div className="w-full space-y-2">
          <div className="grid grid-cols-2 gap-2 text-sm">
            <div className="p-2 border rounded bg-muted/50">Chest: {measurementResult.chest} cm</div>
            <div className="p-2 border rounded bg-muted/50">Waist: {measurementResult.waist} cm</div>
            <div className="p-2 border rounded bg-muted/50">Hips: {measurementResult.hips} cm</div>
            <div className="p-2 border rounded bg-muted/50">Height: {measurementResult.height} cm</div>
          </div>
          <Button variant="outline" className="w-full" onClick={() => setMeasurementResult(null)}>Redo</Button>
        </div>
      )}
    </div>
  );
}
