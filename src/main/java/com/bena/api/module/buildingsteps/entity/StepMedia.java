package com.bena.api.module.buildingsteps.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "step_media")
public class StepMedia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_step_id", nullable = false)
    private BuildingStep buildingStep;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_step_id")
    private SubStep subStep;
    
    @Column(name = "media_type", nullable = false, length = 20)
    private String mediaType;
    
    @Column(nullable = false)
    private String url;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    private String title;
    
    private String caption;
    
    @Column(name = "media_order")
    private Integer mediaOrder;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BuildingStep getBuildingStep() { return buildingStep; }
    public void setBuildingStep(BuildingStep buildingStep) { this.buildingStep = buildingStep; }
    
    public SubStep getSubStep() { return subStep; }
    public void setSubStep(SubStep subStep) { this.subStep = subStep; }
    
    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    
    public Integer getMediaOrder() { return mediaOrder; }
    public void setMediaOrder(Integer mediaOrder) { this.mediaOrder = mediaOrder; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
}
