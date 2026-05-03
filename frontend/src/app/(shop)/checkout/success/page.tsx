"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { CheckCircle2, Package, ArrowRight, ShoppingBag } from "lucide-react";
import { Button } from "@/components/ui/button";
import { motion } from "framer-motion";

export default function OrderSuccessPage() {
  const [showConfetti, setShowConfetti] = useState(false);

   
  useEffect(() => {
    const confettiTimer = setTimeout(() => setShowConfetti(true), 100);
    const timer = setTimeout(() => setShowConfetti(false), 3000);
    return () => {
      clearTimeout(confettiTimer);
      clearTimeout(timer);
    };
  }, []);

  return (
    <div className="min-h-[calc(100dvh-8rem)] flex items-center justify-center relative overflow-hidden">
      {/* Background decorations */}
      <div className="absolute inset-0 pointer-events-none">
        <div className="absolute top-1/4 left-1/4 w-[400px] h-[400px] bg-green-500/5 rounded-full blur-[100px]" />
        <div className="absolute bottom-1/4 right-1/4 w-[300px] h-[300px] bg-primary/5 rounded-full blur-[80px]" />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6, ease: "easeOut" }}
        className="relative z-10 flex flex-col items-center justify-center text-center space-y-8 max-w-lg px-4"
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

        <div className="bg-secondary/20 p-6 rounded-2xl w-full flex items-center gap-4">
          <div className="w-12 h-12 bg-primary/10 rounded-xl flex items-center justify-center shrink-0">
            <Package className="w-6 h-6 text-primary" />
          </div>
          <div className="text-left">
            <p className="text-sm text-muted-foreground">Confirmation Email</p>
            <p className="font-medium">Sent to your registered email</p>
          </div>
        </div>

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

        <p className="text-sm text-muted-foreground pt-4">
          Have a question? <Link href="/contact" className="text-primary hover:underline">Contact our support team</Link>
        </p>
      </motion.div>
    </div>
  );
}
