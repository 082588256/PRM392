package com.fptu.prm391.projectprm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.model.User;
import com.fptu.prm391.projectprm.model.Message;
import java.util.List;

public class ChatContactAdapter extends RecyclerView.Adapter<ChatContactAdapter.ContactViewHolder> {

    public interface OnContactClickListener {
        void onClick(User user);
    }

    private final List<User> contacts;
    private final List<Message> lastMessages;
    private final int recruiterId;
    private final OnContactClickListener listener;

    /**
     * @param contacts      Danh sách sinh viên đã chat
     * @param lastMessages  Tin nhắn cuối cùng ứng với từng sinh viên
     * @param recruiterId   Id recruiter đang đăng nhập
     * @param listener      Sự kiện click
     */
    public ChatContactAdapter(List<User> contacts, List<Message> lastMessages, int recruiterId, OnContactClickListener listener) {
        this.contacts = contacts;
        this.lastMessages = lastMessages;
        this.recruiterId = recruiterId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_contact, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        User user = contacts.get(position);
        holder.tvName.setText(user.getName());

        // Xử lý hiển thị tin nhắn cuối cùng
        Message lastMsg = lastMessages.get(position);
        if (lastMsg != null) {
            if (lastMsg.getSenderId() == recruiterId) {
                holder.tvLastMessage.setText("You: " + lastMsg.getContent());
            } else {
                holder.tvLastMessage.setText(lastMsg.getContent());
            }
        } else {
            holder.tvLastMessage.setText(""); // Không có tin nhắn
        }

        holder.itemView.setOnClickListener(view -> listener.onClick(user));
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLastMessage;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
        }
    }

    public void updateData(List<User> newContacts, List<Message> newLastMessages) {
        this.contacts.clear();
        this.contacts.addAll(newContacts);
        this.lastMessages.clear();
        this.lastMessages.addAll(newLastMessages);
        notifyDataSetChanged();
    }
}