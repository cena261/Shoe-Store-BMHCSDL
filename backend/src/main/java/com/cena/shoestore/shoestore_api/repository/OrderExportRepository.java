package com.cena.shoestore.shoestore_api.repository;

import com.cena.shoestore.shoestore_api.entity.OrderExport;
import com.cena.shoestore.shoestore_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderExportRepository extends JpaRepository<OrderExport, Long> {

    @Query("SELECT oe FROM OrderExport oe WHERE oe.exportId = :exportId AND oe.createdBy = :user")
    Optional<OrderExport> findByExportIdAndCreatedBy(
            @Param("exportId") Long exportId,
            @Param("user") User user);

    @Query("SELECT oe FROM OrderExport oe WHERE oe.createdBy = :user ORDER BY oe.createdAt DESC")
    List<OrderExport> findByCreatedByOrderByCreatedAtDesc(@Param("user") User user);
}
