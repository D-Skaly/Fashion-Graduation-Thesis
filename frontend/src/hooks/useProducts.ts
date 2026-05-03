import { useQuery } from "@tanstack/react-query";
import api from "@/lib/axios";
import { ProductSummary, ProductResponse, Page } from "@/types/product";

export const useProducts = () => {
    return useQuery({
        queryKey: ["products"],
        queryFn: async (): Promise<Product[]> => {
            // After axios interceptor unwraps ApiResponse, data is the Page<ProductResponse> directly
            const { data: page } = await api.get<Page<ProductResponse>>("/products");

            return page.content.map((item) => ({
                id: item.id,
                name: item.name,
                price: item.basePrice || 0,
                category: item.categoryName || "Uncategorized",
                image: item.images && item.images.length > 0 ? item.images[0] : undefined,
                hoverImage: item.images && item.images.length > 1 ? item.images[1] : undefined,
                isNew: item.createdAt
                    ? Date.now() - new Date(item.createdAt).getTime() < 14 * 24 * 60 * 60 * 1000 // < 14 days old
                    : false,
                rating: item.averageRating,
                reviewCount: item.reviewCount,
            }));
        },
    });
};
