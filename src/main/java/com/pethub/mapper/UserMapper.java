package com.pethub.mapper;

import com.pethub.dto.response.AddressResponse;
import com.pethub.dto.response.UserProfileResponse;
import com.pethub.entity.Address;
import com.pethub.entity.Role;
import com.pethub.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserProfileResponse toProfileResponse(User user) {
        if (user == null) return null;

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setEnabled(user.isEnabled());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getRoles() != null) {
            response.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        }

        if (user.getAddresses() != null) {
            response.setAddresses(user.getAddresses().stream().map(this::toAddressResponse).collect(Collectors.toList()));
        }

        return response;
    }

    public AddressResponse toAddressResponse(Address address) {
        if (address == null) return null;

        return new AddressResponse(
                address.getId(),
                address.getFullName(),
                address.getPhone(),
                address.getStreetAddress(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault()
        );
    }
}
