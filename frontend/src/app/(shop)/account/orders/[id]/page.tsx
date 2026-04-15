"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { format } from "date-fns";
import { ArrowLeft, Package, Truck, MapPin, CreditCard, Clock, AlertCircle } from "lucide-react";
import { toast } from "sonner";
import api from "@/lib/axios";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Alert, AlertDescription } from "@/components/ui/alert";

import { OrderStatusTimeline } from "@/components/orders/OrderStatusTimeline";
import { CancelOrderDialog } from "@/components/orders/CancelOrderDialog";

// Types
interface OrderItem {
  id: string;
  productName: string;
  size: string;
  color: string;
  quantity: number;
  price: number;
  subtotal: number;
}

interface OrderStatusHistory {
  id: string;
  status: string;
  note: string;
  createdAt: string;
}

interface Shipping {
  id: string;
  carrier: string;
  trackingNumber: string;
  shippingMethod: string;
  shippingCost: number;
  estimatedDelivery: string;
  shippedAt: string;
  deliveredAt: string;
  status: string;
}

interface Order {
  id: string;
  orderNumber: string;
  totalAmount: number;
  subTotal: number;
  taxAmount: number;
  shippingCost: number;
  discountAmount: number;
  discountCode: string;
  status: string;
  shippingAddress: string;
  notes: string;
  cancelledAt: string;
  cancelledReason: string;
  items: OrderItem[];
  createdAt: string;
}

const fetchOrderDetail = async (orderId: string): Promise<Order> => {
  const { data } = await api.get(`/orders/${orderId}`);
  return data.data;
};

const fetchOrderStatusHistory = async (orderId: string): Promise<OrderStatusHistory[]> => {
  const { data } = await api.get(`/orders/${orderId}/status-history`);
  return data.data;
};

const fetchOrderTracking = async (orderId: string): Promise<Shipping> => {
  const { data } = await api.get(`/orders/${orderId}/tracking`);
  return data.data;
};

const getStatusColor = (status: string) => {
  switch (status) {
    case "PENDING":
      return "bg-yellow-100 text-yellow-800 border-yellow-300";
    case "CONFIRMED":
      return "bg-blue-100 text-blue-800 border-blue-300";
    case "PROCESSING":
      return "bg-purple-100 text-purple-800 border-purple-300";
    case "SHIPPED":
      return "bg-indigo-100 text-indigo-800 border-indigo-300";
    case "DELIVERED":
      return "bg-green-100 text-green-800 border-green-300";
    case "CANCELLED":
      return "bg-red-100 text-red-800 border-red-300";
    default:
      return "bg-gray-100 text-gray-800 border-gray-300";
  }
};

