"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { CheckCircle2, Package, ArrowRight, ShoppingBag, Calendar, Share2, Copy, Check } from "lucide-react";
import { Button } from "@/components/ui/button";
import { motion } from "framer-motion";
import { apiService } from "@/lib/apiService";
import { toast } from "sonner";

interface OrderData {
  id: string;
  totalAmount: number;
  status: string;
  createdAt: string;
  items: Array<{
    productName: string;
    size: string;
    color: string;
    quantity: number;
    price: number;
    subtotal: number;
  }>;
  shippingInfo: {
    fullName: string;
    address: string;
  };
}

export default function OrderSuccessPage() {
  const [showConfetti, setShowConfetti] = useState(false);
  const [orderData, setOrderData] = useState<OrderData | null>(null);
  const [loading, setLoading] = useState(true);
  const [copied, setCopied] = useState(false);
  const searchParams = useSearchParams();
  const orderId = searchParams.get("orderId");

  useEffect(() => {
    const confettiTimer = setTimeout(() => setShowConfetti(true), 100);
    const timer = setTimeout(() => setShowConfetti(false), 3000);

    if (orderId) {
      fetchOrderDetails(orderId);
    } else {
      setLoading(false);
    }

    return () => {
      clearTimeout(confettiTimer);
      clearTimeout(timer);
    };
  }, [orderId]);

  async function fetchOrderDetails(id: string) {
    try {
      const response = await apiService.orders.getById(id);
      setOrderData(response.data);
    } catch (error) {
      console.error("Failed to fetch order details:", error);
    } finally {
      setLoading(false);
    }
  }

  function getEstimatedDelivery(): string {
    const date = new Date();
    date.setDate(date.getDate() + 5); // 5 days from now
    return date.toLocaleDateString('en-US', { weekday: 'long', month: 'long', day: 'numeric' });
  }

  function copyOrderNumber() {
    if (orderData?.id) {
      navigator.clipboard.writeText(orderData.id);
      setCopied(true);
      toast.success("Order number copied!");
      setTimeout(() => setCopied(false), 2000);
    }
  }

  function shareOrder() {
    const shareData = {
      title: 'My Fashion Order',
      text: `Just placed an order #${orderData?.id?.slice(-8)} at Fashion Store!`,
      url: window.location.origin,
    };

    if (navigator.share) {
      navigator.share(shareData).catch(() => {});
    } else {
      // Fallback: copy to clipboard
      const text = `${shareData.text} ${shareData.url}`;
      navigator.clipboard.writeText(text);
      toast.success("Share text copied to clipboard!");
    }
  }

  return (
    <div className="min-h-[calc(100dvh-8rem)] flex items-center justify-center relative overflow-hidden py-12">
      {/* Background decorations */}
      <div className="absolute inset-0 pointer-events-none">
        <div className="absolute top-1/4 left-1/4 w-[400px] h-[400px] bg-green-500/5 rounded-full blur-[100px]" />
        <div className="absolute bottom-1/4 right-1/4 w-[300px] h-[300px] bg-primary/5 rounded-full blur-[80px]" />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: "easeOut" }}
        className="relative z-10 flex flex-col items-center justify-center text-center space-y-8 max-w-2xl px-4 w-full"
      >
        <div className="relative">
          <motion.div
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            transition={{
              type: "spring",
              stiffness: 260,
              damping: 20,
              delay: 0.2
            }}
            className="w-24 h-24 bg-green-500 rounded-full flex items-center justify-center"
          >
            <CheckCircle2 className="w-12 h-12 text-white" />
          </motion.div>

          {showConfetti && (
            <div className="absolute inset-0 flex items-center justify-center -z-10">
              <div className="w-32 h-32 animate-ping bg-green-500/20 rounded-full" />
            </div>
          )}
        </div>

        <div className="space-y-4">
          <h1 className="text-4xl md:text-5xl font-bold tracking-tight">Order Placed!</h1>
          <p className="text-muted-foreground text-lg">
            Thank you for your purchase. Your order has been received and is being processed.
          </p>
        </div>

        {/* Order Details Card */}
        {orderData && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.4 }}
            className="bg-card border rounded-2xl p-6 w-full text-left space-y-6"
          >
            {/* Order Number & Delivery */}
            <div className="flex flex-col sm:flex-row justify-between gap-4 pb-6 border-b">
              <div className="space-y-1">
                <p className="text-sm text-muted-foreground">Order Number</p>
                <div className="flex items-center gap-2">
                  <p className="font-mono font-semibold text-lg">#{orderData.id.slice(-8).toUpperCase()}</p>
                  <button
                    onClick={copyOrderNumber}
                    className="text-muted-foreground hover:text-foreground transition-colors"
                    aria-label="Copy order number"
                  >
                    {copied ? <Check className="h-4 w-4 text-green-500" /> : <Copy className="h-4 w-4" />}
                  </button>
                </div>
              </div>
              <div className="space-y-1">
                <p className="text-sm text-muted-foreground flex items-center gap-1">
                  <Calendar className="h-3 w-3" />
                  Estimated Delivery
                </p>
                <p className="font-medium">{getEstimatedDelivery()}</p>
              </div>
            </div>

            {/* Order Summary */}
            <div className="space-y-3">
              <h3 className="font-semibold text-sm text-muted-foreground uppercase tracking-wider">Order Summary</h3>
              {orderData.items.map((item, index) => (
                <div key={index} className="flex justify-between items-start py-2">
                  <div className="flex-1">
                    <p className="font-medium text-sm">{item.productName}</p>
                    <p className="text-xs text-muted-foreground">
                      {item.size} / {item.color} × {item.quantity}
                    </p>
                  </div>
                  <p className="font-medium text-sm">${item.subtotal.toFixed(2)}</p>
                </div>
              ))}
              <div className="pt-3 border-t flex justify-between font-semibold">
                <span>Total</span>
                <span>${orderData.totalAmount.toFixed(2)}</span>
              </div>
            </div>

            {/* Shipping Info */}
            <div className="pt-4 border-t space-y-1">
              <p className="text-sm text-muted-foreground">Shipping To</p>
              <p className="font-medium text-sm">{orderData.shippingInfo.fullName}</p>
              <p className="text-sm text-muted-foreground">{orderData.shippingInfo.address}</p>
            </div>
          </motion.div>
        )}

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-4 w-full">
          <Button asChild size="lg" className="flex-1 rounded-full h-12">
            <Link href="/account/orders">
              View Order Details <ArrowRight className="ml-2 w-4 h-4" />
            </Link>
          </Button>
          <Button asChild variant="outline" size="lg" className="flex-1 rounded-full h-12">
            <Link href="/shop">
              <ShoppingBag className="mr-2 w-4 h-4" /> Continue Shopping
            </Link>
          </Button>
        </div>

        {/* Share Button */}
        <Button
          variant="ghost"
          size="sm"
          onClick={shareOrder}
          className="text-muted-foreground hover:text-foreground"
        >
          <Share2 className="mr-2 h-4 w-4" />
          Share your order
        </Button>

        <p className="text-sm text-muted-foreground pt-4">
          Have a question? <Link href="/contact" className="text-primary hover:underline">Contact our support team</Link>
        </p>
      </motion.div>
    </div>
  );
}
