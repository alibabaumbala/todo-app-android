package com.example.midterm;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TodoItems {
    @PrimaryKey(autoGenerate = true)
    private long list_id;
    private String id;
    private String title;
    private String content;
    private String date;
    private String userID;

    public TodoItems(String title, String content, String id, String date,String userID) {
        this.title = title;
        this.content = content;
        this.id = id;
        this.date = date;
        this.userID = userID;
    }

    public TodoItems(){

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getList_id() {
        return list_id;
    }

    public void setList_id(long list_id) {
        this.list_id = list_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
