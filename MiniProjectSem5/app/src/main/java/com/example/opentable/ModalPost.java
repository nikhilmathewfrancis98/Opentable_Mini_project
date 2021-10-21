package com.example.opentable;

public class ModalPost {
    String title, location, post_image,  img_comments,
    img_bookmark, img_save, imageView, txt_caption, txt_tags;
    boolean favorite;
    int txt_likes;

    public ModalPost(
            String title, String location, String post_image,
            boolean favorite, int txt_likes, String txt_caption,
            String txt_tags
    )
    {
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

    public String getPost_image() {
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
}
