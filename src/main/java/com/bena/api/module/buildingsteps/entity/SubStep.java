package com.bena.api.module.buildingsteps.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sub_steps")
public class SubStep {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_step_id", nullable = false)
    private BuildingStep buildingStep;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "sub_step_order")
    private Integer subStepOrder;
    
    @Column(columnDefinition = "TEXT")
    private String tips;
    
    @Column(columnDefinition = "TEXT")
    private String warnings;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "subStep", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("mediaOrder ASC")
    private List<StepMedia> mediaList = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BuildingStep getBuildingStep() { return buildingStep; }
    public void setBuildingStep(BuildingStep buildingStep) { this.buildingStep = buildingStep; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getSubStepOrder() { return subStepOrder; }
    public void setSubStepOrder(Integer subStepOrder) { this.subStepOrder = subStepOrder; }
    
    public String getTips() { return tips; }
    public void setTips(String tips) { this.tips = tips; }
    
    public String getWarnings() { return warnings; }
    public void setWarnings(String warnings) { this.warnings = warnings; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public List<StepMedia> getMediaList() { return mediaList; }
    public void setMediaList(List<StepMedia> mediaList) { this.mediaList = mediaList; }
}
