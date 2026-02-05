package com.bena.api.module.project.controller;

import com.bena.api.module.project.dto.BidCreateRequest;
import com.bena.api.module.project.dto.BidResponse;
import com.bena.api.module.project.service.BidService;
import com.bena.api.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/bids")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @PostMapping
    public ResponseEntity<BidResponse> createBid(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BidCreateRequest request) {
        BidResponse response = bidService.createBid(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{bidId}")
    public ResponseEntity<BidResponse> getBid(@PathVariable UUID bidId) {
        BidResponse response = bidService.getBidById(bidId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bids")
    public ResponseEntity<Page<BidResponse>> getMyBids(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<BidResponse> bids = bidService.getClientBids(user.getId(), pageable);
        return ResponseEntity.ok(bids);
    }

    @GetMapping("/provider-bids")
    public ResponseEntity<Page<BidResponse>> getProviderBids(
            @AuthenticationPrincipal User user,
            Pageable pageable) {
        Page<BidResponse> bids = bidService.getProviderBids(user.getId(), pageable);
        return ResponseEntity.ok(bids);
    }

    @PostMapping("/{bidId}/accept")
    public ResponseEntity<BidResponse> acceptBid(
            @PathVariable UUID bidId,
            @RequestParam(required = false) String response) {
        BidResponse bidResponse = bidService.acceptBid(bidId, response);
        return ResponseEntity.ok(bidResponse);
    }

    @PostMapping("/{bidId}/reject")
    public ResponseEntity<BidResponse> rejectBid(
            @PathVariable UUID bidId,
            @RequestParam(required = false) String response) {
        BidResponse bidResponse = bidService.rejectBid(bidId, response);
        return ResponseEntity.ok(bidResponse);
    }
}
