package com.ecom.inventory.repository;

import com.ecom.inventory.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    @Modifying
    @Query("DELETE FROM InventoryTransaction t WHERE t.inventory.id  = :inventoryId")
    void deleteByInventoryId(@Param("inventoryId") Long inventoryId);
}