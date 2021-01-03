package com.example.galaxytechstoreadmin;

import java.util.List;

public class CategoryModel {
    private String categoryId;
    private String categoryImage;
    private String categoryName;
    private Long index;
    private Long lastBrandIndex;
    private List<BrandModel> brandModelList;

    public CategoryModel(String categoryId, String categoryImage, String categoryName, Long index, Long lastBrandIndex, List<BrandModel> brandModelList) {
        this.categoryId = categoryId;
        this.categoryImage = categoryImage;
        this.categoryName = categoryName;
        this.index = index;
        this.lastBrandIndex = lastBrandIndex;
        this.brandModelList = brandModelList;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public Long getLastBrandIndex() {
        return lastBrandIndex;
    }

    public void setLastBrandIndex(Long lastBrandIndex) {
        this.lastBrandIndex = lastBrandIndex;
    }

    public List<BrandModel> getBrandModelList() {
        return brandModelList;
    }

    public void setBrandModelList(List<BrandModel> brandModelList) {
        this.brandModelList = brandModelList;
    }
}
