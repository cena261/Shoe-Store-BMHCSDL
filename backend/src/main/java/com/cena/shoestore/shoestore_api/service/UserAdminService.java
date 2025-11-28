package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.UserAdminPageResponse;
import com.cena.shoestore.shoestore_api.dto.response.UserResponse;
import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.UserRepository;
import com.cena.shoestore.shoestore_api.repository.UserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserAdminService {

    UserRepository userRepository;
    UserRoleRepository userRoleRepository;

    @Transactional(readOnly = true)
    public UserAdminPageResponse getUsersForAdmin(
            String search,
            Integer isActive,
            int page,
            int pageSize
    ) {
        log.info("Admin: Fetching users with filters - search: {}, isActive: {}, page: {}, pageSize: {}",
                search, isActive, page, pageSize);

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);

        Page<User> userPage;

        if (search != null && !search.isEmpty()) {
            userPage = userRepository.findAll(
                    (root, query, cb) -> {
                        var predicates = new ArrayList<>();

                        var searchLower = search.toLowerCase();
                        var searchPredicate = cb.or(
                                cb.like(cb.lower(root.get("email")), "%" + searchLower + "%"),
                                cb.like(cb.lower(root.get("fullName")), "%" + searchLower + "%"),
                                cb.like(cb.lower(root.get("phone")), "%" + searchLower + "%")
                        );
                        predicates.add(searchPredicate);

                        if (isActive != null) {
                            predicates.add(cb.equal(root.get("isActive"), isActive));
                        }

                        return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
                    },
                    pageable
            );
        } else {
            userPage = userRepository.findAll(
                    (root, query, cb) -> {
                        if (isActive != null) {
                            return cb.equal(root.get("isActive"), isActive);
                        }
                        return cb.conjunction();
                    },
                    pageable
            );
        }

        List<UserResponse> users = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return UserAdminPageResponse.builder()
                .currentPage(page)
                .pageSize(pageSize)
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .users(users)
                .build();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByIdForAdmin(Long userId) {
        log.info("Admin: Fetching user by ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return mapToUserResponseWithRoles(user);
    }

    @Transactional
    public void setUserActive(Long userId, boolean active) {
        log.info("Admin: Setting user ID {} active status to: {}", userId, active);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setIsActive(active ? 1 : 0);
        userRepository.save(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .isActive(user.getIsActive())
                .build();
    }

    private UserResponse mapToUserResponseWithRoles(User user) {
        var roles = userRoleRepository.findByUserId(user.getUserId()).stream()
                .map(ur -> ur.getRole().getRoleName())
                .collect(Collectors.toSet());

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
