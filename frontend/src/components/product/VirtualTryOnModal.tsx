"use client";

import { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { Sparkles, Camera, Loader2, RefreshCw, CheckCircle2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { toast } from "sonner";
import api from "@/lib/axios";
import { cn } from "@/lib/utils";

interface VirtualTryOnModalProps {
    productId: string;
    productName: string;
    trigger?: React.ReactNode;
    className?: string;
}

type JobStatus = "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";

interface TryOnJob {
    id: string;
    status: JobStatus;
    resultImageUrl?: string;
    error?: string;
}

export function VirtualTryOnModal({ productId, productName, trigger, className }: VirtualTryOnModalProps) {
    const [isOpen, setIsOpen] = useState(false);
    const [userImageUrl, setUserImageUrl] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [job, setJob] = useState<TryOnJob | null>(null);

    // SSE Listener
    useEffect(() => {
        if (!isOpen || !job || job.status === "COMPLETED" || job.status === "FAILED") return;

        const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1';
        const eventSource = new EventSource(`${baseUrl}/tryon/stream`, {
            withCredentials: true
        });

        const handleUpdate = (event: MessageEvent) => {
            try {
                const updatedJob = JSON.parse(event.data);
                if (updatedJob.id === job.id) {
                    setJob(updatedJob);
                    if (updatedJob.status === "COMPLETED") {
                        toast.success("AI Try-On Completed!");
                        eventSource.close();
                    } else if (updatedJob.status === "FAILED") {
                        toast.error("AI Try-On Failed", { description: updatedJob.error });
                        eventSource.close();
                    }
                }
            } catch (err) {
                console.error("SSE Parse Error:", err);
            }
        };

        eventSource.addEventListener("TRY_ON_UPDATE", handleUpdate as any);

        eventSource.onerror = () => {
            console.error("SSE Connection Error");
            eventSource.close();
        };

        return () => {
            eventSource.removeEventListener("TRY_ON_UPDATE", handleUpdate as any);
            eventSource.close();
        };
    }, [isOpen, job]);

    const handleStartTryOn = async () => {
        if (!userImageUrl.trim()) {
            toast.error("Please provide a photo URL or upload an image");
            return;
        }

        setIsSubmitting(true);
        try {
            // Using searchParams as backend uses @RequestParam
            // The axios instance baseURL already contains /api/v1
            const response = await api.post(`/tryon?productId=${productId}&userImageUrl=${encodeURIComponent(userImageUrl)}`);
            setJob(response.data.data);
            toast.info("AI is processing your request...");
        } catch (error: any) {
            toast.error(error.response?.data?.message || "Failed to start AI Try-On");
        } finally {
            setIsSubmitting(false);
        }
    };

    const reset = () => {
        setJob(null);
        setUserImageUrl("");
    };

    return (
        <Dialog open={isOpen} onOpenChange={(open) => {
            setIsOpen(open);
            if (!open) reset();
        }}>
            <DialogTrigger asChild>
                {trigger || (
                    <Button variant="outline" className={cn("w-full gap-2 rounded-none h-12 border-primary/20 hover:border-primary transition-all group", className)}>
                        <Sparkles className="w-4 h-4 text-primary group-hover:animate-pulse" />
                        Try It On Virtually
                    </Button>
                )}
            </DialogTrigger>
            <DialogContent className="sm:max-w-[600px] bg-black text-white border-white/10 overflow-hidden">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2 text-2xl font-black uppercase tracking-widest text-white">
                        <Sparkles className="w-6 h-6 text-primary" />
                        AI Fitting Room
                    </DialogTitle>
                </DialogHeader>

                <div className="mt-6 space-y-8">
                    {!job ? (
                        <div className="space-y-6">
                            <div className="aspect-[4/5] rounded-2xl bg-white/5 border border-white/10 border-dashed flex flex-col items-center justify-center p-8 text-center gap-4 group hover:bg-white/10 transition-all cursor-pointer">
                                <div className="w-16 h-16 rounded-full bg-primary/10 flex items-center justify-center mb-2">
                                    <Camera className="w-8 h-8 text-primary" />
                                </div>
                                <div className="space-y-1">
                                    <p className="font-bold uppercase tracking-wider">Upload your photo</p>
                                    <p className="text-sm text-white/50 font-light">Show us your style to see the magic</p>
                                </div>
                                <Input 
                                    placeholder="Or paste image URL here..." 
                                    className="bg-black/50 border-white/10 text-white mt-4"
                                    value={userImageUrl}
                                    onChange={(e) => setUserImageUrl(e.target.value)}
                                />
                            </div>

                            <Button 
                                onClick={handleStartTryOn}
                                disabled={isSubmitting}
                                className="w-full h-14 rounded-none bg-primary text-primary-foreground font-black uppercase tracking-widest text-lg hover:scale-[1.02] transition-transform"
                            >
                                {isSubmitting ? <Loader2 className="animate-spin mr-2" /> : <Sparkles className="mr-2 h-5 w-5" />}
                                Generate AI Preview
                            </Button>
                        </div>
                    ) : (
                        <div className="space-y-6">
                            <div className="relative aspect-[4/5] rounded-2xl overflow-hidden bg-neutral-900 border border-white/10 shadow-2xl">
                                <AnimatePresence mode="wait">
                                    {job.status === "COMPLETED" && job.resultImageUrl ? (
                                        <motion.img 
                                            key="result"
                                            initial={{ opacity: 0 }}
                                            animate={{ opacity: 1 }}
                                            src={job.resultImageUrl}
                                            alt="Try-On Result"
                                            className="w-full h-full object-cover"
                                        />
                                    ) : (
                                        <div key="processing" className="w-full h-full flex flex-col items-center justify-center bg-neutral-950 p-12 text-center">
                                            {/* Original User Image under mask if available */}
                                            {userImageUrl && (
                                                <img src={userImageUrl} className="absolute inset-0 w-full h-full object-cover opacity-20 blur-sm" alt="User Base" />
                                            )}
                                            
                                            <div className="relative z-10 space-y-6">
                                                <div className="relative w-24 h-24 mx-auto">
                                                    <Loader2 className="w-full h-full text-primary animate-spin-slow opacity-20" />
                                                    <Sparkles className="absolute inset-0 m-auto w-10 h-10 text-primary animate-pulse" />
                                                </div>
                                                
                                                <div className="space-y-2">
                                                    <h3 className="text-xl font-bold uppercase tracking-widest">
                                                        {job.status === "PROCESSING" ? "Rendering style..." : "Analyzing features..."}
                                                    </h3>
                                                    <p className="text-sm text-white/50 font-light max-w-[250px]">
                                                        Our AI is fitting the <span className="text-white font-medium">{productName}</span> onto your silhouette.
                                                    </p>
                                                </div>

                                                {/* Scanning Bar */}
                                                <motion.div 
                                                    animate={{ y: [0, 300, 0] }}
                                                    transition={{ repeat: Infinity, duration: 3, ease: "linear" }}
                                                    className="absolute top-0 left-0 w-full h-1 bg-primary shadow-[0_0_20px_var(--primary)] z-20"
                                                />
                                            </div>
                                        </div>
                                    )}
                                </AnimatePresence>
                            </div>

                            <div className="flex gap-4">
                                <Button 
                                    variant="outline" 
                                    className="flex-1 rounded-none border-white/10 hover:bg-white/5 uppercase tracking-widest text-xs font-bold text-white"
                                    onClick={reset}
                                    disabled={job.status !== "COMPLETED" && job.status !== "FAILED"}
                                >
                                    <RefreshCw className="mr-2 h-4 w-4" /> Try another photo
                                </Button>
                                {job.status === "COMPLETED" && (
                                    <Button className="flex-1 rounded-none bg-white text-black hover:bg-white/90 uppercase tracking-widest text-xs font-bold">
                                        <CheckCircle2 className="mr-2 h-4 w-4" /> Looks Great
                                    </Button>
                                )}
                            </div>
                        </div>
                    )}

                    <div className="bg-white/5 p-4 rounded-xl border border-white/10">
                        <p className="text-[10px] text-white/40 uppercase tracking-widest text-center leading-relaxed">
                            Privacy Notice: Your photos are processed securely by our Fashion AI and are not stored permanently on our servers.
                        </p>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}
