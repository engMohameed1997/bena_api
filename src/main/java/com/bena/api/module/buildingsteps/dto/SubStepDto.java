package com.bena.api.module.buildingsteps.dto;

import java.util.List;

public class SubStepDto {
    private Long id;
    private String title;
    private String description;
    private Integer subStepOrder;
    private String tips;
    private String warnings;
    private List<StepMediaDto> mediaList;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public List<StepMediaDto> getMediaList() { return mediaList; }
    public void setMediaList(List<StepMediaDto> mediaList) { this.mediaList = mediaList; }
}
