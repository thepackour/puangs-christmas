package com.puang.domain.user.service;

import com.puang.domain.user.dto.TokenResponse;
import com.puang.domain.user.entity.User;
import com.puang.domain.user.exception.UserErrorCode;
import com.puang.domain.user.repository.UserRepository;
import com.puang.global.exception.ProjectException;
import com.puang.global.response.code.GeneralErrorCode;
import com.puang.global.security.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * Checks email and password, then issues an access token and a refresh token.
     * Wrong email and wrong password return the same error on purpose,
     * so nobody can find out which emails are registered.
     */
    public TokenResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .filter(found -> passwordEncoder.matches(password, found.getPassword()))
                .orElseThrow(() -> new ProjectException(UserErrorCode.INVALID_CREDENTIALS));
        return issueTokens(user.getId());
    }

    /**
     * Issues a new token pair from a valid refresh token.
     */
    public TokenResponse refresh(String refreshToken) {
        Long userId = jwtProvider.getUserIdFromRefreshToken(refreshToken);
        // The account may have been deleted after the refresh token was issued
        if (!userRepository.existsById(userId)) {
            throw new ProjectException(GeneralErrorCode.INVALID_TOKEN);
        }
        return issueTokens(userId);
    }

    private TokenResponse issueTokens(Long userId) {
        return new TokenResponse(
                jwtProvider.createAccessToken(userId),
                jwtProvider.createRefreshToken(userId),
                jwtProvider.getAccessTokenExpirationSeconds()
        );
    }
}
