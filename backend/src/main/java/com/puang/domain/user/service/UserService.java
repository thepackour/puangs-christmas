package com.puang.domain.user.service;

import com.puang.domain.user.dto.CreateUserResponse;
import com.puang.domain.user.dto.MyInfoResponse;
import com.puang.domain.user.dto.SignUpRequest;
import com.puang.domain.user.entity.User;
import com.puang.domain.user.exception.UserErrorCode;
import com.puang.domain.user.repository.UserRepository;
import com.puang.global.exception.ProjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Signs up a new user. The password is stored as a BCrypt hash.
     */
    @Transactional
    public CreateUserResponse signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ProjectException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        User user = User.create(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.nickname()
        );
        return CreateUserResponse.from(userRepository.save(user));
    }

    public MyInfoResponse getMyInfo(Long userId) {
        return userRepository.findById(userId)
                .map(MyInfoResponse::from)
                .orElseThrow(() -> new ProjectException(UserErrorCode.USER_NOT_FOUND));
    }
}
