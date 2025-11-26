package com.dfpt.canonical.service;

import com.dfpt.canonical.dto.ExternalTradeDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses fixed-width format files (124 characters per line)
 * Field positions:
 * 1-1     Originator Type
 * 2-5     Firm Number
 * 6-9     Fund Number
 * 10-10   Transaction Type
 * 11-26   Transaction ID
 * 27-34   Trade Date (ddMMyyyy)
 * 35-50   Dollar Amount (implied 2 decimals)
 * 51-70   Client Account Number
 * 71-90   Client Name
 * 91-99   SSN/EIN
 * 100-107 Date of Birth (ddMMyyyy)
 * 108-108 KYC Flag
 * 109-124 Share Quantity (no decimals)
 */
@Service
public class FixedWidthParserService {

    public List<ExternalTradeDTO> parseFixedWidthFile(File file) throws IOException {
        List<ExternalTradeDTO> trades = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Remove trailing pipe
                if (line.endsWith("|")) {
                    line = line.substring(0, line.length() - 1);
                }
                
                // Validate minimum length
                if (line.length() < 124) {
                    continue;
                }
                
                // Parse the line
                ExternalTradeDTO dto = parseLine(line);
                trades.add(dto);
            }
        }
        
        return trades;
    }
    
    /**
     * Parse a single fixed-width line
     */
    private ExternalTradeDTO parseLine(String line) {
        ExternalTradeDTO dto = new ExternalTradeDTO();
        
        // Extract fields by position
        dto.setOriginatorType(extract(line, 1, 1));
        dto.setFirmNumber(extract(line, 2, 5));
        dto.setFundNumber(extract(line, 6, 9));
        dto.setTransactionType(extract(line, 10, 10));
        dto.setTransactionId(extract(line, 11, 26));
        dto.setTradeDate(extract(line, 27, 34));
        dto.setDollarAmount(parseAmount(extract(line, 35, 50), 2)); // 2 decimals
        dto.setClientAccountNo(extract(line, 51, 70));
        dto.setClientName(extract(line, 71, 90));
        dto.setSsn(extract(line, 91, 99));
        dto.setDob(extract(line, 100, 107));
        dto.setKyc(extract(line, 108, 108));
        dto.setShareQuantity(parseAmount(extract(line, 109, 124), 0)); // no decimals
        
        // Map to legacy fields
        dto.setOrderId(dto.getTransactionId());
        dto.setFundCode(dto.getFundNumber());
        dto.setInvestorName(dto.getClientName());
        dto.setTxnType(dto.getTransactionType());
        dto.setAmount(dto.getDollarAmount());
        dto.setUnits(dto.getShareQuantity());
        
        return dto;
    }
    
    /**
     * Extract field from line (positions are 1-indexed)
     */
    private String extract(String line, int start, int end) {
        // Convert to 0-indexed
        int startIdx = start - 1;
        int endIdx = end;
        
        if (startIdx < 0 || endIdx > line.length()) {
            return "";
        }
        
        return line.substring(startIdx, endIdx).trim();
    }
    
    /**
     * Parse amount with implied decimals
     * Example: "0000000000125000" with 2 decimals → 1250.00
     */
    private BigDecimal parseAmount(String value, int decimals) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            long longValue = Long.parseLong(value);
            BigDecimal amount = BigDecimal.valueOf(longValue);
            
            // Apply decimal places
            if (decimals > 0) {
                amount = amount.divide(BigDecimal.TEN.pow(decimals));
            }
            
            return amount;
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
