package com.informatics.supplychain.repository;

import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.model.ItemComponents;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemComponentsRepository extends JpaRepository<ItemComponents, Long> {
    
    List<ItemComponents> findByFinishProduct(Item finishProduct);
    
    List<ItemComponents> findByRawMaterial(Item rawMaterial);
    
    List<ItemComponents> findByFinishProductId(Integer finishProductId);
}
