package com.informatics.supplychain.service;

import com.informatics.supplychain.dto.StockCardDto;
import com.informatics.supplychain.enums.TransactionStatusEnum;
import com.informatics.supplychain.model.AssembleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.model.StockIn;
import com.informatics.supplychain.repository.AssembleDetailRepository;
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

    public List<StockCardDto> getStockCardByItemId(Integer itemId) {
        List<StockIn> stockIns = stockInRepository.findByItem_Id(itemId);
        List<AssembleDetail> assembleDetails = assembleDetailRepository.findByRawMaterial_Id(itemId);

        List<StockCardDto> stockCardList = new ArrayList<>();
        int runningBalance = 0;

        //in transactions
        for (StockIn stock : stockIns.stream().filter(s -> TransactionStatusEnum.COMPLETED.equals(s.getStatus())).collect(Collectors.toList())) {
            runningBalance += stock.getQuantity().intValue();
            stockCardList.add(new StockCardDto(
                    stock.getItem().getId(),
                    stock.getTransactionDate(),
                    stock.getTransactionNo(),
                    stock.getQuantity(),
                    0.0,
                    runningBalance
            ));
        }

        //out transactions
        for (AssembleDetail detail : assembleDetails.stream().filter(d -> TransactionStatusEnum.COMPLETED.equals(d.getAssemble().getStatus())).collect(Collectors.toList())) {
            runningBalance -= detail.getUsedQuantity().intValue();
            stockCardList.add(new StockCardDto(
                    detail.getRawMaterial().getId(),
                    detail.getAssemble().getTransactionDate(),
                    detail.getAssemble().getTransactionNo(),
                    0.0,
                    detail.getUsedQuantity(),
                    runningBalance
            ));
        }
        stockCardList.sort(Comparator.comparing(StockCardDto::getDate));

        return stockCardList;
    }

}
