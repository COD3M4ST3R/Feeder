package com.mysocialmediaappfeeder.social.CLASSES;

public class Posts
{
    private String description;
    private String postID;
    private String postImage;
    private String postOwner;
    private String postType;
    private long date;

    //  DEFAULT CONSTRUCTOR REQUIRED FROM FIREBASE
    public Posts(){

    }

    //  PARAMETED CONSTRUCTOR
    public Posts(String description, String postID, String postImage, String postOwner, String postType, long date)
    {
        this.description = description;
        this.postID = postID;
        this.postImage = postImage;
        this.postOwner = postOwner;
        this.postType = postType;
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostOwner() {
        return postOwner;
    }

    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
