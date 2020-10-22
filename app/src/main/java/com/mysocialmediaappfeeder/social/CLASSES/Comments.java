package com.mysocialmediaappfeeder.social.CLASSES;

public class Comments
{
    private String commentID;
    private String commentatorID;
    private String postID;
    private String comment;

    //  EMPTY CONSTRUCTOR
    public Comments()
    {

    }

    public Comments(String commentID, String commentatorID, String postID, String comment)
    {
        this.commentID = commentID;
        this.commentatorID = commentatorID;
        this.postID = postID;
        this.comment = comment;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getCommentatorID() {
        return commentatorID;
    }

    public void setCommentatorID(String commentatorID) {
        this.commentatorID = commentatorID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
