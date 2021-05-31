package com.alkempl.funnytranslator.presenters;

import android.util.Log;

import com.alkempl.funnytranslator.Application;
import com.alkempl.funnytranslator.R;
import com.alkempl.funnytranslator.entities.Message;
import com.alkempl.funnytranslator.modules.ApiModule;
import com.alkempl.funnytranslator.objects.FunTranslationResponse;
import com.alkempl.funnytranslator.objects.TranslationResponse;
import com.alkempl.funnytranslator.views.VoiceActorView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Locale;
import java.util.concurrent.Executors;

public class VoiceActorPresenter {
    private VoiceActorView baseView;

    public void attachView(VoiceActorView view) {
        baseView = view;
    }

    public void loadMessages() {
        Executors.newSingleThreadExecutor().execute(() -> {
            baseView.showMessages(
                    baseView.getMessageRepository().getAll()
            );

            if (baseView.getMessageRepository().getAll().size() == 0) {
                Message initialMessage = new Message(
                        false,
                        Application.getAppContext().getString(R.string.welcome_message), "");
                baseView.getMessageRepository().insertAll(initialMessage);
                baseView.addMessageToRecycler(initialMessage);
            }
        });
    }

    public void addMessage(Message message, String language) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        Executors.newSingleThreadExecutor().execute(() -> {
            baseView.getMessageRepository().insertAll(message);
            baseView.addMessageToRecycler(message);
            String content = message.content;
            Log.d("CONTENT_ORIGNIAL", content);

            if (!Locale.getDefault().getLanguage().equals("en")) {
                String responseJSON = ApiModule.getTranslation(content, Locale.getDefault().getLanguage());

                TranslationResponse response = gson.fromJson(responseJSON, TranslationResponse.class);
                if (response.outputs.size() > 0) {
                    content = response.outputs.get(0).output;
                }
            }
            Log.d("CONTENT_ENGLISH", content);
            Log.d("TARGET_LANG", language);

            String responseJSON = ApiModule.getFunTranslation(content, language);
            Log.d("RESPONSE", responseJSON);
            FunTranslationResponse response = gson.fromJson(responseJSON, FunTranslationResponse.class);
            String result;

            if (response.error != null){
                result = language + "\r\n" + response.error.message;
            }else if (response.success != null && response.success.total >= 1) {
                result = response.contents.translated;
            }else{
                result = language + "\r\n" + "sorry master, i failed...";
            }

            Log.d("RESULT", responseJSON);
            Message botResponse;
            botResponse = new Message(false, result, "");

            baseView.addMessageToRecycler(botResponse);
            baseView.getMessageRepository().insertAll(botResponse);
        });
    }
}
