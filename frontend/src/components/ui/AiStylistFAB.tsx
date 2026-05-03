"use client";

import { useMemo, useState, useRef, useEffect } from "react";
import { Bot, Send, Sparkles } from "lucide-react";
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
    TooltipTrigger,
} from "@/components/ui/tooltip";
import { TooltipProvider } from "@/components/ui/tooltip";
import { cn } from "@/lib/utils";

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
        
        // Push an empty assistant message to stream into
        setMessages((prev) => [...prev, { role: "assistant", content: "" } as ChatMessage].slice(-MAX_HISTORY));

        try {
            const baseUrl = process.env.NEXT_PUBLIC_API_BASE_URL || 'http://localhost:8080/api/v1';
            const response = await fetch(`${baseUrl}/ai/chat/stream`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    // Note: If auth is required, add authorization header here
                },
                body: JSON.stringify({ message }),
            });

            if (!response.ok) {
                throw new Error("Failed to connect to AI stream");
            }

            const reader = response.body?.getReader();
            const decoder = new TextDecoder();
            if (reader) {
                setIsLoading(false); // Streaming starts, stop initial loading spinner
                let aiResponse = "";
                while (true) {
                    const { done, value } = await reader.read();
                    if (done) break;
                    
                    const chunk = decoder.decode(value, { stream: true });
                    const lines = chunk.split('\n');
                    for (const line of lines) {
                        if (line.startsWith('data:')) {
                            let data = line.slice(5);
                            if (data.startsWith(' ')) data = data.slice(1);
                            
                            if (data) {
                                // Sometimes SSE chunks can contain literal "\n" strings if JSON encoded or just plain text
                                // If the backend sends raw tokens, we append them directly.
                                aiResponse += data;
                                setMessages((prev) => {
                                    const newMessages = [...prev];
                                    newMessages[newMessages.length - 1] = { role: "assistant", content: aiResponse } as ChatMessage;
                                    return newMessages;
                                });
                            }
                        }
                    }
                }
            }
        } catch (_error) {
            setMessages((prev) => {
                const newMessages = [...prev];
                newMessages[newMessages.length - 1] = {
                    role: "assistant",
                    content: "Hiện chưa kết nối được AI service. Hệ thống đang bận, bạn thử lại sau nhé.",
                } as ChatMessage;
                return newMessages;
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

                <SheetContent className="flex w-full flex-col sm:max-w-md border-l-0 sm:rounded-l-2xl sm:border-l sm:h-[95vh] sm:mt-[2.5vh] sm:mr-[2.5vh] shadow-2xl bg-black/60 backdrop-blur-2xl border-white/10 text-white">
                    <SheetHeader className="text-left border-b border-white/10 pb-4">
                        <SheetTitle className="flex items-center gap-2 text-xl font-bold text-white">
                            <div className="bg-primary/20 p-2 rounded-full">
                                <Bot className="h-5 w-5 text-primary" />
                            </div>
                            AI Stylist
                        </SheetTitle>
                        <SheetDescription className="text-sm text-white/60">
                            Tư vấn thời trang, size đồ & tìm kiếm sản phẩm thông minh.
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
                                    <div className="h-8 w-8 rounded-full bg-primary/20 flex items-center justify-center flex-shrink-0 mb-1">
                                        <Bot className="h-4 w-4 text-primary" />
                                    </div>
                                )}
                                <div
                                    className={cn(
                                        "max-w-[75%] rounded-2xl px-4 py-3 text-sm shadow-sm",
                                        msg.role === "user"
                                            ? "bg-primary text-primary-foreground rounded-br-sm"
                                            : "bg-white/10 backdrop-blur-md rounded-bl-sm border border-white/5 text-white"
                                    )}
                                >
                                    {msg.content}
                                </div>
                            </div>
                        ))}
                        {isLoading && (
                            <div className="flex items-end gap-2 justify-start">
                                <div className="h-8 w-8 rounded-full bg-primary/20 flex items-center justify-center flex-shrink-0 mb-1">
                                    <Bot className="h-4 w-4 text-primary" />
                                </div>
                                <div className="bg-white/10 backdrop-blur-md rounded-2xl rounded-bl-sm px-4 py-3 border border-white/5">
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

                    <div className="mt-4 flex gap-2 border-t border-white/10 pt-4">
                        <Input
                            placeholder="Hỏi AI bất kỳ điều gì..."
                            value={input}
                            className="rounded-full border-white/10 bg-white/5 text-white placeholder:text-white/40 focus-visible:ring-1 focus-visible:ring-primary"
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
