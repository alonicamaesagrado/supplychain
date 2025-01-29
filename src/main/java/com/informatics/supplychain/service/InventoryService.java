package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.model.Inventory;
import com.informatics.supplychain.repository.InventoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    public Inventory findByItemIdAndItemType(Integer itemId, String itemType) {
        return inventoryRepository.findByItemIdAndItemType(itemId, itemType);
    }
    
    public List<Inventory> findByItemId(Integer itemId) {
        return inventoryRepository.findByItemId(itemId);
    }
    
    public List<Inventory> findByItemType(String itemType) {
        return inventoryRepository.findByItemType(itemType);
    }
    
    public List<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Inventory save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
}
