"use client";

import { useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { Loader2, CheckCircle2, AlertCircle, Camera, RotateCcw } from "lucide-react";
import Image from "next/image";
import { motion } from "framer-motion";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Alert, AlertDescription } from "@/components/ui/alert";
import api from "@/lib/axios";

interface TryOnResult {
  jobId: string;
  status: "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";
  resultImage?: string;
  confidenceScore?: number;
  recommendedSize?: string;
  errorMessage?: string;
}

export default function TryOnResultPage() {
  const params = useParams();
  const router = useRouter();
  const jobId = params.jobId as string;

  const [result, setResult] = useState<TryOnResult | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchResult = async () => {
    try {
      const { data } = await api.get(`/tryon/result/${jobId}`);
      setResult(data);

      // If still processing, poll again
      if (data.status === "PENDING" || data.status === "PROCESSING") {
        setTimeout(fetchResult, 3000); // Poll every 3 seconds
      } else {
        setIsLoading(false);
      }
    } catch (err) {
      setError("Failed to fetch try-on result");
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (jobId) {
      fetchResult();
    }
  }, [jobId]);

  const getScoreColor = (score?: number) => {
    if (!score) return "text-muted-foreground";
    if (score >= 0.8) return "text-green-500";
    if (score >= 0.6) return "text-yellow-500";
    return "text-red-500";
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold">Virtual Try-On Result</h1>
        <p className="text-muted-foreground mt-2">
          {result?.status === "COMPLETED"
            ? "See how the outfit looks on you"
            : "Processing your virtual try-on..."}
        </p>
      </div>

      {/* Loading State */}
      {isLoading && (
        <Card>
          <CardContent className="pt-6">
            <div className="text-center space-y-4 py-8">
              <Loader2 className="h-8 w-8 animate-spin mx-auto text-primary" />
              <p className="text-muted-foreground">Processing your image...</p>
              <Progress value={33} className="w-48 mx-auto" />
            </div>
          </CardContent>
        </Card>
      )}

      {/* Error State */}
      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {/* Failed State */}
      {!isLoading && result?.status === "FAILED" && (
        <Card>
          <CardContent className="pt-6">
            <div className="text-center space-y-4 py-8">
              <AlertCircle className="h-12 w-12 mx-auto text-red-500" />
              <p className="font-semibold">Processing Failed</p>
              <p className="text-sm text-muted-foreground">
                {result.errorMessage || "Something went wrong. Please try again."}
              </p>
              <Button onClick={() => router.push("/tryon")}>
                <RotateCcw className="mr-2 h-4 w-4" />
                Try Again
              </Button>
            </div>
          </CardContent>
        </Card>
      )}

      {/* Success State */}
      {!isLoading && result?.status === "COMPLETED" && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="space-y-6"
        >
          {/* Result Image */}
          {result.resultImage && (
            <Card className="overflow-hidden">
              <div className="relative aspect-[3/4] w-full">
                <Image
                  src={result.resultImage}
                  alt="Try-On Result"
                  fill
                  className="object-contain"
                  sizes="(max-width: 768px) 100vw, 768px"
                />
              </div>
            </Card>
          )}

          {/* Confidence Score & Size Recommendation */}
          <div className="grid sm:grid-cols-2 gap-4">
            {result.confidenceScore !== undefined && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Confidence Score</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-center">
                    <p className={`text-4xl font-bold ${getScoreColor(result.confidenceScore)}`}>
                      {(result.confidenceScore * 100).toFixed(0)}%
                    </p>
                    <p className="text-sm text-muted-foreground mt-1">
                      {result.confidenceScore >= 0.8
                        ? "Excellent match!"
                        : result.confidenceScore >= 0.6
                        ? "Good match"
                        : "Fair match"}
                    </p>
                  </div>
                </CardContent>
              </Card>
            )}

            {result.recommendedSize && (
              <Card>
                <CardHeader>
                  <CardTitle className="text-base">Recommended Size</CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-center">
                    <div className="w-16 h-16 mx-auto bg-primary/10 rounded-full flex items-center justify-center mb-3">
                      <CheckCircle2 className="h-8 w-8 text-primary" />
                    </div>
                    <p className="text-3xl font-bold">{result.recommendedSize}</p>
                    <p className="text-sm text-muted-foreground mt-1">
                      Based on your body measurements
                    </p>
                  </div>
                </CardContent>
              </Card>
            )}
          </div>

          {/* Actions */}
          <div className="flex gap-4">
            <Button variant="outline" className="flex-1" onClick={() => router.push("/tryon")}>
              <Camera className="mr-2 h-4 w-4" />
              Try Another Photo
            </Button>
            <Button className="flex-1" onClick={() => router.push("/shop")}>
              Continue Shopping
            </Button>
          </div>
        </motion.div>
      )}
    </div>
  );
}
