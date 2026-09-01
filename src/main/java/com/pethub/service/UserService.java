package com.pethub.service;

import com.pethub.dto.request.AddressRequest;
import com.pethub.dto.request.PasswordChangeRequest;
import com.pethub.dto.request.UserProfileUpdateRequest;
import com.pethub.dto.response.AddressResponse;
import com.pethub.dto.response.UserProfileResponse;

import java.util.List;

public interface UserService {
    UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request);
    void changePassword(Long userId, PasswordChangeRequest request);

    List<AddressResponse> getUserAddresses(Long userId);
    AddressResponse addAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    AddressResponse setDefaultAddress(Long userId, Long addressId);
}
