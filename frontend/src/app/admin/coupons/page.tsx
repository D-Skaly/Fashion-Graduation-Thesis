"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
    Table, TableBody, TableCell,
    TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
    DropdownMenu, DropdownMenuContent, DropdownMenuItem,
    DropdownMenuSeparator, DropdownMenuTrigger, DropdownMenuLabel
} from "@/components/ui/dropdown-menu";
import {
    Plus, Search, MoreHorizontal, RefreshCw,
    Trash2, Copy
} from "lucide-react";
import { toast } from "sonner";
import api from "@/lib/axios";
import { CouponForm, CouponFormData } from "@/components/admin/CouponForm";

interface Coupon {
    id: string;
    code: string;
    discountType: "PERCENTAGE" | "FIXED";
    discountValue: number;
    minPurchase: number;
    maxDiscount?: number;
    usageLimit?: number;
    usedCount: number;
    startDate?: string;
    endDate: string;
    isActive: boolean;
}

const fetchCoupons = async (): Promise<Coupon[]> => {
    const { data } = await api.get("/coupons");
     
    if (data && 'content' in (data as object)) return (data as { content: Coupon[] }).content;
     
    return Array.isArray(data) ? data as Coupon[] : [];
};

const isExpired = (endDate: string) => new Date(endDate) < new Date();

