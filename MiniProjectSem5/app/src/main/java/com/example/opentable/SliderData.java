package com.example.opentable;

public class SliderData {

    // string for our image url.
    private String imgUrl;

    // empty constructor which is
    // required when using Firebase.
    public SliderData() {
    }

    // Constructor
    public SliderData(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    // Getter method.
    public String getImgUrl() {
        return imgUrl;
    }

    // Setter method.
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }


}
