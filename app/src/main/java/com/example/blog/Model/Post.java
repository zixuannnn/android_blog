package com.example.blog.Model;
import java.util.Date;

public class Post {

    private String mName;
    private String mImageUrl;
    private String postKey;
    private String mEmail;
    private Date mTime;

    public Post() {
        //empty constructor needed
    }
    public Post(String name, String imageUrl, String key, String email, Date time) {
        mName = name;
        mImageUrl = imageUrl;
        postKey = key;
        mEmail = email;
        mTime = time;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
    public String getImageUrl() {
        return mImageUrl;
    }
    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }
    public void setPostKey(String key){
        postKey = key;
    }
    public String getPostKey(){
        return postKey;
    }
    public String getmEmail(){
        return mEmail;
    }
    public Date getmTime(){
        return mTime;
    }

}
