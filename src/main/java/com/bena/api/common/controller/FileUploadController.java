package com.bena.api.common.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.common.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller لرفع الملفات
 */
@RestController
@RequestMapping("/v1/upload")
@RequiredArgsConstructor
@Tag(name = "File Upload", description = "رفع الملفات")
public class FileUploadController {
    
    private final FileUploadService fileUploadService;
    
    @PostMapping("/image")
    @Operation(summary = "رفع صورة", description = "رفع صورة جديدة")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "images") String folder) {
        
        try {
            String path = fileUploadService.uploadImage(file, folder);
            String url = fileUploadService.getFileUrl(path);
            
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", url);
            
            return ResponseEntity.ok(ApiResponse.success(result, "تم رفع الصورة بنجاح"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("فشل رفع الصورة: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/video")
    @Operation(summary = "رفع فيديو", description = "رفع فيديو جديد")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "videos") String folder) {
        
        try {
            String path = fileUploadService.uploadVideo(file, folder);
            String url = fileUploadService.getFileUrl(path);
            
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", url);
            
            return ResponseEntity.ok(ApiResponse.success(result, "تم رفع الفيديو بنجاح"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("فشل رفع الفيديو: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/file")
    @Operation(summary = "رفع ملف", description = "رفع ملف (صورة أو فيديو)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "files") String folder) {
        
        try {
            String path = fileUploadService.uploadFile(file, folder);
            String url = fileUploadService.getFileUrl(path);
            
            Map<String, String> result = new HashMap<>();
            result.put("path", path);
            result.put("url", url);
            
            return ResponseEntity.ok(ApiResponse.success(result, "تم رفع الملف بنجاح"));
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("فشل رفع الملف: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping
    @Operation(summary = "حذف ملف", description = "حذف ملف مرفوع")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@RequestParam String path) {
        boolean deleted = fileUploadService.deleteFile(path);
        
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success(null, "تم حذف الملف بنجاح"));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("فشل حذف الملف"));
        }
    }
}
