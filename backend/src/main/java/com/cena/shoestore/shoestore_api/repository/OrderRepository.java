package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.Order;
import com.cena.shoestore.shoestore_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findByUserOrderByOrderDateDesc(@Param("user") User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId AND o.user = :user")
    Optional<Order> findByOrderIdAndUser(@Param("orderId") Long orderId, @Param("user") User user);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);
}
