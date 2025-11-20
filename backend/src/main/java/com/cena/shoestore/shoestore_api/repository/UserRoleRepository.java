package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.UserRole;
import com.cena.shoestore.shoestore_api.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUserId(Long userId);
}
