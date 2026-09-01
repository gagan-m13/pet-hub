package com.pethub.controller;

import com.pethub.dto.request.AddressRequest;
import com.pethub.dto.request.PasswordChangeRequest;
import com.pethub.dto.request.UserProfileUpdateRequest;
import com.pethub.dto.response.AddressResponse;
import com.pethub.dto.response.ApiResponse;
import com.pethub.dto.response.UserProfileResponse;
import com.pethub.security.UserPrincipal;
import com.pethub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse profile = userService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody PasswordChangeRequest request) {
        userService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
    }

    @GetMapping("/addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AddressResponse> addresses = userService.getUserAddresses(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved successfully", addresses));
    }

    @PostMapping("/addresses")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = userService.addAddress(userPrincipal.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Address added successfully", address), HttpStatus.CREATED);
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = userService.updateAddress(userPrincipal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        userService.deleteAddress(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }

    @PutMapping("/addresses/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long id) {
        AddressResponse address = userService.setDefaultAddress(userPrincipal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Default address updated", address));
    }
}
