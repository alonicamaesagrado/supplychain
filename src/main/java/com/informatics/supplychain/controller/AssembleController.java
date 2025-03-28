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
import com.informatics.supplychain.model.StockIn;
import com.informatics.supplychain.repository.ItemComponentsRepository;
import com.informatics.supplychain.repository.ItemRepository;
import com.informatics.supplychain.service.AssembleDetailService;
import com.informatics.supplychain.service.AssembleService;
import com.informatics.supplychain.service.InventoryService;
import com.informatics.supplychain.service.StockInService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class AssembleController extends BaseController {

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
    
    @Autowired
    StockInService stockInService;

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
    public ResponseEntity<?> saveAssemble(@RequestHeader String usercode, @RequestHeader String token, @RequestBody AssembleDto assembleDto) throws Exception {
        if (!verify(usercode, token)) {
            return ResponseEntity.badRequest().build();
        }
        if (assembleDto.getTransactionDate() == null) {
            return ResponseEntity.status(404).body("Transaction date cannot be null.");
        }
        //generated transaction number
        String yearMonth = assembleDto.getTransactionDate().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = assembleService.getNextSeriesNumber(yearMonth);
        String transactionNo = "AS" + yearMonth + String.format("%04d", series);
        assembleDto.setTransactionNo(transactionNo);

        //creation of assemble_summary
        var finishProduct = itemRepository.findById(assembleDto.getFinishProduct().getId());
        var assemble = new Assemble(assembleDto);
        assemble.setTransactionDate(assembleDto.getTransactionDate());
        assemble.setRemarks(assembleDto.getRemarks());
        assemble.setAssembleQuantity(assembleDto.getAssembleQuantity());
        assemble.setIssuedQuantity(0.0);
        assemble.setReturnQuantity(0.0);
        assemble.setBatchNo(assembleDto.getBatchNo());
        if (assembleDto.getExpiryDate() == null) {
            return ResponseEntity.status(404).body("Value required for expiry date.");
        }
        assemble.setExpiryDate(assembleDto.getExpiryDate());
        assemble.setStatus(TransactionStatusEnum.DRAFT);
        assemble.setCreatedDateTime(LocalDateTime.now());
        assemble.setCreatedBy(usercode);
        
        //validations
        List<ItemComponents> itemComponents = itemComponentsRepository.findByFinishProductId(assembleDto.getFinishProduct().getId());
        if (finishProduct == null) {
            return ResponseEntity.status(404).body("Item does not exist!");
        }
        if (itemComponents.isEmpty()) {
            return ResponseEntity.status(404).body("No raw materials found for the provided finish product.");
        }
        if (assembleDto.getAssembleQuantity() <= 0) {
            return ResponseEntity.status(404).body("Quantity should be greater than zero.");
        }

        //checking of raw mats stocks
        List<String> insufficientStocks = new ArrayList<>();
        for (ItemComponents component : itemComponents) {
            double requiredQuantity = assembleDto.getAssembleQuantity() * component.getQuantity();
            Inventory inventory = inventoryService.findByItemId(component.getRawMaterial().getId()).stream().findFirst().orElse(null);
            double balance = (inventory != null) ? (inventory.getInQuantity() - inventory.getOutQuantity()) : 0.0;

            if (balance < requiredQuantity) {
                double lackingQuantity = requiredQuantity - balance;
                insufficientStocks.add(component.getRawMaterial().getDescription() + " - lacking of " + lackingQuantity + " qty");
            }
        }
        if (!insufficientStocks.isEmpty()) {
            return ResponseEntity.status(400).body("Insufficient stock for raw materials: \n" + String.join(", \n", insufficientStocks));
        }

        //creation of assemble_detail
        List<AssembleDetail> assembleDetails = new ArrayList<>();
        for (ItemComponents component : itemComponents) {
            AssembleDetail assembleDetail = new AssembleDetail();
            assembleDetail.setRawMaterial(component.getRawMaterial());
            double usedQuantity = assembleDto.getAssembleQuantity() * component.getQuantity();
            assembleDetail.setUsedQuantity(usedQuantity);
            assembleDetail.setAssemble(assemble);
            assembleDetails.add(assembleDetail);
        }
        assemble = assembleService.save(assemble);
        for (AssembleDetail assembleDetail : assembleDetails) {
            assembleDetailService.save(assembleDetail);
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
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("v1/assemble/{transactionNo}")
    public ResponseEntity<?> updateAssemble(@PathVariable("transactionNo") String transactionNo, @RequestBody AssembleDto assembleDto) throws Exception {
        var existingTransaction = assembleService.findByTransactionNo(transactionNo);

        //validations
        if (existingTransaction == null) {
            return ResponseEntity.status(404).body("Transaction not found.");
        }
        if (TransactionStatusEnum.COMPLETED.equals(existingTransaction.getStatus())) {
            return ResponseEntity.status(400).body("Cannot edit completed transactions!");
        }
        if (assembleDto.getAssembleQuantity() <= 0) {
            return ResponseEntity.status(404).body("Quantity should be greater than zero.");
        }

        List<AssembleDetail> assembleDetails = assembleDetailService.findByAssemble(existingTransaction);
        List<String> insufficientMaterials = new ArrayList<>();

        double oldAssembleQuantity = existingTransaction.getAssembleQuantity();
        double newAssembleQuantity = assembleDto.getAssembleQuantity();
        double scaleFactor = (oldAssembleQuantity > 0) ? newAssembleQuantity / oldAssembleQuantity : 1;

        //update usedQuantity in assembleDetails
        for (AssembleDetail assembleDetail : assembleDetails) {
            double newUsedQuantity = assembleDetail.getUsedQuantity() * scaleFactor; // i need to update is based on updated assembled qty * item components 
            assembleDetail.setUsedQuantity(newUsedQuantity);
            assembleDetailService.save(assembleDetail);

            // Check raw material inventory
            Item rawMaterial = assembleDetail.getRawMaterial();
            Inventory existingInventory = inventoryService.findByItemId(rawMaterial.getId()).stream().findFirst().orElse(null);

            double currentStock = (existingInventory != null) ? (existingInventory.getInQuantity() - existingInventory.getOutQuantity()) : 0;
            if (currentStock < newUsedQuantity) {
                double lackingQuantity = newUsedQuantity - currentStock;
                insufficientMaterials.add(rawMaterial.getDescription() + " - lacking " + lackingQuantity + " qty");
            }
        }
        if (!insufficientMaterials.isEmpty()) {
            return ResponseEntity.status(400).body("Insufficient stock for raw materials: \n" + String.join(",\n", insufficientMaterials));
        }

        //update inventory once status is COMPLETED
        if (TransactionStatusEnum.COMPLETED.equals(assembleDto.getStatus())) {
            //add finished product inventory
            Inventory finishProductInventory = inventoryService.findByItemId(existingTransaction.getFinishProduct().getId()).stream().findFirst().orElse(null);

            if (finishProductInventory != null) {
                finishProductInventory.setInQuantity(finishProductInventory.getInQuantity() + assembleDto.getAssembleQuantity());
                inventoryService.save(finishProductInventory);
            } else {
                Inventory newFinishProductInventory = new Inventory();
                newFinishProductInventory.setItem(existingTransaction.getFinishProduct());
                newFinishProductInventory.setItemType(existingTransaction.getFinishProduct().getCategory());
                newFinishProductInventory.setInQuantity(assembleDto.getAssembleQuantity());
                newFinishProductInventory.setOutQuantity(0.0);
                inventoryService.save(newFinishProductInventory);
            }

            //deduct inventory for raw materials
            for (AssembleDetail assembleDetail : assembleDetails) {
                Item rawMaterial = assembleDetail.getRawMaterial();
                List<Inventory> existingInventories = inventoryService.findByItemId(rawMaterial.getId());
                List<StockIn> stockInTransactions = stockInService.findByItemId(rawMaterial.getId()).stream().sorted(Comparator.comparing(StockIn::getExpiryDate)).collect(Collectors.toList());

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
            //update issued quantity on stock in to implement FIFO    
            double remainingRequired = assembleDetail.getUsedQuantity();
                for (StockIn stockIn : stockInTransactions) {
                    double availableQuantity = stockIn.getQuantity() - stockIn.getIssuedQuantity() - stockIn.getReturnQuantity();
                    if (availableQuantity > 0) {
                        double usedQuantity = Math.min(remainingRequired, availableQuantity);

                        stockIn.setIssuedQuantity(stockIn.getIssuedQuantity() + usedQuantity);
                        stockInService.save(stockIn);

                        remainingRequired -= usedQuantity;
                        if (remainingRequired <= 0) {
                            break;
                        }
                    }
                }
            }
        }

        //update assemble transaction
        existingTransaction.setRemarks(assembleDto.getRemarks());
        existingTransaction.setBatchNo(assembleDto.getBatchNo());
        existingTransaction.setStatus(assembleDto.getStatus());
        existingTransaction.setAssembleQuantity(newAssembleQuantity);
        assembleService.save(existingTransaction);

        //response
        AssembleDto responseDto = new AssembleDto(existingTransaction);
        responseDto.setFinishProduct(assembleDto.getFinishProduct());
        List<AssembleDetailDto> detailDtos = new ArrayList<>();
        for (AssembleDetail assembleDetail : assembleDetails) {
            AssembleDetailDto detailDto = new AssembleDetailDto();
            detailDto.setRawMaterial(new ItemDto(assembleDetail.getRawMaterial()));
            detailDto.setUsedQuantity(assembleDetail.getUsedQuantity());
            detailDtos.add(detailDto);
        }

        return ResponseEntity.ok(responseDto);
    }
}
