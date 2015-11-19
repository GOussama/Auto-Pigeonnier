package com.autopigeonnier.auth;

/**
 * Created by oussama on 11/11/2015.
 */

public class User {

    @com.google.gson.annotations.SerializedName("login")
    private String mLogin;

    public String getLogin() {
        return mLogin;
    }

    public void setLogin(String mLogin) {
        this.mLogin = mLogin;
    }

    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }
    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("password")

    private String mPassword;

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public User() {

    }

    public User(String mLogin, String mId, String mPassword) {
        this.mLogin = mLogin;
        this.mId = mId;
        this.mPassword = mPassword;
    }

/**
     * Indicates if the item is completed
     */

}