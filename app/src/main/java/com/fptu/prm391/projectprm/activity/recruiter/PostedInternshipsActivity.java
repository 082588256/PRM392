package com.fptu.prm391.projectprm.activity.recruiter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.activity.auth.LoginActivity;
import com.fptu.prm391.projectprm.activity.common.NotificationActivity;
import com.fptu.prm391.projectprm.activity.student.InternshipDetailActivity;
import com.fptu.prm391.projectprm.adapter.InternshipAdapter;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.model.Internship;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.List;

public class PostedInternshipsActivity extends AppCompatActivity {

    private RecyclerView recyclerPosted;
    private TextView tvEmpty;
    private InternshipAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posted_internships);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerPosted = findViewById(R.id.recycler_posted_internships);
        tvEmpty = findViewById(R.id.tvEmptyMessage);
        recyclerPosted.setLayoutManager(new LinearLayoutManager(this));

        int recruiterId = SharedPrefManager.getInstance(this).getUser().getId();
        InternshipDAO dao = new InternshipDAO(new DatabaseHelper(this).getReadableDatabase());
        List<Internship> postedList = dao.getInternshipsByRecruiterId(recruiterId);

        if (postedList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerPosted.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerPosted.setVisibility(View.VISIBLE);
        }

        Button btnAddInternship = findViewById(R.id.btnAddInternship);
        btnAddInternship.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddInternshipActivity.class);
            startActivity(intent);
        });

        ImageButton btnMessages = findViewById(R.id.btnMessages);
        btnMessages.setOnClickListener(v -> {
            Intent intent = new Intent(this, RecruiterChatsActivity.class);
            startActivity(intent);
        });

        // Đăng xuất
        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPrefManager.getInstance(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        ImageButton btnNotifications = findViewById(R.id.btnNoti);
        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(this, NotificationActivity.class);
            startActivity(intent);
        });

        // Sự kiện click vào icon đồng hồ để chuyển trang
        ImageView imgHistoryIcon = findViewById(R.id.imgHistoryIcon);
        imgHistoryIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, HistoryActivity.class); // Đổi HistoryActivity thành activity của bạn nếu muốn
            startActivity(intent);
        });

        adapter = new InternshipAdapter(postedList, internship -> {
            Intent intent = new Intent(this, RecruiterInternshipDetailActivity.class);
            intent.putExtra("internship_id", internship.getId());
            startActivity(intent);
        });

        recyclerPosted.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadInternships();
    }

    private void reloadInternships() {
        int recruiterId = SharedPrefManager.getInstance(this).getUser().getId();
        InternshipDAO dao = new InternshipDAO(new DatabaseHelper(this).getReadableDatabase());
        List<Internship> postedList = dao.getInternshipsByRecruiterId(recruiterId);
        adapter.updateList(postedList); // Viết thêm hàm này trong InternshipAdapter
        if (postedList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerPosted.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerPosted.setVisibility(View.VISIBLE);
        }
    }
}
