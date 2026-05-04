"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Search, Truck, Package, CheckCircle2, Clock, AlertCircle, Loader2 } from "lucide-react";
import Link from "next/link";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Skeleton } from "@/components/ui/skeleton";
import api from "@/lib/axios";

const formSchema = z.object({
  orderNumber: z.string().min(1, "Order number is required"),
  email: z.string().email("Valid email is required"),
});

type TrackOrderForm = z.infer<typeof formSchema>;

interface OrderStatus {
  status: string;
  timestamp: string;
  note?: string;
}

interface OrderTrackResult {
  id: string;
  orderNumber: string;
  status: string;
  createdAt: string;
  statusHistory: OrderStatus[];
  shippingAddress: string;
  items: Array<{
    productName: string;
    quantity: number;
    size: string;
    color: string;
  }>;
}

export default function TrackOrderPage() {
  const [orderResult, setOrderResult] = useState<OrderTrackResult | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const form = useForm<TrackOrderForm>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      orderNumber: "",
      email: "",
    },
  });

  async function onSubmit(values: TrackOrderForm) {
    setIsLoading(true);
    setError(null);
    setOrderResult(null);

    try {
      const { data } = await api.post("/orders/track", values);
      setOrderResult(data);
    } catch (error: unknown) {
      const axiosError = error as { response?: { data?: { message?: string } } };
      setError(axiosError.response?.data?.message || "Order not found. Please check your details.");
    } finally {
      setIsLoading(false);
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case "PENDING": return "text-yellow-500";
      case "CONFIRMED": return "text-blue-500";
      case "PROCESSING": return "text-purple-500";
      case "SHIPPED": return "text-indigo-500";
      case "DELIVERED": return "text-green-500";
      case "CANCELLED": return "text-red-500";
      default: return "text-gray-500";
    }
  };

  return (
    <div className="container mx-auto px-4 py-12 max-w-3xl">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold">Track Your Order</h1>
        <p className="text-muted-foreground mt-2">
          Enter your order number and email to track your order
        </p>
      </div>

      <Card>
        <CardContent className="pt-6">
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="orderNumber"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Order Number</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Package className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                          placeholder="e.g. ORD-12345"
                          className="pl-10"
                          {...field}
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <FormField
                control={form.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email Address</FormLabel>
                    <FormControl>
                      <div className="relative">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        <Input
                          placeholder="your@email.com"
                          type="email"
                          className="pl-10"
                          {...field}
                        />
                      </div>
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Tracking...
                  </>
                ) : (
                  <>
                    <Truck className="mr-2 h-4 w-4" />
                    Track Order
                  </>
                )}
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>

      {/* Results */}
      {error && (
        <Alert variant="destructive" className="mt-6">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      {isLoading && (
        <div className="mt-6 space-y-4">
          <Skeleton className="h-32 w-full" />
          <Skeleton className="h-48 w-full" />
        </div>
      )}

      {orderResult && (
        <div className="mt-6 space-y-6">
          {/* Order Status Card */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <span>Order #{orderResult.orderNumber}</span>
                <span className={`font-medium ${getStatusColor(orderResult.status)}`}>
                  {orderResult.status}
                </span>
              </CardTitle>
            </CardHeader>
            <CardContent>
              {/* Status Timeline */}
              <div className="space-y-4">
                {orderResult.statusHistory?.map((status, index) => (
                  <div key={index} className="flex gap-4">
                    <div className="flex flex-col items-center">
                      <div className={`rounded-full p-1 ${index === 0 ? "text-green-500" : "text-muted-foreground"}`}>
                        <CheckCircle2 className="h-5 w-5" />
                      </div>
                      {index < orderResult.statusHistory.length - 1 && (
                        <div className="w-px h-8 bg-border" />
                      )}
                    </div>
                    <div className="flex-1 pb-4">
                      <p className="font-medium">{status.status}</p>
                      <p className="text-sm text-muted-foreground">
                        {new Date(status.timestamp).toLocaleString()}
                      </p>
                      {status.note && (
                        <p className="text-sm text-muted-foreground mt-1">{status.note}</p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Order Items */}
          <Card>
            <CardHeader>
              <CardTitle>Order Items</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-3">
                {orderResult.items?.map((item, index) => (
                  <div key={index} className="flex justify-between items-center py-2 border-b last:border-0">
                    <div>
                      <p className="font-medium text-sm">{item.productName}</p>
                      <p className="text-xs text-muted-foreground">
                        {item.size} / {item.color} × {item.quantity}
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Shipping Address */}
          <Card>
            <CardHeader>
              <CardTitle>Shipping Address</CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm whitespace-pre-wrap">{orderResult.shippingAddress}</p>
            </CardContent>
          </Card>

          <div className="text-center">
            <Link href="/" className="text-sm text-muted-foreground hover:text-foreground">
              Continue Shopping
            </Link>
          </div>
        </div>
      )}
    </div>
  );
}
