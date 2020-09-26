package com.jaac08.prueba_fase1.model;

import java.io.Serializable;

public class Post implements Serializable {
    private int userId;
    private int id;
    private String title;
    private String body;
    private int read;
    private int favorite;

    public Post() {
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public int getRead() {
        return read;
    }
    public void setRead(int read) {
        this.read = read;
    }
    public int getFavorite() {return favorite; }
    public void setFavorite(int favorite) { this.favorite = favorite; }

}