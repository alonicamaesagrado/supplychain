package com.informatics.supplychain.service;

import com.informatics.supplychain.model.AssembleDetail;
import com.informatics.supplychain.repository.AssembleDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssembleDetailService {

    @Autowired
    private AssembleDetailRepository assembleDetailRepository;

    public AssembleDetail save(AssembleDetail assembleDetail) {
        return assembleDetailRepository.save(assembleDetail);
    }
}
