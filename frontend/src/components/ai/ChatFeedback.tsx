"use client";

import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { ThumbsUp, ThumbsDown, Send } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Card, CardContent } from "@/components/ui/card";
import { toast } from "sonner";
import api from "@/lib/axios";

interface ChatFeedbackProps {
    messageId: string;
}

export function ChatFeedback({ messageId }: ChatFeedbackProps) {
    const [feedback, setFeedback] = useState<"positive" | "negative" | null>(null);
    const [comment, setComment] = useState("");
    const [showComment, setShowComment] = useState(false);

    const mutation = useMutation({
        mutationFn: (data: { type: "positive" | "negative"; comment?: string }) =>
            api.post("/ai/chat/feedback", { messageId, ...data }),
        onSuccess: () => {
            toast.success("Cảm ơn phản hồi của bạn", {
                description: "Điều này giúp chúng tôi cải thiện dịch vụ.",
            });
            setFeedback(null);
            setComment("");
            setShowComment(false);
        },
        onError: () => {
            toast.error("Lỗi", {
                description: "Không thể gửi phản hồi.",
            });
        },
    });

    const handleFeedback = (type: "positive" | "negative") => {
        if (type === "positive") {
            mutation.mutate({ type });
        } else {
            setFeedback(type);
            setShowComment(true);
        }
    };

    const handleSubmit = () => {
        if (feedback) {
            mutation.mutate({ type: feedback, comment: comment || undefined });
        }
    };

    if (showComment) {
        return (
            <Card className="mt-2">
                <CardContent className="pt-4">
                    <p className="text-sm text-muted-foreground mb-2">
                        Bạn có thể thêm nhận xét để giúp chúng tôi cải thiện:
                    </p>
                    <Textarea
                        value={comment}
                        onChange={(e) => setComment(e.target.value)}
                        placeholder="Nhận xét của bạn..."
                        rows={2}
                        className="mb-2"
                    />
                    <div className="flex gap-2">
                        <Button
                            size="sm"
                            onClick={handleSubmit}
                            disabled={mutation.isPending}
                        >
                            {mutation.isPending ? (
                                <span>Đang gửi...</span>
                            ) : (
                                <>
                                    <Send className="h-4 w-4 mr-1" />
                                    Gửi
                                </>
                            )}
                        </Button>
                        <Button
                            size="sm"
                            variant="ghost"
                            onClick={() => {
                                setFeedback(null);
                                setShowComment(false);
                                setComment("");
                            }}
                        >
                            Hủy
                        </Button>
                    </div>
                </CardContent>
            </Card>
        );
    }

    return (
        <div className="flex gap-2 mt-2">
            <Button
                size="sm"
                variant="ghost"
                onClick={() => handleFeedback("positive")}
                disabled={mutation.isPending}
                className={feedback === "positive" ? "text-green-600" : ""}
            >
                <ThumbsUp className="h-4 w-4" />
            </Button>
            <Button
                size="sm"
                variant="ghost"
                onClick={() => handleFeedback("negative")}
                disabled={mutation.isPending}
                className={feedback === "negative" ? "text-red-600" : ""}
            >
                <ThumbsDown className="h-4 w-4" />
            </Button>
        </div>
    );
}
