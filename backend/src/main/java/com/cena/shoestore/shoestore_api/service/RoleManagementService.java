package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.RoleResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserRolesResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserSummaryResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleManagementService {

    UserRepository userRepository;
    RoleRepository roleRepository;
    UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public UserRolesResponse getUserRoles(Long userId) {
        log.info("Retrieving roles for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);

        List<String> roleNames = userRoles.stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toList());

        return UserRolesResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(roleNames)
                .build();
    }

    @Transactional
    public void assignRole(Long userId, String roleName) {
        log.info("Assigning role {} to user ID: {}", roleName, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        boolean alreadyAssigned = userRoleRepository.existsByUserIdAndRoleId(userId, role.getRoleId());
        if (alreadyAssigned) {
            log.error("Role {} already assigned to user ID: {}", roleName, userId);
            throw new AppException(ErrorCode.ROLE_ALREADY_ASSIGNED);
        }

        UserRole userRole = UserRole.builder()
                .userId(userId)
                .roleId(role.getRoleId())
                .user(user)
                .role(role)
                .assignedAt(Instant.now())
                .build();

        userRoleRepository.save(userRole);

        log.info("Role {} successfully assigned to user ID: {}", roleName, userId);
    }

    @Transactional
    public void revokeRole(Long userId, String roleName) {
        log.info("Revoking role {} from user ID: {}", roleName, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        boolean isAssigned = userRoleRepository.existsByUserIdAndRoleId(userId, role.getRoleId());
        if (!isAssigned) {
            log.error("Role {} not assigned to user ID: {}", roleName, userId);
            throw new AppException(ErrorCode.ROLE_NOT_ASSIGNED);
        }

        userRoleRepository.deleteByUserIdAndRoleId(userId, role.getRoleId());

        log.info("Role {} successfully revoked from user ID: {}", roleName, userId);
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getUsersByRole(String roleName) {
        log.info("Retrieving users with role: {}", roleName);

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        List<User> users = userRoleRepository.findUsersByRoleName(roleName);

        return users.stream()
                .map(user -> UserSummaryResponse.builder()
                        .userId(user.getUserId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .isActive(user.getIsActive())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        log.info("Retrieving all roles");

        List<Role> roles = roleRepository.findAll();

        return roles.stream()
                .map(role -> RoleResponse.builder()
                        .roleId(role.getRoleId())
                        .roleName(role.getRoleName())
                        .build())
                .collect(Collectors.toList());
    }
}
