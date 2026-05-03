"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

interface CouponFormProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSubmit: (data: CouponFormData) => void;
    isLoading?: boolean;
}

export interface CouponFormData {
    code: string;
    discountType: string;
    discountValue: number;
    minPurchase: number;
    maxDiscount: number;
    usageLimit: number;
    startDate: string;
    endDate: string;
}

export function CouponForm({ open, onOpenChange, onSubmit, isLoading = false }: CouponFormProps) {
    const [formData, setFormData] = useState<CouponFormData>(() => ({
        code: "",
        discountType: "PERCENTAGE",
        discountValue: 0,
        minPurchase: 0,
        maxDiscount: 0,
        usageLimit: 100,
         
        startDate: new Date().toISOString().split("T")[0],
         
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split("T")[0],
    }));

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle>Create Coupon</DialogTitle>
                    <DialogDescription>
                        Create a new discount coupon for your customers.
                    </DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmit} className="space-y-4 py-4">
                    <div className="space-y-2">
                        <Label htmlFor="code">Coupon Code</Label>
                        <Input
                            id="code"
                            value={formData.code}
                            onChange={(e) => setFormData({ ...formData, code: e.target.value.toUpperCase() })}
                            placeholder="SAVE10"
                            required
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="discountType">Discount Type</Label>
                        <Select
                            value={formData.discountType}
                            onValueChange={(value) => setFormData({ ...formData, discountType: value })}
                        >
                            <SelectTrigger id="discountType">
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="PERCENTAGE">Percentage</SelectItem>
                                <SelectItem value="FIXED">Fixed Amount</SelectItem>
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="discountValue">Discount Value</Label>
                        <Input
                            id="discountValue"
                            type="number"
                            value={formData.discountValue}
                            onChange={(e) => setFormData({ ...formData, discountValue: Number(e.target.value) })}
                            placeholder={formData.discountType === "PERCENTAGE" ? "10" : "10"}
                            required
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="minPurchase">Minimum Purchase</Label>
                        <Input
                            id="minPurchase"
                            type="number"
                            value={formData.minPurchase}
                            onChange={(e) => setFormData({ ...formData, minPurchase: Number(e.target.value) })}
                            placeholder="0"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="usageLimit">Usage Limit</Label>
                        <Input
                            id="usageLimit"
                            type="number"
                            value={formData.usageLimit}
                            onChange={(e) => setFormData({ ...formData, usageLimit: Number(e.target.value) })}
                            placeholder="100"
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-2">
                            <Label htmlFor="startDate">Start Date</Label>
                            <Input
                                id="startDate"
                                type="date"
                                value={formData.startDate}
                                onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                                required
                            />
                        </div>
                        <div className="space-y-2">
                            <Label htmlFor="endDate">End Date</Label>
                            <Input
                                id="endDate"
                                type="date"
                                value={formData.endDate}
                                onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                                required
                            />
                        </div>
                    </div>
                </form>
                <DialogFooter>
                    <Button variant="outline" onClick={() => onOpenChange(false)}>
                        Cancel
                    </Button>
                    <Button onClick={handleSubmit} disabled={isLoading}>
                        {isLoading ? "Creating..." : "Create Coupon"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
