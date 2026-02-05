package com.bena.api.module.project.service;

import com.bena.api.module.project.dto.BidCreateRequest;
import com.bena.api.module.project.dto.BidResponse;
import com.bena.api.module.project.entity.Bid;
import com.bena.api.module.project.repository.BidRepository;
import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BidService {

    private final BidRepository bidRepository;
    private final UserRepository userRepository;
    private final ProjectNotificationService notificationService;

    @Transactional
    public BidResponse createBid(UUID providerId, BidCreateRequest request) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));

        User client = userRepository.findById(request.getClientId())
                .orElseThrow(() -> new RuntimeException("العميل غير موجود"));

        Bid bid = Bid.builder()
                .client(client)
                .provider(provider)
                .title(request.getTitle())
                .description(request.getDescription())
                .serviceType(request.getServiceType())
                .offeredPrice(request.getOfferedPrice())
                .estimatedDurationDays(request.getEstimatedDurationDays())
                .proposalDetails(request.getProposalDetails())
                .locationCity(request.getLocationCity())
                .locationArea(request.getLocationArea())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .status(Bid.BidStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();

        bid = bidRepository.save(bid);
        notificationService.notifyNewBid(bid);

        return mapToResponse(bid);
    }

    @Transactional(readOnly = true)
    public BidResponse getBidById(UUID bidId) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));
        return mapToResponse(bid);
    }

    @Transactional(readOnly = true)
    public Page<BidResponse> getClientBids(UUID clientId, Pageable pageable) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("العميل غير موجود"));
        return bidRepository.findByClient(client, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<BidResponse> getProviderBids(UUID providerId, Pageable pageable) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));
        return bidRepository.findByProvider(provider, pageable)
                .map(this::mapToResponse);
    }

    @Transactional
    public BidResponse acceptBid(UUID bidId, String clientResponse) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));

        bid.setStatus(Bid.BidStatus.ACCEPTED);
        bid.setClientResponse(clientResponse);
        bid.setResponseDate(LocalDateTime.now());

        bid = bidRepository.save(bid);
        notificationService.notifyBidAccepted(bid);

        return mapToResponse(bid);
    }

    @Transactional
    public BidResponse rejectBid(UUID bidId, String clientResponse) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("العرض غير موجود"));

        bid.setStatus(Bid.BidStatus.REJECTED);
        bid.setClientResponse(clientResponse);
        bid.setResponseDate(LocalDateTime.now());

        bid = bidRepository.save(bid);
        notificationService.notifyBidRejected(bid);

        return mapToResponse(bid);
    }

    private BidResponse mapToResponse(Bid bid) {
        return BidResponse.builder()
                .id(bid.getId())
                .clientId(bid.getClient().getId())
                .clientName(bid.getClient().getFullName())
                .providerId(bid.getProvider().getId())
                .providerName(bid.getProvider().getFullName())
                .title(bid.getTitle())
                .description(bid.getDescription())
                .serviceType(bid.getServiceType())
                .offeredPrice(bid.getOfferedPrice())
                .estimatedDurationDays(bid.getEstimatedDurationDays())
                .proposalDetails(bid.getProposalDetails())
                .status(bid.getStatus())
                .clientResponse(bid.getClientResponse())
                .responseDate(bid.getResponseDate())
                .convertedToProjectId(bid.getConvertedToProjectId())
                .locationCity(bid.getLocationCity())
                .locationArea(bid.getLocationArea())
                .latitude(bid.getLatitude())
                .longitude(bid.getLongitude())
                .expiresAt(bid.getExpiresAt())
                .createdAt(bid.getCreatedAt())
                .updatedAt(bid.getUpdatedAt())
                .build();
    }
}
