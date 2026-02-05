package com.bena.api.module.payment.controller;

import com.bena.api.module.payment.entity.Payment;
import com.bena.api.module.payment.entity.Wallet;
import com.bena.api.module.payment.entity.WalletTransaction;
import com.bena.api.module.payment.service.WalletPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController("walletPaymentController")
@RequestMapping("/v1/wallet-payments")
@RequiredArgsConstructor
public class WalletPaymentController {

    private final WalletPaymentService paymentService;

    /**
     * إنشاء دفعة جديدة
     */
    @PostMapping
    public ResponseEntity<?> createPayment(
            @RequestParam UUID userId,
            @RequestParam Long workerId,
            @RequestParam(required = false) Long jobRequestId,
            @RequestParam BigDecimal amount,
            @RequestParam String paymentMethod) {
        try {
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase());
            Payment payment = paymentService.createPayment(userId, workerId, jobRequestId, amount, method);
            return ResponseEntity.ok(Map.of("success", true, "data", payment, "message", "تم إنشاء الدفعة بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * الدفع من المحفظة
     */
    @PostMapping("/wallet-pay")
    public ResponseEntity<?> payFromWallet(
            @RequestParam UUID userId,
            @RequestParam Long workerId,
            @RequestParam(required = false) Long jobRequestId,
            @RequestParam BigDecimal amount) {
        try {
            Payment payment = paymentService.payFromWallet(userId, workerId, jobRequestId, amount);
            return ResponseEntity.ok(Map.of("success", true, "data", payment, "message", "تم الدفع بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * شحن المحفظة
     */
    @PostMapping("/wallet/deposit")
    public ResponseEntity<?> depositToWallet(
            @RequestParam UUID userId,
            @RequestParam BigDecimal amount) {
        try {
            Wallet wallet = paymentService.depositToWallet(userId, amount);
            return ResponseEntity.ok(Map.of("success", true, "data", wallet, "message", "تم شحن المحفظة بنجاح"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب رصيد المحفظة
     */
    @GetMapping("/wallet/balance")
    public ResponseEntity<?> getWalletBalance(@RequestParam UUID userId) {
        try {
            BigDecimal balance = paymentService.getWalletBalance(userId);
            return ResponseEntity.ok(Map.of("success", true, "balance", balance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب معاملات المحفظة
     */
    @GetMapping("/wallet/transactions")
    public ResponseEntity<?> getWalletTransactions(
            @RequestParam UUID userId,
            Pageable pageable) {
        try {
            Page<WalletTransaction> transactions = paymentService.getWalletTransactions(userId, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * جلب مدفوعات المستخدم
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPayments(
            @PathVariable UUID userId,
            Pageable pageable) {
        try {
            Page<Payment> payments = paymentService.getUserPayments(userId, pageable);
            return ResponseEntity.ok(Map.of("success", true, "data", payments));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
