"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { LayoutDashboard, Package, ShoppingCart, Users, Settings } from "lucide-react";
import { cn } from "@/lib/utils";

const sidebarLinks = [
    { href: "/admin", label: "Dashboard", icon: LayoutDashboard },
    { href: "/admin/products", label: "Products", icon: Package },
    { href: "/admin/orders", label: "Orders", icon: ShoppingCart },
    { href: "/admin/customers", label: "Customers", icon: Users },
    { href: "/admin/settings", label: "Settings", icon: Settings },
];

export function AdminSidebar() {
    const pathname = usePathname();

    return (
        <aside className="hidden md:flex w-64 flex-col border-r border-white/10 glass-dark shadow-2xl relative z-20">
            <div className="flex h-16 items-center border-b border-white/10 px-6">
                <Link href="/admin" className="flex items-center gap-2 font-black text-xl uppercase tracking-widest text-white drop-shadow-md">
                    Fashion<span className="text-primary/80">.AI</span>
                </Link>
            </div>
            <div className="flex-1 overflow-y-auto py-6">
                <nav className="grid items-start px-4 text-sm font-medium gap-3">
                    {sidebarLinks.map((link) => {
                        const Icon = link.icon;
                        const isActive = pathname === link.href || (link.href !== "/admin" && pathname.startsWith(link.href));

                        return (
                            <Link
                                key={link.href}
                                href={link.href}
                                className={cn(
                                    "flex items-center gap-3 rounded-lg px-4 py-3 transition-all duration-300 font-bold uppercase tracking-wider text-xs",
                                    isActive
                                        ? "bg-primary/20 text-primary border border-primary/20 shadow-[0_0_15px_rgba(255,255,255,0.1)]"
                                        : "text-stone-400 hover:text-white hover:bg-white/5"
                                )}
                            >
                                <Icon className="h-4 w-4" />
                                {link.label}
                            </Link>
                        );
                    })}
                </nav>
            </div>
        </aside>
    );
}
