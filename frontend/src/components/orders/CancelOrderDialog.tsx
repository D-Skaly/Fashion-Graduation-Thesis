"use client";

import { useState } from "react";
import { XCircle, AlertTriangle } from "lucide-react";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Alert, AlertDescription } from "@/components/ui/alert";

interface CancelOrderDialogProps {
  orderId: string;
  onCancel: (reason: string) => void;
  isLoading?: boolean;
}

const cancelReasons = [
  "Changed my mind",
  "Found a better price elsewhere",
  "Ordered by mistake",
  "Shipping takes too long",
  "Product not as described",
  "Other",
];

export function CancelOrderDialog({ orderId, onCancel, isLoading = false }: CancelOrderDialogProps) {
  const [open, setOpen] = useState(false);
  const [selectedReason, setSelectedReason] = useState("");
  const [customReason, setCustomReason] = useState("");
  const [showCustomInput, setShowCustomInput] = useState(false);

  const handleReasonSelect = (reason: string) => {
    setSelectedReason(reason);
    setShowCustomInput(reason === "Other");
    if (reason !== "Other") {
      setCustomReason("");
    }
  };

  const handleCancel = () => {
    const finalReason = selectedReason === "Other" ? customReason : selectedReason;
    if (!finalReason.trim()) return;
    
    onCancel(finalReason);
    setOpen(false);
    // Reset state
    setSelectedReason("");
    setCustomReason("");
    setShowCustomInput(false);
  };

  const isValid = selectedReason && (selectedReason !== "Other" || customReason.trim());

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="destructive" className="w-full">
          <XCircle className="h-4 w-4 mr-2" />
          Cancel Order
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 text-destructive">
            <AlertTriangle className="h-5 w-5" />
            Cancel Order
          </DialogTitle>
          <DialogDescription>
            Are you sure you want to cancel this order? This action cannot be undone.
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4 py-4">
          <Alert variant="destructive" className="bg-destructive/10">
            <AlertDescription className="text-sm">
              Once cancelled, the order cannot be reactivated. Any applied discounts or promotions may not be available for future orders.
            </AlertDescription>
          </Alert>

          <div className="space-y-3">
            <Label>Select a reason for cancellation</Label>
            <div className="grid grid-cols-1 gap-2">
              {cancelReasons.map((reason) => (
                <button
                  key={reason}
                  type="button"
                  onClick={() => handleReasonSelect(reason)}
                  className={`text-left px-3 py-2 rounded-md text-sm transition-colors ${
                    selectedReason === reason
                      ? "bg-primary text-primary-foreground"
                      : "bg-secondary hover:bg-secondary/80"
                  }`}
                >
                  {reason}
                </button>
              ))}
            </div>
          </div>

          {showCustomInput && (
            <div className="space-y-2">
              <Label htmlFor="custom-reason">Please specify</Label>
              <Textarea
                id="custom-reason"
                placeholder="Tell us why you're cancelling..."
                value={customReason}
                onChange={(e) => setCustomReason(e.target.value)}
                className="min-h-[80px]"
              />
            </div>
          )}
        </div>

        <DialogFooter className="gap-2">
          <Button variant="outline" onClick={() => setOpen(false)} disabled={isLoading}>
            Keep Order
          </Button>
          <Button
            variant="destructive"
            onClick={handleCancel}
            disabled={!isValid || isLoading}
          >
            {isLoading ? "Cancelling..." : "Confirm Cancellation"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
