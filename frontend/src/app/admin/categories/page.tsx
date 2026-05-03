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
    DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
    Dialog, DialogContent, DialogHeader,
    DialogTitle, DialogFooter,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
    Plus, Search, MoreHorizontal, FolderTree,
    RefreshCw, Pencil, Trash2, ChevronRight
} from "lucide-react";
import { toast } from "sonner";
import api from "@/lib/axios";

interface Category {
    id: string;
    name: string;
    slug: string;
    description?: string;
    productCount?: number;
    parentId?: string;
    parentName?: string;
    isActive?: boolean;
}

interface CategoryPage {
    content: Category[];
    totalElements: number;
}

const fetchCategories = async (): Promise<Category[]> => {
    const { data } = await api.get("/categories");
    if (data && 'content' in (data as object)) return (data as CategoryPage).content;
    return Array.isArray(data) ? data as Category[] : [];
};

export default function AdminCategoriesPage() {
    const [searchTerm, setSearchTerm] = useState("");
    const [isAddOpen, setIsAddOpen] = useState(false);
    const [editCategory, setEditCategory] = useState<Category | null>(null);
    const [formName, setFormName] = useState("");
    const [formDescription, setFormDescription] = useState("");
    const queryClient = useQueryClient();

    const { data: categories, isLoading, refetch } = useQuery({
        queryKey: ["admin-categories"],
        queryFn: fetchCategories,
    });

    const createMutation = useMutation({
        mutationFn: async (payload: { name: string; description: string }) => {
            await api.post("/categories", payload);
        },
        onSuccess: () => {
            toast.success("Genotype initialized");
            queryClient.invalidateQueries({ queryKey: ["admin-categories"] });
            setIsAddOpen(false);
            setFormName("");
            setFormDescription("");
        },
        onError: () => toast.error("Initialization failed"),
    });

    const updateMutation = useMutation({
        mutationFn: async ({ id, name, description }: { id: string; name: string; description: string }) => {
            await api.put(`/categories/${id}`, { name, description });
        },
        onSuccess: () => {
            toast.success("Genotype updated");
            queryClient.invalidateQueries({ queryKey: ["admin-categories"] });
            setEditCategory(null);
        },
        onError: () => toast.error("Update failed"),
    });

    const deleteMutation = useMutation({
        mutationFn: async (id: string) => {
            await api.delete(`/categories/${id}`);
        },
        onSuccess: () => {
            toast.success("Genotype decommissioned");
            queryClient.invalidateQueries({ queryKey: ["admin-categories"] });
        },
        onError: () => toast.error("Decommission failed: Linked entities found"),
    });

    const openEdit = (cat: Category) => {
        setEditCategory(cat);
        setFormName(cat.name);
        setFormDescription(cat.description || "");
    };

    const filteredCategories = categories?.filter(c =>
        c.name.toLowerCase().includes(searchTerm.toLowerCase())
    ) || [];

    return (
        <div className="space-y-8">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <h1 className="text-2xl font-black text-white tracking-tight uppercase">Genotype Registry</h1>
                    <p className="text-zinc-500 text-xs font-bold uppercase tracking-[0.2em] mt-1">
                        {categories ? `${categories.length} ACTIVE CLASSIFICATIONS` : "INITIALIZING CORE..."}
                    </p>
                </div>
                <div className="flex gap-3">
                    <Button
                        variant="outline"
                        size="icon"
                        className="h-11 w-11 rounded-xl bg-zinc-900 border-zinc-800 text-zinc-500 hover:text-zinc-100 transition-all"
                        onClick={() => refetch()}
                    >
                        <RefreshCw className="h-4 w-4" />
                    </Button>
                    <Button
                        className="bg-primary hover:bg-primary/90 text-white rounded-xl h-11 px-6 font-black uppercase tracking-widest text-[11px] gap-2 shadow-lg shadow-primary/20"
                        onClick={() => { setFormName(""); setFormDescription(""); setIsAddOpen(true); }}
                    >
                        <Plus className="h-4 w-4" /> Add Classification
                    </Button>
                </div>
            </div>

            <div className="flex items-center gap-4">
                <div className="relative flex-1 max-w-md">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-zinc-500" />
                    <Input
                        placeholder="Search classifications..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="pl-12 h-12 bg-zinc-900 border-zinc-800 text-white placeholder:text-zinc-600 rounded-xl focus:border-primary/50"
                    />
                </div>
            </div>

            <div className="rounded-2xl border border-zinc-800 bg-zinc-900/50 overflow-hidden shadow-xl">
                <Table>
                    <TableHeader className="bg-zinc-900/50">
                        <TableRow className="border-zinc-800 hover:bg-transparent">
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 pl-6">Classification</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Slug Identifier</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Population</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5">Status</TableHead>
                            <TableHead className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 text-right pr-6">Operational</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            Array.from({ length: 5 }).map((_, i) => (
                                <TableRow key={i} className="border-zinc-800">
                                    <TableCell className="py-5 pl-6"><Skeleton className="h-4 w-48 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-4 w-32 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-6 w-20 rounded-lg bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5"><Skeleton className="h-6 w-20 rounded-lg bg-zinc-800" /></TableCell>
                                    <TableCell className="py-5 pr-6"><Skeleton className="h-9 w-9 ml-auto bg-zinc-800 rounded-lg" /></TableCell>
                                </TableRow>
                            ))
                        ) : filteredCategories.map((category) => (
                            <TableRow key={category.id} className="border-zinc-800 hover:bg-zinc-800/30 transition-colors group">
                                <TableCell className="py-5 pl-6">
                                    <div className="flex items-center gap-3">
                                        <div className="h-8 w-8 rounded-lg bg-zinc-800 border border-zinc-700 flex items-center justify-center">
                                            <FolderTree className="h-4 w-4 text-zinc-500" />
                                        </div>
                                        <div className="flex flex-col">
                                            <span className="font-bold text-zinc-100 text-sm group-hover:text-primary transition-colors">{category.name}</span>
                                            {category.parentName && (
                                                <span className="text-[10px] text-zinc-600 font-bold uppercase tracking-widest flex items-center gap-1 mt-0.5">
                                                    <ChevronRight className="h-2.5 w-2.5" /> {category.parentName}
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                </TableCell>
                                <TableCell className="py-5 font-mono text-zinc-500 text-[11px] font-bold uppercase tracking-wider">{category.slug}</TableCell>
                                <TableCell className="py-5">
                                    <Badge className="bg-zinc-900 border border-zinc-800 text-zinc-400 text-[9px] font-black uppercase tracking-widest px-2 py-0.5">
                                        {category.productCount ?? 0} SUBJECTS
                                    </Badge>
                                </TableCell>
                                <TableCell className="py-5">
                                    {category.isActive !== false ? (
                                        <Badge className="bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 text-[9px] font-black uppercase tracking-widest px-2 py-0.5">Active</Badge>
                                    ) : (
                                        <Badge className="bg-zinc-800 text-zinc-600 border border-zinc-700 text-[9px] font-black uppercase tracking-widest px-2 py-0.5">Inactive</Badge>
                                    )}
                                </TableCell>
                                <TableCell className="py-5 text-right pr-6">
                                    <DropdownMenu>
                                        <DropdownMenuTrigger asChild>
                                            <Button variant="ghost" className="h-9 w-9 p-0 hover:bg-zinc-800 rounded-xl border border-transparent hover:border-zinc-700 transition-all">
                                                <MoreHorizontal className="h-4 w-4 text-zinc-400" />
                                            </Button>
                                        </DropdownMenuTrigger>
                                        <DropdownMenuContent align="end" className="w-56 bg-zinc-900 border-zinc-800 text-zinc-100 rounded-xl p-1 shadow-2xl">
                                            <DropdownMenuLabel className="text-[10px] font-black uppercase tracking-widest text-zinc-500 px-3 py-2">Operational Actions</DropdownMenuLabel>
                                            <DropdownMenuSeparator className="bg-zinc-800" />
                                            <DropdownMenuItem
                                                className="rounded-lg focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5"
                                                onClick={() => openEdit(category)}
                                            >
                                                <Pencil className="mr-3 h-4 w-4 text-zinc-500" />
                                                <span className="text-xs font-bold uppercase tracking-wider">Modify Profile</span>
                                            </DropdownMenuItem>
                                            <DropdownMenuSeparator className="bg-zinc-800" />
                                            <DropdownMenuItem
                                                className="rounded-lg text-red-400 focus:text-red-400 focus:bg-red-500/10 cursor-pointer py-2.5"
                                                onClick={() => deleteMutation.mutate(category.id)}
                                            >
                                                <Trash2 className="mr-3 h-4 w-4" />
                                                <span className="text-xs font-bold uppercase tracking-wider">Decommission</span>
                                            </DropdownMenuItem>
                                        </DropdownMenuContent>
                                    </DropdownMenu>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </div>

            {/* Dialogs */}
            <Dialog open={isAddOpen || !!editCategory} onOpenChange={(open) => { if (!open) { setIsAddOpen(false); setEditCategory(null); } }}>
                <DialogContent className="bg-zinc-900 border border-zinc-800 text-zinc-100 rounded-2xl p-6">
                    <DialogHeader>
                        <DialogTitle className="text-xl font-black uppercase tracking-widest text-white">
                            {editCategory ? "Modify Classification" : "Initialize Classification"}
                        </DialogTitle>
                    </DialogHeader>
                    <div className="space-y-6 py-4">
                        <div className="space-y-2">
                            <Label className="text-[10px] font-black uppercase tracking-widest text-zinc-500">Classification Name</Label>
                            <Input
                                placeholder="E.g. Neural Outerwear"
                                value={formName}
                                onChange={(e) => setFormName(e.target.value)}
                                className="h-12 bg-zinc-800 border-zinc-700 text-white rounded-xl"
                            />
                        </div>
                        <div className="space-y-2">
                            <Label className="text-[10px] font-black uppercase tracking-widest text-zinc-500">System Description</Label>
                            <Textarea
                                placeholder="Describe the classification characteristics..."
                                value={formDescription}
                                onChange={(e) => setFormDescription(e.target.value)}
                                className="bg-zinc-800 border-zinc-700 text-white rounded-xl min-h-[120px]"
                            />
                        </div>
                    </div>
                    <DialogFooter className="gap-3">
                        <Button variant="ghost" className="text-zinc-500 hover:text-white" onClick={() => { setIsAddOpen(false); setEditCategory(null); }}>
                            Abort
                        </Button>
                        <Button
                            className="bg-primary hover:bg-primary/90 text-white font-black uppercase tracking-widest text-[11px] h-12 px-8 rounded-xl"
                            disabled={!formName.trim() || createMutation.isPending || updateMutation.isPending}
                            onClick={() => {
                                if (editCategory) {
                                    updateMutation.mutate({ id: editCategory.id, name: formName, description: formDescription });
                                } else {
                                    createMutation.mutate({ name: formName, description: formDescription });
                                }
                            }}
                        >
                            {editCategory ? "Update Core" : "Initialize Core"}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </div>
    );
}
