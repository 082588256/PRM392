package com.fptu.prm391.projectprm.activity.recruiter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.activity.auth.LoginActivity;
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

        ImageButton btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPrefManager.getInstance(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        adapter = new InternshipAdapter(postedList, internship -> {
            // Chuyển đến InternshipDetailActivity nếu cần
            Intent intent = new Intent(this, InternshipDetailActivity.class);
            intent.putExtra("internship_id", internship.getId());
            startActivity(intent);
        });

        recyclerPosted.setAdapter(adapter);
    }
}
