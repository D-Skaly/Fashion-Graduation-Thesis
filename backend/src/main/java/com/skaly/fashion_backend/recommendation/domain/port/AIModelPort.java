package com.skaly.fashion_backend.recommendation.domain.port;

import com.skaly.fashion_backend.recommendation.domain.model.FashionIntentResult;
import com.skaly.fashion_backend.recommendation.domain.model.RecommendedProduct;

import java.util.List;

/**
 * Cổng trừu tượng tới "bộ não" LLM — Domain chỉ định nghĩa hợp đồng, không import SDK AI.
 * <p>
 * Nguyên tắc Senior / Clean Architecture:
 * <ul>
 *   <li>Thay đổi nhà cung cấp (Gemini, Claude, local model) = thay Adapter ở Infrastructure.</li>
 *   <li>Phương thức nhận/tra về kiểu miền trung lập ({@link FashionIntentResult}, {@link String}), không có JSON schema hay tool call cụ thể của vendor.</li>
 * </ul>
 */
public interface AIModelPort {

    /**
     * Bước điều phối 1: hiểu ý định và chuẩn bị văn bản phục vụ tìm kiếm ngữ nghĩa (RAG).
     */
    FashionIntentResult interpretUserIntent(String userMessage);

    /**
     * Bước sinh văn bản từ prompt đã được Application lắp ráp (RAG context + persona + yêu cầu người dùng).
     * Domain không biết prompt được ghép như thế nào — chỉ biết đây là lời gọi "completion" trung lập.
     */
    String completeChatPrompt(String composedPrompt);

    /**
     * Tóm tắt / tư vấn ngắn dựa trên danh sách sản phẩm đã truy vấn được (sau RAG).
     * Giữ logic marketing trong Adapter; Domain chỉ thấy dữ liệu sản phẩm đã được chuẩn hóa.
     */
    String composeFashionAdvice(String userMessage, List<RecommendedProduct> candidates);
}
