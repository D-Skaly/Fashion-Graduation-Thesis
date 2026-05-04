"use client";

import { useEffect, Suspense } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { Loader2 } from "lucide-react";
import { toast } from "sonner";
import { useAuth } from "@/hooks/useAuth";

function RedirectHandler() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const { checkAuth } = useAuth();

  useEffect(() => {
    const token = searchParams.get("token");

    if (token) {
      // Backend should have set HttpOnly cookie
      // Just refresh auth state
      checkAuth();
      toast.success("Đăng nhập Google thành công!");
      
      // Redirect to account dashboard
      router.push("/account");
      router.refresh();
    } else {
      toast.error("Có lỗi xảy ra trong quá trình đăng nhập Google.");
      router.push("/login");
    }
  }, [router, searchParams, checkAuth]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50/50">
      <div className="flex flex-col items-center gap-4">
        <Loader2 className="h-12 w-12 animate-spin text-primary" />
        <p className="text-zinc-600 font-medium">
          Đang xác thực thông tin tài khoản...
        </p>
      </div>
    </div>
  );
}

export default function OAuth2RedirectPage() {
  return (
    <Suspense
      fallback={
        <div className="min-h-screen flex items-center justify-center bg-gray-50/50">
          <Loader2 className="h-12 w-12 animate-spin text-primary" />
        </div>
      }
    >
      <RedirectHandler />
    </Suspense>
  );
}
