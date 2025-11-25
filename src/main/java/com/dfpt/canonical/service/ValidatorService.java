package com.dfpt.canonical.service;

import com.dfpt.canonical.model.CanonicalTrade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ValidatorService {

    public void validate(CanonicalTrade trade) {
        if (trade.getAmount() == null || trade.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid Amount");
        }
        if (trade.getFundCode() == null || trade.getFundCode().isEmpty()) {
            throw new RuntimeException("Missing Fund Code");
        }
    }
}
