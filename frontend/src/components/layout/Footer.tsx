"use client";

import { useState } from "react";
import Link from "next/link";
import { Facebook, Instagram, Twitter, Send, Loader2, Check, Sparkles } from "lucide-react";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";

export function Footer() {
    const [email, setEmail] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isSubscribed, setIsSubscribed] = useState(false);

    const handleSubscribe = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email.trim()) {
            toast.error("Please enter your email");
            return;
        }
        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            toast.error("Please enter a valid email");
            return;
        }

        setIsSubmitting(true);
        await new Promise(resolve => setTimeout(resolve, 1200));
        setIsSubmitting(false);
        setIsSubscribed(true);
        toast.success("Welcome to the club! Check your inbox.");
        setEmail("");
    };

    return (
        <footer className="bg-black text-white mt-auto border-t border-white/5 relative overflow-hidden">
            {/* Subtle background decoration */}
            <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-primary/5 rounded-full blur-[120px] -translate-y-1/2 translate-x-1/2" />
            
            <div className="container mx-auto px-6 py-20 md:py-32 relative z-10">
                <div className="grid grid-cols-1 md:grid-cols-12 gap-16 md:gap-8">
                    {/* Brand Section */}
                    <div className="md:col-span-4 space-y-8">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 rounded-xl bg-white flex items-center justify-center">
                                <Sparkles className="h-5 w-5 text-black" />
                            </div>
                            <h3 className="text-2xl font-black tracking-tighter uppercase">Fashion<span className="text-primary/50">.Thesis</span></h3>
                        </div>
                        <p className="text-sm text-zinc-400 leading-relaxed max-w-sm font-medium">
                            Synthesizing avant-garde aesthetics with neural intelligence to redefine modern elegance for the conscious digital era.
                        </p>
                        <div className="flex gap-5">
                            {[Instagram, Facebook, Twitter].map((Icon, i) => (
                                <Link key={i} href="#" className="text-zinc-500 hover:text-white transition-all hover:-translate-y-1 duration-300">
                                    <Icon className="h-5 w-5" />
                                </Link>
                            ))}
                        </div>
                    </div>

                    {/* Navigation Columns */}
                    <div className="md:col-span-2 space-y-8">
                        <h4 className="text-[10px] font-black uppercase tracking-[0.3em] text-zinc-500">Navigation</h4>
                        <ul className="space-y-4 text-xs font-bold uppercase tracking-widest text-zinc-400">
                            <li><Link href="/shop" className="hover:text-white transition-colors">Archive</Link></li>
                            <li><Link href="/new-arrivals" className="hover:text-white transition-colors">Drops</Link></li>
                            <li><Link href="/featured" className="hover:text-white transition-colors">Curation</Link></li>
                        </ul>
                    </div>

                    <div className="md:col-span-2 space-y-8">
                        <h4 className="text-[10px] font-black uppercase tracking-[0.3em] text-zinc-500">Logistics</h4>
                        <ul className="space-y-4 text-xs font-bold uppercase tracking-widest text-zinc-400">
                            <li><Link href="/faq" className="hover:text-white transition-colors">Inquiry</Link></li>
                            <li><Link href="/shipping" className="hover:text-white transition-colors">Protocol</Link></li>
                            <li><Link href="/contact" className="hover:text-white transition-colors">Transmission</Link></li>
                        </ul>
                    </div>

                    {/* Newsletter Section */}
                    <div className="md:col-span-4 space-y-8">
                        <h4 className="text-[10px] font-black uppercase tracking-[0.3em] text-zinc-500">Intelligence Unit</h4>
                        <p className="text-sm text-zinc-400 font-medium">
                            Join the neural network for early access to collection drops and diagnostic reports.
                        </p>
                        <form onSubmit={handleSubscribe} className="space-y-4">
                            <div className="relative group">
                                <input
                                    type="email"
                                    placeholder="DESIGNER@WORKSPACE.COM"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    disabled={isSubscribed}
                                    className="w-full bg-zinc-900/50 border border-zinc-800 text-white placeholder:text-zinc-600 px-6 py-4 text-[11px] font-black tracking-widest focus:outline-none focus:border-primary/50 transition-all rounded-2xl group-hover:bg-zinc-800/50"
                                    aria-label="Email address"
                                />
                                <div className="absolute inset-0 rounded-2xl border border-primary/0 group-focus-within:border-primary/20 pointer-events-none transition-all" />
                            </div>
                            <Button
                                type="submit"
                                disabled={isSubmitting || isSubscribed}
                                className="w-full bg-white text-black h-14 text-[10px] font-black tracking-[0.2em] hover:bg-zinc-200 transition-all uppercase rounded-2xl shadow-2xl shadow-white/5 active:scale-95"
                            >
                                {isSubmitting ? (
                                    <Loader2 className="h-4 w-4 animate-spin" />
                                ) : isSubscribed ? (
                                    <>
                                        <Check className="h-4 w-4 mr-2" /> Synced Successfully
                                    </>
                                ) : (
                                    <>
                                        <Send className="h-3 w-3 mr-3" /> Execute Subscription
                                    </>
                                )}
                            </Button>
                        </form>
                    </div>
                </div>

                <div className="mt-32 pt-10 border-t border-zinc-900 flex flex-col md:flex-row justify-between items-center gap-6 text-[10px] font-black uppercase tracking-[0.2em] text-zinc-600">
                    <p>&copy; {new Date().getFullYear()} FASHION.THESIS // V1.0.0 CORE</p>
                    <div className="flex gap-10">
                        <Link href="/privacy" className="hover:text-white transition-colors">Privacy Protocal</Link>
                        <Link href="/terms" className="hover:text-white transition-colors">Terms of Service</Link>
                    </div>
                </div>
            </div>
        </footer>
    );
}
