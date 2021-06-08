package com.alkempl.funnytranslator.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Message {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "isUserMessage")
    public boolean isUserMessage;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @ColumnInfo(name = "lang")
    public String lang;

    public Message(boolean isUserMessage, String content, String imageUrl, String lang) {
        this.isUserMessage = isUserMessage;
        this.content = content;
        this.imageUrl = imageUrl;
        this.lang = lang;
    }
}
