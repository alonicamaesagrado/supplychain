package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.AssembleDto;
import com.informatics.supplychain.dto.AssembleDetailDto;
import com.informatics.supplychain.dto.ItemDto;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Assemble;
import com.informatics.supplychain.model.AssembleDetail;
import com.informatics.supplychain.model.Inventory;
import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.model.ItemComponents;
import com.informatics.supplychain.repository.ItemComponentsRepository;
import com.informatics.supplychain.repository.ItemRepository;
import com.informatics.supplychain.service.AssembleDetailService;
import com.informatics.supplychain.service.AssembleService;
import com.informatics.supplychain.service.InventoryService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class AssembleController {

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private AssembleDetailService assembleDetailService;

    @Autowired
    private ItemComponentsRepository itemComponentsRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    InventoryService inventoryService;

    @GetMapping("v1/assemble")
    public ResponseEntity<?> getAssemble(@RequestParam String transactionNo) {
        var assemble = assembleService.findByTransactionNo(transactionNo);

        if (assemble == null) {
            return ResponseEntity.status(404).body("Transaction not found.");
        }
        return ResponseEntity.ok(new AssembleDto(assemble));
    }

    @GetMapping("v1/assembleList")
    public ResponseEntity<?> getAssembleList(@RequestParam(required = false) Integer itemId, @RequestParam(required = false) TransactionStatusEnum status) {
        List<Assemble> assembleList;

        if (itemId != null && status != null) {
            assembleList = assembleService.findByfinishProductAndStatus(itemId, status);
        } else if (itemId != null) {
            assembleList = assembleService.findByFinishProductId(itemId);
        } else if (status != null) {
            assembleList = assembleService.findByStatus(status);
        } else {
            assembleList = assembleService.findAll();
        }

        if (itemId != null && assembleList.isEmpty()) {
            return ResponseEntity.status(404).body("No assembly records found for itemId: " + itemId);
        }

        List<AssembleDto> assembleDtos = assembleList.stream().map(AssembleDto::new).collect(Collectors.toList());
        return ResponseEntity.ok(assembleDtos);
    }

    @PostMapping("v1/assemble")
    public ResponseEntity<?> saveAssemble(@RequestBody AssembleDto assembleDto) throws Exception {
        if (assembleDto.getTransactionDate() == null) {
            return ResponseEntity.status(404).body("Transaction date cannot be null.");
        }
        //generated transaction number
        String yearMonth = assembleDto.getTransactionDate().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = assembleService.getNextSeriesNumber(yearMonth);
        String transactionNo = "AS" + yearMonth + String.format("%04d", series);
        assembleDto.setTransactionNo(transactionNo);

        //validations
        Item finishProduct = itemRepository.findById(assembleDto.getFinishProduct().getId()).orElseThrow(() -> new RuntimeException("Finish Product not found"));
        var assemble = new Assemble(assembleDto);
        assemble.setStatus(TransactionStatusEnum.DRAFT);
        assemble.setCreatedDateTime(LocalDateTime.now());
        List<ItemComponents> itemComponents = itemComponentsRepository.findByFinishProductId(assembleDto.getFinishProduct().getId());
        if (itemComponents.isEmpty()) {
            return ResponseEntity.status(404).body("No raw materials found for the provided finish product.");
        }

        //checking of raw mats stocks
        List<String> insufficientStocks = new ArrayList<>();
        for (ItemComponents component : itemComponents) {
            double requiredQuantity = assembleDto.getAssemble_quantity() * component.getQuantity();
            Inventory inventory = inventoryService.findByItemId(component.getRawMaterial().getId())
                    .stream().findFirst().orElse(null);
            double balance = (inventory != null) ? (inventory.getInQuantity() - inventory.getOutQuantity()) : 0.0;

            if (balance < requiredQuantity) {
                double lackingQuantity = requiredQuantity - balance;
                insufficientStocks.add(component.getRawMaterial().getDescription() + " - lacking of " + lackingQuantity + " qty");
            }
        }
        if (!insufficientStocks.isEmpty()) {
            return ResponseEntity.status(400).body("Insufficient stock for raw materials: \n" + String.join(", \n", insufficientStocks));
        }

        //creation of assemble
        List<AssembleDetail> assembleDetails = new ArrayList<>();
        for (ItemComponents component : itemComponents) {
            AssembleDetail assembleDetail = new AssembleDetail();
            assembleDetail.setRawMaterial(component.getRawMaterial());
            double usedQuantity = assembleDto.getAssemble_quantity() * component.getQuantity();
            assembleDetail.setUsedQuantity(usedQuantity);
            assembleDetail.setAssemble(assemble);
            assembleDetails.add(assembleDetail);
        }
        assemble = assembleService.save(assemble);
        for (AssembleDetail assembleDetail : assembleDetails) {
            assembleDetailService.save(assembleDetail);
        }

        //creation of inventory for finish product
        Inventory finishProductInventory = inventoryService.findByItemId(finishProduct.getId()).stream().findFirst().orElse(null);
        if (finishProductInventory != null) {
            finishProductInventory.setInQuantity(finishProductInventory.getInQuantity() + assembleDto.getAssemble_quantity());
            inventoryService.save(finishProductInventory);
        } else {
            Inventory newFinishProductInventory = new Inventory();
            newFinishProductInventory.setItem(finishProduct);
            newFinishProductInventory.setItemType(finishProduct.getCategory());
            newFinishProductInventory.setInQuantity(assembleDto.getAssemble_quantity());
            newFinishProductInventory.setOutQuantity(0.0);
            inventoryService.save(newFinishProductInventory);
        }

        //creation of inventory for raw mats
        for (AssembleDetail assembleDetail : assembleDetails) {
            Item rawMaterial = assembleDetail.getRawMaterial();
            List<Inventory> existingInventories = inventoryService.findByItemId(rawMaterial.getId());
            if (!existingInventories.isEmpty()) {
                Inventory existingInventory = existingInventories.get(0);
                existingInventory.setOutQuantity(existingInventory.getOutQuantity() + assembleDetail.getUsedQuantity());
                inventoryService.save(existingInventory);
            } else {
                Inventory rawMaterialInventory = new Inventory();
                rawMaterialInventory.setItem(rawMaterial);
                rawMaterialInventory.setItemType(rawMaterial.getCategory());
                rawMaterialInventory.setOutQuantity(assembleDetail.getUsedQuantity());
                inventoryService.save(rawMaterialInventory);
            }
        }

        //response body
        AssembleDto responseDto = new AssembleDto(assemble);
        responseDto.setFinishProduct(assembleDto.getFinishProduct());
        List<AssembleDetailDto> detailDtos = new ArrayList<>();
        for (AssembleDetail assembleDetail : assembleDetails) {
            AssembleDetailDto detailDto = new AssembleDetailDto();
            detailDto.setRawMaterial(new ItemDto(assembleDetail.getRawMaterial()));
            detailDto.setUsedQuantity(assembleDetail.getUsedQuantity());
            detailDtos.add(detailDto);
        }
        responseDto.setDetails(detailDtos);
        return ResponseEntity.ok(responseDto);
    }

}
