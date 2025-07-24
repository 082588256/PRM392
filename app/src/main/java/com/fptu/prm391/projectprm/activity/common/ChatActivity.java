package com.fptu.prm391.projectprm.activity.common;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.adapter.ChatAdapter;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.MessageDAO;
import com.fptu.prm391.projectprm.model.Message;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText edtMessage;
    private ImageButton btnSend;
    private ChatAdapter adapter;
    private List<Message> messageList;
    private MessageDAO messageDAO;

    private int senderId;
    private int receiverId;
    private String receiverName;

    private final List<String> predefinedReplies = Arrays.asList(
            "Thanks for your interest!",
            "We'll review your application soon.",
            "Can you share your portfolio?",
            "We’ll get back to you shortly.",
            "Please email your CV to hr@techcorp.com"
    );

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        senderId = getIntent().getIntExtra("SENDER_ID", -1);
        receiverId = getIntent().getIntExtra("RECEIVER_ID", -1);
        receiverName = getIntent().getStringExtra("RECEIVER_NAME");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chat with " + receiverName);
        }

        recyclerView = findViewById(R.id.recyclerViewChat);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);

        messageDAO = new MessageDAO(new DatabaseHelper(this).getWritableDatabase());
        messageList = messageDAO.getConversation(senderId, receiverId);

        adapter = new ChatAdapter(messageList, senderId);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        btnSend.setOnClickListener(view -> {
            String content = edtMessage.getText().toString().trim();

            if (!content.isEmpty()) {
                Message newMessage = new Message(senderId, receiverId, content);
                messageDAO.insertMessage(newMessage);
                messageList.add(newMessage);
                adapter.notifyItemInserted(messageList.size() - 1);
                recyclerView.scrollToPosition(messageList.size() - 1);
                edtMessage.setText("");

                // Simulate recruiter reply after 2 seconds
                handler.postDelayed(() -> {
                    String replyText = getRandomReply();
                    Message replyMessage = new Message(receiverId, senderId, replyText);
                    messageDAO.insertMessage(replyMessage);
                    messageList.add(replyMessage);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }, 2000);
            }
        });
    }

    // ✅ Di chuyển ra ngoài
    private String getRandomReply() {
        int index = new Random().nextInt(predefinedReplies.size());
        return predefinedReplies.get(index);
    }
}
