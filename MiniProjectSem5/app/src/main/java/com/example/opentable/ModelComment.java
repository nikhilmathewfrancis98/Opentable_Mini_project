package com.example.opentable;


import com.google.firebase.Timestamp;


public class ModelComment {
    String comment, profileUrl, userName;
    int likesCount, reportCount;
    boolean liked;
    Timestamp timeStamp;
    String postId;

//    Map<String, Object> likedUsers;

    // time of post function is not yet implemented !!

    public ModelComment(
            String comment, String profileUrl, String userName,
            int likesCount,
            boolean liked,
            Timestamp timeStamp,
            String postId,
            int reportCount)
    {
        this.comment=comment;
        this.profileUrl=profileUrl;
        this.userName = userName;
        this.likesCount=likesCount;
        this.timeStamp = timeStamp;
        this.liked = liked;
        this.postId = postId;
        this.reportCount = reportCount;
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

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public String getPostId() {
        return postId;
    }

    public int getReportCount() {
        return reportCount;
    }

    //    public Map<String, Object> getLikedUsers() {
//        return likedUsers;
//    }

    //    public void setComment(String comment) {
//        this.comment = comment;
//    }
}
