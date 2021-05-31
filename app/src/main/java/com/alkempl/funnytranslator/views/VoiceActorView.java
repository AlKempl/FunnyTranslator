package com.alkempl.funnytranslator.views;

import com.alkempl.funnytranslator.entities.Message;
import com.alkempl.funnytranslator.entities.MessageRepository;

import java.util.List;

public interface VoiceActorView {
    MessageRepository getMessageRepository();

    void addMessageToRecycler(Message message);

    void showMessages(List<Message> messages);
}
