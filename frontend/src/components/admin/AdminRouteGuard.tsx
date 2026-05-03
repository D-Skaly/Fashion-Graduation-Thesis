"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { Loader2 } from "lucide-react";
import api from "@/lib/axios";

interface AdminRouteGuardProps {
    children: React.ReactNode;
}

interface UserProfile {
    role: string;
}

const checkAdminAccess = async (): Promise<UserProfile> => {
    const { data } = await api.get("/auth/me");
    return data;
};

export function AdminRouteGuard({ children }: AdminRouteGuardProps) {
    const router = useRouter();
    const { data: profile, isLoading, isError } = useQuery({
        queryKey: ["admin-access"],
        queryFn: checkAdminAccess,
        retry: false,
    });

    const isAdmin = profile?.role === "ADMIN" || profile?.role === "SUPER_ADMIN";

    useEffect(() => {
        if (!isLoading && (isError || !isAdmin)) {
            router.push("/login");
        }
    }, [isAdmin, isLoading, isError, router]);

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center bg-neutral-950">
                <div className="flex flex-col items-center gap-4">
                    <Loader2 className="h-10 w-10 animate-spin text-primary" />
                    <p className="text-stone-400 text-sm tracking-widest uppercase">Verifying access...</p>
                </div>
            </div>
        );
    }

    if (!isAdmin) {
        return null;
    }

    return <>{children}</>;
}
