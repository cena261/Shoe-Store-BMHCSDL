package com.cena.shoestore.shoestore_api.service;

import com.cena.shoestore.shoestore_api.dto.response.VpdDebugResponse;
import com.cena.shoestore.shoestore_api.entity.Order;
import com.cena.shoestore.shoestore_api.repository.OrderRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VpdDebugService {

    OrderRepository orderRepository;
    DbSessionContextService dbSessionContextService;

    @Transactional(readOnly = true)
    public VpdDebugResponse getVpdDebugInfo() {
        log.info("Retrieving VPD debug information");

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String clientIdentifier = dbSessionContextService.getCurrentClientIdentifier();

        List<Order> orders = orderRepository.findAll();

        List<VpdDebugResponse.OrderSummary> orderSummaries = orders.stream()
                .map(order -> VpdDebugResponse.OrderSummary.builder()
                        .orderId(order.getOrderId())
                        .userId(order.getUser() != null ? order.getUser().getUserId() : null)
                        .userEmail(order.getUser() != null ? order.getUser().getEmail() : "N/A")
                        .orderDate(order.getOrderDate() != null ? order.getOrderDate().toString() : "N/A")
                        .totalAmount(order.getTotalAmount())
                        .orderStatus(order.getOrderStatus())
                        .build())
                .collect(Collectors.toList());

        VpdDebugResponse response = VpdDebugResponse.builder()
                .currentClientIdentifier(clientIdentifier != null ? clientIdentifier : "NOT_SET")
                .currentUser(currentUser)
                .totalOrdersVisible(orderSummaries.size())
                .orders(orderSummaries)
                .build();

        log.info("VPD debug info retrieved - User: {}, Client ID: {}, Orders visible: {}",
                currentUser, clientIdentifier, orderSummaries.size());

        return response;
    }
}
