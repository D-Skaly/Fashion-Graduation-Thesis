"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useParams } from "next/navigation";
import { ArrowLeft, Loader2 } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import {
    Table, TableBody, TableCell,
    TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
    Select, SelectContent, SelectItem,
    SelectTrigger, SelectValue,
} from "@/components/ui/select";
import { toast } from "sonner";
import api from "@/lib/axios";

interface OrderItem {
    id: string;
    productName: string;
    size: string;
    color: string;
    quantity: number;
    price: number;
    subtotal: number;
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
    items: OrderItem[];
    createdAt: string;
}

const STATUS_FLOW = ["PENDING", "CONFIRMED", "PROCESSING", "SHIPPED", "DELIVERED", "CANCELLED"];

const STATUS_CONFIG: Record<string, { label: string; class: string }> = {
    PENDING:    { label: "Pending",    class: "bg-yellow-500/20 text-yellow-300 border-yellow-500/30" },
    CONFIRMED:  { label: "Confirmed",  class: "bg-blue-500/20 text-blue-300 border-blue-500/30" },
    PROCESSING: { label: "Processing", class: "bg-violet-500/20 text-violet-300 border-violet-500/30" },
    SHIPPED:    { label: "Shipped",    class: "bg-indigo-500/20 text-indigo-300 border-indigo-500/30" },
    DELIVERED:  { label: "Delivered",  class: "bg-emerald-500/20 text-emerald-300 border-emerald-500/30" },
    CANCELLED:  { label: "Cancelled",  class: "bg-red-500/20 text-red-300 border-red-500/30" },
};

const fetchOrderDetail = async (orderId: string): Promise<Order> => {
    const { data } = await api.get(`/orders/${orderId}`);
    // After interceptor unwraps ApiResponse, data IS the order directly
    return data as Order;
};

const updateOrderStatus = async ({ orderId, status }: { orderId: string; status: string }) => {
    await api.put(`/orders/${orderId}/status`, { status });
};

