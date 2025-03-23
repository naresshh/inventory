package com.ecom.inventory.repository;

import com.ecom.inventory.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {
    // Custom query methods if needed
}