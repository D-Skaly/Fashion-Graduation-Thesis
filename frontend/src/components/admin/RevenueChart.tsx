"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    ResponsiveContainer,
    Legend
} from 'recharts';

interface RevenueData {
    name: string;
    revenue: number;
    orders?: number;
}

interface RevenueChartProps {
    data: RevenueData[];
    title?: string;
    showOrders?: boolean;
}

export function RevenueChart({ data, title = "Revenue Overview", showOrders = false }: RevenueChartProps) {
    return (
        <Card>
            <CardHeader>
                <CardTitle>{title}</CardTitle>
            </CardHeader>
            <CardContent className="pl-2">
                <div className="h-[300px] w-full">
                    <ResponsiveContainer width="100%" height="100%">
                        <LineChart data={data}>
                            <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                            <XAxis
                                dataKey="name"
                                stroke="#888888"
                                fontSize={12}
                                tickLine={false}
                                axisLine={false}
                            />
                            <YAxis
                                stroke="#888888"
                                fontSize={12}
                                tickLine={false}
                                axisLine={false}
                                tickFormatter={(value) => `$${value}`}
                            />
                            <Tooltip
                                contentStyle={{ backgroundColor: 'var(--background)', borderColor: 'var(--border)' }}
                                itemStyle={{ color: 'var(--foreground)' }}
                                formatter={(value: number) => `$${value.toLocaleString()}`}
                            />
                            {showOrders && <Legend />}
                            <Line
                                type="monotone"
                                dataKey="revenue"
                                stroke="var(--primary)"
                                strokeWidth={2}
                                dot={false}
                                name="Revenue"
                            />
                            {showOrders && (
                                <Line
                                    type="monotone"
                                    dataKey="orders"
                                    stroke="var(--secondary)"
                                    strokeWidth={2}
                                    dot={false}
                                    name="Orders"
                                />
                            )}
                        </LineChart>
                    </ResponsiveContainer>
                </div>
            </CardContent>
        </Card>
    );
}
