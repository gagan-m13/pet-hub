package com.pethub.dto.response;

public class ProductImageResponse {

    private Long id;
    private String imageUrl;
    private boolean primary;
    private int displayOrder;

    public ProductImageResponse() {
    }

    public ProductImageResponse(Long id, String imageUrl, boolean primary, int displayOrder) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.primary = primary;
        this.displayOrder = displayOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}
