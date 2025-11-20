package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.request.LoginRequest;
import com.cena.shoestore.shoestore_api.dto.request.RegisterRequest;
import com.cena.shoestore.shoestore_api.dto.response.AuthResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserResponse;
import com.cena.shoestore.shoestore_api.entity.Role;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.entity.UserRole;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.RoleRepository;
import com.cena.shoestore.shoestore_api.repository.UserRepository;
import com.cena.shoestore.shoestore_api.repository.UserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;
    AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .createdAt(Instant.now())
                .isActive(1)
                .build();

        user = userRepository.save(user);

        Role defaultRole = roleRepository.findByRoleName("USER")
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .roleName("USER")
                            .build();
                    return roleRepository.save(newRole);
                });

        UserRole userRole = UserRole.builder()
                .userId(user.getUserId())
                .roleId(defaultRole.getRoleId())
                .assignedAt(Instant.now())
                .build();

        userRoleRepository.save(userRole);

        List<String> roles = List.of("USER");

        org.springframework.security.core.userdetails.UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .authorities("ROLE_USER")
                        .build();

        String token = jwtService.generateToken(userDetails, user.getUserId(), roles);

        UserResponse userResponse = mapToUserResponse(user, Set.of("USER"));

        log.info("User registered successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (user.getIsActive() != 1) {
            throw new AppException(ErrorCode.USER_INACTIVE);
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.replace("ROLE_", ""))
                .collect(Collectors.toList());

        String token = jwtService.generateToken(
                (org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal(),
                user.getUserId(),
                roles
        );

        user.setLastLogin(Instant.now());
        userRepository.save(user);

        UserResponse userResponse = mapToUserResponse(user, Set.copyOf(roles));

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .user(userResponse)
                .build();
    }

    private UserResponse mapToUserResponse(User user, Set<String> roles) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .isActive(user.getIsActive())
                .roles(roles)
                .build();
    }
}
