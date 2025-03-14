package com.informatics.supplychain.service;

import com.informatics.supplychain.dto.StockCardDto;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Assemble;
import com.informatics.supplychain.model.AssembleDetail;
import com.informatics.supplychain.model.SalesorderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.model.StockIn;
import com.informatics.supplychain.repository.AssembleDetailRepository;
import com.informatics.supplychain.repository.AssembleRepository;
import com.informatics.supplychain.repository.SalesorderDetailRepository;
import com.informatics.supplychain.repository.StockInRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StockCardService {

    @Autowired
    StockInRepository stockInRepository;

    @Autowired
    AssembleDetailRepository assembleDetailRepository;
    
    @Autowired
    AssembleRepository assembleRepository;
    
    @Autowired
    SalesorderDetailRepository salesorderDetailRepository;

    public List<StockCardDto> getStockCardByItemId(Integer itemId) {
        List<StockIn> stockIns = stockInRepository.findByItem_Id(itemId); //rawmats in
        List<AssembleDetail> assembleDetails = assembleDetailRepository.findByRawMaterial_Id(itemId); //rawmats out
        List<Assemble> assembles = assembleRepository.findByFinishProduct_Id(itemId); //finishproduct in
        List<SalesorderDetail> salesorderDetails = salesorderDetailRepository.findByItem_Id(itemId); //finishproduct out

        List<StockCardDto> stockCardList = new ArrayList<>();
        double runningBalance = 0;

        //rawmats in transactions
        for (StockIn stock : stockIns.stream().filter(s -> TransactionStatusEnum.COMPLETED.equals(s.getStatus())).collect(Collectors.toList())) {
            runningBalance += stock.getQuantity();
            stockCardList.add(new StockCardDto(
                    stock.getItem().getId(),
                    stock.getTransactionDate(),
                    stock.getTransactionNo(),
                    stock.getQuantity(), //in
                    0.0, //out
                    runningBalance
            ));
        }

        //rawmats out transactions
        for (AssembleDetail detail : assembleDetails.stream().filter(d -> TransactionStatusEnum.COMPLETED.equals(d.getAssemble().getStatus())).collect(Collectors.toList())) {
            runningBalance -= detail.getUsedQuantity();
            stockCardList.add(new StockCardDto(
                    detail.getRawMaterial().getId(),
                    detail.getAssemble().getTransactionDate(),
                    detail.getAssemble().getTransactionNo(),
                    0.0, //in
                    detail.getUsedQuantity(), //out
                    runningBalance
            ));
        }
        
        //finish product in transactions
        for (Assemble assemble : assembles.stream().filter(d -> TransactionStatusEnum.COMPLETED.equals(d.getStatus())).collect(Collectors.toList())) {
            runningBalance += assemble.getAssemble_quantity();
            stockCardList.add(new StockCardDto(
                    assemble.getFinishProduct().getId(),
                    assemble.getTransactionDate(),
                    assemble.getTransactionNo(),
                    assemble.getAssemble_quantity(), //in
                    0.0, //out
                    runningBalance
            ));
        }
        
        //finish product out transactions
        for (SalesorderDetail salesorderdetail : salesorderDetails.stream().filter(d -> TransactionStatusEnum.COMPLETED.equals(d.getSalesorder().getOrderDate())).collect(Collectors.toList())) {
            runningBalance -= salesorderdetail.getOrderQuantity();
            stockCardList.add(new StockCardDto(
                    salesorderdetail.getId(),
                    salesorderdetail.getSalesorder().getOrderDate(),
                    salesorderdetail.getSalesorder().getSalesorderNo(),
                    0.0, //in
                    salesorderdetail.getOrderQuantity(), //out
                    runningBalance
            ));
        }
        
        stockCardList.sort(Comparator.comparing(StockCardDto::getDate));
        return stockCardList;
    }

}
