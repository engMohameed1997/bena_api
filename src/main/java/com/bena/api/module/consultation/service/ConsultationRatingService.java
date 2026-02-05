package com.bena.api.module.consultation.service;

import com.bena.api.module.chat.entity.Message;
import com.bena.api.module.chat.repository.MessageRepository;
import com.bena.api.module.consultation.dto.ConsultationMessageRatingRequest;
import com.bena.api.module.consultation.dto.ConsultationMessageRatingResponse;
import com.bena.api.module.consultation.entity.ConsultationMessageRating;
import com.bena.api.module.consultation.repository.ConsultationMessageRatingRepository;
import com.bena.api.module.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultationRatingService {

    private final ConsultationMessageRatingRepository ratingRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public ConsultationMessageRatingResponse rateMessage(Long messageId, ConsultationMessageRatingRequest request, User principal) {
        if (principal == null || principal.getId() == null) {
            throw new RuntimeException("يرجى تسجيل الدخول أولاً");
        }
        if (messageId == null) {
            throw new RuntimeException("بيانات غير صحيحة");
        }
        if (request == null || request.getRating() == null) {
            throw new RuntimeException("يرجى إدخال التقييم");
        }

        int rating = Math.max(1, Math.min(5, request.getRating()));

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("الرسالة غير موجودة"));

        if (message.getConversation() == null || message.getConversation().getId() == null) {
            throw new RuntimeException("البيانات غير موجودة");
        }

        if (message.getSenderType() != Message.SenderType.WORKER) {
            throw new RuntimeException("يمكنك تقييم ردود المختص فقط");
        }

        UUID userId = principal.getId();
        UUID conversationUserId = message.getConversation().getUser() != null ? message.getConversation().getUser().getId() : null;
        if (conversationUserId == null || !conversationUserId.equals(userId)) {
            log.warn("Rating denied: messageId={} principalId={} conversationUserId={}", messageId, userId, conversationUserId);
            throw new RuntimeException("غير مصرح لك بهذا الإجراء");
        }

        Long workerId = message.getConversation().getWorker() != null ? message.getConversation().getWorker().getId() : null;
        if (workerId == null) {
            throw new RuntimeException("البيانات غير موجودة");
        }

        ConsultationMessageRating entity = ratingRepository.findByUserIdAndMessageId(userId, messageId)
                .orElseGet(() -> ConsultationMessageRating.builder()
                        .userId(userId)
                        .messageId(messageId)
                        .workerId(workerId)
                        .build());

        entity.setRating(rating);
        entity.setHelpful(request.getHelpful());
        entity.setWorkerId(workerId);

        ConsultationMessageRating saved = ratingRepository.save(entity);
        return ConsultationMessageRatingResponse.from(saved);
    }
}
