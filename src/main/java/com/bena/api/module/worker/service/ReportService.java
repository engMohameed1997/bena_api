package com.bena.api.module.worker.service;

import com.bena.api.module.worker.dto.ReportCreateDTO;
import com.bena.api.module.worker.dto.ReportDTO;
import com.bena.api.module.worker.entity.Report;
import com.bena.api.module.worker.entity.Worker;
import com.bena.api.module.worker.repository.ReportRepository;
import com.bena.api.module.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final WorkerRepository workerRepository;

    /**
     * إنشاء بلاغ جديد
     */
    @Transactional
    public ReportDTO createReport(Long reporterId, ReportCreateDTO dto) {
        // التحقق من عدم وجود بلاغ سابق
        if (reportRepository.existsByReporterIdAndWorkerId(reporterId, dto.getWorkerId())) {
            throw new RuntimeException("لقد قمت بالإبلاغ عن هذا العامل مسبقاً");
        }

        Worker worker = workerRepository.findById(dto.getWorkerId())
                .orElseThrow(() -> new RuntimeException("العامل غير موجود"));

        Report report = Report.builder()
                .reporterId(reporterId)
                .worker(worker)
                .reportType(dto.getReportType())
                .description(dto.getDescription())
                .status(Report.ReportStatus.PENDING)
                .build();

        return toDTO(reportRepository.save(report));
    }

    /**
     * جلب البلاغات (للأدمن)
     */
    public Page<ReportDTO> getAllReports(Pageable pageable) {
        return reportRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * جلب البلاغات حسب الحالة
     */
    public Page<ReportDTO> getReportsByStatus(Report.ReportStatus status, Pageable pageable) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
                .map(this::toDTO);
    }

    /**
     * جلب بلاغات عامل معين
     */
    public Page<ReportDTO> getWorkerReports(Long workerId, Pageable pageable) {
        return reportRepository.findByWorkerIdOrderByCreatedAtDesc(workerId, pageable)
                .map(this::toDTO);
    }

    /**
     * تحديث حالة البلاغ (للأدمن)
     */
    @Transactional
    public ReportDTO updateReportStatus(Long reportId, Report.ReportStatus status, String adminNotes) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("البلاغ غير موجود"));

        report.setStatus(status);
        report.setAdminNotes(adminNotes);

        return toDTO(reportRepository.save(report));
    }

    /**
     * تحديث حالة البلاغ (Overloaded - من String)
     */
    @Transactional
    public ReportDTO updateReportStatus(Long reportId, String status) {
        Report.ReportStatus reportStatus;
        try {
            reportStatus = Report.ReportStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("حالة غير صحيحة");
        }
        return updateReportStatus(reportId, reportStatus, null);
    }

    /**
     * جلب تفاصيل بلاغ
     */
    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("البلاغ غير موجود"));
        return toDTO(report);
    }

    /**
     * عدد البلاغات ضد عامل
     */
    public long getReportCount(Long workerId) {
        return reportRepository.countByWorkerId(workerId);
    }

    /**
     * إحصائيات البلاغات
     */
    public ReportStatsDTO getReportStats() {
        long total = reportRepository.count();
        long pending = reportRepository.countByStatus(Report.ReportStatus.PENDING);
        long reviewed = reportRepository.countByStatus(Report.ReportStatus.REVIEWED);
        long resolved = reportRepository.countByStatus(Report.ReportStatus.RESOLVED);
        long dismissed = reportRepository.countByStatus(Report.ReportStatus.DISMISSED);
        
        return new ReportStatsDTO(total, pending, reviewed, resolved, dismissed);
    }

    // Inner DTO class for stats
    public static class ReportStatsDTO {
        public long total;
        public long pending;
        public long underReview; // Keep frontend field name for compatibility
        public long resolved;
        public long dismissed;
        
        public ReportStatsDTO(long total, long pending, long underReview, long resolved, long dismissed) {
            this.total = total;
            this.pending = pending;
            this.underReview = underReview;
            this.resolved = resolved;
            this.dismissed = dismissed;
        }
    }

    private ReportDTO toDTO(Report report) {
        ReportDTO dto = ReportDTO.builder()
                .id(report.getId())
                .reporterId(report.getReporterId())
                .workerId(report.getWorker().getId())
                .workerName(report.getWorker().getName())
                .reportType(report.getReportType())
                .description(report.getDescription())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
        
        // تعيين النصوص العربية
        dto.setReportTypeArabic(dto.getReportTypeArabic());
        dto.setStatusArabic(dto.getStatusArabic());
        
        return dto;
    }
}
