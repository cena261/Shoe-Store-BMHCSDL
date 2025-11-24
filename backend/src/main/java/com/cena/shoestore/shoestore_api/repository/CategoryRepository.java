package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
