package com.example.opentable;

import com.google.firebase.storage.StorageReference;

public class ModalPost {
    String title, location, img_comments,
    img_bookmark, img_save, imageView, txt_caption, txt_tags;
    StorageReference post_image; // FIREBASE store's storage reference of image to be displayed in posts
    boolean favorite, liked;
    int txt_likes,reportCount;
    String userId, postId;

    public ModalPost(
            String title, String location, StorageReference post_image,
            boolean favorite, int txt_likes, String txt_caption,
            String txt_tags,
            String userID,
            String postId,
            int reportCount,
            boolean liked){

         this.title  = title;
         this.location  = location;
         this.post_image  = post_image;
         this.favorite = favorite;
//         this.img_comments  = img_comments;
//         this.img_bookmark  = img_bookmark;
//         this.img_save  = img_save;
//         this.imageView  = imageView;
         this.txt_likes  = txt_likes;
         this.txt_caption  = txt_caption;
         this.txt_tags = txt_tags;
         this.userId = userID;
         this.postId = postId;
         this.reportCount = reportCount;
         this.liked = liked;
    }

    void updateLikes(int c)
    {
        this.txt_likes = txt_likes+c;
    }
    boolean getFavorite(){return favorite;}
    public String getImageView() {
        return imageView;
    }

    public String getImg_bookmark() {
        return img_bookmark;
    }

    public String getImg_comments() {
        return img_comments;
    }

    public String getImg_save() {
        return img_save;
    }

    public String getLocation() {
        return location;
    }

    public StorageReference getPost_image() {
        return post_image;
    }

    public String getTitle() {
        return title;
    }

    public String getTxt_caption() {
        return txt_caption;
    }

    public int getTxt_likes() {
        return txt_likes;
    }

    public String getTxt_tags() {
        return txt_tags;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getPostId() {
        return postId;
    }

    public String getUserId() {
        return userId;
    }

    public int getReportCount() {
        return reportCount;
    }

    public boolean getLiked(){return liked;}

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
