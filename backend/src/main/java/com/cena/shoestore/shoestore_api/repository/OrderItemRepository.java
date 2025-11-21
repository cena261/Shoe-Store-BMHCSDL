package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
