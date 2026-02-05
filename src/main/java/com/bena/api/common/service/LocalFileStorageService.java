package com.bena.api.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Profile("!production")
@Slf4j
public class LocalFileStorageService implements FileStorageService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${app.upload.max-size:10485760}")
    private long maxFileSize;
    
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    );
    
    private static final List<String> ALLOWED_VIDEO_EXTENSIONS = Arrays.asList(
        ".mp4", ".webm", ".mov"
    );
    
    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        validateFile(file);
        
        Path uploadPath = Paths.get(uploadDir, folder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String extension = getFileExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + extension;
        
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("File stored: {}", filePath);
        
        return folder + "/" + filename;
    }
    
    @Override
    public void delete(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            Files.deleteIfExists(path);
            log.info("File deleted: {}", filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", filePath, e);
        }
    }
    
    @Override
    public String getUrl(String filePath) {
        return baseUrl + "/uploads/" + filePath;
    }
    
    @Override
    public byte[] load(String filePath) throws IOException {
        Path path = Paths.get(uploadDir, filePath);
        return Files.readAllBytes(path);
    }
    
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("الملف فارغ");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                "حجم الملف أكبر من المسموح (" + (maxFileSize / 1024 / 1024) + "MB)"
            );
        }
        
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("اسم الملف غير صالح");
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        
        List<String> allAllowedExtensions = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp", ".mp4", ".webm", ".mov", ".pdf"
        );
        
        if (!allAllowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("نوع الملف غير مسموح");
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("نوع المحتوى غير معروف");
        }
        
        // Security Fix: Allow specific MIME types strictly
        boolean isValidType = contentType.startsWith("image/") || 
                            contentType.startsWith("video/") ||
                            contentType.equals("application/pdf");
                            
        if (!isValidType) {
            throw new IllegalArgumentException("نوع الملف غير مدعوم. المسموح: صور، فيديو، PDF");
        }
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
