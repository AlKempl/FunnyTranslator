package com.alkempl.funnytranslator.modules;

import android.app.Application;

import androidx.room.Room;

import com.alkempl.funnytranslator.entities.AppDatabase;
import com.alkempl.funnytranslator.entities.MessageDataSource;
import com.alkempl.funnytranslator.entities.MessageRepository;
import com.alkempl.funnytranslator.entities.dao.MessageDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RoomModule {
    private AppDatabase appDatabase;

    public RoomModule(Application application) {
        this.appDatabase = Room.databaseBuilder(application, AppDatabase.class, "app-db")
                .fallbackToDestructiveMigration() //TODO: remove it
                .build();
    }

    @Singleton
    @Provides
    AppDatabase providesRoomDatabase() {
        return appDatabase;
    }

    @Singleton
    @Provides
    MessageDao providesMessageDao(AppDatabase appDatabase) {
        return appDatabase.messageDao();
    }

    @Singleton
    @Provides
    MessageRepository messageRepository(MessageDao messageDao) {
        return new MessageDataSource(messageDao);
    }
}
