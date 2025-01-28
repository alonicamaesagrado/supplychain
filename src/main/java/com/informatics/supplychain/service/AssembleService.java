package com.informatics.supplychain.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.informatics.supplychain.model.Assemble;
import com.informatics.supplychain.repository.AssembleRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AssembleService {

    @Autowired
    AssembleRepository assembleRepository;

    public Assemble findById(Integer id) {
        return assembleRepository.findById(id).orElse(null);
    }

    public List<Assemble> findAll() {
        return assembleRepository.findAll();
    }

    public Assemble save(Assemble assemble) {
        return assembleRepository.save(assemble);
    }

    public int getNextSeriesNumber(String yearMonth) {
        String lastTransactionNo = assembleRepository.findLastTransactionNoByYearMonth(yearMonth);
        if (lastTransactionNo == null || lastTransactionNo.isEmpty()) {
            return 1;
        }
        String seriesPart = lastTransactionNo.substring(lastTransactionNo.length() - 4);
        int nextSeries = Integer.parseInt(seriesPart) + 1;
        return nextSeries;
    }
}
