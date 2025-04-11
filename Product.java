package com.example.myfinalloginpage;

import java.util.List;

public class Product {
    private String item;
    private String caption;
    private String price;
    private String description;
    private String imageUrl;
    private String userId;
    private String location;
    private String sellerContact;
    private long timestamp;
    private String name;

    // Default constructor required for Firebase
    public Product() {
    }

    public Product(String item, String caption, String price, String description, String imageUrl, String userId, String location, String sellerContact,String name, long timestamp) {
        this.item = item;
        this.caption = caption;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.location = location;
        this.sellerContact = sellerContact;
        this.timestamp = timestamp;
        this.name = name;
    }

    public String getItem() {
        return item;
    }

    public String getCaption() {
        return caption;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getLocation() {
        return location;
    }
    public String getSellerContact() {
        return sellerContact;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public void setItem(String item) { this.item = item; }
    public void setCaption(String caption) { this.caption = caption; }
    public void setPrice(String price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setLocation(String location) { this.location = location; }
    public void setSellerContact(String sellerContact) { this.sellerContact = sellerContact; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getName() {
        return name;
    }
}