"use client";

import { useMemo, useState, useRef, useEffect } from "react";
import { Bot, Loader2, Send, Sparkles } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
    Sheet,
    SheetContent,
    SheetDescription,
    SheetHeader,
    SheetTitle,
    SheetTrigger,
} from "@/components/ui/sheet";
import {
    Tooltip,
    TooltipContent,
    TooltipProvider,
    TooltipTrigger,
} from "@/components/ui/tooltip";
import api from "@/lib/axios";
import { cn } from "@/lib/utils";

interface ApiResponse<T> {
    status: number;
    message: string;
    data: T;
}

interface AiChatData {
    answer: string;
}

const MAX_HISTORY = 20;

interface ChatMessage {
    role: "user" | "assistant";
    content: string;
}

export function AiStylistFAB() {
    const [input, setInput] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    const [hasBounced, setHasBounced] = useState(false);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    const [messages, setMessages] = useState<ChatMessage[]>([
        {
            role: "assistant",
            content:
                "Xin chào! Mình là AI Stylist cá nhân của bạn. Bạn cần tìm trang phục cho dịp nào, hoặc muốn mình tư vấn phối đồ ra sao?",
        },
    ]);

    const canSend = useMemo(
        () => input.trim().length > 0 && !isLoading,
        [input, isLoading]
    );

    // One-time bounce on mount
    useEffect(() => {
        const timer = setTimeout(() => setHasBounced(true), 5000);
        return () => clearTimeout(timer);
    }, []);

    // Auto-scroll to bottom
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
        }
    }, [messages, isLoading]);

    const pushMessage = (message: ChatMessage) => {
        setMessages((prev) => [...prev, message].slice(-MAX_HISTORY));
    };

    const handleSend = async () => {
        const message = input.trim();
        if (!message || isLoading) return;

        pushMessage({ role: "user", content: message });
        setInput("");
        setIsLoading(true);

        try {
            const response = await api.post<ApiResponse<AiChatData>>("/ai/chat", {
                message,
            });
            pushMessage({
                role: "assistant",
                content: response.data.data.answer,
            });
        } catch (error) {
            const backendMessage = (
                error as { response?: { data?: { message?: string } } }
            )?.response?.data?.message;
            pushMessage({
                role: "assistant",
                content:
                    backendMessage ||
                    "Hiện chưa kết nối được AI service. Hệ thống đang bận, bạn thử lại sau nhé.",
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <TooltipProvider delayDuration={200}>
            <Sheet open={isOpen} onOpenChange={setIsOpen}>
                <Tooltip>
                    <TooltipTrigger asChild>
                        <SheetTrigger asChild>
                            <Button
                                size="icon"
                                className={cn(
                                    "fixed bottom-6 right-6 h-14 w-14 rounded-full shadow-2xl shadow-primary/30 z-50 bg-primary text-primary-foreground hover:scale-110 transition-transform duration-300",
                                    !hasBounced && "animate-bounce"
                                )}
                            >
                                <Sparkles className="h-6 w-6" />
                                <span className="sr-only">Open AI Stylist</span>
                            </Button>
                        </SheetTrigger>
                    </TooltipTrigger>
                    <TooltipContent side="left" className="hidden md:block">
                        <p>Chat with AI Stylist</p>
                    </TooltipContent>
                </Tooltip>

                <SheetContent className="flex w-full flex-col sm:max-w-md border-l-0 sm:rounded-l-2xl sm:border-l sm:h-[95vh] sm:mt-[2.5vh] sm:mr-[2.5vh] shadow-2xl">
                    <SheetHeader className="text-left border-b pb-4">
                        <SheetTitle className="flex items-center gap-2 text-xl font-bold">
                            <div className="bg-primary/10 p-2 rounded-full">
                                <Bot className="h-5 w-5 text-primary" />
                            </div>
                            AI Stylist
                        </SheetTitle>
                        <SheetDescription className="text-sm">
                            Tư vấn thờii trang, size đồ & tìm kiếm sản phẩm thông minh.
                        </SheetDescription>
                    </SheetHeader>

                    <div className="mt-4 flex-1 space-y-4 overflow-y-auto pr-2 custom-scrollbar">
                        {messages.map((msg, index) => (
                            <div
                                key={`${msg.role}-${index}`}
                                className={cn(
                                    "flex items-end gap-2 animate-reveal",
                                    msg.role === "user"
                                        ? "justify-end"
                                        : "justify-start"
                                )}
                                style={{ animationDelay: `${index * 0.05}s` }}
                            >
                                {msg.role === "assistant" && (
                                    <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0 mb-1">
                                        <Bot className="h-4 w-4 text-primary" />
                                    </div>
                                )}
                                <div
                                    className={cn(
                                        "max-w-[75%] rounded-2xl px-4 py-3 text-sm shadow-sm",
                                        msg.role === "user"
                                            ? "bg-primary text-primary-foreground rounded-br-sm"
                                            : "bg-muted rounded-bl-sm border border-border/50"
                                    )}
                                >
                                    {msg.content}
                                </div>
                            </div>
                        ))}
                        {isLoading && (
                            <div className="flex items-end gap-2 justify-start">
                                <div className="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center flex-shrink-0 mb-1">
                                    <Bot className="h-4 w-4 text-primary" />
                                </div>
                                <div className="bg-muted rounded-2xl rounded-bl-sm px-4 py-3 border border-border/50">
                                    <div className="flex gap-1">
                                        <span className="w-2 h-2 rounded-full bg-primary/40 animate-bounce"></span>
                                        <span
                                            className="w-2 h-2 rounded-full bg-primary/60 animate-bounce"
                                            style={{ animationDelay: "0.2s" }}
                                        ></span>
                                        <span
                                            className="w-2 h-2 rounded-full bg-primary animate-bounce"
                                            style={{ animationDelay: "0.4s" }}
                                        ></span>
                                    </div>
                                </div>
                            </div>
                        )}
                        <div ref={messagesEndRef} />
                    </div>

                    <div className="mt-4 flex gap-2 border-t pt-4">
                        <Input
                            placeholder="Hỏi AI bất kỳ điều gì..."
                            value={input}
                            className="rounded-full border-border/50 bg-muted/50 focus-visible:ring-1"
                            onChange={(e) => setInput(e.target.value)}
                            onKeyDown={(e) => {
                                if (e.key === "Enter") {
                                    e.preventDefault();
                                    handleSend();
                                }
                            }}
                        />
                        <Button
                            onClick={handleSend}
                            disabled={!canSend}
                            size="icon"
                            className="rounded-full h-10 w-10 shrink-0"
                        >
                            <Send className="h-4 w-4" />
                        </Button>
                    </div>
                </SheetContent>
            </Sheet>
        </TooltipProvider>
    );
}
