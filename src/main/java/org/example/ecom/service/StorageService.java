package org.example.ecom.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
    Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final Path rootLocation = Paths.get("upload-dir");

    // This method uploads a file (typically an image) sent via an HTTP request
    // and saves it to a local directory on the server.
    public void store(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            throw new RuntimeException("FAIL!");
        }
    }

    // This method loads a file (such as an image or PDF) from disk
    // so it can be returned to the client via an HTTP response.
//    public Resource loadFile(String filename) {
//        try {
//            Path file = rootLocation.resolve(filename);
//            Resource resource = new UrlResource(file.toUri());
//            if (resource.exists() || resource.isReadable()) {
//                return resource;
//            } else {
//                throw new RuntimeException("FAIL!");
//            }
//        } catch (MalformedURLException e) {
//            throw new RuntimeException("FAIL!");
//        }
//    }

    public Resource loadFile(String filename) {
        try {
            Path filePath = rootLocation.resolve(filename).normalize();
            if (!Files.exists(filePath)) {
                // File not found, ignore and return null or a default resource
                System.out.println("File not found: " + filename);
                return null; // or return a default placeholder Resource
            }
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null; // ignore the error
        }
    }


    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }

}
