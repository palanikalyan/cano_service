package com.dfpt.canonical.dto;

import com.dfpt.canonical.model.CanonicalTrade;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Result of file processing operation
 * Contains success/failure counts and processed trades
 */
public class ProcessingResult {
    
    private String fileName;
    private String format;
    private LocalDateTime processedAt;
    private int totalRecords;
    private int successCount;
    private int failedCount;
    private int publishedCount;
    private List<CanonicalTrade> processedTrades;
    private List<String> errors;
    private String status; // SUCCESS, PARTIAL_SUCCESS, FAILED, ERROR
    
    public ProcessingResult() {
        this.processedTrades = new ArrayList<>();
        this.errors = new ArrayList<>();
        this.processedAt = LocalDateTime.now();
    }

    // Getters and Setters
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public int getPublishedCount() {
        return publishedCount;
    }

    public void setPublishedCount(int publishedCount) {
        this.publishedCount = publishedCount;
    }

    public List<CanonicalTrade> getProcessedTrades() {
        return processedTrades;
    }

    public void setProcessedTrades(List<CanonicalTrade> processedTrades) {
        this.processedTrades = processedTrades;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    // Helper methods
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    public void addProcessedTrade(CanonicalTrade trade) {
        this.processedTrades.add(trade);
    }

    @Override
    public String toString() {
        return "ProcessingResult{" +
                "fileName='" + fileName + '\'' +
                ", format='" + format + '\'' +
                ", processedAt=" + processedAt +
                ", totalRecords=" + totalRecords +
                ", successCount=" + successCount +
                ", failedCount=" + failedCount +
                ", publishedCount=" + publishedCount +
                ", status='" + status + '\'' +
                ", errorsCount=" + errors.size() +
                '}';
    }
}
