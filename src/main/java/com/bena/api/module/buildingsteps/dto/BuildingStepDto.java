package com.bena.api.module.buildingsteps.dto;

import java.util.List;

public class BuildingStepDto {
    private Long id;
    private String title;
    private String description;
    private Integer stepOrder;
    private Long categoryId;
    private String categoryName;
    private String iconName;
    private String estimatedDuration;
    private Double estimatedCostPercentage;
    private List<StepMediaDto> mediaList;
    private List<SubStepDto> subSteps;
    
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
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public String getEstimatedDuration() { return estimatedDuration; }
    public void setEstimatedDuration(String estimatedDuration) { this.estimatedDuration = estimatedDuration; }
    
    public Double getEstimatedCostPercentage() { return estimatedCostPercentage; }
    public void setEstimatedCostPercentage(Double estimatedCostPercentage) { this.estimatedCostPercentage = estimatedCostPercentage; }
    
    public List<StepMediaDto> getMediaList() { return mediaList; }
    public void setMediaList(List<StepMediaDto> mediaList) { this.mediaList = mediaList; }
    
    public List<SubStepDto> getSubSteps() { return subSteps; }
    public void setSubSteps(List<SubStepDto> subSteps) { this.subSteps = subSteps; }
}
