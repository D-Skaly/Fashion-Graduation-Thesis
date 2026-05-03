"use client";

import * as React from "react";
import {
    ColumnDef,
    flexRender,
    getCoreRowModel,
    getPaginationRowModel,
    getFilteredRowModel,
    useReactTable,
    ColumnFiltersState,
} from "@tanstack/react-table";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import {
    Table, TableBody, TableCell,
    TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import {
    DropdownMenu, DropdownMenuContent,
    DropdownMenuItem, DropdownMenuLabel,
    DropdownMenuSeparator, DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Skeleton } from "@/components/ui/skeleton";
import { MoreHorizontal, RotateCw, ImageOff, ExternalLink, Trash2, Search, Filter } from "lucide-react";
import { toast } from "sonner";
import Link from "next/link";
import api from "@/lib/axios";
import { CreateProductDialog } from "@/components/admin/CreateProductDialog";

export type Product = {
    id: string;
    name: string;
    categoryName: string;
    basePrice: number;
    images?: string[];
    featured?: boolean;
};

interface ProductPage {
    content: Product[];
    totalElements: number;
    totalPages: number;
    number: number;
}

const fetchProducts = async (): Promise<Product[]> => {
    const { data } = await api.get<ProductPage>("/products?size=100");
    if (data && 'content' in data) {
         
        return (data as unknown as ProductPage).content;
    }
     
    return Array.isArray(data) ? data as unknown as Product[] : [];
};

const deleteProduct = async (id: string) => {
    await api.delete(`/products/${id}`);
};

export default function ProductsPage() {
    const [columnFilters, setColumnFilters] = React.useState<ColumnFiltersState>([]);
    const queryClient = useQueryClient();

    const { data, isLoading, isError, refetch } = useQuery({
        queryKey: ["admin-products"],
        queryFn: fetchProducts,
    });

    const deleteMutation = useMutation({
        mutationFn: deleteProduct,
        onSuccess: () => {
            toast.success("Product deleted successfully");
            queryClient.invalidateQueries({ queryKey: ["admin-products"] });
        },
        onError: () => toast.error("Failed to delete product"),
    });

    const columns: ColumnDef<Product>[] = [
        {
            accessorKey: "images",
            header: "Assets",
            cell: ({ row }) => {
                const img = row.original.images?.[0];
                return img ? (
                    <div className="relative h-12 w-12 rounded-xl overflow-hidden border border-zinc-800 shadow-sm">
                        <Image src={img} alt={row.original.name} fill className="object-cover" sizes="48px" />
                    </div>
                ) : (
                    <div className="h-12 w-12 rounded-xl bg-zinc-900 border border-zinc-800 flex items-center justify-center">
                        <ImageOff className="h-4 w-4 text-zinc-600" />
                    </div>
                );
            },
        },
        {
            accessorKey: "name",
            header: "Product Identity",
            cell: ({ row }) => (
                <div className="flex flex-col">
                    <span className="font-bold text-zinc-100 text-sm">{row.getValue("name")}</span>
                    <span className="text-[10px] text-zinc-500 font-bold uppercase tracking-widest mt-0.5">UID: {row.original.id.substring(0, 8)}</span>
                </div>
            ),
        },
        {
            accessorKey: "categoryName",
            header: "Genotype",
            cell: ({ row }) => (
                <Badge className="bg-zinc-900 text-zinc-300 border border-zinc-800 text-[10px] font-black uppercase tracking-widest px-2.5 py-0.5">
                    {row.getValue("categoryName") || "Unclassified"}
                </Badge>
            ),
        },
        {
            accessorKey: "basePrice",
            header: "Valuation",
            cell: ({ row }) => {
                const amount = parseFloat(row.getValue("basePrice"));
                return (
                    <div className="font-black text-zinc-100">
                        {new Intl.NumberFormat("en-US", { style: "currency", currency: "USD" }).format(amount)}
                    </div>
                );
            },
        },
        {
            accessorKey: "featured",
            header: "Status",
            cell: ({ row }) => row.getValue("featured") ? (
                <Badge className="bg-primary/20 text-primary border border-primary/20 text-[9px] font-black uppercase tracking-tighter">Peak Priority</Badge>
            ) : (
                <span className="text-[10px] text-zinc-600 font-bold uppercase tracking-widest">Standard</span>
            ),
        },
        {
            id: "actions",
            cell: ({ row }) => {
                const product = row.original;
                return (
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button variant="ghost" className="h-9 w-9 p-0 hover:bg-zinc-800 rounded-lg border border-transparent hover:border-zinc-700 transition-all">
                                <MoreHorizontal className="h-4 w-4 text-zinc-400" />
                            </Button>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end" className="w-56 bg-zinc-900 border-zinc-800 text-zinc-100 rounded-xl p-1 shadow-2xl">
                            <DropdownMenuLabel className="text-[10px] font-black uppercase tracking-widest text-zinc-500 px-3 py-2">Entity Actions</DropdownMenuLabel>
                            <DropdownMenuSeparator className="bg-zinc-800" />
                            <DropdownMenuItem asChild className="rounded-lg focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5">
                                <Link href={`/product/${product.id}`} target="_blank">
                                    <ExternalLink className="mr-3 h-4 w-4 text-zinc-500" />
                                    <span className="text-xs font-bold uppercase tracking-wider">Inspect Page</span>
                                </Link>
                            </DropdownMenuItem>
                            <DropdownMenuItem
                                onClick={() => { navigator.clipboard.writeText(product.id); toast.success("ID Encrypted & Copied"); }}
                                className="rounded-lg focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5"
                            >
                                <Search className="mr-3 h-4 w-4 text-zinc-500" />
                                <span className="text-xs font-bold uppercase tracking-wider">Duplicate ID</span>
                            </DropdownMenuItem>
                            <DropdownMenuSeparator className="bg-zinc-800" />
                            <DropdownMenuItem
                                className="rounded-lg text-red-400 focus:text-red-400 focus:bg-red-500/10 cursor-pointer py-2.5"
                                onClick={() => deleteMutation.mutate(product.id)}
                            >
                                <Trash2 className="mr-3 h-4 w-4" />
                                <span className="text-xs font-bold uppercase tracking-wider">Decommission</span>
                            </DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>
                );
            },
        },
    ];

    const table = useReactTable({
        data: data || [],
        columns,
        getCoreRowModel: getCoreRowModel(),
        getPaginationRowModel: getPaginationRowModel(),
        onColumnFiltersChange: setColumnFilters,
        getFilteredRowModel: getFilteredRowModel(),
        initialState: { pagination: { pageSize: 12 } },
        state: { columnFilters },
    });

    if (isError) {
        return (
            <div className="flex flex-col items-center justify-center h-96 gap-6">
                <div className="bg-red-500/10 p-4 rounded-full">
                    <RotateCw className="h-8 w-8 text-red-400" />
                </div>
                <div className="text-center">
                    <p className="text-zinc-100 font-black uppercase tracking-widest">Network Interruption</p>
                    <p className="text-zinc-500 text-sm font-medium mt-1">Unable to synthesize product registry.</p>
                </div>
                <Button variant="outline" onClick={() => refetch()} className="gap-2 border-zinc-800 hover:bg-zinc-900 rounded-xl px-8 h-12 text-xs font-bold uppercase tracking-widest">
                    Re-Sync System
                </Button>
            </div>
        );
    }

    return (
        <div className="w-full space-y-8">
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <h1 className="text-2xl font-black text-white tracking-tight uppercase">Product Registry</h1>
                    <p className="text-zinc-500 text-xs font-bold uppercase tracking-[0.2em] mt-1">
                        {data ? `${data.length} ACTIVE GENOTYPES` : "INITIALIZING CORE..."}
                    </p>
                </div>
                <CreateProductDialog />
            </div>

            <div className="flex items-center gap-4">
                <div className="relative flex-1 max-w-md">
                    <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-zinc-500" />
                    <Input
                        placeholder="Search product identifiers..."
                        value={(table.getColumn("name")?.getFilterValue() as string) ?? ""}
                        onChange={(e) => table.getColumn("name")?.setFilterValue(e.target.value)}
                        className="pl-12 h-12 bg-zinc-900 border-zinc-800 text-white placeholder:text-zinc-600 rounded-xl focus:border-primary/50"
                    />
                </div>
                <Button variant="outline" className="h-12 w-12 rounded-xl bg-zinc-900 border-zinc-800 p-0 text-zinc-500">
                    <Filter className="h-4 w-4" />
                </Button>
            </div>

            <div className="rounded-2xl border border-zinc-800 bg-zinc-900/50 overflow-hidden shadow-xl">
                <Table>
                    <TableHeader className="bg-zinc-900/50">
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow key={headerGroup.id} className="border-zinc-800 hover:bg-transparent">
                                {headerGroup.headers.map((header) => (
                                    <TableHead key={header.id} className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em] py-5 h-auto">
                                        {header.isPlaceholder ? null : flexRender(header.column.columnDef.header, header.getContext())}
                                    </TableHead>
                                ))}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            Array.from({ length: 6 }).map((_, i) => (
                                <TableRow key={i} className="border-zinc-800">
                                    <TableCell className="py-4"><Skeleton className="h-12 w-12 rounded-xl bg-zinc-800" /></TableCell>
                                    <TableCell className="py-4"><Skeleton className="h-4 w-48 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-4"><Skeleton className="h-6 w-24 rounded-lg bg-zinc-800" /></TableCell>
                                    <TableCell className="py-4"><Skeleton className="h-4 w-16 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-4"><Skeleton className="h-4 w-20 bg-zinc-800" /></TableCell>
                                    <TableCell className="py-4"><Skeleton className="h-9 w-9 rounded-lg bg-zinc-800" /></TableCell>
                                </TableRow>
                            ))
                        ) : table.getRowModel().rows?.length ? (
                            table.getRowModel().rows.map((row) => (
                                <TableRow key={row.id} className="border-zinc-800 hover:bg-zinc-800/30 transition-colors group">
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell key={cell.id} className="py-4">
                                            {flexRender(cell.column.columnDef.cell, cell.getContext())}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={columns.length} className="h-48 text-center text-zinc-500 font-bold uppercase tracking-widest text-xs">
                                    No matching entities found in core database.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>

            <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-4 border-t border-zinc-800">
                <span className="text-zinc-500 text-[10px] font-black uppercase tracking-[0.2em]">
                    Showing {table.getFilteredRowModel().rows.length} of {data?.length ?? 0} Global Entries
                </span>
                <div className="flex items-center gap-3">
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => table.previousPage()}
                        disabled={!table.getCanPreviousPage()}
                        className="bg-zinc-900 border-zinc-800 hover:bg-zinc-800 text-zinc-400 rounded-xl px-4 h-10 text-[10px] font-black uppercase tracking-widest"
                    >
                        Previous Block
                    </Button>
                    <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-zinc-900 border border-zinc-800 text-[10px] font-black text-zinc-100">
                        <span className="text-primary">{table.getState().pagination.pageIndex + 1}</span>
                        <span className="text-zinc-600">/</span>
                        <span>{table.getPageCount()}</span>
                    </div>
                    <Button
                        variant="outline"
                        size="sm"
                        onClick={() => table.nextPage()}
                        disabled={!table.getCanNextPage()}
                        className="bg-zinc-900 border-zinc-800 hover:bg-zinc-800 text-zinc-400 rounded-xl px-4 h-10 text-[10px] font-black uppercase tracking-widest"
                    >
                        Next Block
                    </Button>
                </div>
            </div>
        </div>
    );
}
