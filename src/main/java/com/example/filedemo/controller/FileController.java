package com.example.filedemo.controller;

import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file)  {
        String encodedString = null;
        try {
            encodedString = Base64.getEncoder().encodeToString(file.getBytes());
            System.out.println("Base64 encoded string: " + encodedString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Store File
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadFileAsJson")
    public void uploadFileAsJson(@RequestBody Map<String, Object> payload) throws IOException {
        System.out.println("Request Payload: " + payload);
        String fileName = (String) payload.get("fileName");
        String purpose = (String) payload.get("purpose");
        String title = (String) payload.get("title");
        String linkExpiresTime = (String) payload.get("linkExpiresTime");

        // Get Base64 encoded file content and decode it
        String encodedString = (String) payload.get("file");
        fileStorageService.storeFile(fileName, encodedString);

        /*byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        File file = new File("/Users/gkalidindi/Documents/Docmation_Git_Projects/Base64-Java/spring-boot-file-upload-download-rest-api-example/uploadedFiles/Testfile12.jpg");
        OutputStream os = new FileOutputStream(file);

        // Starts writing the bytes in it
        os.write(decodedBytes);
        System.out.println("Successfully" + " byte inserted");

        // Close the file
        os.close();*/

        /*FileUtils.writeByteArrayToFile(new File(outputFileName), decodedBytes);
        String fileName = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());*/
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file))
                .collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
