package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.Cart;
import com.cena.shoestore.shoestore_api.entity.ProductVariant;
import com.cena.shoestore.shoestore_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
    Optional<Cart> findByUserAndProductVariant(User user, ProductVariant productVariant);
    Optional<Cart> findByCartIdAndUser(Long cartId, User user);
    void deleteByUser(User user);
}
