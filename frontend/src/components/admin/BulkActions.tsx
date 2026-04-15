"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { MoreHorizontal, Trash2, Archive, Tag } from "lucide-react";

interface BulkActionsProps {
    selectedCount: number;
    onBulkDelete?: () => void;
    onBulkArchive?: () => void;
    onBulkTag?: () => void;
}

export function BulkActions({ selectedCount, onBulkDelete, onBulkArchive, onBulkTag }: BulkActionsProps) {
    const [isOpen, setIsOpen] = useState(false);

    if (selectedCount === 0) {
        return null;
    }

    return (
        <div className="flex items-center gap-2">
            <span className="text-sm text-muted-foreground">
                {selectedCount} selected
            </span>
            
            <DropdownMenu open={isOpen} onOpenChange={setIsOpen}>
                <DropdownMenuTrigger asChild>
                    <Button variant="outline" size="sm">
                        <MoreHorizontal className="h-4 w-4 mr-2" />
                        Bulk Actions
                    </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                    <DropdownMenuItem onClick={onBulkDelete}>
                        <Trash2 className="h-4 w-4 mr-2" />
                        Delete Selected
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={onBulkArchive}>
                        <Archive className="h-4 w-4 mr-2" />
                        Archive Selected
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={onBulkTag}>
                        <Tag className="h-4 w-4 mr-2" />
                        Add Tags
                    </DropdownMenuItem>
                </DropdownMenuContent>
            </DropdownMenu>
            
            <Button
                variant="ghost"
                size="sm"
                onClick={() => window.location.reload()}
            >
                Clear Selection
            </Button>
        </div>
    );
}
