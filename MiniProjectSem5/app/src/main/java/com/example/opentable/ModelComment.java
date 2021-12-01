package com.example.opentable;


import com.google.firebase.Timestamp;

import java.util.ArrayList;


public class ModelComment {
    public String content, userName;
    String profileUrl;
    public int likesCount, reportCount;
    boolean liked;
    public Timestamp time;
    String postId;
    public ArrayList<String> likedUsers, reportedUsers;
    // time of post function is not yet implemented !!

    public ModelComment(
            String comment, String profileUrl, String userName,
            int likesCount,
            boolean liked,
            Timestamp timeStamp,
            String postId,
            int reportCount,
            ArrayList<String> likedUsers,
            ArrayList<String> reportedUsers)
    {
        this.content=comment;
        this.profileUrl=profileUrl;
        this.userName = userName;
        this.likesCount=likesCount;
        this.time = timeStamp;
        this.liked = liked;
        this.postId = postId;
        this.reportCount = reportCount;
        this.likedUsers = likedUsers;
        this.reportedUsers = reportedUsers;
    }

    public ModelComment(){}
    public int getLikesCount() {
        return likesCount;
    }

    public String getComment() {
        return content;
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
        return time;
    }

    public String getPostId() {
        return postId;
    }

    public int getReportCount() {
        return reportCount;
    }

    public void setComment(String comment) {
        this.content = comment;
    }

    //    public Map<String, Object> getLikedUsers() {
//        return likedUsers;
//    }

    //    public void setComment(String comment) {
//        this.comment = comment;
//    }
}
