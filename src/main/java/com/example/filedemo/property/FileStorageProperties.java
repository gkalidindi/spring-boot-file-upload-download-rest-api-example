package com.example.filedemo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;
    private String relativeUploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getRelativeUploadDir() {
        return relativeUploadDir;
    }

    public void setRelativeUploadDir(String relativeUploadDir) {
        this.relativeUploadDir = relativeUploadDir;
    }
}
