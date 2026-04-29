"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
    AreaChart,
    Area,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    BarChart,
    Bar
} from 'recharts';
import { TrendingUp, Package, Sparkles, AlertTriangle } from "lucide-react";

const trendData = [
    { name: 'Week 1', search: 4000, conversion: 2400 },
    { name: 'Week 2', search: 3000, conversion: 1398 },
    { name: 'Week 3', search: 2000, conversion: 9800 },
    { name: 'Week 4', search: 2780, conversion: 3908 },
    { name: 'Week 5', search: 1890, conversion: 4800 },
    { name: 'Week 6', search: 2390, conversion: 3800 },
    { name: 'Week 7', search: 3490, conversion: 4300 },
];

const inventoryData = [
    { name: 'Tops', current: 400, predicted: 240 },
    { name: 'Bottoms', current: 300, predicted: 139 },
    { name: 'Dresses', current: 200, predicted: 980 },
    { name: 'Accessories', current: 278, predicted: 390 },
];

export default function AdminDashboard() {
    return (
        <div className="flex flex-col gap-8 text-stone-100">
            <div>
                <h1 className="text-3xl font-black uppercase tracking-widest mb-2">AI Analytics Center</h1>
                <p className="text-stone-400 font-light tracking-wide">Real-time insights powered by our proprietary fashion genome engine.</p>
            </div>

            {/* AI Highlight Cards */}
            <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
                <Card className="bg-white/5 border-white/10 backdrop-blur-xl text-white shadow-xl">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-xs font-bold uppercase tracking-widest text-stone-400">Trend Forecast</CardTitle>
                        <TrendingUp className="h-4 w-4 text-primary" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-black mb-1">Techwear</div>
                        <p className="text-xs text-primary font-bold tracking-wide">+45% search volume</p>
                    </CardContent>
                </Card>

                <Card className="bg-white/5 border-white/10 backdrop-blur-xl text-white shadow-xl">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-xs font-bold uppercase tracking-widest text-stone-400">Inventory Alert</CardTitle>
                        <AlertTriangle className="h-4 w-4 text-destructive" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-black mb-1">Stockout Risk</div>
                        <p className="text-xs text-destructive font-bold tracking-wide">Size M - Oxford Shirt in 3 days</p>
                    </CardContent>
                </Card>

                <Card className="bg-white/5 border-white/10 backdrop-blur-xl text-white shadow-xl">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-xs font-bold uppercase tracking-widest text-stone-400">AI Match Rate</CardTitle>
                        <Sparkles className="h-4 w-4 text-primary" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-black mb-1">94.2%</div>
                        <p className="text-xs text-stone-400 font-medium tracking-wide">Accuracy on style predictions</p>
                    </CardContent>
                </Card>

                <Card className="bg-white/5 border-white/10 backdrop-blur-xl text-white shadow-xl">
                    <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                        <CardTitle className="text-xs font-bold uppercase tracking-widest text-stone-400">Auto-Restock</CardTitle>
                        <Package className="h-4 w-4 text-stone-400" />
                    </CardHeader>
                    <CardContent>
                        <div className="text-2xl font-black mb-1">12 Items</div>
                        <p className="text-xs text-stone-400 font-medium tracking-wide">Queued for supplier order</p>
                    </CardContent>
                </Card>
            </div>

            {/* Charts Section */}
            <div className="grid gap-6 md:grid-cols-2">
                <Card className="bg-white/5 border-white/10 backdrop-blur-xl text-white shadow-xl">
                    <CardHeader>
                        <CardTitle className="text-sm font-bold uppercase tracking-widest">Trend Demand vs Conversion</CardTitle>
                    </CardHeader>
                    <CardContent className="pl-0">
                        <div className="h-[300px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <AreaChart data={trendData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                                    <defs>
                                        <linearGradient id="colorSearch" x1="0" y1="0" x2="0" y2="1">
                                        <stop offset="5%" stopColor="var(--primary)" stopOpacity={0.3}/>
                                        <stop offset="95%" stopColor="var(--primary)" stopOpacity={0}/>
                                        </linearGradient>
                                    </defs>
                                    <CartesianGrid strokeDasharray="3 3" className="stroke-white/10" vertical={false} />
                                    <XAxis dataKey="name" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                                    <YAxis stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                                    <Tooltip contentStyle={{ backgroundColor: '#1c1917', borderColor: '#292524', color: '#fff' }} />
                                    <Area type="monotone" dataKey="search" stroke="var(--primary)" fillOpacity={1} fill="url(#colorSearch)" />
                                    <Area type="monotone" dataKey="conversion" stroke="#a8a29e" fillOpacity={0} />
                                </AreaChart>
                            </ResponsiveContainer>
                        </div>
                    </CardContent>
                </Card>

                <Card className="bg-white/5 border-white/10 backdrop-blur-xl text-white shadow-xl">
                    <CardHeader>
                        <CardTitle className="text-sm font-bold uppercase tracking-widest">AI Inventory Health Prediction</CardTitle>
                    </CardHeader>
                    <CardContent className="pl-0">
                        <div className="h-[300px] w-full">
                            <ResponsiveContainer width="100%" height="100%">
                                <BarChart data={inventoryData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                                    <CartesianGrid strokeDasharray="3 3" className="stroke-white/10" vertical={false} />
                                    <XAxis dataKey="name" stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                                    <YAxis stroke="#888888" fontSize={12} tickLine={false} axisLine={false} />
                                    <Tooltip contentStyle={{ backgroundColor: '#1c1917', borderColor: '#292524', color: '#fff' }} cursor={{fill: 'rgba(255,255,255,0.05)'}} />
                                    <Bar dataKey="current" fill="#a8a29e" radius={[4, 4, 0, 0]} />
                                    <Bar dataKey="predicted" fill="var(--primary)" radius={[4, 4, 0, 0]} />
                                </BarChart>
                            </ResponsiveContainer>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </div>
    );
}
