package com.informatics.supplychain.service;

import com.informatics.supplychain.enums.TransactionStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.model.Salesorder;
import com.informatics.supplychain.model.SalesorderDetail;
import com.informatics.supplychain.repository.SalesorderDetailRepository;
import com.informatics.supplychain.repository.SalesorderRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SalesorderService {

    @Autowired
    SalesorderRepository salesorderRepository;
    
    @Autowired
    SalesorderDetailRepository salesorderDetailRepository;

    public Salesorder findById(Integer id) {
        return salesorderRepository.findById(id).orElse(null);
    }

    public List<Salesorder> findAll() {
        return salesorderRepository.findAll();
    }
    
    public Salesorder findBySalesorderNo(String salesorderNo) {
        return salesorderRepository.findBySalesorderNo(salesorderNo);
    }
    
    public List<Salesorder> findByStatus(TransactionStatusEnum status) {
        return salesorderRepository.findByStatus(status);
    }
    
    public List<Salesorder> findByItemId(Integer itemId) {
        return salesorderRepository.findByItemId(itemId);
    }
    
    public List<Salesorder> findByOrderDateBetween(LocalDate fromDate, LocalDate toDate) {
        return salesorderRepository.findByOrderDateBetween(fromDate, toDate );
    }
    
    public List<Salesorder> findByStatusAndOrderDateBetween(TransactionStatusEnum status, LocalDate fromDate, LocalDate toDate) {
        return salesorderRepository.findByStatusAndOrderDateBetween(status, fromDate, toDate );
    }

    public Salesorder save(Salesorder salesorder) {
        return salesorderRepository.save(salesorder);
    }
    
    public void saveDetail(SalesorderDetail detail) {
        salesorderDetailRepository.save(detail);
    }

    public int getNextSeriesNumber(String yearMonth) {
        String lastsalesorderNo = salesorderRepository.findLastSalesorderNoByYearMonth(yearMonth);
        if (lastsalesorderNo == null || lastsalesorderNo.isEmpty()) {
            return 1;
        }
        String seriesPart = lastsalesorderNo.substring(lastsalesorderNo.length() - 4);
        int nextSeries = Integer.parseInt(seriesPart) + 1;
        return nextSeries;
    }
}
