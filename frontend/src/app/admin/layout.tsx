import { AdminHeader } from "@/components/admin/AdminHeader";
import { AdminSidebar } from "@/components/admin/AdminSidebar";

export default function AdminLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <div className="flex min-h-screen w-full bg-gradient-to-br from-neutral-950 via-stone-900 to-neutral-900 text-stone-100 font-sans antialiased selection:bg-primary/30">
            <AdminSidebar />
            <div className="flex flex-col flex-1 relative z-10">
                <AdminHeader />
                <main className="flex-1 p-4 md:p-8 overflow-y-auto">
                    {children}
                </main>
            </div>
        </div>
    );
}
