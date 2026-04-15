"use client";

import { useQuery } from "@tanstack/react-query";
import { format } from "date-fns";
import { Package, MapPin, Clock, ChevronRight } from "lucide-react";
import Link from "next/link";

import api from "@/lib/axios";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Separator } from "@/components/ui/separator";

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

// Types matching OrderDto
type OrderItem = {
    id: string;
    productName: string;
    size: string;
    color: string;
    quantity: number;
    price: number;
    subtotal: number;
}

type Order = {
    id: string;
    totalAmount: number;
    status: string;
    shippingAddress: string;
    items: OrderItem[];
    createdAt: string;
}

const fetchOrders = async (): Promise<Order[]> => {
    const { data } = await api.get("/orders");
    return data;
}

export default function OrderHistoryPage() {
    const { data: orders, isLoading, isError } = useQuery({
        queryKey: ["orders"],
        queryFn: fetchOrders,
    });

    if (isLoading) {
        return (
            <div className="container mx-auto px-4 py-8 space-y-4">
                 <h1 className="text-2xl font-bold mb-6">Order History</h1>
                 {[1, 2].map((i) => <Skeleton key={i} className="h-48 w-full" />)}
            </div>
        )
    }

    if (isError) {
         return <div className="container mx-auto px-4 py-16 text-center text-red-500">Failed to load orders. Please try again.</div>
    }

    if (!orders || orders.length === 0) {
        return (
            <div className="container mx-auto px-4 py-16 text-center space-y-4">
                <div className="h-16 w-16 bg-secondary/30 rounded-full flex items-center justify-center mx-auto">
                    <Package className="h-8 w-8 text-muted-foreground" />
                </div>
                <h1 className="text-2xl font-bold">No orders yet</h1>
                <p className="text-muted-foreground">You haven't placed any orders yet.</p>
                <Button asChild className="mt-4">
                    <a href="/shop">Start Shopping</a>
                </Button>
            </div>
        )
    }

    return (
        <div className="container mx-auto px-4 py-8 max-w-5xl">
            <h1 className="text-3xl font-bold mb-8">My Orders</h1>
            
            <div className="space-y-8">
                {orders.map((order) => (
                    <Card key={order.id} className="overflow-hidden">
                        <CardHeader className="bg-secondary/10 border-b flex flex-row items-center justify-between p-6">
                             <div className="space-y-1">
                                <Link href={`/account/orders/${order.id}`} className="hover:underline">
                                    <CardTitle className="flex items-center gap-3">
                                        Order #{order.id.substring(0, 8)}
                                        <Badge className={`uppercase ${getStatusColor(order.status)}`}>
                                            {order.status}
                                        </Badge>
                                    </CardTitle>
                                </Link>
                                <CardDescription className="flex items-center gap-2">
                                    <Clock className="h-3 w-3" />
                                    {format(new Date(order.createdAt), "MMMM dd, yyyy 'at' h:mm a")}
                                </CardDescription>
                             </div>
                             <div className="text-right">
                                 <p className="text-sm text-muted-foreground">Total Amount</p>
                                 <p className="text-xl font-bold">
                                     {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(order.totalAmount)}
                                 </p>
                             </div>
                        </CardHeader>
                        <CardContent className="p-0">
                            <div className="p-6 grid gap-6 md:grid-cols-2">
                                <div className="space-y-4">
                                     <h4 className="font-semibold flex items-center gap-2">
                                         <MapPin className="h-4 w-4" /> Shipping Details
                                     </h4>
                                     <div className="text-sm text-muted-foreground bg-secondary/5 p-3 rounded-md border">
                                         {order.shippingAddress}
                                     </div>
                                </div>
                                <div>
                                     {/* Could add payment method or other details here */}
                                </div>
                            </div>
                            
                            <Separator />

                            <Table>
                                <TableHeader>
                                    <TableRow>
                                        <TableHead>Product</TableHead>
                                        <TableHead>Variant</TableHead>
                                        <TableHead className="text-center">Qty</TableHead>
                                        <TableHead className="text-right">Price</TableHead>
                                    </TableRow>
                                </TableHeader>
                                <TableBody>
                                    {order.items.slice(0, 3).map((item) => (
                                        <TableRow key={item.id}>
                                            <TableCell className="font-medium">{item.productName}</TableCell>
                                            <TableCell className="text-muted-foreground text-sm">
                                                {item.size} / {item.color}
                                            </TableCell>
                                            <TableCell className="text-center">{item.quantity}</TableCell>
                                            <TableCell className="text-right">
                                                {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(item.price)}
                                            </TableCell>
                                        </TableRow>
                                    ))}
                                    {order.items.length > 3 && (
                                        <TableRow>
                                            <TableCell colSpan={4} className="text-center text-muted-foreground">
                                                + {order.items.length - 3} more items
                                            </TableCell>
                                        </TableRow>
                                    )}
                                </TableBody>
                            </Table>
                            
                            <div className="p-4 bg-secondary/5 border-t">
                                <Button variant="outline" className="w-full" asChild>
                                    <Link href={`/account/orders/${order.id}`}>
                                        View Order Details
                                        <ChevronRight className="h-4 w-4 ml-2" />
                                    </Link>
                                </Button>
                            </div>
                        </CardContent>
                    </Card>
                ))}
            </div>
        </div>
    );
}
