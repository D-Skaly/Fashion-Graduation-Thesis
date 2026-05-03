"use client";

import * as React from "react";
import Autoplay from "embla-carousel-autoplay";
import { ArrowRight, Sparkles, ChevronDown } from "lucide-react";
import { motion } from "framer-motion";

import { Button } from "@/components/ui/button";
import { VirtualTryOnModal } from "@/components/product/VirtualTryOnModal";
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/axios";
import {
    Carousel,
    CarouselContent,
    CarouselItem,
    CarouselNext,
    CarouselPrevious,
} from "@/components/ui/carousel";

const banners = [
    {
        id: 1,
        title: "NEW COLLECTION 2026",
        subtitle: "The Future of Fashion is Here",
        description: "Experience the perfect blend of sustainable materials and avant-garde design. Our 2026 collection redefines luxury for the modern era.",
        cta: "Shop Now",
        bgClass: "bg-gradient-to-tr from-stone-300 via-stone-100 to-stone-400 dark:from-stone-900 dark:via-stone-950 dark:to-stone-900",
        hasAIBadge: true
    },
    {
        id: 2,
        title: "URBAN MINIMALISM",
        subtitle: "Effortless Style for City Life",
        description: "Clean lines, neutral tones, and premium fabrics. Elevate your everyday wardrobe with our curated urban essentials.",
        cta: "Explore Urban",
        bgClass: "bg-gradient-to-tr from-neutral-200 via-white to-neutral-300 dark:from-neutral-800 dark:via-black dark:to-neutral-900",
    },
    {
        id: 3,
        title: "SUMMER EDIT",
        subtitle: "Sun-Kissed Sophistication",
        description: "Lightweight linens and breezy silhouettes ready for your next getaway. Embrace the season in comfort and style.",
        cta: "View Summer",
        bgClass: "bg-gradient-to-tr from-orange-100 via-amber-50 to-orange-200 dark:from-orange-950/40 dark:via-background dark:to-amber-900/40",
    },
];

