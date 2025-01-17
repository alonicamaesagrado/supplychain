package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.repository.ItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    public Item findByCodeAndStatus(String code, StatusEnum status) {
        return itemRepository.findByCodeAndStatus(code, status);
    }

    public List<Item> findByCategory(String category) {
        return itemRepository.findByCategory(category);
    }
    public List<Item> findByStatus(StatusEnum status) {
        return itemRepository.findByStatus(status);
    }

    public List<Item> findByCategoryAndStatus(String category, StatusEnum status) {
        return itemRepository.findByCategoryAndStatus(category, status);
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }
}
