package com.bena.api.module.buildingsteps.dto;

public class StepMediaDto {
    private Long id;
    private String mediaType;
    private String url;
    private String thumbnailUrl;
    private String title;
    private String caption;
    private Integer mediaOrder;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
}
