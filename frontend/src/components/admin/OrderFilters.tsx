"use client";

import { Button } from "@/components/ui/button";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

interface OrderFiltersProps {
    statusFilter: string;
    dateFilter: string;
    onStatusChange: (value: string) => void;
    onDateChange: (value: string) => void;
    onReset: () => void;
}

export function OrderFilters({ statusFilter, dateFilter, onStatusChange, onDateChange, onReset }: OrderFiltersProps) {
    return (
        <div className="flex items-center gap-2 flex-wrap">
            <Select value={statusFilter} onValueChange={onStatusChange}>
                <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder="All Status" />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem value="all">All Status</SelectItem>
                    <SelectItem value="PENDING">Pending</SelectItem>
                    <SelectItem value="CONFIRMED">Confirmed</SelectItem>
                    <SelectItem value="PROCESSING">Processing</SelectItem>
                    <SelectItem value="SHIPPED">Shipped</SelectItem>
                    <SelectItem value="DELIVERED">Delivered</SelectItem>
                    <SelectItem value="CANCELLED">Cancelled</SelectItem>
                </SelectContent>
            </Select>

            <Select value={dateFilter} onValueChange={onDateChange}>
                <SelectTrigger className="w-[180px]">
                    <SelectValue placeholder="All Time" />
                </SelectTrigger>
                <SelectContent>
                    <SelectItem value="all">All Time</SelectItem>
                    <SelectItem value="today">Today</SelectItem>
                    <SelectItem value="week">This Week</SelectItem>
                    <SelectItem value="month">This Month</SelectItem>
                    <SelectItem value="year">This Year</SelectItem>
                </SelectContent>
            </Select>

            <Button variant="outline" onClick={onReset}>
                Reset
            </Button>
        </div>
    );
}
