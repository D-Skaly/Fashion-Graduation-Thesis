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
    DialogTrigger,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { AlertTriangle } from "lucide-react";

interface BanUserDialogProps {
    userName: string;
    onBan: (reason: string) => void;
    isLoading?: boolean;
}

export function BanUserDialog({ userName, onBan, isLoading = false }: BanUserDialogProps) {
    const [open, setOpen] = useState(false);
    const [reason, setReason] = useState("");

    const handleBan = () => {
        if (reason.trim()) {
            onBan(reason);
            setOpen(false);
            setReason("");
        }
    };

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <Button variant="destructive" size="sm">
                    Ban User
                </Button>
            </DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2 text-destructive">
                        <AlertTriangle className="h-5 w-5" />
                        Ban User
                    </DialogTitle>
                    <DialogDescription>
                        Are you sure you want to ban <strong>{userName}</strong>? This action will prevent the user from accessing their account.
                    </DialogDescription>
                </DialogHeader>
                <div className="space-y-4 py-4">
                    <div className="space-y-2">
                        <Label htmlFor="reason">Reason for ban</Label>
                        <Textarea
                            id="reason"
                            placeholder="Provide a reason for this ban..."
                            value={reason}
                            onChange={(e) => setReason(e.target.value)}
                            className="min-h-[100px]"
                        />
                    </div>
                </div>
                <DialogFooter>
                    <Button variant="outline" onClick={() => setOpen(false)} disabled={isLoading}>
                        Cancel
                    </Button>
                    <Button
                        variant="destructive"
                        onClick={handleBan}
                        disabled={!reason.trim() || isLoading}
                    >
                        {isLoading ? "Banning..." : "Confirm Ban"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
