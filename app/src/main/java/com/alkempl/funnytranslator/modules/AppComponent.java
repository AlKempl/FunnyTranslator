package com.alkempl.funnytranslator.modules;

import android.app.Application;

import com.alkempl.funnytranslator.activities.VoiceActorActivity;
import com.alkempl.funnytranslator.entities.AppDatabase;
import com.alkempl.funnytranslator.entities.MessageRepository;
import com.alkempl.funnytranslator.entities.dao.MessageDao;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(dependencies = {}, modules = {AppModule.class, RoomModule.class})
public interface AppComponent {
    void inject(VoiceActorActivity mainActivity);

    MessageDao messageDao();

    AppDatabase appDatabase();

    MessageRepository messageRepository();

    Application application();
}
