package com.alkempl.funnytranslator.entities;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.alkempl.funnytranslator.entities.dao.MessageDao;

@Database(entities = {Message.class}, version = AppDatabase.VERSION)
public abstract class AppDatabase extends RoomDatabase {
    static final int VERSION = 6;

    public abstract MessageDao messageDao();
}
