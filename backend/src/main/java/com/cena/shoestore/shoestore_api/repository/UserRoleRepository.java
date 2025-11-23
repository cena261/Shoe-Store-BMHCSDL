package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.User;
import com.cena.shoestore.shoestore_api.entity.UserRole;
import com.cena.shoestore.shoestore_api.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    @Query("SELECT ur FROM UserRole ur JOIN FETCH ur.role WHERE ur.userId = :userId")
    List<UserRole> findByUserId(@Param("userId") Long userId);

    List<UserRole> findByRoleId(Long roleId);

    boolean existsByUserIdAndRoleId(Long userId, Long roleId);

    void deleteByUserIdAndRoleId(Long userId, Long roleId);

    @Query("SELECT ur.user FROM UserRole ur WHERE ur.roleId = :roleId")
    List<User> findUsersByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT ur.user FROM UserRole ur JOIN ur.role r WHERE r.roleName = :roleName")
    List<User> findUsersByRoleName(@Param("roleName") String roleName);
}
