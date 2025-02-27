package com.informatics.supplychain.service;

import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.model.ItemComponents;
import com.informatics.supplychain.repository.ItemComponentsRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemComponentsService {

    @Autowired
    private ItemComponentsRepository itemComponentsRepository;
    
    public List<ItemComponents> findByFinishProduct(Item finishProduct) {
        return itemComponentsRepository.findByFinishProduct(finishProduct);
    }
    
    public List<ItemComponents> findByRawMaterial(Item rawMaterial) {
        return itemComponentsRepository.findByRawMaterial(rawMaterial);
    }

    public ItemComponents save(ItemComponents itemComponent) {
        return itemComponentsRepository.save(itemComponent);
    }
}
