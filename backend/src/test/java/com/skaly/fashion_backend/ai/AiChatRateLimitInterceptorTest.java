package com.skaly.fashion_backend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class AiChatRateLimitInterceptorTest {

    @Test
    void preHandle_ShouldBlockWhenLimitExceeded() throws Exception {
        AiAssistantProperties properties = new AiAssistantProperties(
                true,
                1000,
                new AiAssistantProperties.Retry(3, 10),
                new AiAssistantProperties.Timeout(1000),
                new AiAssistantProperties.RateLimit(true, 2, 60));

        AiChatRateLimitInterceptor interceptor = new AiChatRateLimitInterceptor(
                properties,
                new ObjectMapper(),
                new SimpleMeterRegistry());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, new Object())).isTrue();
        assertThat(interceptor.preHandle(request, response, new Object())).isTrue();
        assertThat(interceptor.preHandle(request, response, new Object())).isFalse();
        assertThat(response.getStatus()).isEqualTo(429);
        assertThat(response.getContentAsString()).contains("Too many AI chat requests");
    }
}
