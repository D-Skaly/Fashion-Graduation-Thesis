"use client";

import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { Star } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";
import api from "@/lib/axios";

interface ReviewFormProps {
    productId: string;
    orderId?: string;
    onSuccess?: () => void;
}

export function ReviewForm({ productId, orderId, onSuccess }: ReviewFormProps) {
    const queryClient = useQueryClient();
    const [rating, setRating] = useState(0);
    const [hoverRating, setHoverRating] = useState(0);
    const [comment, setComment] = useState("");

    const mutation = useMutation({
        mutationFn: (data: { rating: number; comment: string; orderId?: string }) =>
            api.post("/reviews", { productId, ...data }),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["reviews", productId] });
            queryClient.invalidateQueries({ queryKey: ["product", productId] });
            toast.success("Đánh giá thành công", {
                description: "Cảm ơn bạn đã chia sẻ trải nghiệm!",
            });
            setRating(0);
            setComment("");
            onSuccess?.();
        },
        onError: () => {
            toast.error("Lỗi", {
                description: "Không thể gửi đánh giá. Vui lòng thử lại.",
            });
        },
    });

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (rating === 0) {
            toast.error("Vui lòng chọn số sao");
            return;
        }
        mutation.mutate({ rating, comment, orderId });
    };

    return (
        <Card>
            <CardHeader>
                <CardTitle>Viết đánh giá</CardTitle>
            </CardHeader>
            <CardContent>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className="space-y-2">
                        <Label>Đánh giá của bạn</Label>
                        <div className="flex gap-1">
                            {[1, 2, 3, 4, 5].map((star) => (
                                <button
                                    key={star}
                                    type="button"
                                    onClick={() => setRating(star)}
                                    onMouseEnter={() => setHoverRating(star)}
                                    onMouseLeave={() => setHoverRating(0)}
                                    className="focus:outline-none transition-transform hover:scale-110"
                                >
                                    <Star
                                        className={`h-6 w-6 ${
                                            star <= (hoverRating || rating)
                                                ? "fill-yellow-400 text-yellow-400"
                                                : "text-gray-300"
                                        }`}
                                    />
                                </button>
                            ))}
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="comment">Nhận xét của bạn</Label>
                        <Textarea
                            id="comment"
                            value={comment}
                            onChange={(e) => setComment(e.target.value)}
                            placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm này..."
                            rows={4}
                            maxLength={1000}
                        />
                        <p className="text-xs text-muted-foreground text-right">
                            {comment.length}/1000
                        </p>
                    </div>

                    <Button type="submit" disabled={mutation.isPending || rating === 0}>
                        {mutation.isPending ? "Đang gửi..." : "Gửi đánh giá"}
                    </Button>
                </form>
            </CardContent>
        </Card>
    );
}
