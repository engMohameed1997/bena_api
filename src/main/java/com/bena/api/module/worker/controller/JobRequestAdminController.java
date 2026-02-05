package com.bena.api.module.worker.controller;

import com.bena.api.common.dto.ApiResponse;
import com.bena.api.module.worker.dto.JobRequestDTO;
import com.bena.api.module.worker.service.JobRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller لإدارة طلبات العمل (Admin Only)
 */
@RestController
@RequestMapping("/v1/admin/job-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class JobRequestAdminController {

    private final JobRequestService jobRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobRequestDTO>>> getAllJobRequests(
            @PageableDefault(sort = "createdAt") Pageable pageable
    ) {
        Page<JobRequestDTO> requests = jobRequestService.getAllJobRequests(pageable);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobRequestDTO>> getJobRequestById(@PathVariable Long id) {
        JobRequestDTO request = jobRequestService.getJobRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(request));
    }
}
