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

export function PopularProducts({ products }: PopularProductsProps) {
    return (
        <Card>
            <CardHeader>
                <CardTitle className="flex items-center gap-2">
                    <TrendingUp className="h-5 w-5" />
                    Popular Products
                </CardTitle>
            </CardHeader>
            <CardContent>
                <Table>
                    <TableHeader>
                        <TableRow>
                            <TableHead>Product</TableHead>
                            <TableHead>Category</TableHead>
                            <TableHead className="text-right">Sold</TableHead>
                            <TableHead className="text-right">Revenue</TableHead>
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {products.map((product, index) => (
                            <TableRow key={product.id}>
                                <TableCell className="font-medium">
                                    <div className="flex items-center gap-2">
                                        <span className="text-muted-foreground text-sm">#{index + 1}</span>
                                        {product.name}
                                    </div>
                                </TableCell>
                                <TableCell className="text-muted-foreground">{product.category}</TableCell>
                                <TableCell className="text-right">{product.soldCount}</TableCell>
                                <TableCell className="text-right font-medium">
                                    ${product.revenue.toLocaleString()}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </CardContent>
        </Card>
    );
}
