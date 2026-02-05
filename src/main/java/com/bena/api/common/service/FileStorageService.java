package com.bena.api.common.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/*
 * واجهة عامة لخدمات تخزين الملفات.
 * تُستخدم لحفظ الملفات المرفوعة، حذفها، تحميلها،
 * والحصول على الرابط المباشر لها بغض النظر عن طريقة التخزين الفعلية.
 */

public interface FileStorageService {
    
    String store(MultipartFile file, String folder) throws IOException;
    
    void delete(String filePath);
    
    String getUrl(String filePath);
    
    byte[] load(String filePath) throws IOException;
}
