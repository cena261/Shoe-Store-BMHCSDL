package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.Product;
import com.cena.shoestore.shoestore_api.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProduct(Product product);

    void deleteByProduct(Product product);
}
