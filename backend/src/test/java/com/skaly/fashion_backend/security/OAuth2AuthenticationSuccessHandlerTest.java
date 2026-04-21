package com.skaly.fashion_backend.security;

import com.skaly.fashion_backend.user.CustomOAuth2User;
import com.skaly.fashion_backend.user.Provider;
import com.skaly.fashion_backend.user.Role;
import com.skaly.fashion_backend.user.domain.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2AuthenticationSuccessHandlerTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OAuth2AuthenticationSuccessHandler successHandler;

    private User user;
    private CustomOAuth2User customOAuth2User;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@google.com");
        user.setRole(Role.USER);
        user.setProvider(Provider.GOOGLE);

        customOAuth2User = CustomOAuth2User.create(user, Collections.singletonMap("email", "test@google.com"));
    }

    @Test
    void onAuthenticationSuccess_ShouldRedirectWithToken() throws Exception {
        when(response.isCommitted()).thenReturn(false);
        when(authentication.getPrincipal()).thenReturn(customOAuth2User);
        when(jwtUtils.generateToken(user)).thenReturn("mock-jwt-token");

        String expectedUrl = "http://localhost:3000/oauth2/redirect?token=mock-jwt-token";
        lenient().when(response.encodeRedirectURL(expectedUrl)).thenReturn(expectedUrl);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Verify that sendRedirect was called on the mock response
        verify(response).sendRedirect(expectedUrl);
    }

    @Test
    void onAuthenticationSuccess_WhenResponseCommitted_ShouldNotRedirect() throws Exception {
        when(response.isCommitted()).thenReturn(true);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        // Verify zero interactions with redirect logic
        verify(response, never()).sendRedirect(anyString());
        verify(jwtUtils, never()).generateToken(any(User.class));
    }
}
