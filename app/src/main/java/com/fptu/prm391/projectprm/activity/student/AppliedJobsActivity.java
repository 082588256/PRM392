package com.fptu.prm391.projectprm.activity.student;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.adapter.ApplicationAdapter;
import com.fptu.prm391.projectprm.adapter.InterviewInfoAdapter;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InterviewDAO;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.InterviewInfo;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.List;

public class AppliedJobsActivity extends AppCompatActivity {

    private ApplicationAdapter appliedAdapter;
    private InterviewInfoAdapter interviewAdapter;
    private RecyclerView recyclerApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_jobs);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_applied_jobs);

        recyclerApplications = findViewById(R.id.recycler_applied_jobs);
        recyclerApplications.setLayoutManager(new LinearLayoutManager(this));

        TextView tabApplied = findViewById(R.id.tv_applied);
        TextView tabSaved = findViewById(R.id.tv_saved);

        int studentId = SharedPrefManager.getInstance(this).getUser().getId();

        // Tab 1: Đã ứng tuyển
        ApplicationDAO dao = new ApplicationDAO(new DatabaseHelper(this).getReadableDatabase());
        List<Application> appliedList = dao.getApplicationsWithInternship(studentId);
        appliedAdapter = new ApplicationAdapter(appliedList);

        // Tab 2: Việc đã lưu (hiển thị lịch phỏng vấn)
        InterviewDAO interviewDAO = new InterviewDAO(new DatabaseHelper(this).getReadableDatabase());
        List<InterviewInfo> interviewList = interviewDAO.getInterviewInfoByStudentId(studentId);
        interviewAdapter = new InterviewInfoAdapter(interviewList, interviewDAO);

        // Đặt adapter mặc định là danh sách đã ứng tuyển
        recyclerApplications.setAdapter(appliedAdapter);

        // Xử lý tab
        tabApplied.setOnClickListener(v -> {
            tabApplied.setBackgroundResource(R.drawable.tab_selected_bg);
            tabSaved.setBackgroundResource(R.drawable.tab_unselected_bg);
            tabApplied.setTextColor(getColor(R.color.black));
            tabSaved.setTextColor(getColor(R.color.gray));
            recyclerApplications.setAdapter(appliedAdapter);
        });
        List<Application> applications = dao.getApplicationsWithInternship(studentId);

        tabSaved.setOnClickListener(v -> {
            tabSaved.setBackgroundResource(R.drawable.tab_selected_bg);
            tabApplied.setBackgroundResource(R.drawable.tab_unselected_bg);
            tabSaved.setTextColor(getColor(R.color.black));
            tabApplied.setTextColor(getColor(R.color.gray));
            recyclerApplications.setAdapter(interviewAdapter);
        });

        if (appliedList.isEmpty()) {
            if (applications.isEmpty()) {
                Toast.makeText(this, "Bạn chưa ứng tuyển công việc nào.", Toast.LENGTH_SHORT).show();
            }

            ApplicationAdapter adapter = new ApplicationAdapter(applications);
            recyclerApplications.setAdapter(adapter);
        }
    }
}
