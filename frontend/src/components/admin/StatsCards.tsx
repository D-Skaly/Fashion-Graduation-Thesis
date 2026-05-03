"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { TrendingUp, TrendingDown } from "lucide-react";

interface StatCard {
    title: string;
    value: string | number;
    change: string;
    trend: "up" | "down";
    icon: React.ElementType;
}

interface StatsCardsProps {
    stats: StatCard[];
}

export function StatsCards({ stats }: StatsCardsProps) {
    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            {stats.map((stat) => {
                const Icon = stat.icon;
                const TrendIcon = stat.trend === "up" ? TrendingUp : TrendingDown;
                const trendColor = stat.trend === "up" ? "text-green-600" : "text-red-600";

                return (
                    <Card key={stat.title}>
                        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                            <CardTitle className="text-sm font-medium">{stat.title}</CardTitle>
                            <Icon className="h-4 w-4 text-muted-foreground" />
                        </CardHeader>
                        <CardContent>
                            <div className="text-2xl font-bold">{stat.value}</div>
                            <p className={`text-xs ${trendColor} flex items-center gap-1`}>
                                <TrendIcon className="h-3 w-3" />
                                {stat.change}
                            </p>
                        </CardContent>
                    </Card>
                );
            })}
        </div>
    );
}
