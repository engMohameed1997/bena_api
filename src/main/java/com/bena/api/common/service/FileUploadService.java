package com.bena.api.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * خدمة رفع الملفات
 */
@Service
public class FileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadService.class);
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.upload.max-size:10485760}") // 10MB default
    private long maxFileSize;
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    
    private static final List<String> ALLOWED_VIDEO_TYPES = Arrays.asList(
        "video/mp4", "video/webm", "video/quicktime"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );
    
    /**
     * رفع صورة
     */
    public String uploadImage(MultipartFile file, String subFolder) throws IOException {
        validateFile(file, ALLOWED_IMAGE_TYPES);
        return saveFile(file, subFolder);
    }
    
    /**
     * رفع فيديو
     */
    public String uploadVideo(MultipartFile file, String subFolder) throws IOException {
        validateFile(file, ALLOWED_VIDEO_TYPES);
        return saveFile(file, subFolder);
    }
    
    /**
     * رفع أي ملف مسموح
     */
    public String uploadFile(MultipartFile file, String subFolder) throws IOException {
        List<String> allAllowed = new java.util.ArrayList<>(ALLOWED_IMAGE_TYPES);
        allAllowed.addAll(ALLOWED_VIDEO_TYPES);
        allAllowed.addAll(ALLOWED_DOCUMENT_TYPES);
        validateFile(file, allAllowed);
        return saveFile(file, subFolder);
    }
    
    /**
     * حذف ملف
     */
    public boolean deleteFile(String filePath) {
        try {
            String normalizedRelativePath = normalizeRelativePath(filePath);
            if (normalizedRelativePath == null) {
                return false;
            }

            Path baseDir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path targetPath = baseDir.resolve(normalizedRelativePath).normalize();

            if (!targetPath.startsWith(baseDir)) {
                logger.warn("Blocked delete attempt outside upload dir: {}", filePath);
                return false;
            }

            return Files.deleteIfExists(targetPath);
        } catch (IOException e) {
            logger.error("Error deleting file: {}", filePath, e);
            return false;
        }
    }

    private String normalizeRelativePath(String filePath) {
        if (filePath == null) {
            return null;
        }

        String trimmed = filePath.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String normalized = trimmed.replace("\\", "/");

        if (normalized.startsWith("/uploads/")) {
            normalized = normalized.substring("/uploads/".length());
        }

        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (normalized.contains("..")) {
            return null;
        }

        return normalized;
    }
    
    /**
     * التحقق من صحة الملف
     */
    private void validateFile(MultipartFile file, List<String> allowedTypes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("الملف فارغ");
        }
        
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("حجم الملف أكبر من المسموح (" + (maxFileSize / 1024 / 1024) + "MB)");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("نوع الملف غير مسموح");
        }
    }
    
    /**
     * حفظ الملف
     */
    private String saveFile(MultipartFile file, String subFolder) throws IOException {
        // إنشاء المجلد إذا لم يكن موجوداً
        Path uploadPath = Paths.get(uploadDir, subFolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // إنشاء اسم فريد للملف
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // حفظ الملف
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("File uploaded: {}", filePath);
        
        // إرجاع المسار النسبي
        return subFolder + "/" + newFilename;
    }
    
    /**
     * الحصول على URL الملف
     */
    public String getFileUrl(String relativePath) {
        return "/uploads/" + relativePath;
    }
}
