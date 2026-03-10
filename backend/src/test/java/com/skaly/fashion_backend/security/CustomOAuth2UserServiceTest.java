package com.skaly.fashion_backend.security;

import com.skaly.fashion_backend.user.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Test
    @Disabled("loadUser() calls super which requires a real OAuth2 server. " +
              "Implement with WireMock to properly mock the OAuth2 provider endpoint.")
    void loadUser_UnsupportedProvider_ThrowsException() {
        // TODO: Set up WireMock to stub the OAuth2 /userinfo endpoint,
        // then verify CustomOAuth2UserService throws OAuth2AuthenticationException
        // when an unsupported provider (e.g. "github") is used.
    }
}
