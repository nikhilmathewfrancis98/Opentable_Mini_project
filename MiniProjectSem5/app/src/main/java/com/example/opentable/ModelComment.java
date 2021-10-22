package com.example.opentable;

public class ModelComment {
    String comment, profileUrl, userName;
    int likesCount;
    boolean liked;
    // time of post function is not yet implemented !!

    public ModelComment(
            String comment, String profileUrl, String userName,
            int likesCount,
            boolean liked
    )
    {
        this.comment=comment;
        this.profileUrl=profileUrl;
        this.userName = userName;
        this.likesCount=likesCount;
        this.liked=liked;
    }


    public int getLikesCount() {
        return likesCount;
    }

    public String getComment() {
        return comment;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean getLiked(){return liked;}

    void updateLikes(int c)
    {
        this.likesCount = likesCount+c;
    }

//    public void setComment(String comment) {
//        this.comment = comment;
//    }
}
