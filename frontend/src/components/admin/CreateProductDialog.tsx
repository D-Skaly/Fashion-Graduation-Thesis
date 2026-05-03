"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Loader2, Plus, Sparkles, Image as ImageIcon, Box, Activity } from "lucide-react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import api from "@/lib/axios";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { ImageUploader } from "./ImageUploader";
import { ScrollArea } from "@/components/ui/scroll-area";

const productSchema = z.object({
  name: z.string().min(2, "Name must be at least 2 characters."),
  description: z.string().min(10, "Description must be at least 10 characters."),
  basePrice: z.string().min(1, "Price is required"),
  categoryId: z.string().min(1, "Category is required"),
  featured: z.boolean().default(false),
});

type ProductFormValues = z.infer<typeof productSchema>;

interface Category {
    id: string;
    name: string;
}

export function CreateProductDialog() {
  const [open, setOpen] = useState(false);
  const [images, setImages] = useState<string[]>([]);
  const queryClient = useQueryClient();

  const { data: categories } = useQuery<Category[]>({
    queryKey: ["categories"],
    queryFn: async () => {
        const response = await api.get("/categories");
        if (Array.isArray(response.data)) return response.data;
        if (response.data?.content) return response.data.content;
        return [];
    },
    enabled: open,
  });

  const form = useForm<ProductFormValues>({
    resolver: zodResolver(productSchema),
    defaultValues: {
      name: "",
      description: "",
      basePrice: "",
      categoryId: "",
      featured: false,
    },
  });

  const mutation = useMutation({
    mutationFn: async (values: ProductFormValues) => {
      const payload = {
        name: values.name,
        description: values.description,
        basePrice: parseFloat(values.basePrice),
        categoryId: parseInt(values.categoryId),
        images: images,
        featured: values.featured,
      };
      const response = await api.post("/products", payload);
      return response.data;
    },
    onSuccess: () => {
      toast.success("New product genotype synthesized successfully");
      queryClient.invalidateQueries({ queryKey: ["admin-products"] });
      setOpen(false);
      form.reset();
      setImages([]);
    },
    onError: (error: unknown) => {
        const axiosError = error as { response?: { data?: { message?: string } } };
        toast.error(axiosError.response?.data?.message || "Synthesis failure");
    }
  });

  function onSubmit(values: ProductFormValues) {
    if (images.length === 0) {
        toast.error("At least one visual asset is required for synthesis");
        return;
    }
    mutation.mutate(values);
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button className="bg-primary hover:bg-primary/90 text-white rounded-xl h-11 px-6 font-black uppercase tracking-widest text-[11px] gap-2 shadow-lg shadow-primary/20 transition-all active:scale-95">
          <Plus className="h-4 w-4" /> Add New Asset
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[700px] bg-zinc-950 border-zinc-800 text-zinc-100 p-0 overflow-hidden rounded-3xl shadow-2xl">
        <DialogHeader className="p-8 pb-0 flex flex-row items-center justify-between">
          <div className="flex items-center gap-4">
              <div className="h-14 w-14 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center">
                  <Box className="h-7 w-7 text-primary" />
              </div>
              <div>
                  <DialogTitle className="text-2xl font-black uppercase tracking-tight text-white">Asset Synthesis</DialogTitle>
                  <DialogDescription className="text-zinc-500 font-bold uppercase tracking-widest text-[10px] mt-1">
                    Initializing new product genotype in the global registry
                  </DialogDescription>
              </div>
          </div>
        </DialogHeader>
        
        <ScrollArea className="max-h-[80vh]">
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="p-8 pt-6 space-y-8">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                    {/* Basic Info */}
                    <div className="space-y-6">
                        <div className="flex items-center gap-2 text-zinc-400">
                            <Activity className="h-3.5 w-3.5" />
                            <span className="text-[10px] font-black uppercase tracking-widest">Core Information</span>
                        </div>
                        
                        <FormField
                          control={form.control}
                          name="name"
                          render={({ field }) => (
                            <FormItem>
                              <FormLabel className="text-[10px] font-black uppercase tracking-widest text-zinc-500">Asset Identity</FormLabel>
                              <FormControl>
                                <Input placeholder="E.g. Neural Mesh Parka" className="h-12 bg-zinc-900 border-zinc-800 text-white rounded-xl focus:border-primary/50" {...field} />
                              </FormControl>
                              <FormMessage />
                            </FormItem>
                          )}
                        />

                        <div className="grid grid-cols-2 gap-4">
                            <FormField
                              control={form.control}
                              name="categoryId"
                              render={({ field }) => (
                                <FormItem>
                                  <FormLabel className="text-[10px] font-black uppercase tracking-widest text-zinc-500">Classification</FormLabel>
                                  <Select onValueChange={field.onChange} defaultValue={field.value}>
                                    <FormControl>
                                      <SelectTrigger className="h-12 bg-zinc-900 border-zinc-800 text-white rounded-xl">
                                        <SelectValue placeholder="Select Category" />
                                      </SelectTrigger>
                                    </FormControl>
                                    <SelectContent className="bg-zinc-900 border-zinc-800 text-white rounded-xl">
                                      {categories?.map((cat) => (
                                        <SelectItem key={cat.id} value={cat.id.toString()} className="focus:bg-zinc-800 rounded-lg">{cat.name}</SelectItem>
                                      ))}
                                    </SelectContent>
                                  </Select>
                                  <FormMessage />
                                </FormItem>
                              )}
                            />

                            <FormField
                              control={form.control}
                              name="basePrice"
                              render={({ field }) => (
                                <FormItem>
                                  <FormLabel className="text-[10px] font-black uppercase tracking-widest text-zinc-500">Market Value ($)</FormLabel>
                                  <FormControl>
                                    <Input type="number" step="0.01" className="h-12 bg-zinc-900 border-zinc-800 text-white rounded-xl" {...field} />
                                  </FormControl>
                                  <FormMessage />
                                </FormItem>
                              )}
                            />
                        </div>

                        <FormField
                          control={form.control}
                          name="description"
                          render={({ field }) => (
                            <FormItem>
                              <FormLabel className="text-[10px] font-black uppercase tracking-widest text-zinc-500">Genomic Traits</FormLabel>
                              <FormControl>
                                <Textarea 
                                    placeholder="Describe material composition and aesthetic DNA..." 
                                    className="bg-zinc-900 border-zinc-800 text-white rounded-xl min-h-[160px] resize-none" 
                                    {...field} 
                                />
                              </FormControl>
                              <FormMessage />
                            </FormItem>
                          )}
                        />
                    </div>

                    {/* Visuals & Status */}
                    <div className="space-y-6">
                        <div className="flex items-center gap-2 text-zinc-400">
                            <ImageIcon className="h-3.5 w-3.5" />
                            <span className="text-[10px] font-black uppercase tracking-widest">Visual Genome</span>
                        </div>
                        
                        <div className="bg-zinc-900/50 border border-zinc-800 p-4 rounded-2xl min-h-[300px]">
                            <ImageUploader 
                                images={images} 
                                onImagesChange={setImages} 
                                maxImages={6}
                            />
                        </div>

                        <div className="space-y-4 pt-4">
                            <div className="flex items-center gap-2 text-zinc-400">
                                <Sparkles className="h-3.5 w-3.5" />
                                <span className="text-[10px] font-black uppercase tracking-widest">System Overrides</span>
                            </div>
                            <FormField
                              control={form.control}
                              name="featured"
                              render={({ field }) => (
                                <div className="flex items-center justify-between p-4 bg-zinc-900 border border-zinc-800 rounded-xl">
                                    <div className="flex flex-col">
                                        <span className="text-xs font-black text-white uppercase">Peak Priority Status</span>
                                        <span className="text-[10px] text-zinc-500 font-bold uppercase tracking-widest mt-0.5">Mark as a featured asset</span>
                                    </div>
                                    <input 
                                        type="checkbox" 
                                        className="h-5 w-5 rounded-md border-zinc-700 bg-zinc-800 text-primary focus:ring-primary focus:ring-offset-zinc-900"
                                        checked={field.value}
                                        onChange={field.onChange}
                                    />
                                </div>
                              )}
                            />
                        </div>
                    </div>
                </div>

                <div className="pt-8 border-t border-zinc-800 flex items-center justify-end gap-4">
                  <Button 
                    type="button" 
                    variant="ghost" 
                    onClick={() => setOpen(false)}
                    className="h-12 px-8 text-zinc-500 hover:text-white font-black uppercase tracking-widest text-[11px]"
                  >
                      Abort Initialization
                  </Button>
                  <Button 
                    type="submit" 
                    disabled={mutation.isPending}
                    className="bg-primary text-white font-black uppercase tracking-widest px-12 h-12 rounded-xl shadow-lg shadow-primary/20"
                  >
                    {mutation.isPending ? (
                        <Loader2 className="h-5 w-5 animate-spin" />
                    ) : (
                        "Confirm Synthesis"
                    )}
                  </Button>
                </div>
              </form>
            </Form>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