export default function AdminOrderDetailPage() {
    const params = useParams();
    const orderId = params.id as string;
    const queryClient = useQueryClient();

    const { data: order, isLoading, isError } = useQuery({
        queryKey: ["admin-order", orderId],
        queryFn: () => fetchOrderDetail(orderId),
        enabled: !!orderId,
    });

    const statusMutation = useMutation({
        mutationFn: updateOrderStatus,
        onSuccess: () => {
            toast.success("Order status updated");
            queryClient.invalidateQueries({ queryKey: ["admin-order", orderId] });
            queryClient.invalidateQueries({ queryKey: ["admin-orders"] });
        },
        onError: (err: unknown) => {
            const axiosError = err as { response?: { data?: { message?: string } } };
            toast.error(axiosError?.response?.data?.message || "Failed to update status");
        },
    });

    if (isLoading) {
        return (
            <div className="space-y-6">
                <Skeleton className="h-8 w-48 bg-white/10" />
                <div className="grid lg:grid-cols-3 gap-6">
                    <div className="lg:col-span-2"><Skeleton className="h-80 w-full bg-white/10 rounded-xl" /></div>
                    <div className="space-y-4">
                        <Skeleton className="h-40 w-full bg-white/10 rounded-xl" />
                        <Skeleton className="h-32 w-full bg-white/10 rounded-xl" />
                    </div>
                </div>
            </div>
        );
    }

    if (isError || !order) {
        return (
            <div className="flex flex-col items-center justify-center h-64 gap-4 text-stone-400">
                <p>Order not found or failed to load.</p>
                <Button variant="outline" asChild className="border-white/20">
                    <Link href="/admin/orders"><ArrowLeft className="mr-2 h-4 w-4" /> Back to Orders</Link>
                </Button>
            </div>
        );
    }

    const statusCfg = STATUS_CONFIG[order.status] || { label: order.status, class: "bg-stone-500/20 text-stone-300 border-stone-500/30" };

    return (
        <div className="space-y-6">
            {/* Header */}
            <div className="flex items-center gap-4 flex-wrap">
                <Button variant="ghost" size="icon" asChild className="hover:bg-white/10 text-stone-400 hover:text-white">
                    <Link href="/admin/orders"><ArrowLeft className="h-5 w-5" /></Link>
                </Button>
                <div className="flex-1">
                    <h1 className="text-2xl font-black uppercase tracking-widest">
                        Order {order.orderNumber || `#${order.id.substring(0, 8)}`}
                    </h1>
                    <p className="text-stone-400 text-sm">
                        {new Date(order.createdAt).toLocaleString("en-US", {
                            dateStyle: "medium", timeStyle: "short"
                        })}
                    </p>
                </div>
                <div className="flex items-center gap-3">
                    <Badge className={`border text-[11px] uppercase tracking-wider font-bold px-3 py-1 ${statusCfg.class}`}>
                        {statusCfg.label}
                    </Badge>
                    {/* Status Update */}
                    <Select
                        value={order.status}
                        onValueChange={(value) => statusMutation.mutate({ orderId, status: value })}
                        disabled={statusMutation.isPending}
                    >
                        <SelectTrigger className="w-44 bg-white/5 border-white/20 text-white h-9 text-xs uppercase tracking-wider font-bold">
                            {statusMutation.isPending
                                ? <><Loader2 className="h-3 w-3 animate-spin mr-2" /> Updating...</>
                                : <SelectValue />
                            }
                        </SelectTrigger>
                        <SelectContent>
                            {STATUS_FLOW.map(s => (
                                <SelectItem key={s} value={s} className="text-xs uppercase tracking-wider">
                                    {STATUS_CONFIG[s]?.label || s}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                </div>
            </div>

            <div className="grid lg:grid-cols-3 gap-6">
                {/* Main: Order Items */}
                <div className="lg:col-span-2 space-y-6">
                    <Card className="bg-white/5 border-white/10 text-white">
                        <CardHeader>
                            <CardTitle className="text-sm font-bold uppercase tracking-widest text-stone-300">
                                Order Items ({order.items?.length ?? 0})
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <Table>
                                <TableHeader>
                                    <TableRow className="border-white/10 hover:bg-transparent">
                                        {["Product", "Variant", "Qty", "Price", "Subtotal"].map(h => (
                                            <TableHead key={h} className="text-stone-400 text-[10px] uppercase tracking-widest font-bold">
                                                {h}
                                            </TableHead>
                                        ))}
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {order.items?.map((item) => (
                                        <TableRow key={item.id} className="border-white/10 hover:bg-white/5">
                                            <TableCell className="font-semibold text-white">{item.productName}</TableCell>
                                            <TableCell className="text-stone-400 text-sm">{item.size} / {item.color}</TableCell>
                                            <TableCell className="text-center text-white">{item.quantity}</TableCell>
                                            <TableCell className="text-right text-stone-300">${item.price?.toFixed(2)}</TableCell>
                                            <TableCell className="text-right font-bold text-white">${item.subtotal?.toFixed(2)}</TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>

                            <Separator className="my-4 bg-white/10" />

                            {/* Cost breakdown */}
                            <div className="space-y-2 text-sm max-w-xs ml-auto">
                                <div className="flex justify-between text-stone-400">
                                    <span>Subtotal</span>
                                    <span>${(order.subTotal || order.totalAmount)?.toFixed(2)}</span>
                                </div>
                                {order.shippingCost > 0 && (
                                    <div className="flex justify-between text-stone-400">
                                        <span>Shipping</span>
                                        <span>${order.shippingCost?.toFixed(2)}</span>
                                    </div>
                                )}
                                {order.taxAmount > 0 && (
                                    <div className="flex justify-between text-stone-400">
                                        <span>Tax</span>
                                        <span>${order.taxAmount?.toFixed(2)}</span>
                                    </div>
                                )}
                                {order.discountAmount > 0 && (
                                    <div className="flex justify-between text-emerald-400">
                                        <span>Discount {order.discountCode && `(${order.discountCode})`}</span>
                                        <span>-${order.discountAmount?.toFixed(2)}</span>
                                    </div>
                                )}
                                <Separator className="bg-white/10" />
                                <div className="flex justify-between text-lg font-black text-white">
                                    <span>Total</span>
                                    <span>${order.totalAmount?.toFixed(2)}</span>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                {/* Sidebar */}
                <div className="space-y-4">
                    <Card className="bg-white/5 border-white/10 text-white">
                        <CardHeader>
                            <CardTitle className="text-xs font-bold uppercase tracking-widest text-stone-400">
                                Shipping Address
                            </CardTitle>
                        </CardHeader>
                        <CardContent>
                            <p className="text-sm text-stone-300 whitespace-pre-wrap leading-relaxed">
                                {order.shippingAddress || "No address provided"}
                            </p>
                        </CardContent>
                    </Card>

                    {order.notes && (
                        <Card className="bg-white/5 border-white/10 text-white">
                            <CardHeader>
                                <CardTitle className="text-xs font-bold uppercase tracking-widest text-stone-400">
                                    Order Notes
                                </CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p className="text-sm text-stone-300">{order.notes}</p>
                            </CardContent>
                        </Card>
                    )}

                    {/* Quick Actions */}
                    <Card className="bg-white/5 border-white/10 text-white">
                        <CardHeader>
                            <CardTitle className="text-xs font-bold uppercase tracking-widest text-stone-400">
                                Quick Actions
                            </CardTitle>
                        </CardHeader>
                        <CardContent className="space-y-2">
                            <Button
                                variant="outline"
                                className="w-full border-white/20 hover:bg-white/10 text-stone-300 text-xs uppercase tracking-wider font-bold justify-start"
                                onClick={() => window.print()}
                            >
                                Print Invoice
                            </Button>
                            <Button
                                variant="outline"
                                className="w-full border-white/20 hover:bg-white/10 text-stone-300 text-xs uppercase tracking-wider font-bold justify-start"
                                onClick={() => {
                                    navigator.clipboard.writeText(order.id);
                                    toast.success("Order ID copied");
                                }}
                            >
                                Copy Order ID
                            </Button>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
    );
}
