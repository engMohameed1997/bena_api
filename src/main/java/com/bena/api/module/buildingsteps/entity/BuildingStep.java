package com.bena.api.module.buildingsteps.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "building_steps")
public class BuildingStep {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "step_order")
    private Integer stepOrder;
    
    @Column(name = "category_id")
    private Long categoryId;
    
    @Column(name = "icon_name")
    private String iconName;
    
    @Column(name = "estimated_duration")
    private String estimatedDuration;
    
    @Column(name = "estimated_cost_percentage")
    private Double estimatedCostPercentage;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "buildingStep", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("mediaOrder ASC")
    private List<StepMedia> mediaList = new ArrayList<>();
    
    @OneToMany(mappedBy = "buildingStep", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("subStepOrder ASC")
    private List<SubStep> subSteps = new ArrayList<>();
    
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
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public Double getEstimatedCostPercentage() { return estimatedCostPercentage; }
    public void setEstimatedCostPercentage(Double estimatedCostPercentage) { this.estimatedCostPercentage = estimatedCostPercentage; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public List<StepMedia> getMediaList() { return mediaList; }
    public void setMediaList(List<StepMedia> mediaList) { this.mediaList = mediaList; }
    
    public List<SubStep> getSubSteps() { return subSteps; }
    public void setSubSteps(List<SubStep> subSteps) { this.subSteps = subSteps; }
}
