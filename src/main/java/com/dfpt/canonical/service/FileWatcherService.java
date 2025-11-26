package com.dfpt.canonical.service;

import com.dfpt.canonical.dto.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Watches input folder and processes new trade files automatically
 * Two detection methods:
 * 1. Real-time: WatchService for instant file detection
 * 2. Scheduled: Backup scan every 30 seconds
 */
@Service
public class FileWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(FileWatcherService.class);

    @Value("${input.directory:./input}")
    private String inputDirectory;

    @Autowired
    private TradeProcessingService tradeProcessingService;

    // Track processed files to avoid duplicates
    private Set<String> processedFiles = new HashSet<>();
    private WatchService watchService;
    private Path inputPath;

    /**
     * Initialize watcher when application starts
     */
    @PostConstruct
    public void init() {
        try {
            inputPath = Paths.get(inputDirectory).toAbsolutePath();
            File inputDir = inputPath.toFile();
            
            // Create directory if missing
            if (!inputDir.exists()) {
                inputDir.mkdirs();
                logger.info("Created input directory: {}", inputPath);
            }
            
            logger.info("Monitoring: {}", inputPath);
            
            // Start real-time watching
            startWatching();
            
        } catch (Exception e) {
            logger.error("Error initializing FileWatcher", e);
        }
    }

    /**
     * Start real-time file watching
     */
    private void startWatching() {
        Thread watchThread = new Thread(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                inputPath.register(watchService, 
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
                
                logger.info("File watcher started");
                
                while (true) {
                    WatchKey key = watchService.take();
                    
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }
                        
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        String fileName = ev.context().toString();
                        
                        handleNewFile(fileName);
                    }
                    
                    key.reset();
                }
                
            } catch (Exception e) {
                logger.error("Error in file watcher", e);
            }
        });
        
        watchThread.setDaemon(true);
        watchThread.start();
    }

    /**
     * Process newly detected file
     */
    private void handleNewFile(String fileName) {
        // Skip duplicates
        if (processedFiles.contains(fileName)) {
            return;
        }
        
        // Only process supported formats
        if (!isSupportedFile(fileName)) {
            return;
        }
        
        logger.info("New file: {}", fileName);
        
        // Wait for file to finish writing
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Process the file
        ProcessingResult result = tradeProcessingService.processTradeFile(fileName);
        
        logger.info("Processed: {} - Status: {} - Success: {}/{}", 
            fileName, result.getStatus(), result.getSuccessCount(), result.getTotalRecords());
        
        processedFiles.add(fileName);
    }

    /**
     * Backup scan for missed files (runs every 30 seconds)
     */
    @Scheduled(fixedDelay = 30000)
    public void scanForUnprocessedFiles() {
        try {
            File inputDir = inputPath.toFile();
            File[] files = inputDir.listFiles();
            
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && !processedFiles.contains(file.getName())) {
                        if (isSupportedFile(file.getName())) {
                            logger.info("Found unprocessed: {}", file.getName());
                            handleNewFile(file.getName());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error scanning directory", e);
        }
    }

    /**
     * Check if file format is supported
     */
    private boolean isSupportedFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".json") || 
               lower.endsWith(".xml") || 
               lower.endsWith(".csv") || 
               lower.endsWith(".txt");
    }
    
    /**
     * Clear processed files (for testing)
     */
    public void resetProcessedFiles() {
        processedFiles.clear();
    }
}
