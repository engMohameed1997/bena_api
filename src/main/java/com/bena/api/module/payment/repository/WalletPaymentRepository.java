package com.bena.api.module.payment.repository;

import com.bena.api.module.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("walletPaymentRepository")
public interface WalletPaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    Page<Payment> findByWorkerIdOrderByCreatedAtDesc(Long workerId, Pageable pageable);
}
