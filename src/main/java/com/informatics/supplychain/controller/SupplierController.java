package com.informatics.supplychain.controller;

import com.informatics.supplychain.dto.SupplierDto;
import com.informatics.supplychain.enums.StatusEnum;
import com.informatics.supplychain.model.Supplier;
import com.informatics.supplychain.service.SupplierService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
public class SupplierController {
    @Autowired
    SupplierService supplierService;
    
    @GetMapping("v1/supplier")
    ResponseEntity<SupplierDto> getSupplier(@RequestParam String code) {
        var supplier = supplierService.findByCodeAndStatus(code, StatusEnum.ACTIVE);
        return ResponseEntity.ok(new SupplierDto(supplier));
    } 
    
    @GetMapping("v1/supplierList")
    ResponseEntity<List<SupplierDto>> getSupplierList() {
        return ResponseEntity.ok(supplierService.findAll().stream().map(e -> new SupplierDto(e)).collect(Collectors.toList()));
    } 
    
    @PostMapping("v1/supplier")
    ResponseEntity<SupplierDto> saveSupplier(@RequestBody SupplierDto supplierDto) {
        var supplier = new Supplier();
        supplier.setCode(supplierDto.getCode());
        supplier.setName(supplierDto.getName());
        supplier.setAddress(supplierDto.getAddress());
        supplier.setCompany(supplierDto.getCompany());
        return ResponseEntity.ok(new SupplierDto(supplierService.save(supplier)));
    }
}
