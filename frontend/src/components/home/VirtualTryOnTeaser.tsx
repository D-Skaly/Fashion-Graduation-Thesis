"use client";

import { motion } from "framer-motion";
import { Sparkles, Camera, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { VirtualTryOnModal } from "@/components/product/VirtualTryOnModal";
import { useQuery } from "@tanstack/react-query";
import api from "@/lib/axios";

export function VirtualTryOnTeaser() {
    const { data: products, isLoading } = useQuery({
        queryKey: ["featured-products-teaser"],
        queryFn: async () => {
            const { data } = await api.get("/products?size=1");
            return data.data.content;
        },
    });

    const demoProduct = products?.[0];

    return (
        <section className="py-24 bg-black text-white overflow-hidden relative">
            {/* Background elements */}
            <div className="absolute inset-0 opacity-20">
                <div className="absolute top-0 right-1/4 w-[500px] h-[500px] bg-primary/40 rounded-full blur-[100px]" />
                <div className="absolute bottom-0 left-1/4 w-[400px] h-[400px] bg-primary/20 rounded-full blur-[100px]" />
            </div>

            <div className="container mx-auto px-4 relative z-10">
                <div className="flex flex-col md:flex-row items-center justify-between gap-12">
                    <motion.div 
                        initial={{ opacity: 0, x: -50 }}
                        whileInView={{ opacity: 1, x: 0 }}
                        viewport={{ once: true }}
                        transition={{ duration: 0.8 }}
                        className="flex-1 space-y-8"
                    >
                        <div className="inline-flex items-center gap-2 px-4 py-2 rounded-full bg-primary/10 border border-primary/20 backdrop-blur-md">
                            <Sparkles className="w-4 h-4 text-primary" />
                            <span className="text-sm font-semibold tracking-widest uppercase text-primary">Live Now</span>
                        </div>
                        
                        <h2 className="text-4xl md:text-6xl font-black tracking-tighter uppercase leading-tight">
                            Try It On <br /> <span className="text-transparent bg-clip-text bg-gradient-to-r from-white to-white/40">Virtually</span>
                        </h2>
                        
                        <p className="text-lg text-white/70 max-w-md font-light leading-relaxed">
                            Upload your photo and let our AI show you exactly how our premium collection looks on you. The fitting room, reimagined.
                        </p>
                        
                        <div className="pt-4 max-w-xs">
                            {isLoading ? (
                                <Button disabled className="h-14 w-full rounded-full bg-white text-black">
                                    <Loader2 className="animate-spin mr-2" /> Initializing...
                                </Button>
                            ) : demoProduct ? (
                                <VirtualTryOnModal 
                                    productId={demoProduct.id} 
                                    productName={demoProduct.name} 
                                />
                            ) : (
                                <Button disabled className="h-14 w-full rounded-full bg-white text-black">
                                    Service Offline
                                </Button>
                            )}
                        </div>
                    </motion.div>

                    <motion.div 
                        initial={{ opacity: 0, scale: 0.9 }}
                        whileInView={{ opacity: 1, scale: 1 }}
                        viewport={{ once: true }}
                        transition={{ duration: 1 }}
                        className="flex-1 w-full max-w-lg relative"
                    >
                        {/* Placeholder for the Try-On UI Graphic */}
                        <div className="aspect-[4/5] rounded-3xl bg-gradient-to-b from-white/10 to-white/5 border border-white/10 backdrop-blur-xl p-6 relative overflow-hidden shadow-2xl">
                            <div className="absolute inset-0 bg-[url('https://www.transparenttextures.com/patterns/cubes.png')] opacity-10" />
                            
                            {/* Scanning Line Animation */}
                            <motion.div 
                                animate={{ y: ["0%", "400%", "0%"] }}
                                transition={{ repeat: Infinity, duration: 4, ease: "linear" }}
                                className="absolute top-0 left-0 w-full h-1 bg-primary/50 shadow-[0_0_15px_rgba(255,255,255,0.8)] z-20"
                            />

                            <div className="h-full w-full rounded-2xl border border-white/20 border-dashed flex flex-col items-center justify-center text-white/50 relative z-10 bg-black/40">
                                <Camera className="w-16 h-16 mb-4 opacity-50" />
                                <span className="font-semibold tracking-widest uppercase text-sm">AI Processing</span>
                            </div>
                        </div>
                        
                        {/* Floating Badges */}
                        <motion.div 
                            animate={{ y: [0, -10, 0] }}
                            transition={{ repeat: Infinity, duration: 3, ease: "easeInOut" }}
                            className="absolute -right-8 top-1/4 bg-white text-black p-4 rounded-2xl shadow-xl font-bold text-sm"
                        >
                            98% Match Rate
                        </motion.div>
                    </motion.div>
                </div>
            </div>
        </section>
    );
}
