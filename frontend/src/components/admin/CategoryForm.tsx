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
import { Textarea } from "@/components/ui/textarea";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";

interface CategoryFormProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    onSubmit: (data: CategoryFormData) => void;
    isLoading?: boolean;
    categories?: { id: string; name: string }[];
}

export interface CategoryFormData {
    name: string;
    slug: string;
    description: string;
    parentId?: string;
    isActive: boolean;
}

export function CategoryForm({ 
    open, 
    onOpenChange, 
    onSubmit, 
    isLoading = false,
    categories = []
}: CategoryFormProps) {
    const [formData, setFormData] = useState<CategoryFormData>({
        name: "",
        slug: "",
        description: "",
        parentId: undefined,
        isActive: true,
    });

    const handleNameChange = (value: string) => {
        const slug = value.toLowerCase().replace(/\s+/g, "-").replace(/[^a-z0-9-]/g, "");
        setFormData({ ...formData, name: value, slug });
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <Dialog open={open} onOpenChange={onOpenChange}>
            <DialogContent className="max-w-md">
                <DialogHeader>
                    <DialogTitle>Add Category</DialogTitle>
                    <DialogDescription>
                        Create a new category for organizing your products.
                    </DialogDescription>
                </DialogHeader>
                <form onSubmit={handleSubmit} className="space-y-4 py-4">
                    <div className="space-y-2">
                        <Label htmlFor="name">Category Name</Label>
                        <Input
                            id="name"
                            value={formData.name}
                            onChange={(e) => handleNameChange(e.target.value)}
                            placeholder="Men&apos;s Clothing"
                            required
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="slug">Slug</Label>
                        <Input
                            id="slug"
                            value={formData.slug}
                            onChange={(e) => setFormData({ ...formData, slug: e.target.value })}
                            placeholder="mens-clothing"
                            required
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="parentId">Parent Category</Label>
                        <Select
                            value={formData.parentId}
                            onValueChange={(value) => setFormData({ ...formData, parentId: value })}
                        >
                            <SelectTrigger id="parentId">
                                <SelectValue placeholder="None (Root Category)" />
                            </SelectTrigger>
                            <SelectContent>
                                <SelectItem value="">None (Root Category)</SelectItem>
                                {categories.map((cat) => (
                                    <SelectItem key={cat.id} value={cat.id}>
                                        {cat.name}
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="description">Description</Label>
                        <Textarea
                            id="description"
                            value={formData.description}
                            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                            placeholder="Category description..."
                            rows={3}
                        />
                    </div>

                    <div className="flex items-center justify-between">
                        <Label htmlFor="isActive">Active</Label>
                        <Switch
                            id="isActive"
                            checked={formData.isActive}
                            onCheckedChange={(checked) => setFormData({ ...formData, isActive: checked })}
                        />
                    </div>
                </form>
                <DialogFooter>
                    <Button variant="outline" onClick={() => onOpenChange(false)}>
                        Cancel
                    </Button>
                    <Button onClick={handleSubmit} disabled={isLoading}>
                        {isLoading ? "Creating..." : "Create Category"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
