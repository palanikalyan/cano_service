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

@Service
public class FixedWidthParserService {

    public List<ExternalTradeDTO> parseFixedWidthFile(File file) throws IOException {
        List<ExternalTradeDTO> trades = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                if (line.endsWith("|")) {
                    line = line.substring(0, line.length() - 1);
                }
                
                if (line.length() < 114) {
                    continue;
                }
                
                ExternalTradeDTO dto = parseLine(line);
                trades.add(dto);
            }
        }
        
        return trades;
    }
    
    
    private ExternalTradeDTO parseLine(String line) {
        ExternalTradeDTO dto = new ExternalTradeDTO();
        
        dto.setOriginatorType(extract(line, 1, 1));
        dto.setFirmNumber(parseInteger(extract(line, 2, 5)));
        dto.setFundNumber(parseInteger(extract(line, 6, 9)));
        dto.setTransactionType(extract(line, 10, 10));
        dto.setTransactionId(extract(line, 11, 26));
        dto.setTradeDateTime(extract(line, 27, 40));
        dto.setDollarAmount(parseAmount(extract(line, 41, 56), 2)); 
        dto.setClientAccountNo(null);
        dto.setClientName(extract(line, 77, 96));
        dto.setSsn(extract(line, 97, 105));
        dto.setDob(extract(line, 106, 113));
        dto.setShareQuantity(parseAmount(extract(line, 114, 130), 0)); 
        
        return dto;
    }
    
    
    private String extract(String line, int start, int end) {
        int startIdx = start - 1;
        int endIdx = end;
        
        if (startIdx < 0 || endIdx > line.length()) {
            return "";
        }
        
        return line.substring(startIdx, endIdx).trim();
    }
    
    
    private BigDecimal parseAmount(String value, int decimals) {
        if (value == null || value.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        try {
            long longValue = Long.parseLong(value);
            BigDecimal amount = BigDecimal.valueOf(longValue);
            
            if (decimals > 0) {
                amount = amount.divide(BigDecimal.TEN.pow(decimals));
            }
            
            return amount;
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
    
    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
