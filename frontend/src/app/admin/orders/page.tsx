"use client";

import { useQuery } from "@tanstack/react-query";
import {
    Table, TableBody, TableCell,
    TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { Eye, Search, RotateCw, Filter, FileDown } from "lucide-react";
import Link from "next/link";
import api from "@/lib/axios";
import { Input } from "@/components/ui/input";

interface Order {
    id: string;
    orderNumber: string;
    totalAmount: number;
    status: string;
    createdAt: string;
}

interface OrderPage {
    content: Order[];
    totalElements: number;
}

const STATUS_CONFIG: Record<string, { label: string; class: string }> = {
    PENDING:    { label: "Pending",    class: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20" },
    CONFIRMED:  { label: "Confirmed",  class: "bg-blue-500/10 text-blue-400 border-blue-500/20" },
    PROCESSING: { label: "Processing", class: "bg-violet-500/10 text-violet-400 border-violet-500/20" },
    SHIPPED:    { label: "Shipped",    class: "bg-indigo-500/10 text-indigo-400 border-indigo-500/20" },
    DELIVERED:  { label: "Delivered",  class: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" },
    CANCELLED:  { label: "Cancelled",  class: "bg-red-500/10 text-red-400 border-red-500/20" },
};

const fetchOrders = async (): Promise<Order[]> => {
    const { data } = await api.get<OrderPage>("/orders?size=100");
    if (data && 'content' in data) return data.content;
    return Array.isArray(data) ? data : [];
};

export default function OrdersPage() {
    const { data: orders, isLoading, isError, refetch } = useQuery({
        queryKey: ["admin-orders"],
        queryFn: fetchOrders,
    });

    if (isError) {
        return (
            <div className="flex flex-col items-center justify-center h-96 gap-6">
                <div className="bg-red-500/10 p-4 rounded-full">
                    <RotateCw className="h-8 w-8 text-red-400" />
                </div>
                <div className="text-center">
                    <p className="text-zinc-100 font-black uppercase tracking-widest">Registry Sync Failed</p>
                    <p className="text-zinc-500 text-sm font-medium mt-1">Access to transaction database was denied or interrupted.</p>
                </div>
                <Button variant="outline" onClick={() => refetch()} className="gap-2 border-zinc-800 hover:bg-zinc-900 rounded-xl px-8 h-12 text-xs font-bold uppercase tracking-widest">
                    Attempt Recovery
                </Button>
            </div>
        );
    }

    return (
        <div className="w-full space-y-8">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <h1 className="text-2xl font-black text-white tracking-tight uppercase">Transaction Registry</h1>
                    <p className="text-zinc-500 text-xs font-bold uppercase tracking-[0.2em] mt-1">
                        {orders ? `${orders.length} TOTAL OPERATIONS` : "INITIALIZING STREAM..."}
                    </p>
                </div>
                <Button className="bg-zinc-900 border border-zinc-800 hover:bg-zinc-800 text-zinc-300 rounded-xl uppercase tracking-widest text-[10px] font-black h-11 px-6 gap-2">
                    <FileDown className="h-3.5 w-3.5" /> Export Data
                </Button>
            </div>

            <div className="flex items-center gap-4">
                <div className="relative flex-1 max-w-md">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-zinc-500" />
                    <Input
                        placeholder="Filter by Registry Number..."
                        className="pl-12 h-12 bg-zinc-900 border-zinc-800 text-white placeholder:text-zinc-600 rounded-xl focus:border-primary/50"
                    />
                </div>
                <Button variant="outline" className="h-12 px-6 rounded-xl bg-zinc-900 border-zinc-800 text-zinc-500 text-[10px] font-black uppercase tracking-widest gap-2">
                    <Filter className="h-4 w-4" /> Advance Filters
                </Button>
            </div>

            <div className="rounded-2xl border border-zinc-800 bg-zinc-900/50 overflow-hidden shadow-xl">
                <Table>
                    <TableHeader className="bg-zinc-900/50">
                        <TableRow className="border-zinc-800 hover:bg-transparent">
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 pl-6">Registry ID</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Temporal Stamp</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Operational Status</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 text-right pr-6">Yield Valuation</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 text-right pr-6">Action</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            Array.from({ length: 8 }).map((_, i) => (
                                <TableRow key={i} className="border-zinc-800">
                                    <TableCell className="py-5 pl-6"><Skeleton className="h-4 w-32 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-4 w-24 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-6 w-24 rounded-lg bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5 pr-6"><Skeleton className="h-4 w-16 ml-auto bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5 pr-6"><Skeleton className="h-9 w-9 ml-auto rounded-lg bg-zinc-800" /></TableCell>
                                </TableRow>
                            ))
                        ) : orders?.map((order) => {
                            const statusCfg = STATUS_CONFIG[order.status] || { label: order.status, class: "bg-zinc-800 text-zinc-400 border-zinc-700" };
                            return (
                                <TableRow key={order.id} className="border-zinc-800 hover:bg-zinc-800/30 transition-colors group">
                                    <TableCell className="py-5 pl-6 font-mono font-bold text-zinc-100 text-sm">
                                        {order.orderNumber || `#${order.id.substring(0, 8)}`}
                                    </TableCell>
                                    <TableCell className="py-5 text-zinc-500 text-xs font-bold uppercase">
                                        {new Date(order.createdAt).toLocaleDateString()}
                                    </TableCell>
                                    <TableCell className="py-5">
                                        <Badge className={`border text-[9px] uppercase tracking-widest font-black px-2.5 py-0.5 rounded-md ${statusCfg.class}`}>
                                            {statusCfg.label}
                                        </Badge>
                                    </TableCell>
                                    <TableCell className="py-5 text-right pr-6 font-black text-zinc-100 text-sm">
                                        ${order.totalAmount.toLocaleString()}
                                    </TableCell>
                                    <TableCell className="py-5 text-right pr-6">
                                        <Button variant="ghost" size="icon" asChild className="h-9 w-9 hover:bg-zinc-800 rounded-xl border border-transparent hover:border-zinc-700 transition-all">
                                            <Link href={`/admin/orders/${order.id}`}>
                                                <Eye className="h-4 w-4 text-zinc-400 group-hover:text-primary" />
                                            </Link>
                                        </Button>
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
