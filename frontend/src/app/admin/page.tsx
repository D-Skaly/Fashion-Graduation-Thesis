"use client";

import { useQuery } from "@tanstack/react-query";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import {
    AreaChart, Area, XAxis, YAxis, CartesianGrid,
    Tooltip, ResponsiveContainer
} from 'recharts';
import {
    Package, ShoppingCart, Users, RefreshCw,
    Activity, DollarSign, Sparkles
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import api from "@/lib/axios";
import { PopularProducts } from "@/components/admin/PopularProducts";
import { RecentOrdersTable } from "@/components/admin/RecentOrdersTable";

interface DashboardStats {
    totalRevenue: number;
    totalOrders: number;
    totalCustomers: number;
    totalProducts: number;
}

const fetchDashboardStats = async (): Promise<DashboardStats> => {
    const { data } = await api.get("/admin/dashboard/stats");
    return data;
};

const fetchPopularProducts = async () => {
    const { data } = await api.get("/admin/dashboard/popular-products");
    return data;
};

const fetchRecentOrders = async () => {
    const { data } = await api.get("/admin/dashboard/recent-orders");
    return data;
};

const fallbackRevenue = [
    { name: 'Jan', revenue: 4200 },
    { name: 'Feb', revenue: 5800 },
    { name: 'Mar', revenue: 3900 },
    { name: 'Apr', revenue: 7200 },
    { name: 'May', revenue: 6100 },
    { name: 'Jun', revenue: 8400 },
];

export default function AdminDashboard() {
    const { data: stats, isLoading: statsLoading, refetch } = useQuery({
        queryKey: ["dashboard-stats"],
        queryFn: fetchDashboardStats,
    });

    const { data: popular } = useQuery({
        queryKey: ["popular-products"],
        queryFn: fetchPopularProducts,
    });

    const { data: recent } = useQuery({
        queryKey: ["recent-orders"],
        queryFn: fetchRecentOrders,
    });

    const statCards = [
        {
            title: "Gross Revenue",
            value: stats ? `$${stats.totalRevenue.toLocaleString()}` : "$0",
            change: "+12.5%",
            icon: DollarSign,
            color: "text-emerald-400",
            bg: "bg-emerald-500/10",
        },
        {
            title: "Total Orders",
            value: stats?.totalOrders ?? "0",
            change: "+8.2%",
            icon: ShoppingCart,
            color: "text-blue-400",
            bg: "bg-blue-500/10",
        },
        {
            title: "Active Users",
            value: stats?.totalCustomers ?? "0",
            change: "+4.1%",
            icon: Users,
            color: "text-violet-400",
            bg: "bg-violet-500/10",
        },
        {
            title: "Live Inventory",
            value: stats?.totalProducts ?? "0",
            change: "Stable",
            icon: Package,
            color: "text-amber-400",
            bg: "bg-amber-500/10",
        },
    ];

    return (
        <div className="space-y-12 max-w-[1600px] mx-auto">
            {/* Header Area */}
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-8 pb-4 border-b border-zinc-800/50">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <div className="h-2 w-2 rounded-full bg-primary animate-pulse" />
                        <span className="text-[10px] font-black uppercase tracking-[0.3em] text-zinc-500">Live Analytics Engine</span>
                    </div>
                    <h1 className="text-4xl font-black text-white tracking-tighter uppercase leading-none">Command Center</h1>
                    <p className="text-zinc-500 font-bold mt-2 uppercase tracking-widest text-[10px] opacity-70">Real-time enterprise statistics and neural diagnostics</p>
                </div>
                <Button
                    variant="outline"
                    className="bg-zinc-900 border-zinc-800 hover:bg-zinc-800 rounded-2xl uppercase tracking-[0.2em] text-[10px] font-black gap-3 h-14 px-8 text-zinc-300 transition-all active:scale-95"
                    onClick={() => refetch()}
                >
                    <RefreshCw className="h-3.5 w-3.5" /> Re-Sync Metrics
                </Button>
            </div>

            {/* Quick Stats Grid */}
            <div className="grid gap-8 md:grid-cols-2 lg:grid-cols-4">
                {statsLoading
                    ? Array.from({ length: 4 }).map((_, i) => (
                        <Card key={i} className="bg-zinc-900/40 border-zinc-800 h-40 rounded-[2rem]">
                            <CardContent className="p-8 h-full flex flex-col justify-between">
                                <Skeleton className="h-4 w-24 bg-zinc-800 rounded-full" />
                                <Skeleton className="h-10 w-20 bg-zinc-800 rounded-xl" />
                            </CardContent>
                        </Card>
                    ))
                    : statCards.map((card) => {
                        const Icon = card.icon;
                        return (
                            <Card key={card.title} className="bg-zinc-900/40 border-zinc-800 hover:border-zinc-700 transition-all cursor-default shadow-sm group rounded-[2rem] overflow-hidden relative">
                                <CardContent className="p-8">
                                    <div className="flex items-center justify-between mb-6">
                                        <div className={`p-4 rounded-2xl ${card.bg}`}>
                                            <Icon className={`h-6 w-6 ${card.color}`} />
                                        </div>
                                        <Badge className="text-[10px] font-black text-zinc-400 uppercase tracking-widest bg-zinc-800/50 px-3 py-1.5 rounded-full border-none">
                                            {card.change}
                                        </Badge>
                                    </div>
                                    <div className="text-4xl font-black text-white group-hover:text-primary transition-colors tracking-tighter">{card.value}</div>
                                    <p className="text-[10px] text-zinc-500 font-black uppercase tracking-[0.2em] mt-2 opacity-60">{card.title}</p>
                                </CardContent>
                                {/* Subtle internal glow */}
                                <div className="absolute -bottom-10 -right-10 w-32 h-32 bg-primary/5 rounded-full blur-[40px] opacity-0 group-hover:opacity-100 transition-opacity" />
                            </Card>
                        );
                    })}
            </div>

            {/* Visual Analytics */}
            <div className="grid gap-8 lg:grid-cols-3">
                {/* Revenue Graph */}
                <Card className="lg:col-span-2 bg-zinc-900/40 border-zinc-800 shadow-sm overflow-hidden rounded-[2.5rem]">
                    <CardHeader className="p-8 pb-4">
                        <CardTitle className="text-xs font-black uppercase tracking-[0.3em] text-zinc-400 flex items-center gap-3">
                            <Activity className="h-4 w-4 text-primary" />
                            Revenue Stream Analysis
                        </CardTitle>
                    </CardHeader>
                    <CardContent className="p-8 pt-0">
                        <div className="h-[380px] w-full mt-6">
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={fallbackRevenue} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                                    <defs>
                                        <linearGradient id="colorRev" x1="0" y1="0" x2="0" y2="1">
                                            <stop offset="5%" stopColor="var(--primary)" stopOpacity={0.15} />
                                            <stop offset="95%" stopColor="var(--primary)" stopOpacity={0} />
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#27272a" opacity={0.5} />
                                    <XAxis dataKey="name" stroke="#52525b" fontSize={10} tickLine={false} axisLine={false} dy={10} />
                                    <YAxis stroke="#52525b" fontSize={10} tickLine={false} axisLine={false} tickFormatter={(v) => `$${v}`} />
                                    <Tooltip 
                                        contentStyle={{ backgroundColor: '#09090b', borderColor: '#27272a', borderRadius: '16px', border: '1px solid #3f3f46', padding: '12px' }}
                                        itemStyle={{ color: '#fff', fontSize: '12px', fontWeight: '900', textTransform: 'uppercase' }}
                                        cursor={{ stroke: '#3f3f46', strokeWidth: 1 }}
                                    />
                                    <Area type="monotone" dataKey="revenue" stroke="var(--primary)" fillOpacity={1} fill="url(#colorRev)" strokeWidth={4} animationDuration={2000} />
                                </AreaChart>
                            </ResponsiveContainer>
                        </div>
                    </CardContent>
                </Card>

                {/* Popular Products Sidebar */}
                <div className="lg:col-span-1 h-full">
                    <PopularProducts products={popular || []} />
                </div>

                {/* Full Recent Orders */}
                <div className="lg:col-span-3">
                    <RecentOrdersTable orders={recent || []} />
                </div>
            </div>

            {/* AI Insight Footer */}
            <div className="p-10 rounded-[2.5rem] bg-zinc-900 border border-zinc-800 flex flex-col md:flex-row items-center gap-10 justify-between shadow-2xl relative overflow-hidden group">
                {/* Background glow */}
                <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-r from-primary/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-1000" />
                
                <div className="flex items-center gap-6 text-center md:text-left relative z-10">
                    <div className="bg-primary/10 p-5 rounded-3xl border border-primary/20">
                        <Sparkles className="h-8 w-8 text-primary animate-pulse" />
                    </div>
                    <div>
                        <h4 className="text-lg font-black text-white uppercase tracking-widest">Neural Optimization Active</h4>
                        <p className="text-xs text-zinc-500 font-bold uppercase tracking-widest mt-2 max-w-xl opacity-80 leading-relaxed">
                            Our AI models are currently analyzing global sales patterns. <span className="text-zinc-200 underline decoration-primary/50 underline-offset-4">Predictive analysis suggests a 15% increase</span> in high-tier fashion engagement for the upcoming weekend cycle.
                        </p>
                    </div>
                </div>
                <Button className="bg-primary hover:bg-white hover:text-black text-black font-black uppercase tracking-[0.2em] text-[11px] h-16 px-12 rounded-2xl shadow-2xl shadow-primary/20 shrink-0 transition-all active:scale-95 relative z-10">
                    Auto-Optimize Stock
                </Button>
            </div>
        </div>
    );
}
