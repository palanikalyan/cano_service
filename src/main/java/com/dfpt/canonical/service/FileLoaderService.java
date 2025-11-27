package com.dfpt.canonical.service;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileLoaderService {

    public File load(String fileName) {
        return new File("input/" + fileName);
    }
}
