package com.pethub.service.impl;

import com.pethub.dto.request.AddressRequest;
import com.pethub.dto.request.PasswordChangeRequest;
import com.pethub.dto.request.UserProfileUpdateRequest;
import com.pethub.dto.response.AddressResponse;
import com.pethub.dto.response.UserProfileResponse;
import com.pethub.entity.Address;
import com.pethub.entity.User;
import com.pethub.exception.BadRequestException;
import com.pethub.exception.ResourceNotFoundException;
import com.pethub.mapper.UserMapper;
import com.pethub.repository.AddressRepository;
import com.pethub.repository.UserRepository;
import com.pethub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : user.getPhone());

        return userMapper.toProfileResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(userMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Address> existingAddresses = addressRepository.findByUserId(userId);
        boolean makeDefault = request.isDefault() || existingAddresses.isEmpty();

        if (makeDefault && !existingAddresses.isEmpty()) {
            for (Address addr : existingAddresses) {
                addr.setDefault(false);
                addressRepository.save(addr);
            }
        }

        Address address = new Address(
                user,
                request.getFullName().trim(),
                request.getPhone().trim(),
                request.getStreetAddress().trim(),
                request.getCity().trim(),
                request.getState().trim(),
                request.getPostalCode().trim(),
                request.getCountry() != null ? request.getCountry().trim() : "India",
                makeDefault
        );

        return userMapper.toAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (request.isDefault()) {
            List<Address> existingAddresses = addressRepository.findByUserId(userId);
            for (Address addr : existingAddresses) {
                if (!addr.getId().equals(addressId)) {
                    addr.setDefault(false);
                    addressRepository.save(addr);
                }
            }
        }

        address.setFullName(request.getFullName().trim());
        address.setPhone(request.getPhone().trim());
        address.setStreetAddress(request.getStreetAddress().trim());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setCountry(request.getCountry() != null ? request.getCountry().trim() : "India");
        address.setDefault(request.isDefault());

        return userMapper.toAddressResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefaultAddress(Long userId, Long addressId) {
        Address targetAddress = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        List<Address> existingAddresses = addressRepository.findByUserId(userId);
        for (Address addr : existingAddresses) {
            addr.setDefault(addr.getId().equals(addressId));
            addressRepository.save(addr);
        }

        return userMapper.toAddressResponse(targetAddress);
    }
}
