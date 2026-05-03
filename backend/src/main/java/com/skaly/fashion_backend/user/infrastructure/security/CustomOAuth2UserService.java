package com.skaly.fashion_backend.user.infrastructure.security;

import com.skaly.fashion_backend.user.domain.entities.Provider;
import com.skaly.fashion_backend.user.domain.entities.Role;
import com.skaly.fashion_backend.user.domain.entities.User;

import com.skaly.fashion_backend.user.infrastructure.persistence.entities.UserEntity;
import com.skaly.fashion_backend.user.infrastructure.persistence.jpa.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JpaUserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("Loaded OAuth2User from {}", userRequest.getClientRegistration().getRegistrationId());

        return processOAuth2User(userRequest, oAuth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();

        if (!registrationId.equalsIgnoreCase(Provider.GOOGLE.name())) {
            throw new OAuth2AuthenticationException("Sorry! Login with " + registrationId + " is not supported yet.");
        }

        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");

        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getProvider().equals(Provider.GOOGLE)) {
                throw new OAuth2AuthenticationException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            user = updateExistingUser(user, firstName, lastName);
        } else {
            user = registerNewUser(oAuth2User, email, firstName, lastName);
        }

        return CustomOAuth2User.create(toDomain(user), oAuth2User.getAttributes());
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .role(entity.getRole())
                .provider(entity.getProvider())
                .build();
    }

    private UserEntity registerNewUser(OAuth2User oAuth2User, String email, String firstName, String lastName) {
        UserEntity user = UserEntity.builder()
                .provider(Provider.GOOGLE)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    private UserEntity updateExistingUser(UserEntity existingUser, String firstName, String lastName) {
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        return userRepository.save(existingUser);
    }
}


