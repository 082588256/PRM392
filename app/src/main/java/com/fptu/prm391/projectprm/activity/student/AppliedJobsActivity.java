package com.fptu.prm391.projectprm.activity.student;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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
    private ApplicationDAO applicationDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_jobs);
        Log.d("AppliedJobsActivity", "onCreate started");

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerApplications = findViewById(R.id.recycler_applied_jobs);
        recyclerApplications.setLayoutManager(new LinearLayoutManager(this));
        Log.d("AppliedJobsActivity", "RecyclerView initialized");

        TextView tabApplied = findViewById(R.id.tv_applied);
        TextView tabSaved = findViewById(R.id.tv_saved);

        int studentId = SharedPrefManager.getInstance(this).getUser().getId();
        Log.d("AppliedJobsActivity", "Student ID: " + studentId);

        // Initialize ApplicationDAO with writable database
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        applicationDAO = new ApplicationDAO(dbHelper.getWritableDatabase());
        Log.d("AppliedJobsActivity", "ApplicationDAO initialized");

        // Tab 1: Applied jobs
        List<Application> appliedList = applicationDAO.getApplicationsWithInternship(studentId);
        Log.d("AppliedJobsActivity", "Fetched " + appliedList.size() + " applications for studentId " + studentId);
        appliedAdapter = new ApplicationAdapter(appliedList, applicationId -> {
            Log.d("AppliedJobsActivity", "Withdraw clicked for applicationId: " + applicationId);
            // Show confirmation dialog
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận rút đơn")
                    .setMessage("Bạn có chắc muốn rút đơn ứng tuyển này?")
                    .setPositiveButton("Rút", (dialog, which) -> {
                        try {
                            Log.d("AppliedJobsActivity", "Attempting to update status for applicationId: " + applicationId);
                            int result = applicationDAO.updateApplicationStatus(applicationId, "Withdrawn application");
                            if (result > 0) {
                                Toast.makeText(this, "Rút đơn thành công!", Toast.LENGTH_SHORT).show();
                                Log.d("AppliedJobsActivity", "Update successful for applicationId: " + applicationId);
                                applicationDAO.debugApplication(applicationId); // Debug application status
                                refreshApplications(studentId);
                            } else {
                                Toast.makeText(this, "Lỗi khi rút đơn! Đơn không tồn tại hoặc trạng thái không hợp lệ.", Toast.LENGTH_SHORT).show();
                                Log.w("AppliedJobsActivity", "Update failed for applicationId: " + applicationId);
                            }
                        } catch (Exception e) {
                            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("AppliedJobsActivity", "Error updating status for applicationId " + applicationId + ": " + e.getMessage());
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        }, false); // Set isRecruiterMode to false for student

        // Tab 2: Saved jobs (interviews)
        InterviewDAO interviewDAO = new InterviewDAO(dbHelper.getReadableDatabase());
        List<InterviewInfo> interviewList = interviewDAO.getInterviewInfoByStudentId(studentId);
        Log.d("AppliedJobsActivity", "Fetched " + interviewList.size() + " interviews for studentId " + studentId);
        interviewAdapter = new InterviewInfoAdapter(interviewList, interviewDAO);

        // Set default adapter to applied jobs
        recyclerApplications.setAdapter(appliedAdapter);
        Log.d("AppliedJobsActivity", "Set default adapter to appliedAdapter");

        // Handle tab clicks
        tabApplied.setOnClickListener(v -> {
            tabApplied.setBackgroundResource(R.drawable.tab_selected_bg);
            tabSaved.setBackgroundResource(R.drawable.tab_unselected_bg);
            tabApplied.setTextColor(getColor(R.color.black));
            tabSaved.setTextColor(getColor(R.color.gray));
            recyclerApplications.setAdapter(appliedAdapter);
            Log.d("AppliedJobsActivity", "Switched to applied tab");
            refreshApplications(studentId);
        });

        tabSaved.setOnClickListener(v -> {
            tabSaved.setBackgroundResource(R.drawable.tab_selected_bg);
            tabApplied.setBackgroundResource(R.drawable.tab_unselected_bg);
            tabSaved.setTextColor(getColor(R.color.black));
            tabApplied.setTextColor(getColor(R.color.gray));
            recyclerApplications.setAdapter(interviewAdapter);
            Log.d("AppliedJobsActivity", "Switched to saved tab");
        });

        if (appliedList.isEmpty()) {
            Toast.makeText(this, "Bạn chưa ứng tuyển công việc nào.", Toast.LENGTH_SHORT).show();
            Log.d("AppliedJobsActivity", "No applications found for studentId " + studentId);
        }
    }

    private void refreshApplications(int studentId) {
        List<Application> updatedList = applicationDAO.getApplicationsWithInternship(studentId);
        appliedAdapter.updateApplications(updatedList);
        Log.d("AppliedJobsActivity", "Refreshed applications, new count: " + updatedList.size());
        if (updatedList.isEmpty()) {
            Toast.makeText(this, "Bạn chưa ứng tuyển công việc nào.", Toast.LENGTH_SHORT).show();
            Log.d("AppliedJobsActivity", "Refreshed list is empty for studentId " + studentId);
        }
    }
}