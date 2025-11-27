package com.dfpt.canonical.service;

import com.dfpt.canonical.dto.ExternalTradeDTO;
import com.dfpt.canonical.dto.ExternalTradeListDTO;
import com.dfpt.canonical.dto.ProcessingResult;
import com.dfpt.canonical.model.CanonicalTrade;
import com.dfpt.canonical.repository.CanonicalTradeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

@Service
public class TradeProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(TradeProcessingService.class);

    @Autowired
    private FileLoaderService fileLoaderService;

    @Autowired
    private MapperService mapperService;

    @Autowired
    private CanonicalTradeRepository tradeRepository;

    @Autowired
    private FixedWidthParserService fixedWidthParserService;

    public ProcessingResult processTradeFile(String fileName) {
        ProcessingResult result = new ProcessingResult();
        result.setFileName(fileName);
        
        try {
            
            String format = getFileFormat(fileName);
            result.setFormat(format);
            
            File file = fileLoaderService.load(fileName);
            List<ExternalTradeDTO> trades = parseFile(file, format);
            result.setTotalRecords(trades.size());
            
            if (trades.isEmpty()) {
                result.setStatus("FAILED");
                result.addError("No records found");
                return result;
            }
            
            
            int successCount = 0;
            int failedCount = 0;
            
            for (int i = 0; i < trades.size(); i++) {
                ExternalTradeDTO trade = trades.get(i);
                try {
                    trade.setClientAccountNo(i + 1);
                    
                    CanonicalTrade canonical = mapperService.mapFromJson(trade);
                    
                    String txnType = canonical.getTransactionType();
                    boolean isValid = true;

                    if ("B".equalsIgnoreCase(txnType)) {
                        if (canonical.getDollarAmount() == null || 
                            canonical.getDollarAmount().compareTo(BigDecimal.ZERO) <= 0 ||
                            canonical.getFundNumber() == null) {
                            isValid = false;
                        }
                    } else if ("S".equalsIgnoreCase(txnType)) {
                        if (canonical.getShareQuantity() == null || 
                            canonical.getShareQuantity().compareTo(BigDecimal.ZERO) <= 0 ||
                            canonical.getFundNumber() == null) {
                            isValid = false;
                        }
                    } else {
                        isValid = false;
                    }

                    if (!isValid) {
                        failedCount++;
                        result.addError("Invalid trade: " + canonical.getTransactionId());
                        continue;
                    }
                    
                    CanonicalTrade saved = tradeRepository.save(canonical);
                    
                    successCount++;
                    result.addProcessedTrade(saved);
                    
                } catch (Exception e) {
                    failedCount++;
                    result.addError("Processing error: " + e.getMessage());
                    logger.error("Trade processing failed", e);
                }
            }
            
            
            result.setSuccessCount(successCount);
            result.setFailedCount(failedCount);
            
            if (failedCount == 0) {
                result.setStatus("SUCCESS");
            } else if (successCount > 0) {
                result.setStatus("PARTIAL_SUCCESS");
            } else {
                result.setStatus("FAILED");
            }
            
            logger.info("Processing completed: {}", result);
            
        } catch (Exception e) {
            result.setStatus("ERROR");
            result.addError("File processing error: " + e.getMessage());
            logger.error("Error processing file: {}", fileName, e);
        }
        
        return result;
    }
    
    private List<ExternalTradeDTO> parseFile(File file, String format) throws Exception {
        List<ExternalTradeDTO> trades = new ArrayList<>();
        
        switch (format.toLowerCase()) {
            case "json":
                trades = parseJsonFile(file);
                break;

            case "xml":
                trades = parseXmlFile(file);
                break;

            case "csv":
                trades = parseCsvFile(file);
                break;
                
            case "txt":
                trades = fixedWidthParserService.parseFixedWidthFile(file);
                break;

            default:
                throw new IllegalArgumentException("Unsupported format: " + format);
        }
        
        return trades;
    }
    
    private List<ExternalTradeDTO> parseJsonFile(File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();

        if (file.length() == 0) {
            return Collections.emptyList();
        }

        try {
            ExternalTradeDTO[] array = mapper.readValue(file, ExternalTradeDTO[].class);
            return Arrays.asList(array);
        } catch (Exception arrayException) {
            try {
                ExternalTradeListDTO wrapped = mapper.readValue(file, ExternalTradeListDTO.class);
                if (wrapped != null && wrapped.getOrders() != null && !wrapped.getOrders().isEmpty()) {
                    return wrapped.getOrders();
                }
            } catch (Exception wrappedException) {
            }
            
            try {
                ExternalTradeDTO single = mapper.readValue(file, ExternalTradeDTO.class);
                return Collections.singletonList(single);
            } catch (Exception singleException) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    List<ExternalTradeDTO> list = new ArrayList<>();
                    String line;

                    while ((line = br.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty()) continue;

                        ExternalTradeDTO dto = mapper.readValue(line, ExternalTradeDTO.class);
                        list.add(dto);
                    }

                    if (!list.isEmpty()) return list;
                } catch (Exception jsonLinesEx) {
                }

                throw new Exception(
                    "Invalid JSON format in file: " + file.getName() 
                    + ". Expected JSON array, single object, wrapped array, or JSON lines."
                );
            }
        }
    }
    
    
    private List<ExternalTradeDTO> parseXmlFile(File file) throws Exception {
        XmlMapper mapper = new XmlMapper();
        try {

            ExternalTradeListDTO listDTO = mapper.readValue(file, ExternalTradeListDTO.class);
            return listDTO.getOrders();
        } catch (Exception e) {
        
            ExternalTradeDTO single = mapper.readValue(file, ExternalTradeDTO.class);
            return Arrays.asList(single);
        }
    }
    
    
    private List<ExternalTradeDTO> parseCsvFile(File file) throws Exception {
        try (FileReader reader = new FileReader(file)) {
            CsvToBean<ExternalTradeDTO> csvToBean = new CsvToBeanBuilder<ExternalTradeDTO>(reader)
                    .withType(ExternalTradeDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }
    
    
    private String getFileFormat(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1);
        }
        return "unknown";
    }
}

