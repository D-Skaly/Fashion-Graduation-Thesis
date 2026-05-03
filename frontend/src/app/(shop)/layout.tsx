import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { AiStylistFAB } from "@/components/ui/AiStylistFAB";

export default function ShopLayout({
    children,
}: Readonly<{
    children: React.ReactNode;
}>) {
    return (
        <div className="flex min-h-screen flex-col">
            <Header />
            <main className="flex-1">
                {children}
            </main>
            <Footer />
            <AiStylistFAB />
        </div>
    );
}
