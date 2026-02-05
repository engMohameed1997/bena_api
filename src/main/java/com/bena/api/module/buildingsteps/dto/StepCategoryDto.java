package com.bena.api.module.buildingsteps.dto;

public class StepCategoryDto {
    private Long id;
    private String name;
    private String description;
    private String iconName;
    private String colorHex;
    private Integer categoryOrder;
    private Integer stepsCount;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
    
    public Integer getCategoryOrder() { return categoryOrder; }
    public void setCategoryOrder(Integer categoryOrder) { this.categoryOrder = categoryOrder; }
    
    public Integer getStepsCount() { return stepsCount; }
    public void setStepsCount(Integer stepsCount) { this.stepsCount = stepsCount; }
}
