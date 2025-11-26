package com.dfpt.canonical.service;

import com.dfpt.canonical.dto.ExternalTradeDTO;
import com.dfpt.canonical.model.CanonicalTrade;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Maps external DTOs to canonical trade entities
 * Handles date parsing in ddMMyyyy format (e.g., 21012025)
 */
@Service
public class MapperService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    /**
     * Transform external DTO to canonical trade entity
     */
    public CanonicalTrade mapFromJson(ExternalTradeDTO dto) {
        CanonicalTrade trade = new CanonicalTrade();
        trade.setId(UUID.randomUUID());
        
        // Legacy fields (for backward compatibility)
        trade.setOrderId(dto.getOrderId());
        trade.setFundCode(dto.getFundCode());
        trade.setInvestorName(dto.getInvestorName());
        trade.setTxnType(dto.getTxnType());
        trade.setAmount(dto.getAmount());
        trade.setUnits(dto.getUnits());
        
        // New fields (from fixed-width format)
        trade.setOriginatorType(dto.getOriginatorType());
        trade.setFirmNumber(dto.getFirmNumber());
        trade.setFundNumber(dto.getFundNumber());
        trade.setTransactionType(dto.getTransactionType());
        trade.setTransactionId(dto.getTransactionId());
        trade.setDollarAmount(dto.getDollarAmount());
        trade.setClientAccountNo(dto.getClientAccountNo());
        trade.setClientName(dto.getClientName());
        trade.setSsn(dto.getSsn());
        trade.setKyc(dto.getKyc());
        trade.setShareQuantity(dto.getShareQuantity());
        
        // Parse dates (ddMMyyyy format)
        trade.setTradeDate(parseDate(dto.getTradeDate()));
        trade.setDob(parseDate(dto.getDob()));
        
        // Set metadata
        trade.setStatus("RECEIVED");
        trade.setCreatedAt(LocalDateTime.now());
        
        return trade;
    }
    
    /**
     * Parse date string in ddMMyyyy format
     */
    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        
        try {
            return LocalDate.parse(dateStr, DATE_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }
}
