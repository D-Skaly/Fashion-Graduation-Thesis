"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { User, Menu, LogIn, X } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Sheet,
    SheetContent,
    SheetTrigger,
    SheetTitle,
    SheetDescription,
} from "@/components/ui/sheet";
import { ThemeToggle } from "@/components/ui/theme-toggle";
import { useState, useEffect } from "react";
import { SemanticSearchDialog } from "@/components/ui/SemanticSearchDialog";
import { CartDrawer } from "@/components/layout/CartDrawer";
import { cn } from "@/lib/utils";
import Cookies from "js-cookie";

export function Header() {
    const [isOpen, setIsOpen] = useState(false);
    const [isScrolled, setIsScrolled] = useState(false);
    const [isLoggedIn, setIsLoggedIn] = useState(() => !!Cookies.get("token"));
    const pathname = usePathname();

     
    useEffect(() => {
        const hasToken = !!Cookies.get("token");
        if (hasToken !== isLoggedIn) {
            setTimeout(() => setIsLoggedIn(hasToken), 0);
        }
    }, [pathname, isLoggedIn]);

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 20);
        };
        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    const navLinks = [
        { href: "/", label: "Origin" },
        { href: "/shop", label: "Archive" },
        { href: "/new-arrivals", label: "Drops" },
        { href: "/about", label: "Philosophy" },
    ];

    return (
        <header
            className={cn(
                "fixed top-0 z-50 w-full transition-all duration-700 ease-in-out",
                isScrolled
                    ? "border-b border-white/10 bg-black/80 backdrop-blur-2xl shadow-2xl py-3"
                    : "bg-transparent border-transparent py-8"
            )}
        >
            <div className="container mx-auto flex items-center justify-between px-6">
                {/* Mobile Menu */}
                <div className="md:hidden">
                    <Sheet open={isOpen} onOpenChange={setIsOpen}>
                        <SheetTrigger asChild>
                            <Button variant="ghost" size="icon" className="mr-2 hover:bg-white/10 rounded-2xl text-white">
                                <Menu className="h-5 w-5" />
                                <span className="sr-only">Toggle menu</span>
                            </Button>
                        </SheetTrigger>
                        <SheetContent side="left" className="w-full sm:w-[400px] bg-black border-r border-white/10 p-0">
                            <div className="flex flex-col h-full p-8">
                                <div className="flex items-center justify-between mb-16">
                                    <SheetTitle className="text-left font-black text-2xl tracking-tighter text-white uppercase">
                                        Fashion<span className="text-primary/50">.Thesis</span>
                                    </SheetTitle>
                                    <Button variant="ghost" size="icon" onClick={() => setIsOpen(false)} className="text-zinc-500 hover:text-white rounded-full">
                                        <X className="h-6 w-6" />
                                    </Button>
                                </div>
                                <SheetDescription className="sr-only">Mobile Navigation Menu</SheetDescription>
                                <nav className="flex flex-col gap-8">
                                    {navLinks.map((link) => (
                                        <Link
                                            key={link.href}
                                            href={link.href}
                                            onClick={() => setIsOpen(false)}
                                            className={cn(
                                                "text-4xl font-black transition-all hover:text-primary tracking-tighter uppercase",
                                                pathname === link.href ? "text-primary" : "text-zinc-500"
                                            )}
                                        >
                                            {link.label}
                                        </Link>
                                    ))}
                                </nav>
                                <div className="mt-auto pt-10 border-t border-white/5 space-y-6">
                                    <div className="flex items-center justify-between">
                                        <span className="text-[10px] font-black uppercase tracking-[0.3em] text-zinc-500">Interface Mode</span>
                                        <ThemeToggle className="rounded-2xl bg-zinc-900 border-zinc-800" />
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <Button variant="outline" className="rounded-2xl border-zinc-800 text-zinc-400 font-bold uppercase tracking-widest text-[10px] h-14" asChild>
                                            <Link href="/login">Identity</Link>
                                        </Button>
                                        <Button className="rounded-2xl bg-white text-black font-black uppercase tracking-widest text-[10px] h-14" asChild>
                                            <Link href="/shop">Archive</Link>
                                        </Button>
                                    </div>
                                </div>
                            </div>
                        </SheetContent>
                    </Sheet>
                </div>

                {/* Logo */}
                <div className="flex items-center gap-2">
                    <Link href="/" className={cn(
                        "text-2xl font-black tracking-tighter hover:opacity-80 transition-all uppercase",
                        isScrolled ? "text-white" : "text-foreground"
                    )}>
                        Fashion<span className="text-primary/70">.Thesis</span>
                    </Link>
                </div>

                {/* Desktop Navigation */}
                <nav className="hidden md:flex items-center gap-10 text-[11px] font-black tracking-[0.2em] uppercase">
                    {navLinks.map((link) => (
                        <Link
                            key={link.href}
                            href={link.href}
                            className={cn(
                                "transition-all hover:text-primary relative group py-1",
                                pathname === link.href 
                                    ? "text-primary" 
                                    : (isScrolled ? "text-zinc-400" : "text-foreground/70")
                            )}
                        >
                            {link.label}
                            <span
                                className={cn(
                                    "absolute left-0 -bottom-1 w-0 h-[2px] bg-primary transition-all duration-500 group-hover:w-full",
                                    pathname === link.href && "w-full"
                                )}
                            />
                        </Link>
                    ))}
                </nav>

                {/* Actions */}
                <div className="flex items-center gap-2 md:gap-4">
                    <SemanticSearchDialog />

                    <div className="flex items-center gap-1 md:gap-2">
                        <ThemeToggle className={cn(
                            "hidden md:flex rounded-2xl transition-all",
                            isScrolled ? "hover:bg-white/10 text-white" : "hover:bg-black/5"
                        )} />

                        {isLoggedIn ? (
                            <Button
                                variant="ghost"
                                size="icon"
                                className={cn(
                                    "rounded-2xl transition-all h-10 w-10",
                                    isScrolled ? "hover:bg-white/10 text-white" : "hover:bg-black/5"
                                )}
                                asChild
                            >
                                <Link href="/account">
                                    <User className="h-5 w-5" />
                                    <span className="sr-only">Account</span>
                                </Link>
                            </Button>
                        ) : (
                            <Button
                                variant="ghost"
                                size="sm"
                                className={cn(
                                    "rounded-2xl gap-2 text-[10px] font-black tracking-[0.2em] uppercase h-10 px-4 transition-all",
                                    isScrolled ? "hover:bg-white/10 text-white" : "hover:bg-black/5"
                                )}
                                asChild
                            >
                                <Link href="/login">
                                    <LogIn className="h-4 w-4" />
                                    <span className="hidden sm:inline">Identity</span>
                                </Link>
                            </Button>
                        )}
                        <CartDrawer />
                    </div>
                </div>
            </div>
        </header>
    );
}
