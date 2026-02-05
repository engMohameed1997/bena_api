package com.bena.api.module.payment.service;

import com.bena.api.module.user.entity.User;
import com.bena.api.module.user.repository.UserRepository;
import com.bena.api.module.payment.entity.Payment;
import com.bena.api.module.payment.entity.Wallet;
import com.bena.api.module.payment.entity.WalletTransaction;
import com.bena.api.module.payment.repository.WalletPaymentRepository;
import com.bena.api.module.payment.repository.WalletRepository;
import com.bena.api.module.payment.repository.WalletTransactionRepository;
import com.bena.api.module.worker.entity.JobRequest;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.JobRequestRepository;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service("walletPaymentService")
@RequiredArgsConstructor
public class WalletPaymentService {

    private final WalletPaymentRepository paymentRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final JobRequestRepository jobRequestRepository;

    /**
     * إنشاء دفعة جديدة
     */
    @Transactional
    public Payment createPayment(UUID userId, Long workerId, Long jobRequestId,
                                  BigDecimal amount, Payment.PaymentMethod method) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("المختص غير موجود"));
        
        JobRequest jobRequest = null;
        if (jobRequestId != null) {
            jobRequest = jobRequestRepository.findById(jobRequestId).orElse(null);
        }

        Payment payment = Payment.builder()
                .user(user)
                .worker(worker)
                .jobRequest(jobRequest)
                .amount(amount)
                .paymentMethod(method)
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .build();

        return paymentRepository.save(payment);
    }

    /**
     * إتمام الدفعة
     */
    @Transactional
    public Payment completePayment(Long paymentId, String transactionId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("الدفعة غير موجودة"));

        payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        payment.setTransactionId(transactionId);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * الدفع من المحفظة
     */
    @Transactional
    public Payment payFromWallet(UUID userId, Long workerId, Long jobRequestId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("المحفظة غير موجودة"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("رصيد غير كافٍ في المحفظة");
        }

        // إنشاء الدفعة
        Payment payment = createPayment(userId, workerId, jobRequestId, amount, Payment.PaymentMethod.WALLET);

        // خصم من المحفظة
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.subtractBalance(amount);
        walletRepository.save(wallet);

        // تسجيل المعاملة
        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .type(WalletTransaction.TransactionType.PAYMENT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(wallet.getBalance())
                .referenceType("payment")
                .referenceId(payment.getId())
                .description("دفع مقابل طلب عمل")
                .build();
        walletTransactionRepository.save(transaction);

        // إتمام الدفعة
        payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    /**
     * شحن المحفظة
     */
    @Transactional
    public Wallet depositToWallet(UUID userId, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> createWallet(userId));

        BigDecimal balanceBefore = wallet.getBalance();
        wallet.addBalance(amount);
        walletRepository.save(wallet);

        WalletTransaction transaction = WalletTransaction.builder()
                .wallet(wallet)
                .type(WalletTransaction.TransactionType.DEPOSIT)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(wallet.getBalance())
                .description("شحن رصيد المحفظة")
                .build();
        walletTransactionRepository.save(transaction);

        return wallet;
    }

    /**
     * إنشاء محفظة جديدة
     */
    @Transactional
    public Wallet createWallet(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("المستخدم غير موجود"));

        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(BigDecimal.ZERO)
                .isActive(true)
                .build();

        return walletRepository.save(wallet);
    }

    /**
     * جلب رصيد المحفظة
     */
    public BigDecimal getWalletBalance(UUID userId) {
        return walletRepository.findByUserId(userId)
                .map(Wallet::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * جلب معاملات المحفظة
     */
    public Page<WalletTransaction> getWalletTransactions(UUID userId, Pageable pageable) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("المحفظة غير موجودة"));
        return walletTransactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId(), pageable);
    }

    /**
     * جلب مدفوعات المستخدم
     */
    public Page<Payment> getUserPayments(UUID userId, Pageable pageable) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * جلب مدفوعات المختص
     */
    public Page<Payment> getWorkerPayments(Long workerId, Pageable pageable) {
        return paymentRepository.findByWorkerIdOrderByCreatedAtDesc(workerId, pageable);
    }
}
