package com.cena.shoestore.shoestore_api.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DacAccessResponse {
    List<DatabaseRole> databaseRoles;
    List<AccessibleObject> accessibleObjects;
    List<SafeUserData> sampleUsersViewData;
    List<OrderSummaryData> sampleOrdersViewData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class DatabaseRole {
        String grantedRole;
        String adminOption;
        String defaultRole;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class AccessibleObject {
        String objectName;
        String objectType;
        String privilege;
        String grantable;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class SafeUserData {
        Long userId;
        String fullName;
        String createdAt;
        String lastLogin;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class OrderSummaryData {
        Long orderId;
        Long userId;
        String orderDate;
        String totalAmount;
        String orderStatus;
    }
}
