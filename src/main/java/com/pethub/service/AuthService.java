package com.pethub.service;

import com.pethub.dto.request.LoginRequest;
import com.pethub.dto.request.RegisterRequest;
import com.pethub.dto.response.AuthResponse;
import com.pethub.dto.response.UserProfileResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserProfileResponse getCurrentUserProfile();
}
