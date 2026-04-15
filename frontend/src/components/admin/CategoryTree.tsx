"use client";

import { useState } from "react";
import { ChevronDown, ChevronRight, Folder, FolderOpen } from "lucide-react";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

interface CategoryNode {
    id: string;
    name: string;
    slug: string;
    productCount: number;
    children?: CategoryNode[];
    parentId?: string;
}

interface CategoryTreeProps {
    categories: CategoryNode[];
    onCategorySelect?: (categoryId: string) => void;
    selectedId?: string;
}

export function CategoryTree({ categories, onCategorySelect, selectedId }: CategoryTreeProps) {
    const [expandedIds, setExpandedIds] = useState<Set<string>>(new Set());

    const toggleExpand = (id: string) => {
        const newExpanded = new Set(expandedIds);
        if (newExpanded.has(id)) {
            newExpanded.delete(id);
        } else {
            newExpanded.add(id);
        }
        setExpandedIds(newExpanded);
    };

    const renderNode = (node: CategoryNode, level: number = 0) => {
        const hasChildren = node.children && node.children.length > 0;
        const isExpanded = expandedIds.has(node.id);
        const isSelected = selectedId === node.id;

        return (
            <div key={node.id}>
                <div
                    className={cn(
                        "flex items-center gap-2 py-2 px-2 hover:bg-secondary/50 cursor-pointer rounded-md transition-colors",
                        isSelected && "bg-secondary"
                    )}
                    style={{ paddingLeft: `${level * 16 + 8}px` }}
                    onClick={() => onCategorySelect?.(node.id)}
                >
                    {hasChildren && (
                        <Button
                            variant="ghost"
                            size="icon"
                            className="h-4 w-4 p-0"
                            onClick={(e) => {
                                e.stopPropagation();
                                toggleExpand(node.id);
                            }}
                        >
                            {isExpanded ? (
                                <ChevronDown className="h-3 w-3" />
                            ) : (
                                <ChevronRight className="h-3 w-3" />
                            )}
                        </Button>
                    )}
                    {isExpanded ? (
                        <FolderOpen className="h-4 w-4 text-primary" />
                    ) : (
                        <Folder className="h-4 w-4 text-muted-foreground" />
                    )}
                    <span className="flex-1 text-sm">{node.name}</span>
                    <span className="text-xs text-muted-foreground">{node.productCount}</span>
                </div>
                {hasChildren && isExpanded && (
                    <div>
                        {node.children?.map((child) => renderNode(child, level + 1))}
                    </div>
                )}
            </div>
        );
    };

    return (
        <div className="space-y-1">
            {categories.map((category) => renderNode(category))}
        </div>
    );
}
