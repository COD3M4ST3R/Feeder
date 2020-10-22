package com.mysocialmediaappfeeder.social.CLASSES;

public class Notification
{
    private String senderID;
    private String receiverID;
    private String postImage;
    private String postID;
    private String message;
    private long date;
    private String type;

    public  Notification()
    {
        //  EMPTY CONSTRUCTOR NEEDED FOR FIREBASE
    }

    public Notification(String senderID, String receiverID, String postImage,String postID, String message, long date, String type)
    {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.postImage = postImage;
        this.postID = postID;
        this.message = message;
        this.date = date;
        this.type = type;

    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
