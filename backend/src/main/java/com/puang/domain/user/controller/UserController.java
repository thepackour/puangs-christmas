package com.puang.domain.user.controller;

import com.puang.domain.user.dto.CreateUserResponse;
import com.puang.domain.user.dto.MyInfoResponse;
import com.puang.domain.user.dto.SignUpRequest;
import com.puang.domain.user.service.UserService;
import com.puang.global.response.ApiResponse;
import com.puang.global.response.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Sign up with email, password and nickname.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CreateUserResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntity
                .status(GeneralSuccessCode.CREATED.getHttpStatus())
                .body(ApiResponse.onSuccess(GeneralSuccessCode.CREATED, userService.signUp(request)));
    }

    /**
     * Returns the logged-in user. Useful for checking that the token works.
     */
    @GetMapping("/me")
    public ApiResponse<MyInfoResponse> getMyInfo(@AuthenticationPrincipal Long userId) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, userService.getMyInfo(userId));
    }
}
