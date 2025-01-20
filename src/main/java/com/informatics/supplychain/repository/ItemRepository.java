package com.informatics.supplychain.repository;

import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Item;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Integer>{
    
    Optional<Item> findByCode(String code);
     
    public Item findByCodeAndStatus(String code, StatusEnum status);
    
    List<Item> findByCategory(String category);
    List<Item> findByCategoryAndStatus(String category, StatusEnum status);
    List<Item> findByStatus(StatusEnum status);
}
