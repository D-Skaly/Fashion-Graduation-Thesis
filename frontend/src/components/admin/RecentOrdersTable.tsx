"use client";

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
import { Button } from "@/components/ui/button";
import { Eye, Clock, ArrowRight } from "lucide-react";
import Link from "next/link";

interface Order {
    id: string;
    orderNumber: string;
    customerName: string;
    totalAmount: number;
    status: string;
    createdAt: string;
}

interface RecentOrdersTableProps {
    orders: Order[];
}

const STATUS_CONFIG: Record<string, { label: string; class: string }> = {
    PENDING:    { label: "Pending",    class: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20" },
    CONFIRMED:  { label: "Confirmed",  class: "bg-blue-500/10 text-blue-400 border-blue-500/20" },
    PROCESSING: { label: "Processing", class: "bg-violet-500/10 text-violet-400 border-violet-500/20" },
    SHIPPED:    { label: "Shipped",    class: "bg-indigo-500/10 text-indigo-400 border-indigo-500/20" },
    DELIVERED:  { label: "Delivered",  class: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" },
    CANCELLED:  { label: "Cancelled",  class: "bg-red-500/10 text-red-400 border-red-500/20" },
};

const fallbackRecent = [
    { id: '101', orderNumber: 'ORD-8291', customerName: 'Alex Rivers', totalAmount: 249, status: 'PROCESSING', createdAt: new Date().toISOString() },
    { id: '102', orderNumber: 'ORD-8292', customerName: 'Sam Vimes', totalAmount: 120, status: 'SHIPPED', createdAt: new Date().toISOString() },
    { id: '103', orderNumber: 'ORD-8293', customerName: 'Mina Harker', totalAmount: 540, status: 'PENDING', createdAt: new Date().toISOString() },
];

export function RecentOrdersTable({ orders }: RecentOrdersTableProps) {
    const displayOrders = orders.length > 0 ? orders : fallbackRecent;

    return (
        <Card className="bg-zinc-900/40 border-zinc-800 shadow-sm overflow-hidden rounded-[2.5rem]">
            <CardHeader className="flex flex-row items-center justify-between p-8 pb-4">
                <CardTitle className="text-[10px] font-black uppercase tracking-[0.3em] flex items-center gap-3 text-zinc-400">
                    <Clock className="h-4 w-4 text-primary" />
                    Operational Registry
                </CardTitle>
                <Button variant="ghost" size="sm" asChild className="text-[9px] uppercase font-black tracking-[0.2em] hover:bg-zinc-800 text-zinc-500 hover:text-white rounded-full h-9 px-5 transition-all">
                    <Link href="/admin/orders" className="flex items-center gap-2">View All Records <ArrowRight className="h-3 w-3" /></Link>
                </Button>
            </CardHeader>
            <CardContent className="p-0 px-2 pb-6">
                <Table>
                    <TableHeader>
                        <TableRow className="border-zinc-800/30 hover:bg-transparent border-none">
                            <TableHead className="text-zinc-600 text-[9px] uppercase tracking-[0.2em] font-black pl-8 py-4">Registry ID</TableHead>
                            <TableHead className="text-zinc-600 text-[9px] uppercase tracking-[0.2em] font-black py-4">Status</TableHead>
                            <TableHead className="text-zinc-600 text-[9px] uppercase tracking-[0.2em] font-black text-right pr-8 py-4">Valuation</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {displayOrders.map((order) => {
                            const statusCfg = STATUS_CONFIG[order.status] || { label: order.status, class: "bg-zinc-800 text-zinc-400 border-zinc-700" };
                            return (
                                <TableRow key={order.id} className="border-zinc-800/30 hover:bg-zinc-800/20 transition-all group border-none">
                                    <TableCell className="py-6 pl-8">
                                        <div className="flex flex-col gap-1">
                                            <span className="font-mono font-bold text-sm text-zinc-100 group-hover:text-primary transition-colors tracking-tighter">{order.orderNumber}</span>
                                            <span className="text-[9px] text-zinc-600 uppercase font-black tracking-[0.2em]">{order.customerName}</span>
                                        </div>
                                    </TableCell>
                                    <TableCell className="py-6">
                                        <Badge className={`border text-[8px] uppercase tracking-[0.2em] font-black px-3 py-1 rounded-full ${statusCfg.class}`}>
                                            {statusCfg.label}
                                        </Badge>
                                    </TableCell>
                                    <TableCell className="text-right py-6 pr-8">
                                        <div className="flex items-center justify-end gap-6">
                                            <span className="font-black text-sm text-white tracking-tighter">${order.totalAmount.toLocaleString()}</span>
                                            <Button variant="ghost" size="icon" asChild className="h-10 w-10 hover:bg-primary text-zinc-400 hover:text-black rounded-full border border-zinc-800 hover:border-primary transition-all">
                                                <Link href={`/admin/orders/${order.id}`}>
                                                    <Eye className="h-4 w-4" />
                                                </Link>
                                            </Button>
                                        </div>
                                    </TableCell>
                                </TableRow>
                            );
                        })}
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
    );
}
