package com.bena.api.module.buildingsteps.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "step_categories")
public class StepCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "icon_name")
    private String iconName;
    
    @Column(name = "color_hex")
    private String colorHex;
    
    @Column(name = "category_order")
    private Integer categoryOrder;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
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
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
