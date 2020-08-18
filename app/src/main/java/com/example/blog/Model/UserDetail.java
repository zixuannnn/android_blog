package com.example.blog.Model;
import android.net.Uri;

public class UserDetail {
    private String username;
    private String email;
    private String password;
    private String id;
    private Uri photo = null;
    private int follower, following;

    public UserDetail(String username, String email, String password, String id){
        this.username = username;
        this.email = email;
        this.password = password;
        this.id = id;
        this.follower = 0;
        this.following = 0;
    }

    public String getUsername(){
        return username;
    }

    public String getEmail(){
        return email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getPassword(){
        return password;
    }

    public String getId(){
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setPhoto(Uri uri){
        photo = uri;
    }

    public Uri getPhoto(){
        return photo;
    }

    public int getFollower(){
        return follower;
    }
    public int getFollowing(){
        return following;
    }
    public void increaseFollower(){
        follower += 1;
    }
    public void increaseFollowing(){
        following += 1;
    }
}
