package com.example.myfarmstory.Models;

public class Users {
    String profileepic,userName,mail,password,userId,lastpoint;

    public Users(String profileepic, String userName, String mail, String password, String userId, String lastpoint) {
        this.profileepic = profileepic;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastpoint = lastpoint;
    }

    public Users(){}

    public Users(String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }


    public String getProfileepic() {
        return profileepic;
    }

    public void setProfileepic(String profileepic) {
        this.profileepic = profileepic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {this.userName = userName; }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastpoint() {
        return lastpoint;
    }

    public void setLastpoint(String lastpoint) {
        this.lastpoint = lastpoint;
    }
}
