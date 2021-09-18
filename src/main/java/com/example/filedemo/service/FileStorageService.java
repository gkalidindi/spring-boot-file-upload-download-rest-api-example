package com.example.filedemo.service;

import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    private final Path relativeFileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        this.relativeFileStorageLocation = Paths.get(fileStorageProperties.getRelativeUploadDir());

        try {
            //Files.createDirectories(this.fileStorageLocation);
            Files.createDirectories(this.relativeFileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public String storeFile(String fileName, String base64EncodedFileContent) {
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            //Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Path targetLocation = this.relativeFileStorageLocation.resolve(fileName);
            System.out.println("Absolute path location: " + targetLocation.toAbsolutePath().normalize());
            System.out.println("Relative path location: " + targetLocation);
            byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedFileContent);
            /*InputStream fileInputStream = new ByteArrayInputStream(decodedBytes);
            Files.copy(fileInputStream, targetLocation.toAbsolutePath().normalize(), StandardCopyOption.REPLACE_EXISTING);*/
            File file = new File("/app/uploadedFiles/" + fileName);
            OutputStream os = new FileOutputStream(file);

            // Starts writing the bytes in it
            os.write(decodedBytes);

            // Close the file
            os.close();
            System.out.println("File uploaded.....");
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
