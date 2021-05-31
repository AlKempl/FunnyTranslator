package com.alkempl.funnytranslator.entities.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.alkempl.funnytranslator.entities.Message;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    List<Message> getAll();

    @Query("SELECT * FROM message WHERE id IN (:messageIds)")
    List<Message> loadAllByIds(int[] messageIds);

    @Insert
    void insertAll(Message... messages);

    @Delete
    void delete(Message messages);
}
