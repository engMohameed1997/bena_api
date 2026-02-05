package com.bena.api.module.design.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

/**
 * خدمة تخزين الصور في قاعدة البيانات PostgreSQL
 */
@Service
public class ImageStorageService {

    /**
     * حفظ الصورة وإرجاع معرف فريد
     */
    public byte[] saveImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("الملف فارغ");
        }

        // التحقق من نوع الملف
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("الملف يجب أن يكون صورة");
        }

        // التحقق من حجم الملف (أقل من 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("حجم الصورة يجب أن يكون أقل من 5MB");
        }

        return file.getBytes();
    }

    /**
     * تحويل الصورة إلى Base64 للعرض في Flutter
     */
    public String convertToBase64(byte[] imageData) {
        if (imageData == null || imageData.length == 0) {
            return null;
        }
        return Base64.getEncoder().encodeToString(imageData);
    }

    /**
     * الحصول على نوع الصورة من البيانات
     */
    public String getImageType(byte[] imageData) {
        if (imageData == null || imageData.length < 4) {
            return "image/jpeg";
        }

        // التحقق من signature الملف
        if (imageData[0] == (byte) 0xFF && imageData[1] == (byte) 0xD8) {
            return "image/jpeg";
        } else if (imageData[0] == (byte) 0x89 && imageData[1] == (byte) 0x50) {
            return "image/png";
        } else if (imageData[0] == (byte) 0x47 && imageData[1] == (byte) 0x49) {
            return "image/gif";
        }

        return "image/jpeg"; // افتراضي
    }
}
