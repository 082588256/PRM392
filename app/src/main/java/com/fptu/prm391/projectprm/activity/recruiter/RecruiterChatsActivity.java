package com.fptu.prm391.projectprm.activity.recruiter;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.adapter.ChatContactAdapter;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.MessageDAO;
import com.fptu.prm391.projectprm.model.User;
import com.fptu.prm391.projectprm.model.Message;
import com.fptu.prm391.projectprm.activity.common.ChatActivity;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class RecruiterChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_chats);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerViewContacts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int recruiterId = SharedPrefManager.getInstance(this).getUser().getId();
        MessageDAO messageDAO = new MessageDAO(new DatabaseHelper(this).getReadableDatabase());
        List<User> studentList = messageDAO.getAllChatStudentsForRecruiter(recruiterId);

        // Lấy tin nhắn cuối cùng với từng student
        List<Message> lastMsgList = new ArrayList<>();
        for (User student : studentList) {
            Message lastMsg = messageDAO.getLastMessageBetween(recruiterId, student.getId());
            lastMsgList.add(lastMsg);
        }

        adapter = new ChatContactAdapter(studentList, lastMsgList, recruiterId, student -> {
            // Mở ChatActivity với recruiter là sender, student là receiver
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra("SENDER_ID", recruiterId);
            intent.putExtra("RECEIVER_ID", student.getId());
            intent.putExtra("RECEIVER_NAME", student.getName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadChatList();
    }

    private void reloadChatList() {
        int recruiterId = SharedPrefManager.getInstance(this).getUser().getId();
        MessageDAO messageDAO = new MessageDAO(new DatabaseHelper(this).getReadableDatabase());
        List<User> studentList = messageDAO.getAllChatStudentsForRecruiter(recruiterId);

        List<Message> lastMsgList = new ArrayList<>();
        for (User student : studentList) {
            Message lastMsg = messageDAO.getLastMessageBetween(recruiterId, student.getId());
            lastMsgList.add(lastMsg);
        }

        if (adapter == null) {
            adapter = new ChatContactAdapter(studentList, lastMsgList, recruiterId, student -> {
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("SENDER_ID", recruiterId);
                intent.putExtra("RECEIVER_ID", student.getId());
                intent.putExtra("RECEIVER_NAME", student.getName());
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(studentList, lastMsgList); // Viết thêm hàm này trong adapter
        }
    }
}