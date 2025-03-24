package com.informatics.supplychain.service;

import com.informatics.supplychain.dto.StockCardDto;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.Assemble;
import com.informatics.supplychain.model.AssembleDetail;
import com.informatics.supplychain.model.SalesorderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.model.StockIn;
import com.informatics.supplychain.model.StockOut;
import com.informatics.supplychain.repository.AssembleDetailRepository;
import com.informatics.supplychain.repository.AssembleRepository;
import com.informatics.supplychain.repository.SalesorderDetailRepository;
import com.informatics.supplychain.repository.StockInRepository;
import com.informatics.supplychain.repository.StockOutRepository;
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
    StockOutRepository stockOutRepository;

    @Autowired
    AssembleDetailRepository assembleDetailRepository;

    @Autowired
    AssembleRepository assembleRepository;

    @Autowired
    SalesorderDetailRepository salesorderDetailRepository;

    public List<StockCardDto> getStockCardByItemId(Integer itemId) {
        List<StockIn> stockIns = stockInRepository.findByItem_Id(itemId); //rawmats in
        List<AssembleDetail> assembleDetails = assembleDetailRepository.findByRawMaterial_Id(itemId); //rawmats out
        List<StockOut> stockOuts = stockOutRepository.findByItemId(itemId); //rawmats out

        List<Assemble> assembles = assembleRepository.findByFinishProduct_Id(itemId); //finishproduct in
        List<SalesorderDetail> salesorderDetails = salesorderDetailRepository.findByItem_Id(itemId); //finishproduct out

        List<StockCardDto> stockCardList = new ArrayList<>();

        //rawmats in transactions
        for (StockIn stockIn : stockIns.stream().filter(s -> TransactionStatusEnum.COMPLETED.equals(s.getStatus())).collect(Collectors.toList())) {
            stockCardList.add(new StockCardDto(
                    stockIn.getItem().getId(),
                    stockIn.getTransactionDate(),
                    stockIn.getTransactionNo(),
                    stockIn.getQuantity(), //in
                    0.0, //out
                    0.0, //running balace
                    stockIn.getCreatedDateTime()
            ));
        }

        //rawmats out transactions
        for (AssembleDetail detail : assembleDetails.stream().filter(d -> TransactionStatusEnum.COMPLETED.equals(d.getAssemble().getStatus())).collect(Collectors.toList())) {
            stockCardList.add(new StockCardDto(
                    detail.getRawMaterial().getId(),
                    detail.getAssemble().getTransactionDate(),
                    detail.getAssemble().getTransactionNo(),
                    0.0, //in
                    detail.getUsedQuantity(), //out
                    0.0, //running balace
                    detail.getAssemble().getCreatedDateTime()
            ));
        }

        //rawmats amd finish product out transactions
        for (StockOut stockOut : stockOuts.stream().filter(s -> TransactionStatusEnum.COMPLETED.equals(s.getStatus())).collect(Collectors.toList())) {
            stockCardList.add(new StockCardDto(
                    stockOut.getItem().getId(),
                    stockOut.getTransactionDate(),
                    stockOut.getTransactionNo(),
                    0.0, //in
                    stockOut.getQuantity(), //out
                    0.0, //running balace
                    stockOut.getCreatedDateTime()
            ));
        }

        //finish product in transactions
        for (Assemble assemble : assembles.stream().filter(d -> TransactionStatusEnum.COMPLETED.equals(d.getStatus())).collect(Collectors.toList())) {
            stockCardList.add(new StockCardDto(
                    assemble.getFinishProduct().getId(),
                    assemble.getTransactionDate(),
                    assemble.getTransactionNo(),
                    assemble.getAssembleQuantity(), //in
                    0.0, //out
                    0.0, //running balace
                    assemble.getCreatedDateTime()
            ));
        }

        //finish product out transactions
        for (SalesorderDetail salesorderdetail : salesorderDetails.stream().filter(d -> TransactionStatusEnum.COMPLETED.equals(d.getSalesorder().getStatus())).collect(Collectors.toList())) {
            stockCardList.add(new StockCardDto(
                    salesorderdetail.getId(),
                    salesorderdetail.getSalesorder().getOrderDate(),
                    salesorderdetail.getSalesorder().getSalesorderNo(),
                    0.0, //in
                    salesorderdetail.getOrderQuantity(), //out
                    0.0, //running balace
                    salesorderdetail.getSalesorder().getCreatedDateTime()
            ));
        }

        stockCardList.sort(Comparator.comparing(StockCardDto::getDate).thenComparing(StockCardDto::getCreatedDateTime));
        
        //update running balance based on sorted data
        double runningBalance = 0;
        for (StockCardDto stockCard : stockCardList) {
            runningBalance += stockCard.getStockIn() - stockCard.getStockOut();
            stockCard.setRunningBalance(runningBalance);
        }
        return stockCardList;
    }

}
