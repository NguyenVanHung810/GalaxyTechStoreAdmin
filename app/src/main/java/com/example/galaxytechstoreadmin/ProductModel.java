package com.example.galaxytechstoreadmin;

import java.util.ArrayList;

public class ProductModel {
    private String productID;
    private String productImage;
    private String productDesc;
    private String productTitle;
    private String average_Ratings;
    private long total_Ratings;
    private String productPrice;
    private String cuttedPrice;
    private Boolean COD;
    private Boolean inStock;
    private ArrayList<String> tags;
    private String Category_Id;
    private String Brand_Id;
    private long index;

    public ProductModel(String productID, String productImage, String productDesc, String productTitle, String average_Ratings, long total_Ratings, String productPrice, String cuttedPrice, Boolean COD, Boolean inStock, String category_Id, String brand_Id, long index) {
        this.productID = productID;
        this.productImage = productImage;
        this.productDesc = productDesc;
        this.productTitle = productTitle;
        this.average_Ratings = average_Ratings;
        this.total_Ratings = total_Ratings;
        this.productPrice = productPrice;
        this.cuttedPrice = cuttedPrice;
        this.COD = COD;
        this.inStock = inStock;
        Category_Id = category_Id;
        Brand_Id = brand_Id;
        this.index = index;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getAverage_Ratings() {
        return average_Ratings;
    }

    public void setAverage_Ratings(String average_Ratings) {
        this.average_Ratings = average_Ratings;
    }

    public long getTotal_Ratings() {
        return total_Ratings;
    }

    public void setTotal_Ratings(long total_Ratings) {
        this.total_Ratings = total_Ratings;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCuttedPrice() {
        return cuttedPrice;
    }

    public void setCuttedPrice(String cuttedPrice) {
        this.cuttedPrice = cuttedPrice;
    }

    public Boolean getCOD() {
        return COD;
    }

    public void setCOD(Boolean COD) {
        this.COD = COD;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getCategory_Id() {
        return Category_Id;
    }

    public void setCategory_Id(String category_Id) {
        Category_Id = category_Id;
    }

    public String getBrand_Id() {
        return Brand_Id;
    }

    public void setBrand_Id(String brand_Id) {
        Brand_Id = brand_Id;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }
}
