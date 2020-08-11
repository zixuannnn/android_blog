package com.example.blog.Model;

import java.util.Date;

public class Comment {

    private String comment;
    private String publisher;
    private Date date;

    public Comment(String comment, String publisher, Date date){
        this.comment = comment;
        this.publisher = publisher;
        this.date = date;
    }

    public String getComment(){
        return comment;
    }

    public String getPublisher(){
        return  publisher;
    }

    public Date getDate(){
        return date;
    }

    public void setComment(String comment){
        this.comment = comment;
    }

    public void  setPublisher(String publisher){
        this.publisher = publisher;
    }

    public void setDate(Date date){
        this.date = date;
    }
}
