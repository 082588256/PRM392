package com.fptu.prm391.projectprm.activity.recruiter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.adapter.ApplicationAdapter;
import com.fptu.prm391.projectprm.adapter.ScheduleAdapter;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerRecruiterJobs;
    private TextView tvEmptyRecruiterMessage;
    private TextView tvAppliedTab, tvScheduleTab;

    private ApplicationAdapter applicationAdapter;
    private ScheduleAdapter scheduleAdapter;

    private List<Application> applicationList;
    private List<Application> scheduleList;

    private ApplicationDAO applicationDAO;

    private int recruiterId;

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

        recyclerRecruiterJobs.setLayoutManager(new LinearLayoutManager(this));

        applicationList = new ArrayList<>();
        scheduleList = new ArrayList<>();

        applicationAdapter = new ApplicationAdapter(applicationList, null, true); // Set isRecruiterMode to true for recruiter
        scheduleAdapter = new ScheduleAdapter(scheduleList);

        recyclerRecruiterJobs.setAdapter(applicationAdapter);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        applicationDAO = new ApplicationDAO(dbHelper.getWritableDatabase());

        recruiterId = SharedPrefManager.getInstance(this).getUser().getId();

        // Set confirm click listener
        applicationAdapter.setOnConfirmClickListener((application, position) -> {
            int rowsUpdated = applicationDAO.updateApplicationStatus(application.getId(), "Confirmed");
            if (rowsUpdated > 0) {
                application.setStatus("Confirmed");
                applicationAdapter.updateApplications(applicationList); // Update the entire list
                loadApplications(); // Reload data after confirmation
            }
        });

        tvAppliedTab.setOnClickListener(v -> {
            selectTab(true);
            loadApplications(); // Reload data when switching to applied tab
        });

        tvScheduleTab.setOnClickListener(v -> {
            selectTab(false);
            loadScheduledAppointments(); // Reload data when switching to schedule tab
        });

        selectTab(true);
        loadApplications();
    }

    private void selectTab(boolean isAppliedSelected) {
        if (isAppliedSelected) {
            tvAppliedTab.setBackgroundResource(R.drawable.tab_selected_bg);
            tvAppliedTab.setTextColor(getResources().getColor(R.color.black));
            tvScheduleTab.setBackgroundResource(R.drawable.tab_unselected_bg);
            tvScheduleTab.setTextColor(getResources().getColor(R.color.gray));
        } else {
            tvScheduleTab.setBackgroundResource(R.drawable.tab_selected_bg);
            tvScheduleTab.setTextColor(getResources().getColor(R.color.black));
            tvAppliedTab.setBackgroundResource(R.drawable.tab_unselected_bg);
            tvAppliedTab.setTextColor(getResources().getColor(R.color.gray));
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

        recyclerRecruiterJobs.setAdapter(applicationAdapter); // Ensure adapter is set
        applicationAdapter.updateApplications(applicationList); // Update adapter with new data
    }

    private void loadScheduledAppointments() {
        scheduleList.clear();
        scheduleList.addAll(applicationDAO.getConfirmedApplicationsWithInterviewByRecruiter(recruiterId));
        Log.d("HistoryActivity", "Loaded " + scheduleList.size() + " scheduled appointments for recruiterId " + recruiterId);

        if (scheduleList.isEmpty()) {
            tvEmptyRecruiterMessage.setVisibility(View.VISIBLE);
            tvEmptyRecruiterMessage.setText("Bạn chưa có lịch hẹn nào.");
            recyclerRecruiterJobs.setVisibility(View.GONE);
        } else {
            tvEmptyRecruiterMessage.setVisibility(View.GONE);
            recyclerRecruiterJobs.setVisibility(View.VISIBLE);
        }

        recyclerRecruiterJobs.setAdapter(scheduleAdapter); // Set adapter for schedule list
        scheduleAdapter.updateScheduleList(scheduleList); // Update schedule adapter with new data
    }
}