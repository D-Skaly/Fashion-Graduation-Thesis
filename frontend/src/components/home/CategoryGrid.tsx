"use client";

import Link from "next/link";
import { ArrowUpRight, ImageOff } from "lucide-react";
import { motion } from "framer-motion";
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/axios";
import { Skeleton } from "@/components/ui/skeleton";
import { cn } from "@/lib/utils";

interface CategoryData {
    id: string;
    name: string;
    slug: string;
    description?: string;
    productCount?: number;
    imageUrl?: string;
}

const containerVariants = {
    hidden: { opacity: 0 },
    show: {
        opacity: 1,
        transition: {
            staggerChildren: 0.2
        }
    }
};

const itemVariants = {
    hidden: { opacity: 0, y: 50 },
    show: { opacity: 1, y: 0, transition: { duration: 0.8, ease: "easeOut" as const } }
};

function CategoryCard({ category, index }: { category: CategoryData; index: number }) {
    return (
        <motion.div variants={itemVariants} className="h-full w-full">
            <Link
                href={`/shop?category=${category.slug}`}
                className="group relative overflow-hidden rounded-2xl block h-full w-full shadow-sm hover:shadow-xl transition-all duration-500"
            >
                {/* Background Image */}
                {category.imageUrl ? (
                    <div
                        className="absolute inset-0 w-full h-full transition-transform duration-1000 ease-out group-hover:scale-110 bg-cover bg-center"
                        style={{ backgroundImage: `url(${category.imageUrl})` }}
                    />
                ) : (
                    <div className={cn(
                        "absolute inset-0 w-full h-full transition-transform duration-1000 ease-out group-hover:scale-110",
                        index === 0 ? "bg-slate-300 dark:bg-slate-800" :
                        index === 1 ? "bg-stone-300 dark:bg-stone-800" :
                        "bg-neutral-300 dark:bg-neutral-800"
                    )}>
                        <div className="absolute inset-0 flex items-center justify-center">
                            <ImageOff className="h-12 w-12 text-foreground/10" />
                        </div>
                    </div>
                )}

                {/* Overlay Gradient */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent opacity-60 transition-opacity duration-500 group-hover:opacity-80" />

                {/* Content */}
                <div className="absolute inset-0 p-8 flex flex-col justify-end text-white">
                    <div className="transform transition-transform duration-700 ease-out translate-y-6 group-hover:translate-y-0">
                        <div className="flex items-center justify-between mb-3 backdrop-blur-md bg-white/10 p-4 rounded-xl border border-white/20">
                            <h3 className="text-3xl font-black tracking-widest uppercase drop-shadow-md">
                                {category.name}
                            </h3>
                            <div className="h-10 w-10 rounded-full bg-white text-black flex items-center justify-center transform -translate-x-4 opacity-0 transition-all duration-500 group-hover:translate-x-0 group-hover:opacity-100">
                                <ArrowUpRight className="h-5 w-5" />
                            </div>
                        </div>
                        <p className="text-white/90 text-lg opacity-0 group-hover:opacity-100 transition-opacity duration-500 delay-150 px-4 font-light">
                            {category.description || `${category.productCount || 0} products`}
                        </p>
                    </div>
                </div>
            </Link>
        </motion.div>
    );
}

function CategorySkeleton() {
    return (
        <div className="h-full w-full">
            <div className="relative overflow-hidden rounded-2xl block h-full w-full bg-secondary animate-pulse">
                <div className="absolute inset-0 bg-gradient-to-t from-black/40 to-transparent" />
                <div className="absolute inset-0 p-8 flex flex-col justify-end">
                    <Skeleton className="h-12 w-2/3 bg-white/20 mb-2" />
                    <Skeleton className="h-4 w-1/2 bg-white/10" />
                </div>
            </div>
        </div>
    );
}

export function CategoryGrid() {
    const { data: categories, isLoading } = useQuery({
        queryKey: ["categories"],
        queryFn: async (): Promise<CategoryData[]> => {
            try {
                const { data } = await api.get("/categories");
                const items = data.data || [];
                // Map to our format if needed
                    return items.slice(0, 3).map((cat: Record<string, unknown>, idx: number) => ({
                    id: String(cat.id || String(idx)),
                    name: String(cat.name || ["MEN", "WOMEN", "ACCESSORIES"][idx]),
                    slug: String(cat.slug || String(cat.name || "").toLowerCase() || ["men", "women", "accessories"][idx]),
                    description: String(cat.description || [
                        "Sharp tailoring & casual essentials",
                        "Contemporary silhouettes for her",
                        "The perfect finishing touches"
                    ][idx]),
                    productCount: Number(cat.productCount || 0),
                    imageUrl: cat.imageUrl ? String(cat.imageUrl) : undefined,
                }));
            } catch {
                // Fallback
                return [
                    { id: "men", name: "MEN", slug: "men", description: "Sharp tailoring & casual essentials" },
                    { id: "women", name: "WOMEN", slug: "women", description: "Contemporary silhouettes for her" },
                    { id: "accessories", name: "ACCESSORIES", slug: "accessories", description: "The perfect finishing touches" },
                ];
            }
        },
    });

    return (
        <section className="container mx-auto px-4 py-16 md:py-24">
            <motion.div
                variants={containerVariants}
                initial="hidden"
                whileInView="show"
                viewport={{ once: true, margin: "-100px" }}
                className="grid grid-cols-1 md:grid-cols-3 gap-6 h-[500px] md:h-[700px]"
            >
                {isLoading
                    ? Array.from({ length: 3 }).map((_, i) => (
                        <CategorySkeleton key={i} />
                    ))
                    : categories?.map((category, index) => (
                        <CategoryCard key={category.id} category={category} index={index} />
                    ))}
            </motion.div>
        </section>
    );
}
