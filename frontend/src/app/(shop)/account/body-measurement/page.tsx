"use client";

import { useState } from "react";
<<<<<<< ours
=======
import { motion } from "framer-motion";
>>>>>>> theirs
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Ruler, CheckCircle2, Loader2, Shirt, ShoppingBag } from "lucide-react";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { BodyMeasurement } from "@/components/ai/body/BodyMeasurement";

interface MeasurementData {
  height: number;
  weight: number;
  chest: number;
  waist: number;
}

export default function BodyMeasurementPage() {
  const queryClient = useQueryClient();
  const [measurements, setMeasurements] = useState<MeasurementData | null>(null);

  const saveMutation = useMutation({
    mutationFn: async (data: MeasurementData) => {
      const response = await fetch("/api/user/measurements", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });
      if (!response.ok) throw new Error("Failed to save");
      return response.json();
    },
    onSuccess: () => {
      toast.success("Measurements saved successfully!");
      queryClient.invalidateQueries({ queryKey: ["user-measurements"] });
    },
    onError: () => toast.error("Failed to save measurements"),
  });

  const getSizeRecommendation = (chest: number, waist: number): string => {
    if (chest < 36) return "XS";
    if (chest < 38) return "S";
    if (chest < 40) return "M";
    if (chest < 43) return "L";
    return "XL";
  };

  const handleMeasurementComplete = (data: MeasurementData) => {
    setMeasurements(data);
  };

  return (
    <div className="container mx-auto px-4 py-8 max-w-4xl">
      <div className="mb-8">
        <h1 className="text-3xl font-bold flex items-center gap-2">
          <Ruler className="h-6 w-6" />
          Body Measurement
        </h1>
        <p className="text-muted-foreground mt-2">
          Use your camera to capture your body measurements for accurate size recommendations
        </p>
      </div>

      {/* Measurement Info */}
      <Alert className="mb-6">
        <AlertDescription>
          <div className="flex items-start gap-2">
            <div className="space-y-1">
              <p className="font-medium">How it works:</p>
              <ul className="text-sm space-y-1 list-disc list-inside">
                <li>Stand 6-8 feet away from your camera</li>
                <li>Wear fitted clothing for accurate measurements</li>
                <li>Make sure your full body is visible in the frame</li>
              </ul>
            </div>
          </div>
        </AlertDescription>
      </Alert>

      {/* Body Measurement Component */}
      <Card className="mb-6">
        <CardContent className="pt-6">
          <BodyMeasurement onComplete={handleMeasurementComplete} />
        </CardContent>
      </Card>

      {/* Results */}
      {measurements && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="space-y-6"
        >
          <Card>
            <CardHeader>
              <CardTitle>Your Measurements</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="text-center p-4 bg-secondary/20 rounded-xl">
                  <p className="text-sm text-muted-foreground">Height</p>
                  <p className="text-2xl font-bold">{measurements.height}"</p>
                </div>
                <div className="text-center p-4 bg-secondary/20 rounded-xl">
                  <p className="text-sm text-muted-foreground">Weight</p>
                  <p className="text-2xl font-bold">{measurements.weight} lbs</p>
                </div>
                <div className="text-center p-4 bg-secondary/20 rounded-xl">
                  <p className="text-sm text-muted-foreground">Chest</p>
                  <p className="text-2xl font-bold">{measurements.chest}"</p>
                </div>
                <div className="text-center p-4 bg-secondary/20 rounded-xl">
                  <p className="text-sm text-muted-foreground">Waist</p>
                  <p className="text-2xl font-bold">{measurements.waist}"</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Size Recommendation */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Shirt className="h-4 w-4" />
                Recommended Size
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-center py-4">
                <div className="w-20 h-20 mx-auto bg-primary/10 rounded-full flex items-center justify-center mb-4">
                  <span className="text-3xl font-bold text-primary">
                    {getSizeRecommendation(measurements.chest, measurements.waist)}
                  </span>
                </div>
                <p className="text-sm text-muted-foreground">
                  Based on your chest and waist measurements
                </p>
              </div>
            </CardContent>
          </Card>

          {/* Save Button */}
          <Button
            onClick={() => saveMutation.mutate(measurements)}
            disabled={saveMutation.isPending}
            className="w-full"
            size="lg"
          >
            {saveMutation.isPending ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Saving...
              </>
            ) : (
              <>
                <CheckCircle2 className="mr-2 h-4 w-4" />
                Save Measurements
              </>
            )}
          </Button>
        </motion.div>
      )}
    </div>
  );
}
