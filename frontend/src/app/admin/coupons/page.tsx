"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { 
    Table, 
    TableBody, 
    TableCell, 
    TableHead, 
    TableHeader, 
    TableRow 
} from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { Plus, Search, MoreHorizontal } from "lucide-react";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import api from "@/lib/axios";

interface Coupon {
    id: string;
    code: string;
    discountType: string;
    discountValue: number;
    minPurchase: number;
    maxDiscount: number;
    usageLimit: number;
    usedCount: number;
    startDate: string;
    endDate: string;
    isActive: boolean;
}

const fetchCoupons = async (): Promise<Coupon[]> => {
    const { data } = await api.get("/coupons");
    return data;
};

export default function AdminCouponsPage() {
    const [searchTerm, setSearchTerm] = useState("");
    const { data: coupons, isLoading } = useQuery({
        queryKey: ["admin-coupons"],
        queryFn: fetchCoupons,
    });

    const filteredCoupons = coupons?.filter(coupon =>
        coupon.code.toLowerCase().includes(searchTerm.toLowerCase())
    ) || [];

    return (
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h1 className="text-2xl font-bold tracking-tight">Coupons</h1>
                <Button>
                    <Plus className="h-4 w-4 mr-2" />
                    Create Coupon
                </Button>
            </div>

            <div className="flex items-center gap-2">
                <div className="relative flex-1 max-w-sm">
                    <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
                    <Input
                        placeholder="Search coupons..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="pl-8"
                    />
                </div>
            </div>

            <div className="rounded-md border bg-card">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Code</TableHead>
                            <TableHead>Discount</TableHead>
                            <TableHead>Usage</TableHead>
                            <TableHead>Valid Until</TableHead>
                            <TableHead>Status</TableHead>
                            <TableHead className="text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={6} className="text-center">Loading...</TableCell>
                            </TableRow>
                        ) : filteredCoupons.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={6} className="text-center">No coupons found</TableCell>
                            </TableRow>
                        ) : (
                            filteredCoupons.map((coupon) => (
                                <TableRow key={coupon.id}>
                                    <TableCell className="font-medium font-mono">{coupon.code}</TableCell>
                                    <TableCell>
                                        {coupon.discountType === "PERCENTAGE" 
                                            ? `${coupon.discountValue}%` 
                                            : `$${coupon.discountValue}`}
                                    </TableCell>
                                    <TableCell>
                                        {coupon.usedCount}/{coupon.usageLimit || "∞"}
                                    </TableCell>
                                    <TableCell className="text-sm">
                                        {new Date(coupon.endDate).toLocaleDateString()}
                                    </TableCell>
                                    <TableCell>
                                        {coupon.isActive ? (
                                            <Badge variant="default">Active</Badge>
                                        ) : (
                                            <Badge variant="secondary">Inactive</Badge>
                                        )}
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant="ghost" size="icon">
                                                    <MoreHorizontal className="h-4 w-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent align="end">
                                                <DropdownMenuItem>Edit</DropdownMenuItem>
                                                <DropdownMenuItem>Duplicate</DropdownMenuItem>
                                                <DropdownMenuItem className="text-red-600">Delete</DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
}
