package com.alkempl.funnytranslator.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.alkempl.funnytranslator.R;
import com.alkempl.funnytranslator.adapters.MessagesAdapter;
import com.alkempl.funnytranslator.entities.Message;
import com.alkempl.funnytranslator.entities.MessageRepository;
import com.alkempl.funnytranslator.modules.AppModule;
import com.alkempl.funnytranslator.modules.DaggerAppComponent;
import com.alkempl.funnytranslator.modules.RoomModule;
import com.alkempl.funnytranslator.presenters.VoiceActorPresenter;
import com.alkempl.funnytranslator.views.VoiceActorView;
import com.mikhaellopez.circularimageview.CircularImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VoiceActorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RecognitionListener, VoiceActorView {

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    private TextToSpeech textToSpeech;
    private boolean ttsEnabled;

    private final int AUDIO_RECORD_REQUEST_CODE = 1;
    private boolean isListening = false;

    private MessagesAdapter messagesAdapter;

    @BindView(R.id.recognized_text_container)
    EditText recognizedTextContainer;

    @BindView(R.id.stop_listening)
    ImageButton stopListeningButton;

    @BindView(R.id.avatar_img)
    CircularImageView avatarImg;

    @BindView(R.id.messages_recycler)
    RecyclerView messagesRecycler;

    @Inject
    public MessageRepository messageRepository;

    public HashMap<String, String> languagesMapping;

    public VoiceActorPresenter voiceActorPresenter = new VoiceActorPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_actor);

        ButterKnife.bind(this);

        DaggerAppComponent.builder()
                .appModule(new AppModule(getApplication()))
                .roomModule(new RoomModule(getApplication()))
                .build()
                .inject(this);

        voiceActorPresenter.attachView(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messagesRecycler.setLayoutManager(linearLayoutManager);

        messagesAdapter = new MessagesAdapter();
        messagesRecycler.setAdapter(messagesAdapter);

        voiceActorPresenter.loadMessages();

        if (ContextCompat.checkSelfPermission(
                VoiceActorActivity.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        setupTTS();
        setupSpeechRecognizer();

        this.languagesMapping = new HashMap<>();
        this.languagesMapping.put(getString(R.string.lang_yoda), "yoda.json");
        this.languagesMapping.put(getString(R.string.lang_dotrakiyan), "dothraki.json");
        this.languagesMapping.put(getString(R.string.lang_minion), "minion.json");
        this.languagesMapping.put(getString(R.string.lang_doge), "doge.json");
        this.languagesMapping.put(getString(R.string.lang_valyrian), "valyrian.json");
        this.languagesMapping.put(getString(R.string.pig_latin), "pig-latin.json");
        this.languagesMapping.put(getString(R.string.pirate), "pirate.json");
        this.languagesMapping.put(getString(R.string.dolan), "dolan.json");


        Spinner spinner = (Spinner) findViewById(R.id.spinner_nav);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.fun_languages, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    AUDIO_RECORD_REQUEST_CODE);
        }
    }

    private void setupTTS() {
        this.textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                if (textToSpeech.isLanguageAvailable(new Locale(Locale.getDefault().getLanguage()))
                        == TextToSpeech.LANG_AVAILABLE) {
                    textToSpeech.setLanguage(new Locale(Locale.getDefault().getLanguage()));
                } else {
                    textToSpeech.setLanguage(Locale.US);
                }
                textToSpeech.setPitch(1.0f);
                textToSpeech.setSpeechRate(1f);
                ttsEnabled = true;
                Log.d("SETUP_TTS", "OK");
            } else if (status == TextToSpeech.ERROR) {
                Toast.makeText(
                        VoiceActorActivity.this,
                        getResources().getString(R.string.tts_initialization_error),
                        Toast.LENGTH_LONG).show();
                ttsEnabled = false;
                Log.d("SETUP_TTS", "ERROR");
            }
        });
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(this);
    }

    @OnClick(R.id.stop_listening)
    public void onStopListeningClicked() {
        if (isListening) {
            speechRecognizer.cancel();
            stopListeningButton.setImageResource(R.drawable.ic_baseline_mic_none_24);
        } else {
            speechRecognizer.startListening(speechRecognizerIntent);
            stopListeningButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
        }
    }

    @OnClick(R.id.save_user_message)
    public void onUserMessageSave() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.lang_yoda);
        String language = sharedPref.getString(getString(R.string.saved_selected_lang_id), defaultValue);

        voiceActorPresenter.addMessage(
                new Message(true, recognizedTextContainer.getText().toString(), "", language ),
                language
        );
        recognizedTextContainer.setText("");
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        recognizedTextContainer.setText(data.get(0));
        Toast.makeText(VoiceActorActivity.this, "Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        ArrayList<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        recognizedTextContainer.setText(data.get(0));
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d("LISTENER", "onEvent " + eventType);
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("LISTENER", "onReady " + params);
    }

    @Override
    public void onBeginningOfSpeech() {
        recognizedTextContainer.setText("Listening...");
        Toast.makeText(VoiceActorActivity.this, "Listening", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRmsChanged(float rmsdB) {

    }

    @Override
    public void onBufferReceived(byte[] buffer) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d("LISTENER", "onEnd ");
        stopListeningButton.setImageResource(R.drawable.ic_baseline_mic_none_24);
    }

    @Override
    public void onError(int error) {
        Log.e("LISTENER", error + "");
        speechRecognizer.cancel();
        isListening = false;
        stopListeningButton.setImageResource(R.drawable.ic_baseline_mic_none_24);
    }

    @Override
    public MessageRepository getMessageRepository() {
        return messageRepository;
    }

    @Override
    public void addMessageToRecycler(Message message) {
        runOnUiThread(() -> {
            if (!message.isUserMessage) {
                textToSpeech.speak(message.content, TextToSpeech.QUEUE_ADD, null, Math.random() + "");
            }

            messagesAdapter.addMessage(message);
            messagesRecycler.scrollToPosition(messagesAdapter.getItemCount() - 1);
        });
    }

    @Override
    public void showMessages(List<Message> messages) {
        runOnUiThread(() -> {
            messagesAdapter.setMessages(messages);
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
        String selectedEntry = adapterView.getItemAtPosition(i).toString();
        String selectedLangId = this.languagesMapping.get(selectedEntry);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_selected_lang_id), selectedLangId);
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Another interface callback
    }
}