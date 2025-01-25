package com.informatics.supplychain.repository;

import com.informatics.supplychain.model.ItemComponents;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemComponentsRepository extends JpaRepository<ItemComponents, Long> {
}
