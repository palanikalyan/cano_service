package com.dfpt.canonical.service;

import com.dfpt.canonical.dto.ExternalTradeDTO;
import com.dfpt.canonical.model.CanonicalTrade;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MapperService {

    public CanonicalTrade mapFromJson(ExternalTradeDTO dto) {
        CanonicalTrade trade = new CanonicalTrade();
        trade.setId(UUID.randomUUID());
        trade.setOrderId(dto.getOrderId());
        trade.setFundCode(dto.getFundCode());
        trade.setInvestorName(dto.getInvestorName());
        trade.setTxnType(dto.getTxnType());
        trade.setAmount(dto.getAmount());
        trade.setUnits(dto.getUnits());
        trade.setStatus("RECEIVED");
        trade.setCreatedAt(LocalDateTime.now());
        return trade;
    }
}
