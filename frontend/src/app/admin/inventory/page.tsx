"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Loader2, AlertTriangle, Package, TrendingDown, TrendingUp, Save } from "lucide-react";
import { toast } from "sonner";
import api from "@/lib/axios";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Skeleton } from "@/components/ui/skeleton";

interface InventoryItem {
  id: string;
  productName: string;
  size: string;
  color: string;
  skuCode: string;
  stockQuantity: number;
  lowStockThreshold: number;
}

interface BulkUpdateItem {
  variantId: string;
  quantity: number;
}

const fetchInventory = async (): Promise<InventoryItem[]> => {
  const { data } = await api.get("/admin/inventory");
  return Array.isArray(data) ? data : data.content || [];
};

const bulkUpdateStock = async (updates: BulkUpdateItem[]) => {
  const { data } = await api.put("/admin/inventory/bulk-update", { updates });
  return data;
};

export default function InventoryPage() {
  const queryClient = useQueryClient();
  const [stockUpdates, setStockUpdates] = useState<Record<string, string>>({});

  const { data: inventory, isLoading, isError } = useQuery({
    queryKey: ["admin-inventory"],
    queryFn: fetchInventory,
  });

  const mutation = useMutation({
    mutationFn: bulkUpdateStock,
    onSuccess: () => {
      toast.success("Inventory updated successfully");
      queryClient.invalidateQueries({ queryKey: ["admin-inventory"] });
      setStockUpdates({});
    },
    onError: (error: unknown) => {
      const axiosError = error as { response?: { data?: { message?: string } } };
      toast.error(axiosError.response?.data?.message || "Failed to update inventory");
    },
  });

  const lowStockItems = inventory?.filter(
    (item) => item.stockQuantity <= item.lowStockThreshold
  ) || [];

  const handleStockChange = (variantId: string, value: string) => {
    setStockUpdates((prev) => ({ ...prev, [variantId]: value }));
  };

  const handleBulkUpdate = () => {
    const updates = Object.entries(stockUpdates)
      .filter(([, value]) => value !== "" && !isNaN(Number(value)))
      .map(([variantId, value]) => ({
        variantId,
        quantity: Number(value),
      }));

    if (updates.length === 0) {
      toast.error("No valid stock updates to save");
      return;
    }

    mutation.mutate(updates);
  };

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-7xl space-y-6">
        <Skeleton className="h-8 w-64 bg-white/10" />
        <Skeleton className="h-32 w-full bg-white/10 rounded-xl" />
        <Skeleton className="h-96 w-full bg-white/10 rounded-xl" />
      </div>
    );
  }

  if (isError) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-7xl">
        <Alert variant="destructive">
          <AlertTriangle className="h-4 w-4" />
          <AlertDescription>
            Failed to load inventory data. Please try again.
          </AlertDescription>
        </Alert>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white">Inventory Management</h1>
        <p className="text-sm text-stone-400">
          Manage stock levels for all product variants
        </p>
      </div>

      {/* Low Stock Alert */}
      {lowStockItems.length > 0 && (
        <Alert className="border-yellow-500/50 bg-yellow-500/10">
          <AlertTriangle className="h-4 w-4 text-yellow-500" />
          <AlertDescription className="text-yellow-200">
            <div className="flex items-center justify-between">
              <span>
                {lowStockItems.length} item(s) are below low stock threshold
              </span>
              <Badge variant="outline" className="border-yellow-500/50 text-yellow-400">
                Low Stock Alert
              </Badge>
            </div>
          </AlertDescription>
        </Alert>
      )}

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card className="bg-white/5 border-white/10 text-white">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-stone-400">
              Total Variants
            </CardTitle>
            <Package className="h-4 w-4 text-stone-400" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{inventory?.length || 0}</div>
          </CardContent>
        </Card>

        <Card className="bg-white/5 border-white/10 text-white">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-stone-400">
              Low Stock Items
            </CardTitle>
            <TrendingDown className="h-4 w-4 text-yellow-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-yellow-500">
              {lowStockItems.length}
            </div>
          </CardContent>
        </Card>

        <Card className="bg-white/5 border-white/10 text-white">
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium text-stone-400">
              Total Stock
            </CardTitle>
            <TrendingUp className="h-4 w-4 text-green-500" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-500">
              {inventory?.reduce((sum, item) => sum + item.stockQuantity, 0) || 0}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Inventory Table */}
      <Card className="bg-white/5 border-white/10 text-white">
        <CardHeader>
          <CardTitle>Stock Levels</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow className="border-white/10 hover:bg-transparent">
                <TableHead className="text-stone-400">Product</TableHead>
                <TableHead className="text-stone-400">Variant</TableHead>
                <TableHead className="text-stone-400">SKU</TableHead>
                <TableHead className="text-right text-stone-400">Current Stock</TableHead>
                <TableHead className="text-right text-stone-400">Threshold</TableHead>
                <TableHead className="text-right text-stone-400">New Stock</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {inventory?.map((item) => {
                const isLowStock = item.stockQuantity <= item.lowStockThreshold;
                return (
                  <TableRow key={item.id} className="border-white/10 hover:bg-white/5">
                    <TableCell className="font-medium">{item.productName}</TableCell>
                    <TableCell className="text-stone-400">
                      {item.size} / {item.color}
                    </TableCell>
                    <TableCell className="text-stone-400 font-mono text-xs">
                      {item.skuCode}
                    </TableCell>
                    <TableCell className="text-right">
                      <span
                        className={
                          isLowStock ? "text-yellow-500 font-bold" : "text-white"
                        }
                      >
                        {item.stockQuantity}
                      </span>
                    </TableCell>
                    <TableCell className="text-right text-stone-400">
                      {item.lowStockThreshold}
                    </TableCell>
                    <TableCell className="text-right">
                      <Input
                        type="number"
                        min="0"
                        placeholder={item.stockQuantity.toString()}
                        className="w-24 h-8 ml-auto bg-white/5 border-white/20 text-white text-right"
                        value={stockUpdates[item.id] || ""}
                        onChange={(e) => handleStockChange(item.id, e.target.value)}
                      />
                    </TableCell>
                  </TableRow>
                );
              })}
            </TableBody>
          </Table>

          {/* Bulk Update Button */}
          {Object.keys(stockUpdates).length > 0 && (
            <div className="mt-4 flex items-center justify-end gap-4">
              <span className="text-sm text-stone-400">
                {Object.keys(stockUpdates).length} item(s) to update
              </span>
              <Button
                onClick={handleBulkUpdate}
                disabled={mutation.isPending}
                className="bg-primary hover:bg-primary/90"
              >
                {mutation.isPending ? (
                  <>
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                    Saving...
                  </>
                ) : (
                  <>
                    <Save className="mr-2 h-4 w-4" />
                    Save Changes
                  </>
                )}
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
