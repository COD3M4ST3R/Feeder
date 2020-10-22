package com.mysocialmediaappfeeder.social.CLASSES;


public class User {
    private String FullName;
    private String UserName;
    private String Email;
    private String Password;
    private String ImageURL;
    private String AuthID;
    private String Bio;
    private String AccountType;

    public User(){
    }

    public User(String FullName, String UserName, String Email, String Password, String ImageURL, String AuthID, String Bio, String AccountType){
        this.FullName = FullName;
        this.UserName = UserName;
        this.Email = Email;
        this.Password = Password;
        this.ImageURL = ImageURL;
        this.AuthID = AuthID;
        this.Bio = Bio;
        this.AccountType = AccountType;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getAuthID() {
        return AuthID;
    }

    public void setAuthID(String authID) {
        AuthID = authID;
    }

    public String getBio() {
        return Bio;
    }

    public void setBio(String bio) {
        this.Bio = bio;
    }

    public String getAccountType() {
        return AccountType;
    }

    public void setAccountType(String accountType) {
        AccountType = accountType;
    }
}
