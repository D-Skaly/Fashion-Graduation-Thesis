"use client";

import { useState } from "react";
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Sparkles, Upload, RefreshCw } from "lucide-react";
import Image from "next/image";
import api from "@/lib/axios";
import { toast } from "sonner";

interface VirtualTryOnProps {
    productImage: string;
    productName: string;
    productId: string;
}

export function VirtualTryOn({ productImage, productName, productId }: VirtualTryOnProps) {
    const [isOpen, setIsOpen] = useState(false);
    const [userImage, setUserImage] = useState<string | null>(null);
    const [userImageFile, setUserImageFile] = useState<File | null>(null);
    const [isGenerating, setIsGenerating] = useState(false);
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [isUploading, setIsUploading] = useState(false);
    const [resultImage, setResultImage] = useState<string | null>(null);
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    const [jobId, setJobId] = useState<string | null>(null);

    const handleUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            const file = e.target.files[0];
            setUserImage(URL.createObjectURL(file));
            setUserImageFile(file);
            setResultImage(null);
            setJobId(null);
        }
    };

    const uploadImage = async (file: File): Promise<string> => {
        const formData = new FormData();
        formData.append("file", file);
        
        try {
            const response = await api.post("/images/upload?folder=tryon", formData, {
                headers: { "Content-Type": "multipart/form-data" },
            });
            return response.data.data.url;
        } catch (error) {
            console.error("Failed to upload image", error);
            throw new Error("Failed to upload image");
        }
    };

    const handleTryOn = async () => {
        if (!userImageFile) return;
        setIsGenerating(true);
        
        try {
            // Step 1: Upload image to get URL
            setIsUploading(true);
            const imageUrl = await uploadImage(userImageFile);
            setIsUploading(false);

            // Step 2: Create Try-On job
            const response = await api.post("/tryon", null, {
                params: {
                    productId: productId,
                    userImageUrl: imageUrl,
                },
            });

            const jobId = response.data.data.id;
            setJobId(jobId);

            // Step 3: Poll for result
            const pollInterval = setInterval(async () => {
                try {
                    const statusResponse = await api.get(`/tryon/${jobId}`);
                    const job = statusResponse.data.data;

                    if (job.status === "COMPLETED") {
                        clearInterval(pollInterval);
                        setResultImage(job.resultImageUrl);
                        setIsGenerating(false);
                        toast.success("Virtual Try-On completed!");
                    } else if (job.status === "FAILED") {
                        clearInterval(pollInterval);
                        setIsGenerating(false);
                        toast.error("Try-On failed. Please try again.");
                    }
                } catch (error) {
                    console.error("Failed to check job status", error);
                }
            }, 2000);

            // Timeout after 60 seconds
            setTimeout(() => {
                clearInterval(pollInterval);
                if (isGenerating) {
                    setIsGenerating(false);
                    toast.error("Try-On is taking too long. Please try again.");
                }
            }, 60000);

        } catch (error) {
            console.error("Try-On failed", error);
            setIsGenerating(false);
            setIsUploading(false);
            toast.error("Failed to start Virtual Try-On. Please try again.");
        }
    };

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
            <DialogTrigger asChild>
                <Button 
                    variant="secondary" 
                    className="w-full h-12 rounded-xl bg-primary/5 hover:bg-primary/10 text-primary border border-primary/20 transition-all group"
                >
                    <span className="mr-2 group-hover:animate-pulse">✨</span>
                    Virtual Try-on
                </Button>
            </DialogTrigger>
            <DialogContent className="sm:max-w-[800px] p-0 overflow-hidden bg-background border-border/50">
                <div className="absolute top-0 right-1/4 w-64 h-64 bg-primary/10 rounded-full blur-[80px] pointer-events-none" />
                <div className="absolute bottom-0 left-1/4 w-48 h-48 bg-secondary/30 rounded-full blur-[60px] pointer-events-none" />
                
                <div className="p-6 sm:p-8 relative z-10">
                    <DialogHeader className="mb-6">
                        <DialogTitle className="flex items-center gap-2 text-2xl font-bold tracking-tight">
                            <Sparkles className="h-6 w-6 text-primary" /> 
                            AI Virtual Try-on
                        </DialogTitle>
                        <DialogDescription className="text-base text-muted-foreground">
                            Upload your photo to see how <strong className="text-foreground">{productName}</strong> looks on you before buying.
                        </DialogDescription>
                    </DialogHeader>

                    <div className="grid md:grid-cols-2 gap-6 items-start">
                        {/* Step 1: User Photo */}
                        <div className="space-y-4">
                            <h3 className="font-semibold text-sm uppercase tracking-wider text-muted-foreground flex items-center gap-2">
                                <span className="bg-primary/10 text-primary w-5 h-5 rounded-full flex items-center justify-center text-xs">1</span> 
                                Your Photo
                            </h3>
                            
                            <div className="relative aspect-[3/4] w-full rounded-2xl border-2 border-dashed border-border/60 bg-secondary/10 flex flex-col items-center justify-center overflow-hidden group hover:border-primary/50 transition-colors">
                                {userImage ? (
                                    <>
                                        <Image src={userImage} alt="User" fill className="object-cover" />
                                        <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                                            <label className="cursor-pointer">
                                                <div className="bg-white/20 backdrop-blur-md text-white px-4 py-2 rounded-full font-medium text-sm flex items-center gap-2">
                                                    <RefreshCw className="h-4 w-4" /> Change Photo
                                                </div>
                                                <input type="file" className="hidden" accept="image/*" onChange={handleUpload} />
                                            </label>
                                        </div>
                                    </>
                                ) : (
                                    <label className="cursor-pointer flex flex-col items-center justify-center w-full h-full p-6 text-center">
                                        <div className="h-16 w-16 rounded-full bg-background shadow-sm flex items-center justify-center mb-4 group-hover:scale-110 transition-transform">
                                            <Upload className="h-6 w-6 text-primary" />
                                        </div>
                                        <p className="font-medium text-foreground mb-1">Click to upload photo</p>
                                        <p className="text-xs text-muted-foreground">JPEG, PNG up to 10MB</p>
                                        <p className="text-xs text-muted-foreground mt-2">Full body photo works best</p>
                                        <input type="file" className="hidden" accept="image/*" onChange={handleUpload} />
                                    </label>
                                )}
                            </div>
                        </div>

                        {/* Step 2: Result */}
                        <div className="space-y-4">
                            <h3 className="font-semibold text-sm uppercase tracking-wider text-muted-foreground flex items-center gap-2">
                                <span className="bg-primary/10 text-primary w-5 h-5 rounded-full flex items-center justify-center text-xs">2</span> 
                                AI Result
                            </h3>
                            
                            <div className="relative aspect-[3/4] w-full rounded-2xl border border-border/50 bg-secondary/20 flex flex-col items-center justify-center overflow-hidden">
                                {!userImage && !resultImage && (
                                    <div className="text-center p-6 opacity-50">
                                        <div className="h-32 w-24 border-2 border-dashed border-primary/30 rounded-xl mx-auto mb-4" />
                                        <p className="text-sm font-medium">Upload a photo first</p>
                                    </div>
                                )}
                                
                                {userImage && !resultImage && !isGenerating && (
                                    <div className="text-center p-6 animate-in zoom-in-95 duration-300">
                                        <Image src={productImage} alt={productName} width={80} height={100} className="rounded-lg object-cover shadow-lg mx-auto mb-6" />
                                        <Button 
                                            onClick={handleTryOn} 
                                            className="rounded-full px-8 h-12 shadow-xl bg-primary hover:bg-primary/90 text-primary-foreground font-semibold tracking-wide"
                                        >
                                            Generate Try-on
                                        </Button>
                                    </div>
                                )}

                                {isGenerating && (
                                    <div className="absolute inset-0 bg-background/80 backdrop-blur-sm flex flex-col items-center justify-center z-10">
                                        <div className="relative w-20 h-20 mb-6">
                                            <div className="absolute inset-0 border-4 border-primary/20 rounded-full" />
                                            <div className="absolute inset-0 border-4 border-primary rounded-full border-t-transparent animate-spin" />
                                            <div className="absolute inset-0 flex items-center justify-center">
                                                <Sparkles className="h-6 w-6 text-primary animate-pulse" />
                                            </div>
                                        </div>
                                        <p className="font-medium text-lg mb-1 tracking-tight">AI is working its magic...</p>
                                        <p className="text-sm text-muted-foreground">Fitting {productName}</p>
                                        
                                        <div className="w-48 h-1.5 bg-secondary rounded-full mt-6 overflow-hidden">
                                            <div className="h-full bg-primary rounded-full w-1/2 animate-[pulse_2s_ease-in-out_infinite]" style={{ width: '60%', animation: 'progress 3s ease-in-out infinite' }} />
                                        </div>
                                    </div>
                                )}

                                {resultImage && !isGenerating && (
                                    <div className="absolute inset-0 animate-in fade-in duration-700">
                                        <Image src={resultImage} alt="Try-on Result" fill className="object-cover" />
                                        <div className="absolute top-4 right-4 bg-background/80 backdrop-blur-md px-3 py-1.5 rounded-full text-xs font-semibold shadow-sm flex items-center gap-1.5 border border-border/50">
                                            <Sparkles className="h-3 w-3 text-primary" /> AI Generated
                                        </div>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    );
}
