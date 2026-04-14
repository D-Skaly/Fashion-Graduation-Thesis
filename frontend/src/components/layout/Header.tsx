"use client";

import Link from "next/link";
import { ShoppingBag, User, Menu } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
    Sheet,
    SheetContent,
    SheetTrigger,
    SheetTitle,
    SheetDescription,
} from "@/components/ui/sheet";
import { useState } from "react";
import { SemanticSearchDialog } from "@/components/ui/SemanticSearchDialog";
import { AiAssistantSheet } from "@/components/ui/AiAssistantSheet";

export function Header() {
    const [isOpen, setIsOpen] = useState(false);

    const navLinks = [
        { href: "/", label: "Home" },
        { href: "/shop", label: "Shop" },
        { href: "/new-arrivals", label: "New Arrivals" },
        { href: "/about", label: "About" },
    ];

    return (
        <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
            <div className="container mx-auto flex h-16 items-center justify-between px-4">
                {/* Mobile Menu */}
                <div className="md:hidden">
                    <Sheet open={isOpen} onOpenChange={setIsOpen}>
                        <SheetTrigger asChild>
                            <Button variant="ghost" size="icon" className="mr-2">
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
                                        className="text-lg font-medium transition-colors hover:text-primary tracking-wide"
                                    >
                                        {link.label}
                                    </Link>
                                ))}
                            </nav>
                        </SheetContent>
                    </Sheet>
                </div>

                {/* Logo */}
                <div className="flex items-center gap-2">
                    <Link href="/" className="text-2xl font-bold tracking-widest hover:opacity-80 transition-opacity uppercase">
                        Fashion<span className="text-primary">.Thesis</span>
                    </Link>
                </div>

                {/* Desktop Navigation */}
                <nav className="hidden md:flex items-center gap-8 text-sm font-medium tracking-wide">
                    {navLinks.map((link) => (
                        <Link key={link.href} href={link.href} className="transition-colors hover:text-primary/70 relative group">
                            {link.label}
                            <span className="absolute left-0 -bottom-1 w-0 h-[1px] bg-primary transition-all group-hover:w-full" />
                        </Link>
                    ))}
                </nav>

                {/* Actions */}
                <div className="flex items-center gap-2 md:gap-4">
                    <div className="hidden md:flex relative w-full items-center">
                        <SemanticSearchDialog />
                    </div>

                    <div className="flex items-center gap-1 md:gap-2">
                        <AiAssistantSheet />
                        <Button variant="ghost" size="icon" className="hover:bg-transparent hover:text-primary/70" asChild>
                            <Link href="/account">
                                <User className="h-5 w-5" />
                                <span className="sr-only">Account</span>
                            </Link>
                        </Button>
                        <Button variant="ghost" size="icon" className="relative hover:bg-transparent hover:text-primary/70" asChild>
                            <Link href="/cart">
                                <ShoppingBag className="h-5 w-5" />
                                <span className="absolute -top-1 -right-1 flex h-4 w-4 items-center justify-center rounded-full bg-primary text-[10px] text-primary-foreground font-bold">
                                    0
                                </span>
                                <span className="sr-only">Cart</span>
                            </Link>
                        </Button>
                    </div>
                </div>
            </div>
        </header>
    );
}
