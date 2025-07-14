package com.fptu.prm391.projectprm.activity.student;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.adapter.ApplicationAdapter;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.List;

public class AppliedJobsActivity extends AppCompatActivity {

    private RecyclerView recyclerApplications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applied_jobs);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        recyclerApplications = findViewById(R.id.recycler_applied_jobs);
        recyclerApplications.setLayoutManager(new LinearLayoutManager(this));

        int studentId = SharedPrefManager.getInstance(this).getUser().getId();
        ApplicationDAO dao = new ApplicationDAO(new DatabaseHelper(this).getReadableDatabase());
        List<Application> applications = dao.getApplicationsWithInternship(studentId);

        if (applications.isEmpty()) {
            Toast.makeText(this, "Bạn chưa ứng tuyển công việc nào.", Toast.LENGTH_SHORT).show();
        }

        ApplicationAdapter adapter = new ApplicationAdapter(applications);
        recyclerApplications.setAdapter(adapter);
    }
}
