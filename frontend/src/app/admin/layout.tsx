import { AdminHeader } from "@/components/admin/AdminHeader";
import { AdminSidebar } from "@/components/admin/AdminSidebar";
import { AdminRouteGuard } from "@/components/admin/AdminRouteGuard";

export default function AdminLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <AdminRouteGuard>
            <div className="flex min-h-screen w-full bg-[#09090b] text-zinc-100 font-sans antialiased selection:bg-primary/30">
                <AdminSidebar />
                <div className="flex flex-col flex-1 relative z-10">
                    <AdminHeader />
                    <main className="flex-1 p-6 md:p-10 overflow-y-auto">
                        {children}
                    </main>
                </div>
            </div>
        </AdminRouteGuard>
    );
}