export function HeroBanner() {
    const plugin = React.useRef(
        Autoplay({ delay: 6000, stopOnInteraction: true })
    );

    const { data: products } = useQuery({
        queryKey: ["featured-products-hero"],
        queryFn: async () => {
            const { data } = await api.get("/products?size=1");
            return data.data.content;
        },
    });

    const demoProduct = products?.[0];

    const scrollToContent = () => {
        window.scrollTo({ top: window.innerHeight * 0.85, behavior: "smooth" });
    };

    return (
        <section className="w-full relative group">
            <Carousel
                plugins={[plugin.current]}
                className="w-full"
                onMouseEnter={plugin.current.stop}
                onMouseLeave={plugin.current.reset}
            >
                <CarouselContent>
                    {banners.map((banner) => (
                        <CarouselItem key={banner.id}>
                            <div className={`relative h-[calc(100dvh-4rem)] min-h-[600px] w-full flex items-center justify-center overflow-hidden ${banner.bgClass}`}>
                                {/* Premium Abstract Overlay Shapes */}
                                <div className="absolute inset-0 opacity-40 mix-blend-multiply dark:mix-blend-screen">
                                    <div className="absolute top-1/4 left-1/4 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-white/40 dark:bg-primary/5 rounded-full blur-[100px] animate-pulse" />
                                    <div className="absolute bottom-1/4 right-1/4 translate-x-1/4 translate-y-1/4 w-[800px] h-[800px] bg-white/30 dark:bg-secondary/10 rounded-full blur-[120px]" />
                                </div>

                                {/* Noise Texture Overlay */}
                                <div className="absolute inset-0 opacity-[0.03] dark:opacity-[0.05] pointer-events-none"
                                    style={{
                                        backgroundImage: `url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)'/%3E%3C/svg%3E")`,
                                    }}
                                />

                                <div className="container relative z-10 px-4 md:px-6 flex flex-col items-center text-center space-y-6 max-w-4xl">
                                    <motion.div
                                        initial={{ opacity: 0, y: 20 }}
                                        whileInView={{ opacity: 1, y: 0 }}
                                        transition={{ duration: 0.8, delay: 0.2 }}
                                        viewport={{ once: true }}
                                        className="flex flex-col items-center gap-4"
                                    >
                                        {banner.hasAIBadge && (
                                            <span className="flex items-center gap-1.5 rounded-full bg-primary/10 text-primary px-3 py-1 text-xs font-semibold tracking-widest uppercase border border-primary/20 shadow-sm">
                                                <Sparkles className="h-3.5 w-3.5" /> AI Curated
                                            </span>
                                        )}
                                        <span className="inline-block rounded-full bg-background/60 px-5 py-2 text-sm font-semibold tracking-[0.2em] backdrop-blur-md uppercase shadow-sm border border-border/50">
                                            {banner.subtitle}
                                        </span>
                                    </motion.div>
                                    <motion.h1
                                        initial={{ opacity: 0, y: 30 }}
                                        whileInView={{ opacity: 1, y: 0 }}
                                        transition={{ duration: 1, delay: 0.4 }}
                                        viewport={{ once: true }}
                                        className="text-4xl md:text-7xl lg:text-8xl font-black tracking-tighter text-foreground uppercase drop-shadow-sm text-balance"
                                    >
                                        {banner.title}
                                    </motion.h1>
                                    <motion.p
                                        initial={{ opacity: 0 }}
                                        whileInView={{ opacity: 1 }}
                                        transition={{ duration: 1, delay: 0.6 }}
                                        viewport={{ once: true }}
                                        className="text-base md:text-2xl text-foreground/80 max-w-[700px] leading-relaxed font-light text-balance px-4"
                                    >
                                        {banner.description}
                                    </motion.p>
                                    <div className="pt-4 flex flex-col sm:flex-row gap-4 justify-center">
                                        <Button size="lg" className="h-14 px-10 text-lg rounded-full gap-3 transition-all duration-300 hover:scale-105 hover:shadow-xl hover:shadow-primary/20 group/btn">
                                            {banner.cta} <ArrowRight className="h-5 w-5 transition-transform group-hover/btn:translate-x-1" />
                                        </Button>

                                        {banner.hasAIBadge && demoProduct && (
                                            <VirtualTryOnModal
                                                productId={demoProduct.id}
                                                productName={demoProduct.name}
                                                trigger={
                                                    <Button variant="outline" size="lg" className="h-14 px-10 text-lg rounded-full gap-3 border-foreground/20 hover:bg-foreground/5 dark:border-white/20 dark:hover:bg-white/5 transition-all duration-300">
                                                        <Sparkles className="h-5 w-5 text-primary" /> AI Try On
                                                    </Button>
                                                }
                                            />
                                        )}
                                    </div>
                                </div>
                            </div>
                        </CarouselItem>
                    ))}
                </CarouselContent>
                <div className="hidden md:block opacity-0 group-hover:opacity-100 transition-opacity duration-500">
                    <CarouselPrevious className="absolute left-8 top-1/2 -translate-y-1/2 border-border/50 bg-background/30 hover:bg-background/70 backdrop-blur-md h-14 w-14 shadow-lg" />
                    <CarouselNext className="absolute right-8 top-1/2 -translate-y-1/2 border-border/50 bg-background/30 hover:bg-background/70 backdrop-blur-md h-14 w-14 shadow-lg" />
                </div>
            </Carousel>

            {/* Scroll Indicator */}
            <button
                onClick={scrollToContent}
                className="absolute bottom-8 left-1/2 -translate-x-1/2 z-20 flex flex-col items-center gap-2 text-foreground/50 hover:text-foreground transition-colors cursor-pointer"
                aria-label="Scroll to content"
            >
                <span className="text-[10px] uppercase tracking-[0.3em] font-medium">Scroll</span>
                <ChevronDown className="h-5 w-5 animate-scroll-indicator" />
            </button>
        </section>
    );
}
