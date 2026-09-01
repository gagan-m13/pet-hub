package com.pethub.service.impl;

import com.pethub.dto.request.LoginRequest;
import com.pethub.dto.request.RegisterRequest;
import com.pethub.dto.response.AuthResponse;
import com.pethub.dto.response.UserProfileResponse;
import com.pethub.entity.Cart;
import com.pethub.entity.Role;
import com.pethub.entity.User;
import com.pethub.exception.BadRequestException;
import com.pethub.exception.DuplicateResourceException;
import com.pethub.exception.ResourceNotFoundException;
import com.pethub.exception.UnauthorizedException;
import com.pethub.mapper.UserMapper;
import com.pethub.repository.CartRepository;
import com.pethub.repository.RoleRepository;
import com.pethub.repository.UserRepository;
import com.pethub.security.JwtTokenProvider;
import com.pethub.security.UserPrincipal;
import com.pethub.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;

    @Autowired
    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           RoleRepository roleRepository,
                           CartRepository cartRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider tokenProvider,
                           UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("An account with email " + request.getEmail() + " already exists.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setEnabled(true);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user.setRoles(Collections.singleton(userRole));

        User savedUser = userRepository.save(user);

        // Initialize shopping cart for user
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);

        String jwt = tokenProvider.generateTokenFromUserId(savedUser.getId(), savedUser.getEmail());
        List<String> roles = savedUser.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail(), roles);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail().trim().toLowerCase(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(authentication);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new AuthResponse(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getFirstName(),
                userPrincipal.getLastName(),
                userPrincipal.getEmail(),
                roles
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UnauthorizedException("User is not authenticated");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userPrincipal.getId()));

        return userMapper.toProfileResponse(user);
    }
}
