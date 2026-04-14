"use client";

import { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger, DialogDescription } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Search, Sparkles, Loader2, ArrowRight } from "lucide-react";
import Image from "next/image";
import Link from "next/link";
import api from "@/lib/axios";

// Def Product type matching backend DTO
interface Product {
  id: string;
  name: string;
  categoryName?: string;
  basePrice: number;
  featuredImageUrl?: string;
}

interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

export function SemanticSearchDialog() {
  const [open, setOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState("");
  const [debouncedTerm, setDebouncedTerm] = useState("");

  // Debounce user input
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedTerm(searchTerm);
    }, 500);
    return () => clearTimeout(handler);
  }, [searchTerm]);

  const { data: products, isLoading } = useQuery({
    queryKey: ["semanticSearch", debouncedTerm],
    queryFn: async () => {
      if (!debouncedTerm) return [];
      const res = await api.get<ApiResponse<Product[]>>(`/products/search/semantic`, {
        params: { q: debouncedTerm },
      });
      return res.data.data;
    },
    enabled: debouncedTerm.length > 2, // only run if input > 2 chars
  });

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline" className="relative h-10 w-full justify-start rounded-[0.5rem] bg-background text-sm font-normal text-muted-foreground shadow-none sm:pr-12 md:w-64 lg:w-80 border-primary/20 hover:border-primary/50 transition-colors">
          <span className="hidden lg:inline-flex">Hỏi AI để tìm quần áo...</span>
          <span className="inline-flex lg:hidden">Tìm kiếm AI...</span>
          <Sparkles className="absolute right-1.5 top-2.5 h-4 w-4 text-primary animate-pulse" />
        </Button>
      </DialogTrigger>
      
      <DialogContent className="sm:max-w-xl mx-auto top-[5%] translate-y-0 h-auto max-h-[85vh] flex flex-col p-0 gap-0 overflow-hidden border-zinc-200">
        <DialogHeader className="p-4 border-b">
          <DialogTitle className="flex items-center gap-2 text-xl font-semibold">
            <Sparkles className="h-5 w-5 text-primary" />
            Tìm Kiếm AI Thông Minh
          </DialogTitle>
          <DialogDescription className="sr-only">
            Gõ mô tả bằng ngôn ngữ tự nhiên để tìm kiếm quần áo bằng công nghệ AI.
          </DialogDescription>
        </DialogHeader>
        
        <div className="p-4 bg-zinc-50 flex-1 overflow-y-auto">
           <div className="relative">
             <Search className="absolute left-3 top-3 h-5 w-5 text-zinc-400" />
             <Input 
                placeholder="Ví dụ: Tìm cho tôi đầm dạ hội màu đỏ nổi bật đi tiệc..."
                className="pl-10 h-12 text-base border-zinc-300 shadow-sm focus-visible:ring-primary/50"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
             />
           </div>

           <div className="mt-6">
              {isLoading && searchTerm.length > 2 && (
                 <div className="flex flex-col items-center justify-center py-10 text-zinc-500">
                     <Loader2 className="h-8 w-8 animate-spin text-primary mb-4" />
                     <p>AI đang nỗ lực suy nghĩ và chọn véc-tơ...</p>
                 </div>
              )}

              {!isLoading && products && products.length > 0 && (
                 <div className="flex flex-col gap-3 pb-4">
                    <p className="text-sm text-zinc-500 font-medium mb-1">Kết quả gợi ý từ AI:</p>
                    {products.map((product) => (
                       <Link key={product.id} href={`/product/${product.id}`} onClick={() => setOpen(false)}>
                          <div className="flex items-center gap-4 bg-white p-3 rounded-lg border border-zinc-200 hover:border-primary/50 hover:shadow-md transition-all group">
                             <div className="h-16 w-16 bg-zinc-100 rounded-md overflow-hidden flex-shrink-0 relative">
                                {product.featuredImageUrl ? (
                                   <Image src={product.featuredImageUrl} alt={product.name} fill className="object-cover" />
                                ) : (
                                   <div className="w-full h-full flex items-center justify-center text-zinc-300"><Search size={20}/></div>
                                )}
                             </div>
                             <div className="flex-1 min-w-0">
                                <h4 className="font-semibold text-zinc-900 truncate group-hover:text-primary transition-colors">{product.name}</h4>
                                <p className="text-sm text-zinc-500 truncate">{product.categoryName || "Uncategorized"}</p>
                             </div>
                             <div className="flex items-center gap-3">
                                <span className="font-bold text-zinc-900">${product.basePrice.toFixed(2)}</span>
                                <ArrowRight className="h-4 w-4 text-zinc-300 group-hover:text-primary transition-colors" />
                             </div>
                          </div>
                       </Link>
                    ))}
                 </div>
              )}

              {!isLoading && products?.length === 0 && debouncedTerm.length > 2 && (
                 <div className="text-center py-12">
                    <p className="text-zinc-500 text-lg">Hệ thống AI không tìm thấy sản phẩm nào phù hợp với miêu tả của bạn 😢</p>
                 </div>
              )}

              {!searchTerm && (
                 <div className="text-center py-10 opacity-70">
                    <Sparkles className="h-12 w-12 text-zinc-300 mx-auto mb-3" />
                    <p className="text-zinc-500">Mô tả bất cứ thứ gì bạn muốn tìm bằng ngôn ngữ tự nhiên.<br/>AI sẽ hiểu ý định của bạn thay vì chỉ tìm theo từ khóa.</p>
                 </div>
              )}
           </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
