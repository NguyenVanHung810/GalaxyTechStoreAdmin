package com.example.galaxytechstoreadmin;

import java.util.ArrayList;
import java.util.Date;

public class OrderItemModel {

    private String userId;
    private String orderId;
    private String orderStatus;
    private String address, fullName, phoneNumber;
    private Date orderedDate,packedDate,shippedDate,delveredDate,cancelleddate;
    private String paymentMethod;
    private String deliveryPrice;
    private Long totalItems;
    private String discountedPrice;
    private Long totalItemsPrice;
    private ArrayList<ProductsInOrderModel> products;

    public OrderItemModel(String userId,String orderId, String orderStatus, String address, String fullName, String phoneNumber, Date orderedDate, Date packedDate, Date shippedDate, Date delveredDate, Date cancelleddate, String paymentMethod, String deliveryPrice, Long totalItems, String discountedPrice, Long totalItemsPrice, ArrayList<ProductsInOrderModel> products) {
        this.userId = userId;
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.address = address;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.orderedDate = orderedDate;
        this.packedDate = packedDate;
        this.shippedDate = shippedDate;
        this.delveredDate = delveredDate;
        this.cancelleddate = cancelleddate;
        this.paymentMethod = paymentMethod;
        this.deliveryPrice = deliveryPrice;
        this.totalItems = totalItems;
        this.discountedPrice = discountedPrice;
        this.totalItemsPrice = totalItemsPrice;
        this.products = products;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date packedDate) {
        this.packedDate = packedDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Date getDelveredDate() {
        return delveredDate;
    }

    public void setDelveredDate(Date delveredDate) {
        this.delveredDate = delveredDate;
    }

    public Date getCancelleddate() {
        return cancelleddate;
    }

    public void setCancelleddate(Date cancelleddate) {
        this.cancelleddate = cancelleddate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public Long getTotalItemsPrice() {
        return totalItemsPrice;
    }

    public void setTotalItemsPrice(Long totalItemsPrice) {
        this.totalItemsPrice = totalItemsPrice;
    }

    public ArrayList<ProductsInOrderModel> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<ProductsInOrderModel> products) {
        this.products = products;
    }
}
