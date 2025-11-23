package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.DacAccessResponse;
import com.cena.shoestore.shoestore_api.exception.AppException;
import com.cena.shoestore.shoestore_api.exception.ErrorCode;
import com.cena.shoestore.shoestore_api.repository.DacRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DacService {

    DacRepository dacRepository;

    @Transactional(readOnly = true)
    public DacAccessResponse getDacInfo() {
        log.info("Retrieving DAC information for current database user");

        try {
            List<DacAccessResponse.DatabaseRole> roles = getDatabaseRoles();

            List<DacAccessResponse.AccessibleObject> accessibleObjects = getAccessibleObjects();

            List<DacAccessResponse.SafeUserData> sampleUsers = getSampleUsersData();
            List<DacAccessResponse.OrderSummaryData> sampleOrders = getSampleOrdersData();

            DacAccessResponse response = DacAccessResponse.builder()
                    .databaseRoles(roles)
                    .accessibleObjects(accessibleObjects)
                    .sampleUsersViewData(sampleUsers)
                    .sampleOrdersViewData(sampleOrders)
                    .build();

            log.info("DAC information retrieved successfully. Roles: {}, Accessible Objects: {}, Sample Users: {}, Sample Orders: {}",
                    roles.size(), accessibleObjects.size(), sampleUsers.size(), sampleOrders.size());

            return response;

        } catch (Exception e) {
            log.error("Failed to retrieve DAC information", e);
            throw new AppException(ErrorCode.DAC_RETRIEVAL_FAILED);
        }
    }

    private List<DacAccessResponse.DatabaseRole> getDatabaseRoles() {
        try {
            List<Object[]> rolesData = dacRepository.findCurrentUserRoles();

            return rolesData.stream()
                    .map(row -> DacAccessResponse.DatabaseRole.builder()
                            .grantedRole(row[0] != null ? row[0].toString() : "")
                            .adminOption(row[1] != null ? row[1].toString() : "NO")
                            .defaultRole(row[2] != null ? row[2].toString() : "NO")
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to retrieve database roles", e);
            return new ArrayList<>();
        }
    }

    private List<DacAccessResponse.AccessibleObject> getAccessibleObjects() {
        try {
            List<Object[]> privilegesData = dacRepository.findCurrentUserTablePrivileges();

            return privilegesData.stream()
                    .map(row -> DacAccessResponse.AccessibleObject.builder()
                            .objectName(row[0] != null ? row[0].toString() : "")
                            .objectType("VIEW") // All DAC objects are views
                            .privilege(row[1] != null ? row[1].toString() : "")
                            .grantable(row[2] != null ? row[2].toString() : "NO")
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Failed to retrieve accessible objects", e);
            return new ArrayList<>();
        }
    }

    private List<DacAccessResponse.SafeUserData> getSampleUsersData() {
        try {
            List<Object[]> usersData = dacRepository.findSampleUsersViewData();

            return usersData.stream()
                    .map(row -> DacAccessResponse.SafeUserData.builder()
                            .userId(row[0] != null ? ((BigDecimal) row[0]).longValue() : null)
                            .fullName(row[1] != null ? row[1].toString() : "")
                            .createdAt(row[2] != null ? ((Timestamp) row[2]).toString() : "")
                            .lastLogin(row[3] != null ? ((Timestamp) row[3]).toString() : "")
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("Failed to retrieve sample users data (view may not be accessible)", e);
            return new ArrayList<>();
        }
    }

    private List<DacAccessResponse.OrderSummaryData> getSampleOrdersData() {
        try {
            List<Object[]> ordersData = dacRepository.findSampleOrdersViewData();

            return ordersData.stream()
                    .map(row -> DacAccessResponse.OrderSummaryData.builder()
                            .orderId(row[0] != null ? ((BigDecimal) row[0]).longValue() : null)
                            .userId(row[1] != null ? ((BigDecimal) row[1]).longValue() : null)
                            .orderDate(row[2] != null ? ((Timestamp) row[2]).toString() : "")
                            .totalAmount(row[3] != null ? row[3].toString() : "")
                            .orderStatus(row[4] != null ? row[4].toString() : "")
                            .build())
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.warn("Failed to retrieve sample orders data (view may not be accessible)", e);
            return new ArrayList<>();
        }
    }
}
