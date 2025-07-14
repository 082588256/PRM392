package com.fptu.prm391.projectprm.activity.student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.Internship;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

import java.util.List;

public class InternshipDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvCompany, tvLocation, tvDuration,
            tvStipend, tvDeadline, tvDescription, tvRequirements;
    private Button btnApply;
    private Internship internship;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        int internshipId = getIntent().getIntExtra("INTERNSHIP_ID", -1);
        if (internshipId == -1) {
            Toast.makeText(this, "Invalid Internship ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Init views
        tvTitle = findViewById(R.id.tvTitle);
        tvCompany = findViewById(R.id.tvCompany);
        tvLocation = findViewById(R.id.tvLocation);
        tvDuration = findViewById(R.id.tvDuration);
        tvStipend = findViewById(R.id.tvStipend);
        tvDeadline = findViewById(R.id.tvDeadline);
        tvDescription = findViewById(R.id.tvDescription);
        tvRequirements = findViewById(R.id.tvRequirements);
        btnApply = findViewById(R.id.btnApply);

        // Get student info from session
        studentId = SharedPrefManager.getInstance(this).getUser().getId();

        // Get internship details
        InternshipDAO internshipDAO = new InternshipDAO(new DatabaseHelper(this).getReadableDatabase());
        internship = internshipDAO.getInternshipById(internshipId);

        if (internship != null) {
            bindInternshipData(internship);
            checkIfAlreadyApplied();
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin thực tập.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Handle Apply button
        btnApply.setOnClickListener(v -> {
            ApplicationDAO appDAO = new ApplicationDAO(new DatabaseHelper(this).getWritableDatabase());

            Application application = new Application(studentId, internship.getId(), "My resume here");

            long result = appDAO.insertApplication(application);

            if (result != -1) {
                Toast.makeText(this, "Ứng tuyển thành công!", Toast.LENGTH_SHORT).show();
                btnApply.setEnabled(false);
                btnApply.setText("Đã ứng tuyển");
            } else {
                Toast.makeText(this, "Ứng tuyển thất bại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bindInternshipData(Internship internship) {
        tvTitle.setText(internship.getTitle());
        tvCompany.setText(internship.getCompany());
        tvLocation.setText(internship.getLocation());
        tvDuration.setText(internship.getDuration());
        tvStipend.setText(internship.getStipend());
        tvDeadline.setText(internship.getDeadline());
        tvDescription.setText(internship.getDescription());
        tvRequirements.setText(internship.getRequirements());
    }

    private void checkIfAlreadyApplied() {
        ApplicationDAO appDAO = new ApplicationDAO(new DatabaseHelper(this).getReadableDatabase());
        List<Application> existingApplications = appDAO.getApplicationsByStudentId(studentId);

        for (Application app : existingApplications) {
            if (app.getInternshipId() == internship.getId()) {
                btnApply.setEnabled(false);
                btnApply.setText("Đã ứng tuyển");
                break;
            }
        }
    }
}
