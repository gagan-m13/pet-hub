package com.pethub.service;

import com.pethub.dto.response.DashboardStatsResponse;
import com.pethub.dto.response.PagedResponse;
import com.pethub.dto.response.UserProfileResponse;

public interface AdminService {
    DashboardStatsResponse getDashboardStats();
    PagedResponse<UserProfileResponse> getAllUsers(String query, int page, int size);
    UserProfileResponse toggleUserStatus(Long userId);
}
