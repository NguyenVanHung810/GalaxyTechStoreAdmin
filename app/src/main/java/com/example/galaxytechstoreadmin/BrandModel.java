package com.example.galaxytechstoreadmin;

public class BrandModel {
    private String brand_id;
    private String brand_name;
    private String cate_id;

    public BrandModel(String brand_id, String brand_name, String cate_id) {
        this.brand_id = brand_id;
        this.brand_name = brand_name;
        this.cate_id = cate_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getCate_id() {
        return cate_id;
    }

    public void setCate_id(String cate_id) {
        this.cate_id = cate_id;
    }
}
