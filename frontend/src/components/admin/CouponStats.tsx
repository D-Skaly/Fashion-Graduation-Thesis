"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Ticket, Percent, DollarSign, Users } from "lucide-react";

interface CouponStatsProps {
    totalCoupons: number;
    activeCoupons: number;
    totalSavings: number;
    totalRedemptions: number;
}

export function CouponStats({ 
    totalCoupons, 
    activeCoupons, 
    totalSavings, 
    totalRedemptions 
}: CouponStatsProps) {
    return (
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
            <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Total Coupons</CardTitle>
                    <Ticket className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                    <div className="text-2xl font-bold">{totalCoupons}</div>
                    <p className="text-xs text-muted-foreground">
                        {activeCoupons} currently active
                    </p>
                </CardContent>
            </Card>

            <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Active Coupons</CardTitle>
                    <Percent className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                    <div className="text-2xl font-bold">{activeCoupons}</div>
                    <p className="text-xs text-muted-foreground">
                        {totalCoupons > 0 ? `${Math.round((activeCoupons / totalCoupons) * 100)}% of total` : "0%"}
                    </p>
                </CardContent>
            </Card>

            <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Total Savings</CardTitle>
                    <DollarSign className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                    <div className="text-2xl font-bold">
                        ${totalSavings.toLocaleString()}
                    </div>
                    <p className="text-xs text-muted-foreground">
                        Customer savings
                    </p>
                </CardContent>
            </Card>

            <Card>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                    <CardTitle className="text-sm font-medium">Total Redemptions</CardTitle>
                    <Users className="h-4 w-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                    <div className="text-2xl font-bold">{totalRedemptions}</div>
                    <p className="text-xs text-muted-foreground">
                        Times used
                    </p>
                </CardContent>
            </Card>
        </div>
    );
}
