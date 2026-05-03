"use client";

import { useState, Suspense, useMemo, useEffect } from "react";
import { useSearchParams } from "next/navigation";
import Link from "next/link";
import { CheckCircle2, XCircle, Loader2, AlertCircle } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Separator } from "@/components/ui/separator";

function PaymentCallbackContent() {
  const searchParams = useSearchParams();
  
  const orderId = searchParams.get("orderId");
  const method = searchParams.get("method");
  
  // VNPay/Momo return parameters
  const vnp_ResponseCode = searchParams.get("vnp_ResponseCode");
  const resultCode = searchParams.get("resultCode"); // Momo

  // Determine initial status and message from URL parameters
  const initialState = useMemo(() => {
    // VNPay success code is '00'
    if (vnp_ResponseCode === "00") {
      return { status: "success" as const, message: "Your payment has been processed successfully!" };
    } 
    
    if (vnp_ResponseCode) {
      return { status: "error" as const, message: `Payment failed (Error code: ${vnp_ResponseCode}). Please try again or contact support.` };
    }
    
    // Momo success code is '0'
    if (resultCode === "0") {
      return { status: "success" as const, message: "Your payment has been processed successfully!" };
    } 
    
    if (resultCode) {
      return { status: "error" as const, message: `Payment failed (Error code: ${resultCode}). Please try again or contact support.` };
    }

    if (orderId && method) {
      return { status: "loading" as const, message: "Processing your payment..." };
    }

    return { status: "error" as const, message: "Invalid payment session. Please try again from your cart." };
  }, [vnp_ResponseCode, resultCode, orderId, method]);

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [status, setStatus] = useState<"loading" | "success" | "error">(initialState.status);
  const [message, setMessage] = useState(initialState.message);

  useEffect(() => {
    // Only handle loading/redirect state updates in effect
    if (initialState.status === "loading" && orderId && method) {
      const timer = setTimeout(() => {
        // In real implementation, this would redirect to payment gateway URL
        if (method === "VNPAY") {
          setMessage("Redirecting to VNPay payment gateway...");
        } else if (method === "MOMO") {
          setMessage("Redirecting to MoMo payment gateway...");
        }
      }, 1500);

      return () => clearTimeout(timer);
    }
  }, [initialState.status, orderId, method]);

  return (
    <div className="container mx-auto px-4 py-16 max-w-lg">
      <Card>
        <CardHeader className="text-center">
          <div className="mx-auto h-20 w-20 rounded-full flex items-center justify-center mb-4">
            {status === "loading" && (
              <Loader2 className="h-12 w-12 text-primary animate-spin" />
            )}
            {status === "success" && (
              <CheckCircle2 className="h-12 w-12 text-green-500" />
            )}
            {status === "error" && (
              <XCircle className="h-12 w-12 text-red-500" />
            )}
          </div>
          
          <CardTitle className="text-2xl">
            {status === "loading" && "Processing Payment"}
            {status === "success" && "Payment Successful!"}
            {status === "error" && "Payment Failed"}
          </CardTitle>
          
          <CardDescription className="text-base">
            {message}
          </CardDescription>
        </CardHeader>

        <CardContent className="space-y-6">
          {orderId && (
            <>
              <div className="bg-muted p-4 rounded-lg">
                <p className="text-sm text-muted-foreground">Order ID</p>
                <p className="font-medium">{orderId}</p>
              </div>

              <Separator />
            </>
          )}

          {status === "success" && (
            <Alert className="bg-green-50 border-green-200">
              <CheckCircle2 className="h-4 w-4 text-green-600" />
              <AlertDescription className="text-green-800">
                Thank you for your purchase! A confirmation email has been sent to your registered email address.
              </AlertDescription>
            </Alert>
          )}

          {status === "error" && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>
                If you were charged but see this error, please contact our support team immediately with your order ID.
              </AlertDescription>
            </Alert>
          )}

          <div className="flex flex-col gap-3">
            {status === "success" ? (
              <>
                <Button asChild size="lg">
                  <Link href={`/account/orders/${orderId || ""}`}>View Order Details</Link>
                </Button>
                <Button variant="outline" asChild>
                  <Link href="/shop">Continue Shopping</Link>
                </Button>
              </>
            ) : status === "error" ? (
              <>
                <Button asChild size="lg">
                  <Link href="/checkout">Try Again</Link>
                </Button>
                <Button variant="outline" asChild>
                  <Link href="/account/orders">View My Orders</Link>
                </Button>
              </>
            ) : (
              <Button disabled size="lg">
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Please wait...
              </Button>
            )}
          </div>
        </CardContent>
      </Card>

      <p className="text-center text-sm text-muted-foreground mt-6">
        Need help? Contact us at{" "}
        <a href="mailto:support@fashionthesis.com" className="text-primary hover:underline">
          support@fashionthesis.com
        </a>
      </p>
    </div>
  );
}

export default function PaymentCallbackPage() {
  return (
    <Suspense fallback={
      <div className="container mx-auto px-4 py-16 max-w-lg">
        <Card>
          <CardHeader className="text-center">
            <Loader2 className="h-12 w-12 text-primary animate-spin mx-auto mb-4" />
            <CardTitle>Loading...</CardTitle>
            <CardDescription>Please wait while we process your request.</CardDescription>
          </CardHeader>
        </Card>
      </div>
    }>
      <PaymentCallbackContent />
    </Suspense>
  );
}
