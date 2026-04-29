"use client";

import { useState } from "react";
import Link from "next/link";
import { Facebook, Instagram, Twitter, Send, Loader2, Check } from "lucide-react";
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
        // Simulate API call
        await new Promise(resolve => setTimeout(resolve, 1000));
        setIsSubmitting(false);
        setIsSubscribed(true);
        toast.success("Welcome to the club! Check your inbox.");
        setEmail("");
    };

    return (
        <footer className="bg-black text-white mt-auto border-t border-white/5">
            <div className="container mx-auto px-4 py-16 md:py-20">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-12">
                    <div className="space-y-6">
                        <h3 className="text-2xl font-bold tracking-widest uppercase">Fashion.Thesis</h3>
                        <p className="text-sm text-primary-foreground/80 leading-relaxed max-w-xs">
                            Redefining modern elegance. Sustainable materials meets contemporary design for the conscious consumer.
                        </p>
                        <div className="flex gap-4">
                            <Link href="#" className="hover:text-primary-foreground/70 transition-colors hover:scale-110 transform duration-200">
                                <Instagram className="h-5 w-5" />
                                <span className="sr-only">Instagram</span>
                            </Link>
                            <Link href="#" className="hover:text-primary-foreground/70 transition-colors hover:scale-110 transform duration-200">
                                <Facebook className="h-5 w-5" />
                                <span className="sr-only">Facebook</span>
                            </Link>
                            <Link href="#" className="hover:text-primary-foreground/70 transition-colors hover:scale-110 transform duration-200">
                                <Twitter className="h-5 w-5" />
                                <span className="sr-only">Twitter</span>
                            </Link>
                        </div>
                    </div>

                    <div className="space-y-6">
                        <h4 className="text-sm font-bold uppercase tracking-widest">Shop</h4>
                        <ul className="space-y-3 text-sm text-primary-foreground/70">
                            <li><Link href="/shop" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">All Products</Link></li>
                            <li><Link href="/new-arrivals" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">New Arrivals</Link></li>
                            <li><Link href="/featured" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">Featured Collection</Link></li>
                            <li><Link href="/accessories" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">Accessories</Link></li>
                        </ul>
                    </div>

                    <div className="space-y-6">
                        <h4 className="text-sm font-bold uppercase tracking-widest">Support</h4>
                        <ul className="space-y-3 text-sm text-primary-foreground/70">
                            <li><Link href="/faq" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">FAQ</Link></li>
                            <li><Link href="/shipping" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">Shipping & Returns</Link></li>
                            <li><Link href="/sizing" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">Sizing Guide</Link></li>
                            <li><Link href="/contact" className="hover:text-primary-foreground transition-colors hover:translate-x-1 inline-block duration-200">Contact Us</Link></li>
                        </ul>
                    </div>

                    <div className="space-y-6">
                        <h4 className="text-sm font-bold uppercase tracking-widest">Newsletter</h4>
                        <p className="text-sm text-primary-foreground/70">
                            Join our exclusive community for early access to drops and events.
                        </p>
                        <form onSubmit={handleSubscribe} className="flex flex-col gap-2">
                            <div className="relative">
                                <input
                                    type="email"
                                    placeholder="YOUR EMAIL"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    disabled={isSubscribed}
                                    className="w-full bg-primary-foreground/10 border border-primary-foreground/20 text-primary-foreground placeholder:text-primary-foreground/50 px-4 py-2.5 text-sm focus:outline-none focus:border-primary-foreground/50 transition-colors disabled:opacity-50"
                                    aria-label="Email address"
                                />
                            </div>
                            <Button
                                type="submit"
                                disabled={isSubmitting || isSubscribed}
                                className="bg-primary-foreground text-primary px-4 py-2.5 text-sm font-bold tracking-widest hover:bg-white/90 transition-colors uppercase rounded-none h-auto"
                            >
                                {isSubmitting ? (
                                    <Loader2 className="h-4 w-4 animate-spin" />
                                ) : isSubscribed ? (
                                    <>
                                        <Check className="h-4 w-4 mr-2" /> Subscribed
                                    </>
                                ) : (
                                    <>
                                        <Send className="h-4 w-4 mr-2" /> Subscribe
                                    </>
                                )}
                            </Button>
                        </form>
                    </div>
                </div>

                <div className="mt-16 pt-8 border-t border-primary-foreground/10 text-center text-xs text-primary-foreground/50 tracking-wide">
                    <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                        <p>&copy; {new Date().getFullYear()} FASHION.THESIS. All rights reserved.</p>
                        <div className="flex gap-6">
                            <Link href="/privacy" className="hover:text-primary-foreground transition-colors">Privacy Policy</Link>
                            <Link href="/terms" className="hover:text-primary-foreground transition-colors">Terms of Service</Link>
                        </div>
                    </div>
                </div>
            </div>
        </footer>
    );
}
