package com.fptu.prm391.projectprm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.Message;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Message> messages;
    private final int currentUserId;

    public ChatAdapter(List<Message> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        // Nếu người gửi là chính mình → layout bên phải
        return messages.get(position).getSenderId() == currentUserId ? 1 : 0;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1)
                ? R.layout.item_message_right
                : R.layout.item_message_left;

        View view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textMessage.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }
}
