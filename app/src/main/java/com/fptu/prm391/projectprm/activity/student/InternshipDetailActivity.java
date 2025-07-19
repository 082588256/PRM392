package com.fptu.prm391.projectprm.activity.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.model.Internship;

public class InternshipDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvCompany, tvLocation, tvDuration,
            tvStipend, tvDeadline, tvDescription, tvRequirements;

    private MaterialButton btnApply;
    private ImageButton btnMap;

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

        // Ánh xạ view
        tvTitle = findViewById(R.id.tvTitle);
        tvCompany = findViewById(R.id.tvCompany);
        tvLocation = findViewById(R.id.tvLocation);
        tvDuration = findViewById(R.id.tvDuration);
        tvStipend = findViewById(R.id.tvStipend);
        tvDeadline = findViewById(R.id.tvDeadline);
        tvDescription = findViewById(R.id.tvDescription);
        tvRequirements = findViewById(R.id.tvRequirements);
        btnApply = findViewById(R.id.btnApply);
        btnMap = findViewById(R.id.btnMap); // Ánh xạ nút bản đồ

        // Lấy dữ liệu từ DAO
        InternshipDAO dao = new InternshipDAO(new DatabaseHelper(this).getReadableDatabase());
        Internship internship = dao.getInternshipById(internshipId);

        if (internship != null) {
            bindInternshipData(internship);

            // Xử lý nút APPLY
            btnApply.setOnClickListener(v -> {
                Intent intent = new Intent(InternshipDetailActivity.this, ApplyActivity.class);
                intent.putExtra("INTERNSHIP_ID", internshipId);
                startActivity(intent);
            });

            // Xử lý nút bản đồ
            btnMap.setOnClickListener(v -> {
                Intent intent = new Intent(InternshipDetailActivity.this, com.fptu.prm391.projectprm.activity.common.MapActivity.class);
                intent.putExtra("LATITUDE", internship.getLatitude());
                intent.putExtra("LONGITUDE", internship.getLongitude());
                intent.putExtra("LOCATION_NAME", internship.getLocation());
                startActivity(intent);
            });

        } else {
            Toast.makeText(this, "Internship not found", Toast.LENGTH_SHORT).show();
            finish();
        }
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
}
