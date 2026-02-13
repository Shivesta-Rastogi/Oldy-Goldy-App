package com.example.signuploginrealtime;


public class DataClass {
    private String imageURL, caption;
    private String item;

    public DataClass(){

    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
    public String getItem() {
        return item;
    }


    public void setItem(String item) { this.item = item; }




    public DataClass(String imageURL, String caption) {
        this.caption = caption;
        this.imageURL = imageURL;

    }

}