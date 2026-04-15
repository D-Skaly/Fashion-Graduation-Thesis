"use client";

import { useQuery } from "@tanstack/react-query";
import { useParams } from "next/navigation";
import { ArrowLeft } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import api from "@/lib/axios";

import { OrderStatusBadge } from "@/components/admin/OrderStatusBadge";
import { UpdateStatusDialog } from "@/components/admin/UpdateStatusDialog";
import { PrintInvoiceButton } from "@/components/admin/PrintInvoiceButton";

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

const fetchOrderDetail = async (orderId: string): Promise<Order> => {
    const { data } = await api.get(`/orders/${orderId}`);
    return data.data;
};

export default function AdminOrderDetailPage() {
    const params = useParams();
    const orderId = params.id as string;

    const { data: order, isLoading } = useQuery({
        queryKey: ["admin-order", orderId],
        queryFn: () => fetchOrderDetail(orderId),
        enabled: !!orderId,
    });

    if (isLoading) {
        return <div className="p-8">Loading...</div>;
    }

    if (!order) {
        return <div className="p-8">Order not found</div>;
    }

    return (
        <div className="space-y-6">
            <div className="flex items-center gap-4">
                <Button variant="ghost" size="icon" asChild>
                    <Link href="/admin/orders">
                        <ArrowLeft className="h-5 w-5" />
                    </Link>
                </Button>
                <div>
                    <h1 className="text-2xl font-bold">Order {order.orderNumber}</h1>
                    <p className="text-sm text-muted-foreground">
                        {new Date(order.createdAt).toLocaleString()}
                    </p>
                </div>
                <div className="ml-auto flex gap-2">
                    <OrderStatusBadge status={order.status} />
                    <UpdateStatusDialog
                        currentStatus={order.status}
                        onUpdate={(status, note) => console.log("Update:", status, note)}
                    />
                    <PrintInvoiceButton orderId={orderId} />
                </div>
            </div>

            <div className="grid lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2 space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Order Items</CardTitle>
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
                                            <TableCell>{item.size} / {item.color}</TableCell>
                                            <TableCell className="text-center">{item.quantity}</TableCell>
                                            <TableCell className="text-right">
                                                ${item.price.toLocaleString()}
                                            </TableCell>
                                            <TableCell className="text-right font-medium">
                                                ${item.subtotal.toLocaleString()}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                </TableBody>
                            </Table>
                            <Separator className="my-4" />
                            <div className="space-y-2 text-sm">
                                <div className="flex justify-between">
                                    <span className="text-muted-foreground">Subtotal</span>
                                    <span>${order.subTotal.toLocaleString()}</span>
                                </div>
                                {order.shippingCost > 0 && (
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Shipping</span>
                                        <span>${order.shippingCost.toLocaleString()}</span>
                                    </div>
                                )}
                                {order.taxAmount > 0 && (
                                    <div className="flex justify-between">
                                        <span className="text-muted-foreground">Tax</span>
                                        <span>${order.taxAmount.toLocaleString()}</span>
                                    </div>
                                )}
                                {order.discountAmount > 0 && (
                                    <div className="flex justify-between text-green-600">
                                        <span>Discount {order.discountCode && `(${order.discountCode})`}</span>
                                        <span>-${order.discountAmount.toLocaleString()}</span>
                                    </div>
                                )}
                                <Separator />
                                <div className="flex justify-between text-lg font-bold">
                                    <span>Total</span>
                                    <span>${order.totalAmount.toLocaleString()}</span>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </div>

                <div className="space-y-6">
                    <Card>
                        <CardHeader>
                            <CardTitle>Shipping Address</CardTitle>
                        </CardHeader>
                        <CardContent>
                            <p className="text-sm whitespace-pre-wrap">{order.shippingAddress}</p>
                        </CardContent>
                    </Card>

                    {order.notes && (
                        <Card>
                            <CardHeader>
                                <CardTitle>Order Notes</CardTitle>
                            </CardHeader>
                            <CardContent>
                                <p className="text-sm">{order.notes}</p>
                            </CardContent>
                        </Card>
                    )}
                </div>
            </div>
        </div>
    );
}
