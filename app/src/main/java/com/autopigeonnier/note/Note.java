package com.autopigeonnier.note;

/**
 * Created by oussama on 14/11/2015.
 */
public class Note {


    @com.google.gson.annotations.SerializedName("title")
    private String mTitle;

    @com.google.gson.annotations.SerializedName("body")
    private String mBody;

    @com.google.gson.annotations.SerializedName("id")
    private String mId;


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmBody() {
        return mBody;
    }

    public void setmBody(String mBody) {
        this.mBody = mBody;
    }

    public Note(String mTitle, String mBody, String mId) {
        this.mTitle = mTitle;
        this.mBody = mBody;
        this.mId = mId;
    }

    public Note(){
    }
}
