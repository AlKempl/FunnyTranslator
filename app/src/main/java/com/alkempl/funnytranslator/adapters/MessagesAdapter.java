package com.alkempl.funnytranslator.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alkempl.funnytranslator.Application;
import com.alkempl.funnytranslator.R;
import com.alkempl.funnytranslator.entities.Message;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;

    public MessagesAdapter() {
        messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @LayoutRes int layout = viewType == 0
                ? R.layout.user_message_view_holder
                : R.layout.bot_message_view_holder;

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(layout, parent, false);

        if (viewType == 0) {
            return new UsersMessageViewHolder(view);
        } else {
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UsersMessageViewHolder) {
            ((UsersMessageViewHolder) holder).setMessage(messages.get(position).content);
            ((UsersMessageViewHolder) holder).setLanguage(messages.get(position).lang);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).setMessage(messages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUserMessage ? 0 : 1;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
        this.notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        this.notifyDataSetChanged();
    }

    public static class UsersMessageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.message_content)
        TextView messageContent;
        @BindView(R.id.language_label)
        TextView languageLabel;

        public UsersMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setMessage(String message) {
            messageContent.setText(message);
        }

        public void setLanguage(String language) {
            languageLabel.setText(language);
        }
    }

    public static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message_content)
        TextView messageContent;

        @BindView(R.id.movie_link)
        TextView movieLink;

        @BindView(R.id.movie_poster)
        ImageView moviePoster;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setMessage(Message message) {
            messageContent.setText(message.content);

            if (message.imageUrl != null && !message.imageUrl.isEmpty()) {
                RequestOptions myOptions = new RequestOptions()
                        .override(150);

                Glide.with(Application.getAppContext())
                        .applyDefaultRequestOptions(myOptions)
                        .load(message.imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(moviePoster);
            } else {
                moviePoster.setImageDrawable(null);
            }
        }
    }
}
