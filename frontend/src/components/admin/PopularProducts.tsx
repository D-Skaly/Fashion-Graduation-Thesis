"use client";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { TrendingUp } from "lucide-react";
import { Badge } from "@/components/ui/badge";

interface Product {
    id: string;
    name: string;
    category: string;
    soldCount: number;
    revenue: number;
}

interface PopularProductsProps {
    products: Product[];
}

const fallbackPopular = [
    { id: '1', name: 'Cyberpunk Shell', category: 'Outerwear', soldCount: 142, revenue: 12400 },
    { id: '2', name: 'Neural Mesh Tee', category: 'Tops', soldCount: 98, revenue: 4900 },
    { id: '3', name: 'Void Cargo Pants', category: 'Bottoms', soldCount: 85, revenue: 6800 },
];

export function PopularProducts({ products }: PopularProductsProps) {
    const displayProducts = products.length > 0 ? products : fallbackPopular;

    return (
        <Card className="bg-zinc-900/40 border-zinc-800 shadow-sm h-full overflow-hidden rounded-[2.5rem]">
            <CardHeader className="p-8 pb-4">
                <CardTitle className="text-[10px] font-black uppercase tracking-[0.3em] flex items-center gap-3 text-zinc-400">
                    <TrendingUp className="h-4 w-4 text-primary" />
                    Peak Performance Assets
                </CardTitle>
            </CardHeader>
            <CardContent className="p-0 px-2 pb-6">
                <Table>
                    <TableHeader>
                        <TableRow className="border-zinc-800/50 hover:bg-transparent border-none">
                            <TableHead className="text-zinc-600 text-[9px] uppercase tracking-[0.2em] font-black pl-8 py-4">Asset Class</TableHead>
                            <TableHead className="text-zinc-600 text-[9px] uppercase tracking-[0.2em] font-black text-right pr-8 py-4">Yield</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {displayProducts.map((product) => (
                            <TableRow key={product.id} className="border-zinc-800/30 hover:bg-zinc-800/20 transition-all group border-none">
                                <TableCell className="py-5 pl-8">
                                    <div className="flex flex-col gap-1">
                                        <span className="font-bold text-sm text-zinc-100 group-hover:text-primary transition-colors tracking-tight">{product.name}</span>
                                        <span className="text-[9px] uppercase tracking-[0.2em] text-zinc-600 font-black">{product.category}</span>
                                    </div>
                                </TableCell>
                                <TableCell className="text-right py-5 pr-8">
                                    <div className="flex flex-col items-end gap-1">
                                        <span className="text-sm font-black text-white tracking-tighter">${product.revenue.toLocaleString()}</span>
                                        <Badge className="text-[8px] bg-zinc-800 text-zinc-500 border-none font-black px-2 py-0">
                                            {product.soldCount} UNITS
                                        </Badge>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
    );
}
