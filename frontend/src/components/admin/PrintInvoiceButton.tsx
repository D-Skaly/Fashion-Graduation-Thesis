"use client";

import { Button } from "@/components/ui/button";
import { Printer } from "lucide-react";

interface PrintInvoiceButtonProps {
    orderId: string;
    onPrint?: (orderId: string) => void;
}

export function PrintInvoiceButton({ orderId, onPrint }: PrintInvoiceButtonProps) {
    const handlePrint = () => {
        if (onPrint) {
            onPrint(orderId);
        } else {
            // Default print behavior
            window.print();
        }
    };

    return (
        <Button variant="outline" size="sm" onClick={handlePrint}>
            <Printer className="h-4 w-4 mr-2" />
            Print Invoice
        </Button>
    );
}