export default function AdminCouponsPage() {
    const [searchTerm, setSearchTerm] = useState("");
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [editCoupon, setEditCoupon] = useState<Coupon | null>(null);
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [form, setForm] = useState({
        code: "", discountType: "PERCENTAGE" as "PERCENTAGE" | "FIXED",
        discountValue: 10, minPurchase: 0, usageLimit: 100, endDate: "",
    });
    const queryClient = useQueryClient();

    const { data: coupons, isLoading, refetch } = useQuery({
        queryKey: ["admin-coupons"],
        queryFn: fetchCoupons,
    });

    const createMutation = useMutation({
        mutationFn: (data: CouponFormData) => api.post("/coupons", data),
        onSuccess: () => {
            toast.success("Coupon created successfully");
            setIsDialogOpen(false);
            queryClient.invalidateQueries({ queryKey: ["admin-coupons"] });
        },
        onError: () => toast.error("Failed to create coupon"),
    });

    const deleteMutation = useMutation({
        mutationFn: (id: string) => api.delete(`/coupons/${id}`),
        onSuccess: () => {
            toast.success("Coupon deleted successfully");
            queryClient.invalidateQueries({ queryKey: ["admin-coupons"] });
        },
        onError: () => toast.error("Failed to delete coupon"),
    });

    const toggleStatusMutation = useMutation({
        mutationFn: (id: string) => api.patch(`/coupons/${id}/toggle-status`),
        onSuccess: () => {
            toast.success("Coupon status updated");
            queryClient.invalidateQueries({ queryKey: ["admin-coupons"] });
        },
        onError: () => toast.error("Failed to update status"),
    });

    const filteredCoupons = coupons?.filter(c => 
        c.code.toLowerCase().includes(searchTerm.toLowerCase())
    ) || [];

    const copyCode = (code: string) => {
        navigator.clipboard.writeText(code);
        toast.success("Code copied to clipboard");
    };

    return (
        <div className="space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div>
                    <h1 className="text-3xl font-bold tracking-tight">Coupons</h1>
                    <p className="text-muted-foreground">Manage discount codes and promotions.</p>
                </div>
                <Button onClick={() => setIsDialogOpen(true)} className="rounded-full">
                    <Plus className="mr-2 h-4 w-4" /> Create Coupon
                </Button>
            </div>

            <div className="flex items-center gap-2 max-w-sm">
                <Search className="h-4 w-4 text-muted-foreground" />
                <Input
                    placeholder="Search by code..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="h-10"
                />
                <Button variant="outline" size="icon" onClick={() => refetch()} disabled={isLoading}>
                    <RefreshCw className={isLoading ? "h-4 w-4 animate-spin" : "h-4 w-4"} />
                </Button>
            </div>

            <div className="rounded-xl border bg-card">
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Code</TableHead>
                            <TableHead>Discount</TableHead>
                            <TableHead>Usage</TableHead>
                            <TableHead>Expiry</TableHead>
                            <TableHead>Status</TableHead>
                            <TableHead className="text-right">Actions</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            Array(5).fill(0).map((_, i) => (
                                <TableRow key={i}>
                                    {Array(6).fill(0).map((_, j) => (
                                        <TableCell key={j}><Skeleton className="h-6 w-full" /></TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : filteredCoupons.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={6} className="h-32 text-center text-muted-foreground">
                                    No coupons found.
                                </TableCell>
                            </TableRow>
                        ) : (
                            filteredCoupons.map((coupon) => (
                                <TableRow key={coupon.id}>
                                    <TableCell>
                                        <div className="flex items-center gap-2">
                                            <code className="bg-muted px-2 py-1 rounded text-sm font-bold tracking-wider">
                                                {coupon.code}
                                            </code>
                                            <Button variant="ghost" size="icon" className="h-6 w-6" onClick={() => copyCode(coupon.code)}>
                                                <Copy className="h-3 w-3" />
                                            </Button>
                                        </div>
                                    </TableCell>
                                    <TableCell>
                                        <div className="flex items-center gap-1">
                                            {coupon.discountType === "PERCENTAGE" ? (
                                                <Badge variant="secondary" className="bg-blue-100 text-blue-700 hover:bg-blue-100">
                                                    {coupon.discountValue}% Off
                                                </Badge>
                                            ) : (
                                                <Badge variant="secondary" className="bg-green-100 text-green-700 hover:bg-green-100">
                                                    ${coupon.discountValue} Off
                                                </Badge>
                                            )}
                                        </div>
                                    </TableCell>
                                    <TableCell>
                                        <div className="flex flex-col gap-1">
                                            <span className="text-sm font-medium">{coupon.usedCount} used</span>
                                            {coupon.usageLimit && (
                                                <span className="text-xs text-muted-foreground">Limit: {coupon.usageLimit}</span>
                                            )}
                                        </div>
                                    </TableCell>
                                    <TableCell>
                                        <div className="flex flex-col">
                                            <span className="text-sm">{new Date(coupon.endDate).toLocaleDateString()}</span>
                                            {isExpired(coupon.endDate) && (
                                                <span className="text-xs text-destructive font-medium">Expired</span>
                                            )}
                                        </div>
                                    </TableCell>
                                    <TableCell>
                                        <Badge variant={coupon.isActive && !isExpired(coupon.endDate) ? "success" : "secondary"}>
                                            {coupon.isActive && !isExpired(coupon.endDate) ? "Active" : "Inactive"}
                                        </Badge>
                                    </TableCell>
                                    <TableCell className="text-right">
                                        <DropdownMenu>
                                            <DropdownMenuTrigger asChild>
                                                <Button variant="ghost" size="icon">
                                                    <MoreHorizontal className="h-4 w-4" />
                                                </Button>
                                            </DropdownMenuTrigger>
                                            <DropdownMenuContent align="end" className="w-48">
                                                { }
                                                <DropdownMenuLabel>Options</DropdownMenuLabel>
                                                <DropdownMenuItem onClick={() => toggleStatusMutation.mutate(coupon.id)}>
                                                    <RefreshCw className="mr-2 h-4 w-4" />
                                                    {coupon.isActive ? "Deactivate" : "Activate"}
                                                </DropdownMenuItem>
                                                <DropdownMenuSeparator />
                                                <DropdownMenuItem 
                                                    className="text-destructive focus:bg-destructive focus:text-destructive-foreground"
                                                    onClick={() => {
                                                        if(confirm("Are you sure?")) deleteMutation.mutate(coupon.id);
                                                    }}
                                                >
                                                    <Trash2 className="mr-2 h-4 w-4" />
                                                    Delete
                                                </DropdownMenuItem>
                                            </DropdownMenuContent>
                                        </DropdownMenu>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </div>

            <CouponForm 
                open={isDialogOpen} 
                onOpenChange={setIsDialogOpen}
                onSubmit={(data) => createMutation.mutate(data)}
                isLoading={createMutation.isPending}
            />
        </div>
    );
}
