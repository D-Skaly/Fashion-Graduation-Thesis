"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ShoppingBag, User, Menu, Search } from "lucide-react";
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
import { AiAssistantSheet } from "@/components/ui/AiAssistantSheet";
import { CartDrawer } from "@/components/layout/CartDrawer";
import { cn } from "@/lib/utils";

export function Header() {
    const [isOpen, setIsOpen] = useState(false);
    const [isScrolled, setIsScrolled] = useState(false);
    const pathname = usePathname();

    useEffect(() => {
        const handleScroll = () => {
            setIsScrolled(window.scrollY > 20);
        };
        window.addEventListener("scroll", handleScroll);
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    const navLinks = [
        { href: "/", label: "Home" },
        { href: "/shop", label: "Shop" },
        { href: "/new-arrivals", label: "New Arrivals" },
        { href: "/about", label: "About" },
    ];

    return (
        <header
            className={cn(
                "fixed top-0 z-50 w-full transition-all duration-500",
                isScrolled
                    ? "border-b border-border/40 bg-background/80 backdrop-blur-xl shadow-sm py-2"
                    : "bg-transparent border-transparent py-6"
            )}
        >
            <div className="container mx-auto flex items-center justify-between px-4">
                {/* Mobile Menu */}
                <div className="md:hidden">
                    <Sheet open={isOpen} onOpenChange={setIsOpen}>
                        <SheetTrigger asChild>
                            <Button variant="ghost" size="icon" className="mr-2 hover:bg-background/20 rounded-full">
                                <Menu className="h-5 w-5" />
                                <span className="sr-only">Toggle menu</span>
                            </Button>
                        </SheetTrigger>
                        <SheetContent side="left" className="w-[300px] sm:w-[400px]">
                            <SheetTitle className="text-left font-bold text-lg mb-6 tracking-widest">MENU</SheetTitle>
                            <SheetDescription className="sr-only">Mobile Navigation Menu</SheetDescription>
                            <nav className="flex flex-col gap-4">
                                {navLinks.map((link) => (
                                    <Link
                                        key={link.href}
                                        href={link.href}
                                        onClick={() => setIsOpen(false)}
                                        className={cn(
                                            "text-lg font-medium transition-colors hover:text-primary tracking-wide",
                                            pathname === link.href && "text-primary font-bold"
                                        )}
                                    >
                                        {link.label}
                                    </Link>
                                ))}
                                <div className="pt-4 border-t">
                                    <p className="text-sm text-muted-foreground mb-3">Preferences</p>
                                    <div className="flex items-center gap-2">
                                        <span className="text-sm">Theme</span>
                                        <ThemeToggle className="rounded-full" />
                                    </div>
                                </div>
                            </nav>
                        </SheetContent>
                    </Sheet>
                </div>

                {/* Logo */}
                <div className="flex items-center gap-2">
                    <Link href="/" className="text-2xl font-black tracking-widest hover:opacity-80 transition-opacity uppercase drop-shadow-sm">
                        Fashion<span className="text-primary/70">.Thesis</span>
                    </Link>
                </div>

                {/* Desktop Navigation */}
                <nav className="hidden md:flex items-center gap-8 text-sm font-medium tracking-wide">
                    {navLinks.map((link) => (
                        <Link
                            key={link.href}
                            href={link.href}
                            className={cn(
                                "transition-colors hover:text-primary/70 relative group py-1",
                                pathname === link.href ? "text-primary" : "text-foreground/80"
                            )}
                        >
                            {link.label}
                            <span
                                className={cn(
                                    "absolute left-0 -bottom-1 w-0 h-[1.5px] bg-primary transition-all duration-300 group-hover:w-full",
                                    pathname === link.href && "w-full"
                                )}
                            />
                        </Link>
                    ))}
                </nav>

                {/* Actions */}
                <div className="flex items-center gap-1 md:gap-3">
                    {/* Desktop Search */}
                    <div className="hidden md:flex relative w-full items-center">
                        <SemanticSearchDialog />
                    </div>

                    {/* Mobile Search */}
                    <div className="md:hidden">
                        <SemanticSearchDialog />
                    </div>

                    <div className="flex items-center gap-0.5 md:gap-1">
                        <ThemeToggle className="hidden md:flex rounded-full hover:bg-background/20 hover:text-primary/70" />

                        <Button
                            variant="ghost"
                            size="icon"
                            className="hover:bg-background/20 hover:text-primary/70 rounded-full"
                            asChild
                        >
                            <Link href="/account">
                                <User className="h-5 w-5" />
                                <span className="sr-only">Account</span>
                            </Link>
                        </Button>
                        <CartDrawer />
                    </div>
                </div>
            </div>
        </header>
    );
}
