package com.auctions.service.filestorage;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileStorageService {

    String storeFile(MultipartFile file);
    void deleteFile(String fileName);
    Path getFilePath(String fileName);
}
