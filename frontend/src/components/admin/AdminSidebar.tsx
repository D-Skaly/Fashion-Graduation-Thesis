"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import {
    LayoutDashboard,
    Package,
    ShoppingCart,
    Users,
    Tag,
    Ticket,
    LogOut,
    ChevronRight,
    Sparkles,
    ShoppingBag
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { useAuth } from "@/hooks/useAuth";

const sidebarLinks = [
    { href: "/admin", label: "Dashboard", icon: LayoutDashboard, exact: true },
    { href: "/admin/products", label: "Products", icon: Package },
    { href: "/admin/orders", label: "Orders", icon: ShoppingCart },
    { href: "/admin/customers", label: "Customers", icon: Users },
    { href: "/admin/categories", label: "Categories", icon: Tag },
    { href: "/admin/coupons", label: "Coupons", icon: Ticket },
];

export function AdminSidebar() {
    const pathname = usePathname();
    const router = useRouter();
    const { logout } = useAuth();

    const handleLogout = async () => {
        try {
            await logout();
            toast.success("Signed out successfully");
            router.push("/login");
        } catch (error) {
            console.error("Logout failed", error);
            toast.error("Failed to sign out");
        }
    };

    return (
        <aside className="hidden md:flex w-72 flex-col border-r border-zinc-800 bg-zinc-950/50 backdrop-blur-md relative z-20">
            {/* Logo */}
            <div className="flex h-20 items-center border-b border-zinc-800 px-8 gap-3">
                <div className="w-8 h-8 rounded-lg bg-primary flex items-center justify-center">
                    <Sparkles className="h-4 w-4 text-white" />
                </div>
                <Link href="/admin" className="font-black text-xl uppercase tracking-tighter text-white">
                    Fashion<span className="text-primary">.AI</span>
                </Link>
            </div>

            {/* Nav */}
            <div className="flex-1 overflow-y-auto py-8">
                <nav className="grid items-start px-4 text-sm font-medium gap-1.5">
                    <p className="text-[10px] font-bold tracking-[0.2em] uppercase text-zinc-500 px-4 mb-4">
                        Management System
                    </p>
                    {sidebarLinks.map((link) => {
                        const Icon = link.icon;
                        const isActive = link.exact
                            ? pathname === link.href
                            : pathname.startsWith(link.href);

                        return (
                            <Link
                                key={link.href}
                                href={link.href}
                                className={cn(
                                    "flex items-center gap-3 rounded-xl px-4 py-3 transition-all duration-200 group",
                                    isActive
                                        ? "bg-primary/10 text-primary"
                                        : "text-zinc-400 hover:text-zinc-100 hover:bg-zinc-900"
                                )}
                            >
                                <Icon className={cn("h-5 w-5", isActive ? "text-primary" : "text-zinc-500 group-hover:text-zinc-100")} />
                                <span className="font-bold uppercase tracking-widest text-[11px]">{link.label}</span>
                                {isActive && (
                                    <ChevronRight className="h-4 w-4 ml-auto" />
                                )}
                            </Link>
                        );
                    })}
                </nav>
            </div>

            {/* Bottom Actions */}
            <div className="border-t border-zinc-800 p-6 space-y-2">
                <Link
                    href="/"
                    className="flex items-center gap-3 rounded-xl px-4 py-3 text-zinc-400 hover:text-white hover:bg-zinc-900 transition-all group"
                >
                    <ShoppingBag className="h-5 w-5 text-zinc-500 group-hover:text-white" />
                    <span className="font-bold uppercase tracking-widest text-[11px]">Storefront</span>
                </Link>
                <Button
                    variant="ghost"
                    className="w-full justify-start gap-3 rounded-xl px-4 py-3 text-zinc-400 hover:text-red-400 hover:bg-red-500/10 transition-all h-auto"
                    onClick={handleLogout}
                >
                    <LogOut className="h-5 w-5" />
                    <span className="font-bold uppercase tracking-widest text-[11px]">Terminate Session</span>
                </Button>
            </div>
        </aside>
    );
}
