package com.dfpt.canonical.service;

import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Loads files from the input directory
 */
@Service
public class FileLoaderService {

    /**
     * Load a file from input directory
     */
    public File load(String fileName) {
        return new File("input/" + fileName);
    }
}
