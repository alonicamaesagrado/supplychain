package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.AssembleDto;
import com.informatics.supplychain.dto.AssembleDetailDto;
import com.informatics.supplychain.dto.ItemDto;
import com.informatics.supplychain.model.Assemble;
import com.informatics.supplychain.model.AssembleDetail;
import com.informatics.supplychain.model.Item;
import com.informatics.supplychain.model.ItemComponents;
import com.informatics.supplychain.repository.ItemComponentsRepository;
import com.informatics.supplychain.repository.ItemRepository;
import com.informatics.supplychain.service.AssembleDetailService;
import com.informatics.supplychain.service.AssembleService;
import com.informatics.supplychain.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class AssembleController {

    @Autowired
    private AssembleService assembleService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private AssembleDetailService assembleDetailService;

    @Autowired
    private ItemComponentsRepository itemComponentsRepository;

    @Autowired
    private ItemRepository itemRepository;

    @PostMapping("v1/assemble")
    public ResponseEntity<?> saveAssemble(@RequestBody AssembleDto assembleDto) throws Exception {
        if (assembleDto.getTransactionDate() == null) {
            return ResponseEntity.status(404).body("Transaction date cannot be null.");
        }

        String yearMonth = assembleDto.getTransactionDate().format(DateTimeFormatter.ofPattern("yyMM"));
        int series = assembleService.getNextSeriesNumber(yearMonth);
        String transactionNo = "AS" + yearMonth + String.format("%04d", series);
        assembleDto.setTransactionNo(transactionNo);

        Item finishProduct = itemRepository.findById(assembleDto.getFinishProduct().getId()).orElseThrow(() -> new RuntimeException("Finish Product not found"));
        var assemble = new Assemble(assembleDto);
        List<ItemComponents> itemComponents = itemComponentsRepository.findByFinishProductId(assembleDto.getFinishProduct().getId());
        if (itemComponents.isEmpty()) {
            return ResponseEntity.status(404).body("No raw materials found for the provided finish product.");
        }

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
