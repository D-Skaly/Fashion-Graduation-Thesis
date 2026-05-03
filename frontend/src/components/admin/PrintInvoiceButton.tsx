"use client";

import { Button } from "@/components/ui/button";
import { Printer } from "lucide-react";

interface PrintInvoiceButtonProps {
    orderId: string;
    onPrint?: (orderId: string) => void;
    className?: string;
    variant?: "default" | "outline" | "ghost" | "link" | "destructive" | null | undefined;
    children?: React.ReactNode;
}

export function PrintInvoiceButton({ orderId, onPrint, className, variant = "outline", children }: PrintInvoiceButtonProps) {
    const handlePrint = () => {
        if (onPrint) {
            onPrint(orderId);
        } else {
            // Default print behavior
            window.print();
        }
    };

    return (
        <Button variant={variant} size="sm" className={className} onClick={handlePrint}>
            <Printer className="h-4 w-4 mr-2" />
            {children || "Print Invoice"}
        </Button>
    );
}
