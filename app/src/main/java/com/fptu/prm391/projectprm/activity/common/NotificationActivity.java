package com.fptu.prm391.projectprm.activity.common;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.adapter.NotificationAdapter;
import com.fptu.prm391.projectprm.db.NotificationDAO;
import com.fptu.prm391.projectprm.model.Notification;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerView = findViewById(R.id.recyclerNotifications);
        tvEmpty = findViewById(R.id.tvEmptyNotification);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // ✅ Load tất cả notification
        int userId = SharedPrefManager.getInstance(this).getUser().getId();
        NotificationDAO dao = new NotificationDAO(this);
        List<Notification> notifications = dao.getNotificationsByUserId(userId);

        if (notifications.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new NotificationAdapter(notifications);
        recyclerView.setAdapter(adapter);
    }
}
