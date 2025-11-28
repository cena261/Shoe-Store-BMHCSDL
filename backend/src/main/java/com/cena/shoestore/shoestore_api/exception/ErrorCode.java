package com.cena.shoestore.shoestore_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1002, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1003, "User not found", HttpStatus.NOT_FOUND),
    INVALID_EMAIL(1004, "Email format is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "Password must be at least 8 characters", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_CREDENTIALS(1008, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    USER_INACTIVE(1009, "User account is inactive", HttpStatus.FORBIDDEN),
    INCORRECT_CURRENT_PASSWORD(1010, "Current password is incorrect", HttpStatus.BAD_REQUEST),

    PRODUCT_NOT_FOUND(2001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_INACTIVE(2002, "Product is not active", HttpStatus.BAD_REQUEST),
    VARIANT_NOT_FOUND(2003, "Product variant not found", HttpStatus.NOT_FOUND),
    VARIANT_INACTIVE(2004, "Product variant is not active", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(2005, "Insufficient stock", HttpStatus.BAD_REQUEST),
    INVALID_SLUG(2006, "Product slug already exists", HttpStatus.BAD_REQUEST),
    INVALID_SKU(2007, "Product SKU already exists", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(2008, "Product image not found", HttpStatus.NOT_FOUND),

    CATEGORY_NOT_FOUND(3001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(3002, "Category already exists", HttpStatus.BAD_REQUEST),

    ORDER_NOT_FOUND(4001, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_CANNOT_CANCEL(4002, "Order cannot be cancelled", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(4003, "Invalid order status", HttpStatus.BAD_REQUEST),
    ORDER_ACCESS_DENIED(4004, "You do not have access to this order", HttpStatus.FORBIDDEN),
    INVALID_ORDER_STATUS_TRANSITION(4005, "Invalid order status transition", HttpStatus.BAD_REQUEST),
    OUT_OF_STOCK(4006, "Product is out of stock", HttpStatus.BAD_REQUEST),

    CART_EMPTY(5001, "Cart is empty", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(5002, "Cart item not found", HttpStatus.NOT_FOUND),

    ADDRESS_NOT_FOUND(6001, "Address not found", HttpStatus.NOT_FOUND),

    ROLE_NOT_FOUND(7001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(7002, "Role already exists", HttpStatus.BAD_REQUEST),
    ROLE_ALREADY_ASSIGNED(7003, "Role already assigned to user", HttpStatus.BAD_REQUEST),
    ROLE_NOT_ASSIGNED(7004, "Role not assigned to user", HttpStatus.BAD_REQUEST),

    RESET_TOKEN_INVALID(8001, "Reset token is invalid or expired", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED(8002, "Reset token has expired", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_USED(8003, "Reset token has already been used", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_NOT_FOUND(8004, "Reset token not found", HttpStatus.NOT_FOUND),
    RSA_VERIFICATION_FAILED(8005, "RSA signature verification failed", HttpStatus.BAD_REQUEST),
    RSA_ENCRYPTION_FAILED(8006, "RSA encryption operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_SEND_FAILED(8007, "Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR),

    REVIEW_EXISTED(9001, "You have already reviewed this product", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(9002, "Review not found", HttpStatus.NOT_FOUND),

    ORDER_EXPORT_NOT_FOUND(10001, "Order export not found", HttpStatus.NOT_FOUND),
    ORDER_EXPORT_ACCESS_DENIED(10002, "You do not have access to this export", HttpStatus.FORBIDDEN),
    HYBRID_ENCRYPTION_FAILED(10003, "Hybrid encryption operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    HYBRID_DECRYPTION_FAILED(10004, "Hybrid decryption operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    ORDER_EXPORT_SERIALIZATION_FAILED(10005, "Failed to serialize order data", HttpStatus.INTERNAL_SERVER_ERROR),

    SESSION_AUDIT_NOT_FOUND(11001, "Session audit record not found", HttpStatus.NOT_FOUND),
    SESSION_LOGGING_FAILED(11002, "Failed to log session information", HttpStatus.INTERNAL_SERVER_ERROR),
    DB_CONTEXT_RETRIEVAL_FAILED(11003, "Failed to retrieve database context information", HttpStatus.INTERNAL_SERVER_ERROR),

    DAC_RETRIEVAL_FAILED(12001, "Failed to retrieve DAC information", HttpStatus.INTERNAL_SERVER_ERROR),
    DAC_ROLE_QUERY_FAILED(12002, "Failed to query database roles", HttpStatus.INTERNAL_SERVER_ERROR),
    DAC_PRIVILEGE_QUERY_FAILED(12003, "Failed to query database privileges", HttpStatus.INTERNAL_SERVER_ERROR),
    DAC_VIEW_ACCESS_DENIED(12004, "Access to restricted view denied", HttpStatus.FORBIDDEN),

    VPD_CONTEXT_SET_FAILED(13001, "Failed to set VPD context", HttpStatus.INTERNAL_SERVER_ERROR),
    VPD_POLICY_ERROR(13002, "VPD policy execution error", HttpStatus.INTERNAL_SERVER_ERROR),
    VPD_CLIENT_IDENTIFIER_INVALID(13003, "Invalid VPD client identifier", HttpStatus.BAD_REQUEST),

    INVALID_SECURITY_LABEL(14001, "Invalid security label - must be PUBLIC, CONFIDENTIAL, or ADMIN_ONLY", HttpStatus.BAD_REQUEST),
    ORDER_LABEL_UPDATE_FAILED(14002, "Failed to update order security label", HttpStatus.INTERNAL_SERVER_ERROR),
    LABEL_POLICY_VIOLATION(14003, "Access denied due to security label policy", HttpStatus.FORBIDDEN),
    ;

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
