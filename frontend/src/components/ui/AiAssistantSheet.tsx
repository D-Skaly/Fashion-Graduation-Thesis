"use client";

import { useState } from "react";
import { Bot, Loader2, MessageCircle, Send, User } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import api from "@/lib/axios";

interface ApiResponse<T> {
  status: number;
  message: string;
  data: T;
}

interface AiChatData {
  answer: string;
}

interface ChatMessage {
  role: "user" | "assistant";
  content: string;
}

export function AiAssistantSheet() {
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [messages, setMessages] = useState<ChatMessage[]>([
    {
      role: "assistant",
      content: "Xin chào! Mình là stylist AI. Bạn muốn phối đồ theo dịp nào hôm nay?",
    },
  ]);

  const handleSend = async () => {
    const message = input.trim();
    if (!message || isLoading) return;

    setMessages((prev) => [...prev, { role: "user", content: message }]);
    setInput("");
    setIsLoading(true);

    try {
      const response = await api.post<ApiResponse<AiChatData>>("/ai/chat", { message });
      setMessages((prev) => [...prev, { role: "assistant", content: response.data.data.answer }]);
    } catch {
      setMessages((prev) => [
        ...prev,
        {
          role: "assistant",
          content:
            "Hiện chưa kết nối được AI service (có thể API key chưa bật ở backend). Bạn thử lại sau nhé.",
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Sheet>
      <SheetTrigger asChild>
        <Button variant="outline" size="icon" className="border-primary/20 hover:border-primary/60">
          <MessageCircle className="h-5 w-5" />
          <span className="sr-only">AI Assistant</span>
        </Button>
      </SheetTrigger>
      <SheetContent className="flex w-full flex-col sm:max-w-lg">
        <SheetHeader>
          <SheetTitle className="flex items-center gap-2">
            <Bot className="h-5 w-5 text-primary" />
            Fashion AI Assistant
          </SheetTitle>
          <SheetDescription>Tư vấn phối đồ và chọn sản phẩm bằng AI.</SheetDescription>
        </SheetHeader>

        <div className="mt-4 flex-1 space-y-3 overflow-y-auto pr-1">
          {messages.map((msg, index) => (
            <div
              key={`${msg.role}-${index}`}
              className={`flex items-start gap-2 ${msg.role === "user" ? "justify-end" : "justify-start"}`}
            >
              {msg.role === "assistant" && <Bot className="mt-1 h-4 w-4 text-primary" />}
              <div
                className={`max-w-[80%] rounded-lg px-3 py-2 text-sm ${
                  msg.role === "user" ? "bg-primary text-primary-foreground" : "bg-muted"
                }`}
              >
                {msg.content}
              </div>
              {msg.role === "user" && <User className="mt-1 h-4 w-4 text-muted-foreground" />}
            </div>
          ))}
          {isLoading && (
            <div className="flex items-center gap-2 text-sm text-muted-foreground">
              <Loader2 className="h-4 w-4 animate-spin" />
              AI đang trả lời...
            </div>
          )}
        </div>

        <div className="mt-4 flex gap-2 border-t pt-4">
          <Input
            placeholder="Ví dụ: Tư vấn outfit đi tiệc màu đen"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => {
              if (e.key === "Enter") {
                e.preventDefault();
                handleSend();
              }
            }}
          />
          <Button onClick={handleSend} disabled={isLoading || !input.trim()}>
            <Send className="h-4 w-4" />
          </Button>
        </div>
      </SheetContent>
    </Sheet>
  );
}
