package com.example.socialmediaintegration.models;

public class Users {
    String userName;
    String profilePic;
    String eMail;

    public Users(String userName, String profilePic, String eMail) {
        this.userName = userName;
        this.profilePic = profilePic;
        this.eMail = eMail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
}
