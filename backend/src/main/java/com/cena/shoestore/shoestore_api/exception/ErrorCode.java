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

    PRODUCT_NOT_FOUND(2001, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_INACTIVE(2002, "Product is not active", HttpStatus.BAD_REQUEST),
    VARIANT_NOT_FOUND(2003, "Product variant not found", HttpStatus.NOT_FOUND),
    VARIANT_INACTIVE(2004, "Product variant is not active", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(2005, "Insufficient stock", HttpStatus.BAD_REQUEST),
    INVALID_SLUG(2006, "Product slug already exists", HttpStatus.BAD_REQUEST),
    INVALID_SKU(2007, "Product SKU already exists", HttpStatus.BAD_REQUEST),

    CATEGORY_NOT_FOUND(3001, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(3002, "Category already exists", HttpStatus.BAD_REQUEST),

    ORDER_NOT_FOUND(4001, "Order not found", HttpStatus.NOT_FOUND),
    ORDER_CANNOT_CANCEL(4002, "Order cannot be cancelled", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS(4003, "Invalid order status", HttpStatus.BAD_REQUEST),

    CART_EMPTY(5001, "Cart is empty", HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND(5002, "Cart item not found", HttpStatus.NOT_FOUND),

    ADDRESS_NOT_FOUND(6001, "Address not found", HttpStatus.NOT_FOUND),

    ROLE_NOT_FOUND(7001, "Role not found", HttpStatus.NOT_FOUND),
    ROLE_EXISTED(7002, "Role already exists", HttpStatus.BAD_REQUEST),

    RESET_TOKEN_INVALID(8001, "Reset token is invalid or expired", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_EXPIRED(8002, "Reset token has expired", HttpStatus.BAD_REQUEST),
    RESET_TOKEN_USED(8003, "Reset token has already been used", HttpStatus.BAD_REQUEST),

    REVIEW_EXISTED(9001, "You have already reviewed this product", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_FOUND(9002, "Review not found", HttpStatus.NOT_FOUND),
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
