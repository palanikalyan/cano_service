package com.dfpt.canonical.service;

import com.dfpt.canonical.dto.ProcessingResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Service
public class FileWatcherService {

    private static final Logger logger = LoggerFactory.getLogger(FileWatcherService.class);

    @Value("${input.directory:./input}")
    private String inputDirectory;
    
    @Value("${file.processing.threads:5}")
    private int processingThreads;

    @Autowired
    private TradeProcessingService tradeProcessingService;
    
    private Set<String> processedFiles = new HashSet<>();
    private Set<String> processingFiles = new HashSet<>();
    private WatchService watchService;
    private Path inputPath;
    private ExecutorService fileProcessingExecutor;

   
    @PostConstruct
    public void init() {
        try {
            // Initialize thread pool for parallel file processing
            fileProcessingExecutor = Executors.newFixedThreadPool(processingThreads);
            logger.info("File processing thread pool initialized with {} threads", processingThreads);
            
            inputPath = Paths.get(inputDirectory).toAbsolutePath();
            File inputDir = inputPath.toFile();
            
            if (!inputDir.exists()) {
                inputDir.mkdirs();
                logger.info("Created input directory: {}", inputPath);
            }
            
            logger.info("Monitoring: {}", inputPath);
        
            startWatching();
            
        } catch (Exception e) {
            logger.error("Error initializing FileWatcher", e);
        }
    }
    
    @PreDestroy
    public void cleanup() {
        try {
            logger.info("Shutting down file processing executor...");
            fileProcessingExecutor.shutdown();
            if (!fileProcessingExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                fileProcessingExecutor.shutdownNow();
            }
            
            if (watchService != null) {
                watchService.close();
            }
            logger.info("File watcher service shut down successfully");
        } catch (Exception e) {
            logger.error("Error during cleanup", e);
            fileProcessingExecutor.shutdownNow();
        }
    }

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

    private void handleNewFile(String fileName) {
        synchronized (processingFiles) {
            if (processedFiles.contains(fileName) || processingFiles.contains(fileName)) {
                return;
            }
            
            if (!isSupportedFile(fileName)) {
                return;
            }
            
            // Mark file as being processed
            processingFiles.add(fileName);
        }
        
        logger.info("New file: {}", fileName);
        
        // Submit file processing task to thread pool
        fileProcessingExecutor.submit(() -> {
            try {
                // Small delay to ensure file is fully written
                Thread.sleep(500);
                
                logger.info("Processing file: {} on thread: {}", fileName, Thread.currentThread().getName());
                
                ProcessingResult result = tradeProcessingService.processTradeFile(fileName);
                
                logger.info("Processed: {} - Status: {} - Success: {}/{} on thread: {}", 
                    fileName, result.getStatus(), result.getSuccessCount(), 
                    result.getTotalRecords(), Thread.currentThread().getName());
                
                synchronized (processingFiles) {
                    processedFiles.add(fileName);
                    processingFiles.remove(fileName);
                }
                
            } catch (Exception e) {
                logger.error("Error processing file: {} on thread: {}", 
                    fileName, Thread.currentThread().getName(), e);
                
                synchronized (processingFiles) {
                    processingFiles.remove(fileName);
                }
            }
        });
    }

    @Scheduled(fixedDelay = 30000)
    public void scanForUnprocessedFiles() {
        try {
            File inputDir = inputPath.toFile();
            File[] files = inputDir.listFiles();
            
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        
                        synchronized (processingFiles) {
                            if (!processedFiles.contains(fileName) && 
                                !processingFiles.contains(fileName) &&
                                isSupportedFile(fileName)) {
                                logger.info("Found unprocessed: {}", fileName);
                                handleNewFile(fileName);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error scanning directory", e);
        }
    }

    private boolean isSupportedFile(String fileName) {
        String lower = fileName.toLowerCase();
        return lower.endsWith(".json") || 
               lower.endsWith(".xml") || 
               lower.endsWith(".csv") || 
               lower.endsWith(".txt");
    }
    

    public void resetProcessedFiles() {
        synchronized (processingFiles) {
            processedFiles.clear();
            processingFiles.clear();
        }
    }
    
    public int getProcessedFileCount() {
        synchronized (processingFiles) {
            return processedFiles.size();
        }
    }
    
    public int getProcessingFileCount() {
        synchronized (processingFiles) {
            return processingFiles.size();
        }
    }
}
