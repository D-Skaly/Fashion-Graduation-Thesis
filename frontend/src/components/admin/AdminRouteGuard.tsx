"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";

import api from "@/lib/axios";
import { Skeleton } from "@/components/ui/skeleton";

interface AdminRouteGuardProps {
    children: React.ReactNode;
}

const checkAdminAccess = async () => {
    try {
        const { data } = await api.get("/auth/me");
        return data.role === "ADMIN" || data.role === "SUPER_ADMIN";
    } catch {
        return false;
    }
};

export function AdminRouteGuard({ children }: AdminRouteGuardProps) {
    const router = useRouter();
    const { data: isAdmin, isLoading } = useQuery({
        queryKey: ["admin-access"],
        queryFn: checkAdminAccess,
    });

    useEffect(() => {
        if (!isLoading && !isAdmin) {
            router.push("/login");
        }
    }, [isAdmin, isLoading, router]);

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <div className="space-y-4 w-full max-w-md">
                    <Skeleton className="h-8 w-64" />
                    <Skeleton className="h-12 w-full" />
                    <Skeleton className="h-12 w-full" />
                </div>
            </div>
        );
    }

    if (!isAdmin) {
        return null;
    }

    return <>{children}</>;
}
