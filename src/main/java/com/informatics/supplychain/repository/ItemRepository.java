package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer>{
    
    
    public Item findByCodeAndStatus(String code, StatusEnum status);
}
