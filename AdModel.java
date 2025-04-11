package com.example.myfinalloginpage;

public class AdModel {
    private String imageUrl, title, price, location;

    public AdModel() {
        // Required empty constructor for Firestore
    }

    public AdModel(String imageUrl, String title, String price, String location) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }
}
