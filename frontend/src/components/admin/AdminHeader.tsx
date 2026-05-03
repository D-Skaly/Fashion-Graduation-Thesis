"use client";

import { useQuery } from "@tanstack/react-query";
import { useRouter } from "next/navigation";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { LogOut, ExternalLink, User, Settings } from "lucide-react";
import Cookies from "js-cookie";
import { toast } from "sonner";
import api from "@/lib/axios";
import Link from "next/link";

interface UserProfile {
    firstname: string;
    lastname: string;
    email: string;
    role: string;
}

const fetchProfile = async (): Promise<UserProfile> => {
    const { data } = await api.get("/auth/me");
    return data;
};

export function AdminHeader() {
    const router = useRouter();

    const { data: profile } = useQuery({
        queryKey: ["admin-profile"],
        queryFn: fetchProfile,
        retry: false,
    });

    const handleLogout = () => {
        Cookies.remove("token");
        toast.success("Signed out successfully");
        router.push("/login");
        router.refresh();
    };

    const initials = profile
        ? `${profile.firstname?.[0] || ""}${profile.lastname?.[0] || ""}`.toUpperCase()
        : "AD";

    const fullName = profile
        ? `${profile.firstname || ""} ${profile.lastname || ""}`.trim()
        : "Admin Access";

    return (
        <header className="flex h-20 items-center gap-4 border-b border-zinc-800 bg-zinc-950/50 backdrop-blur-md px-8 sticky top-0 z-30 shadow-sm">
            {/* System Status */}
            <div className="flex-1">
                <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-zinc-900 border border-zinc-800">
                    <span className="w-2 h-2 rounded-full bg-primary shadow-[0_0_8px_rgba(var(--primary-rgb),0.5)]" />
                    <span className="text-[10px] font-black tracking-[0.2em] uppercase text-zinc-300">
                        Neural Network Operational
                    </span>
                </div>
            </div>

            {/* User Profile */}
            <div className="flex items-center gap-4">
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="flex items-center gap-4 h-12 py-2 px-4 rounded-2xl hover:bg-zinc-900 transition-all border border-transparent hover:border-zinc-800">
                            <div className="hidden sm:flex flex-col items-end">
                                <span className="text-sm font-black text-zinc-100 leading-none">{fullName}</span>
                                <span className="text-[10px] text-zinc-500 uppercase tracking-widest mt-1">{profile?.role || "System Administrator"}</span>
                            </div>
                            <Avatar className="h-9 w-9 border-2 border-zinc-800 group-hover:border-primary transition-colors">
                                <AvatarFallback className="bg-primary text-white text-xs font-black">
                                    {initials}
                                </AvatarFallback>
                            </Avatar>
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="w-64 p-2 bg-zinc-900 border-zinc-800 text-zinc-100 rounded-2xl shadow-2xl">
                        <DropdownMenuLabel className="font-normal p-4">
                            <div className="flex flex-col space-y-1">
                                <p className="text-sm font-black uppercase tracking-tight">{fullName}</p>
                                <p className="text-xs text-zinc-500 font-medium">{profile?.email}</p>
                            </div>
                        </DropdownMenuLabel>
                        <DropdownMenuSeparator className="bg-zinc-800" />
                        <div className="p-1">
                            <DropdownMenuItem asChild className="rounded-xl focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5">
                                <Link href="/">
                                    <ExternalLink className="mr-3 h-4 w-4 text-zinc-500" />
                                    <span className="text-xs font-bold uppercase tracking-wider">Public Interface</span>
                                </Link>
                            </DropdownMenuItem>
                            <DropdownMenuItem className="rounded-xl focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5">
                                <User className="mr-3 h-4 w-4 text-zinc-500" />
                                <span className="text-xs font-bold uppercase tracking-wider">Profile Access</span>
                            </DropdownMenuItem>
                            <DropdownMenuItem className="rounded-xl focus:bg-zinc-800 focus:text-white cursor-pointer py-2.5">
                                <Settings className="mr-3 h-4 w-4 text-zinc-500" />
                                <span className="text-xs font-bold uppercase tracking-wider">System Config</span>
                            </DropdownMenuItem>
                        </div>
                        <DropdownMenuSeparator className="bg-zinc-800" />
                        <div className="p-1">
                            <DropdownMenuItem
                                onClick={handleLogout}
                                className="rounded-xl text-red-400 focus:text-red-400 focus:bg-red-500/10 cursor-pointer py-2.5"
                            >
                                <LogOut className="mr-3 h-4 w-4" />
                                <span className="text-xs font-bold uppercase tracking-wider">Emergency Logout</span>
                            </DropdownMenuItem>
                        </div>
                    </DropdownMenuContent>
                </DropdownMenu>
            </div>
        </header>
    );
}
