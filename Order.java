package com.example.myfinalloginpage;

public class Order {
    private String status;
    private String productName;
    private int productImage;

    public Order(String status, String productName, int productImage) {
        this.status = status;
        this.productName = productName;
        this.productImage = productImage;
    }

    public String getStatus() { return status; }
    public String getProductName() { return productName; }
    public int getProductImage() { return productImage; }
}

