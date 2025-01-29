package com.informatics.supplychain.repository;

import com.informatics.supplychain.model.Inventory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Inventory findByItemIdAndItemType(Integer itemId, String itemType);
    
    List<Inventory> findByItemId(@Param("itemId") Integer itemId);
    List<Inventory> findByItemType(String itemType);
}
