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

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main service that processes trade files
 * Flow: Read File → Parse → Validate → Save → Publish to Queue
 */
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
    private OutboxService outboxService;

    @Autowired
    private QueuePublisherService queuePublisherService;
    
    @Autowired
    private FixedWidthParserService fixedWidthParserService;

    /**
     * Process a trade file
     */
    public ProcessingResult processTradeFile(String fileName) {
        ProcessingResult result = new ProcessingResult();
        result.setFileName(fileName);
        
        try {
            // Step 1: Load and parse file
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
            
            // Step 2: Process each trade
            int successCount = 0;
            int failedCount = 0;
            int publishedCount = 0;
            
            for (ExternalTradeDTO trade : trades) {
                try {
                    // Transform to canonical model
                    CanonicalTrade canonical = mapperService.mapFromJson(trade);
                    
                    // Validate (simple check)
                    if (canonical.getAmount() == null || canonical.getFundCode() == null) {
                        failedCount++;
                        result.addError("Invalid trade: " + canonical.getOrderId());
                        continue;
                    }
                    
                    // Save to database
                    CanonicalTrade saved = tradeRepository.save(canonical);
                    
                    // Create outbox event
                    outboxService.create(saved);
                    
                    // Publish to ActiveMQ
                    try {
                        queuePublisherService.publishToQueue(saved);
                        publishedCount++;
                    } catch (Exception e) {
                        logger.warn("Queue publish failed: {}", e.getMessage());
                    }
                    
                    successCount++;
                    result.addProcessedTrade(saved);
                    
                } catch (Exception e) {
                    failedCount++;
                    result.addError("Processing error: " + e.getMessage());
                    logger.error("Trade processing failed", e);
                }
            }
            
            // Step 3: Set final status
            result.setSuccessCount(successCount);
            result.setFailedCount(failedCount);
            result.setPublishedCount(publishedCount);
            
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
    
    /**
     * Parse file based on format (json/xml/csv/txt)
     */
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
    
    /**
     * Parse JSON file (handles both array and single object)
     */
    private List<ExternalTradeDTO> parseJsonFile(File file) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Try array first
            ExternalTradeDTO[] array = mapper.readValue(file, ExternalTradeDTO[].class);
            return Arrays.asList(array);
        } catch (Exception e) {
            // Single object
            ExternalTradeDTO single = mapper.readValue(file, ExternalTradeDTO.class);
            return Arrays.asList(single);
        }
    }
    
    /**
     * Parse XML file (handles both multiple orders and single order)
     */
    private List<ExternalTradeDTO> parseXmlFile(File file) throws Exception {
        XmlMapper mapper = new XmlMapper();
        try {
            // Try wrapper with multiple orders
            ExternalTradeListDTO listDTO = mapper.readValue(file, ExternalTradeListDTO.class);
            return listDTO.getOrders();
        } catch (Exception e) {
            // Single order
            ExternalTradeDTO single = mapper.readValue(file, ExternalTradeDTO.class);
            return Arrays.asList(single);
        }
    }
    
    /**
     * Parse CSV file
     */
    private List<ExternalTradeDTO> parseCsvFile(File file) throws Exception {
        try (FileReader reader = new FileReader(file)) {
            CsvToBean<ExternalTradeDTO> csvToBean = new CsvToBeanBuilder<ExternalTradeDTO>(reader)
                    .withType(ExternalTradeDTO.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();
            return csvToBean.parse();
        }
    }
    
    /**
     * Get file extension
     */
    private String getFileFormat(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1);
        }
        return "unknown";
    }
}