export default function OrderDetailPage() {
  const params = useParams();
  const router = useRouter();
  const queryClient = useQueryClient();
  const orderId = params.id as string;

  const { data: order, isLoading: isOrderLoading, error: orderError } = useQuery({
    queryKey: ["order", orderId],
    queryFn: () => fetchOrderDetail(orderId),
    enabled: !!orderId,
  });

  const { data: statusHistory, isLoading: isHistoryLoading } = useQuery({
    queryKey: ["order-history", orderId],
    queryFn: () => fetchOrderStatusHistory(orderId),
    enabled: !!orderId,
  });

  const { data: shipping, isLoading: isShippingLoading } = useQuery({
    queryKey: ["order-tracking", orderId],
    queryFn: () => fetchOrderTracking(orderId),
    enabled: !!orderId,
  });

  const cancelMutation = useMutation({
    mutationFn: async (reason: string) => {
      const response = await api.put(`/orders/${orderId}/cancel`, { reason });
      return response.data;
    },
    onSuccess: () => {
      toast.success("Order cancelled successfully");
      queryClient.invalidateQueries({ queryKey: ["order", orderId] });
      queryClient.invalidateQueries({ queryKey: ["order-history", orderId] });
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || "Failed to cancel order");
    },
  });

  const canCancel = (status: string) => {
    return ["PENDING", "CONFIRMED"].includes(status);
  };

  if (isOrderLoading) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-5xl">
        <Skeleton className="h-8 w-48 mb-6" />
        <div className="space-y-4">
          <Skeleton className="h-64 w-full" />
          <Skeleton className="h-48 w-full" />
        </div>
      </div>
    );
  }

  if (orderError || !order) {
    return (
      <div className="container mx-auto px-4 py-16 max-w-2xl">
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>
            Failed to load order details. Please try again.
          </AlertDescription>
        </Alert>
        <div className="mt-6 text-center">
          <Button asChild variant="outline">
            <Link href="/account/orders">Back to Orders</Link>
          </Button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-5xl">
      {/* Header */}
      <div className="flex items-center gap-4 mb-6">
        <Button variant="ghost" size="icon" asChild>
          <Link href="/account/orders">
            <ArrowLeft className="h-5 w-5" />
          </Link>
        </Button>
        <div>
          <h1 className="text-2xl font-bold">Order #{order.orderNumber || order.id.substring(0, 8)}</h1>
          <p className="text-sm text-muted-foreground">
            Placed on {format(new Date(order.createdAt), "MMMM dd, yyyy 'at' h:mm a")}
          </p>
        </div>
        <Badge className={`ml-auto ${getStatusColor(order.status)}`}>
          {order.status}
        </Badge>
      </div>

      <div className="grid lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Order Items */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Package className="h-5 w-5" />
                Order Items ({order.items.length})
              </CardTitle>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Product</TableHead>
                    <TableHead>Variant</TableHead>
                    <TableHead className="text-center">Qty</TableHead>
                    <TableHead className="text-right">Price</TableHead>
                    <TableHead className="text-right">Subtotal</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {order.items.map((item) => (
                    <TableRow key={item.id}>
                      <TableCell className="font-medium">{item.productName}</TableCell>
                      <TableCell className="text-muted-foreground">
                        {item.size} / {item.color}
                      </TableCell>
                      <TableCell className="text-center">{item.quantity}</TableCell>
                      <TableCell className="text-right">
                        {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(item.price)}
                      </TableCell>
                      <TableCell className="text-right font-medium">
                        {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(item.subtotal)}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>

              <Separator className="my-4" />

              {/* Cost Breakdown */}
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Subtotal</span>
                  <span>
                    {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(order.subTotal || order.totalAmount)}
                  </span>
                </div>
                {order.shippingCost > 0 && (
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Shipping</span>
                    <span>
                      {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(order.shippingCost)}
                    </span>
                  </div>
                )}
                {order.taxAmount > 0 && (
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Tax</span>
                    <span>
                      {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(order.taxAmount)}
                    </span>
                  </div>
                )}
                {order.discountAmount > 0 && (
                  <div className="flex justify-between text-green-600">
                    <span>Discount {order.discountCode && `(${order.discountCode})`}</span>
                    <span>
                      -{new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(order.discountAmount)}
                    </span>
                  </div>
                )}
                <Separator />
                <div className="flex justify-between text-lg font-bold">
                  <span>Total</span>
                  <span>
                    {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(order.totalAmount)}
                  </span>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Order Status Timeline */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Clock className="h-5 w-5" />
                Order Status History
              </CardTitle>
            </CardHeader>
            <CardContent>
              {isHistoryLoading ? (
                <Skeleton className="h-32 w-full" />
              ) : (
                <OrderStatusTimeline history={statusHistory || []} />
              )}
            </CardContent>
          </Card>

          {/* Shipping Information */}
          {shipping && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Truck className="h-5 w-5" />
                  Shipping Information
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <p className="text-muted-foreground">Carrier</p>
                    <p className="font-medium">{shipping.carrier || "N/A"}</p>
                  </div>
                  <div>
                    <p className="text-muted-foreground">Tracking Number</p>
                    <p className="font-medium">{shipping.trackingNumber || "N/A"}</p>
                  </div>
                  <div>
                    <p className="text-muted-foreground">Shipping Method</p>
                    <p className="font-medium">{shipping.shippingMethod || "Standard"}</p>
                  </div>
                  <div>
                    <p className="text-muted-foreground">Status</p>
                    <p className="font-medium">{shipping.status}</p>
                  </div>
                </div>
                {shipping.estimatedDelivery && (
                  <div className="bg-blue-50 p-3 rounded-lg text-sm">
                    <p className="text-blue-800">
                      Estimated Delivery: {format(new Date(shipping.estimatedDelivery), "MMMM dd, yyyy")}
                    </p>
                  </div>
                )}
              </CardContent>
            </Card>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Shipping Address */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-base">
                <MapPin className="h-4 w-4" />
                Shipping Address
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-sm whitespace-pre-wrap">{order.shippingAddress}</p>
            </CardContent>
          </Card>

          {/* Payment Info */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-base">
                <CreditCard className="h-4 w-4" />
                Payment Information
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Method</span>
                <span className="font-medium">COD</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-muted-foreground">Status</span>
                <Badge variant="outline" className="text-xs">
                  {order.status === "DELIVERED" ? "Paid" : "Pending"}
                </Badge>
              </div>
            </CardContent>
          </Card>

          {/* Actions */}
          <div className="space-y-3">
            {canCancel(order.status) && (
              <CancelOrderDialog 
                orderId={orderId}
                onCancel={(reason) => cancelMutation.mutate(reason)}
                isLoading={cancelMutation.isPending}
              />
            )}

            <Button variant="outline" className="w-full" asChild>
              <Link href="/account/orders">Back to Orders</Link>
            </Button>
          </div>

          {/* Cancelled Notice */}
          {order.status === "CANCELLED" && order.cancelledAt && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>
                <p className="font-medium">Order Cancelled</p>
                <p className="text-sm">
                  {format(new Date(order.cancelledAt), "MMMM dd, yyyy")}
                  {order.cancelledReason && ` - Reason: ${order.cancelledReason}`}
                </p>
              </AlertDescription>
            </Alert>
          )}
        </div>
      </div>
    </div>
  );
}
