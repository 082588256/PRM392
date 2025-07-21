package com.fptu.prm391.projectprm.activity.recruiter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.adapter.ApplicationAdapter;
import com.fptu.prm391.projectprm.adapter.ScheduleAdapter;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InterviewDAO;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.Interview;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerRecruiterJobs;
    private TextView tvEmptyRecruiterMessage;
    private TextView tvAppliedTab, tvScheduleTab;
    private Button btnSaveSchedules;

    private ApplicationAdapter applicationAdapter;
    private ScheduleAdapter scheduleAdapter;

    private List<Application> applicationList;
    private List<Application> scheduleList;

    private ApplicationDAO applicationDAO;

    private int recruiterId;

    private boolean isAppliedTab = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_job_recruiter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerRecruiterJobs = findViewById(R.id.recycler_recruiter_jobs);
        tvEmptyRecruiterMessage = findViewById(R.id.tvEmptyRecruiterMessage);
        tvAppliedTab = findViewById(R.id.tv_applied);
        tvScheduleTab = findViewById(R.id.tv_schedule);
        btnSaveSchedules = findViewById(R.id.btnSaveSchedules);

        recyclerRecruiterJobs.setLayoutManager(new LinearLayoutManager(this));

        applicationList = new ArrayList<>();
        scheduleList = new ArrayList<>();

        applicationAdapter = new ApplicationAdapter(applicationList, null, true);
        scheduleAdapter = new ScheduleAdapter(scheduleList);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        applicationDAO = new ApplicationDAO(dbHelper.getWritableDatabase());

        recruiterId = SharedPrefManager.getInstance(this).getUser().getId();

        applicationAdapter.setOnConfirmClickListener((application, position, newStatus) -> {
            int rowsUpdated = applicationDAO.updateApplicationStatus(application.getId(), newStatus);
            if (rowsUpdated > 0) {
                application.setStatus(newStatus);
                application.setInterviewStatus(newStatus);
                applicationAdapter.updateApplications(applicationList);
                loadApplications();
            }
        });

        tvAppliedTab.setOnClickListener(v -> {
            selectTab(true);
            loadApplications();
        });

        tvScheduleTab.setOnClickListener(v -> {
            selectTab(false);
            loadScheduledAppointments();
        });

        btnSaveSchedules.setOnClickListener(v -> {
            Log.d("HistoryActivity", "Lưu lịch hẹn đã nhấn");

            DatabaseHelper dbHelper1 = new DatabaseHelper(this);
            InterviewDAO interviewDAO = new InterviewDAO(dbHelper1.getWritableDatabase());
            boolean allSuccess = true;

            for (Application application : scheduleList) {
                Log.d("DEBUG", "Application ID: " + application.getId()
                        + ", interviewId: " + application.getInterviewId()
                        + ", scheduledTime: " + application.getInterviewScheduledTime());

                int interviewId = application.getInterviewId();
                String newTime = application.getInterviewScheduledTime();

                if (interviewId > 0 && newTime != null && !newTime.isEmpty()) {
                    boolean updated = interviewDAO.updateInterviewTime(interviewId, newTime);
                    if (!updated) {
                        allSuccess = false;
                        Log.e("HistoryActivity", "Không thể cập nhật lịch hẹn cho interviewId: " + interviewId);
                    } else {
                        Log.d("HistoryActivity", "Đã cập nhật lịch hẹn cho interviewId: " + interviewId + " với thời gian: " + newTime);
                    }
                } else {
                    Log.w("HistoryActivity", "Dữ liệu không hợp lệ: interviewId=" + interviewId + ", newTime=" + newTime);
                }
            }

            if (allSuccess) {
                Toast.makeText(HistoryActivity.this, "Cập nhật lịch hẹn thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(HistoryActivity.this, "Một số lịch hẹn không thể cập nhật.", Toast.LENGTH_SHORT).show();
            }
        });

        selectTab(true);
        loadApplications();
    }

    private void selectTab(boolean isAppliedSelected) {
        this.isAppliedTab = isAppliedSelected;

        if (isAppliedSelected) {
            tvAppliedTab.setBackgroundResource(R.drawable.tab_selected_bg);
            tvAppliedTab.setTextColor(getResources().getColor(R.color.black));
            tvScheduleTab.setBackgroundResource(R.drawable.tab_unselected_bg);
            tvScheduleTab.setTextColor(getResources().getColor(R.color.gray));
            btnSaveSchedules.setVisibility(View.GONE);
        } else {
            tvScheduleTab.setBackgroundResource(R.drawable.tab_selected_bg);
            tvScheduleTab.setTextColor(getResources().getColor(R.color.black));
            tvAppliedTab.setBackgroundResource(R.drawable.tab_unselected_bg);
            tvAppliedTab.setTextColor(getResources().getColor(R.color.gray));
            btnSaveSchedules.setVisibility(View.VISIBLE);
        }
    }

    private void loadApplications() {
        applicationList.clear();
        applicationList.addAll(applicationDAO.getPendingApplicationsWithDetailsByRecruiter(recruiterId));
        Log.d("HistoryActivity", "Loaded " + applicationList.size() + " pending applications for recruiterId " + recruiterId);

        if (applicationList.isEmpty()) {
            tvEmptyRecruiterMessage.setVisibility(View.VISIBLE);
            tvEmptyRecruiterMessage.setText("Không có ứng tuyển nào.");
            recyclerRecruiterJobs.setVisibility(View.GONE);
        } else {
            tvEmptyRecruiterMessage.setVisibility(View.GONE);
            recyclerRecruiterJobs.setVisibility(View.VISIBLE);
        }

        recyclerRecruiterJobs.setAdapter(applicationAdapter);
        applicationAdapter.updateApplications(applicationList);
    }

    private void loadScheduledAppointments() {
        scheduleList.clear();
        scheduleList.addAll(applicationDAO.getConfirmedApplicationsWithInterviewByRecruiter(recruiterId));
        Log.d("HistoryActivity", "Loaded " + scheduleList.size() + " scheduled appointments for recruiterId " + recruiterId);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        InterviewDAO interviewDAO = new InterviewDAO(dbHelper.getWritableDatabase());

        for (Application application : scheduleList) {
            Log.d("DEBUG", "Application ID: " + application.getId()
                    + ", interviewId: " + application.getInterviewId()
                    + ", scheduledTime: " + application.getInterviewScheduledTime());

            if (application.getInterviewId() == 0) {
                Interview interview = new Interview();
                interview.setApplicationId(application.getId());
                interview.setScheduledTime(null);
                interview.setStatus("Pending");
                interview.setNotes(null);

                long interviewId = interviewDAO.insertInterview(interview);
                Log.d("DEBUG", "Created interview for applicationId=" + application.getId() + " -> interviewId=" + interviewId);

                application.setInterviewId((int) interviewId);
            }
        }

        if (scheduleList.isEmpty()) {
            tvEmptyRecruiterMessage.setVisibility(View.VISIBLE);
            tvEmptyRecruiterMessage.setText("Bạn chưa có lịch hẹn nào.");
            recyclerRecruiterJobs.setVisibility(View.GONE);
        } else {
            tvEmptyRecruiterMessage.setVisibility(View.GONE);
            recyclerRecruiterJobs.setVisibility(View.VISIBLE);
        }

        recyclerRecruiterJobs.setAdapter(scheduleAdapter);
        scheduleAdapter.updateScheduleList(scheduleList);
    }
}
