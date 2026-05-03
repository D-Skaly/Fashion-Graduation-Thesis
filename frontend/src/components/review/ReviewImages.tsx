"use client";

import { useState, useCallback } from "react";
import { X, Upload } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

interface ReviewImagesProps {
    images: string[];
    onImagesChange: (images: string[]) => void;
    maxImages?: number;
}

export function ReviewImages({ images, onImagesChange, maxImages = 5 }: ReviewImagesProps) {
    const [isDragging, setIsDragging] = useState(false);

    const handleFiles = useCallback((files: File[]) => {
        const remainingSlots = maxImages - images.length;
        const filesToAdd = files.slice(0, remainingSlots);

        filesToAdd.forEach(file => {
            if (file.type.startsWith("image/")) {
                const reader = new FileReader();
                reader.onloadend = () => {
                    onImagesChange([...images, reader.result as string]);
                };
                reader.readAsDataURL(file);
            }
        });
    }, [images, onImagesChange, maxImages]);

    const handleDragOver = useCallback((e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(true);
    }, []);

    const handleDragLeave = useCallback((e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
    }, []);

    const handleDrop = useCallback((e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
        
        const files = Array.from(e.dataTransfer.files);
        handleFiles(files);
    }, [handleFiles]);

    const handleFileInput = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
        const files = Array.from(e.target.files || []);
        handleFiles(files);
    }, [handleFiles]);

    const removeImage = (index: number) => {
        onImagesChange(images.filter((_, i) => i !== index));
    };

    return (
        <div className="space-y-4">
            <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-4">
                {images.map((image, index) => (
                    <Card key={index} className="relative overflow-hidden group">
                        <CardContent className="p-0">
                            <div className="aspect-square relative">
                                <img
                                    src={image}
                                    alt={`Review image ${index + 1}`}
                                    className="w-full h-full object-cover"
                                />
                                <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center">
                                    <Button
                                        size="sm"
                                        variant="destructive"
                                        onClick={() => removeImage(index)}
                                    >
                                        <X className="h-4 w-4" />
                                    </Button>
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                ))}
                
                {images.length < maxImages && (
                    <Card className={`border-2 border-dashed ${isDragging ? "border-primary bg-primary/5" : "border-input"}`}>
                        <CardContent className="p-0">
                            <div
                                className="aspect-square flex flex-col items-center justify-center cursor-pointer hover:bg-secondary/50 transition-colors"
                                onDragOver={handleDragOver}
                                onDragLeave={handleDragLeave}
                                onDrop={handleDrop}
                                onClick={() => document.getElementById("review-image-input")?.click()}
                            >
                                <Upload className="h-8 w-8 text-muted-foreground mb-2" />
                                <p className="text-sm text-muted-foreground text-center px-2">
                                    {isDragging ? "Thả ảnh vào đây" : "Tải ảnh lên"}
                                </p>
                                <input
                                    id="review-image-input"
                                    type="file"
                                    accept="image/*"
                                    multiple
                                    className="hidden"
                                    onChange={handleFileInput}
                                />
                            </div>
                        </CardContent>
                    </Card>
                )}
            </div>
            
            {images.length >= maxImages && (
                <p className="text-sm text-muted-foreground">
                    Tối đa {maxImages} ảnh
                </p>
            )}
        </div>
    );
}
