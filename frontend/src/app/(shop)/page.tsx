import { CategoryGrid } from "@/components/home/CategoryGrid";
import { FeaturedProducts } from "@/components/home/FeaturedProducts";
import { HeroBanner } from "@/components/home/HeroBanner";
import { WhyChooseUs } from "@/components/home/WhyChooseUs";
import { VirtualTryOnTeaser } from "@/components/home/VirtualTryOnTeaser";

export default function Home() {
    return (
        <div className="flex flex-col min-h-screen">
            <HeroBanner />
            <CategoryGrid />
            <FeaturedProducts />
            <VirtualTryOnTeaser />
            <WhyChooseUs />
        </div>
    );
}
