package com.fptu.prm391.projectprm.activity.recruiter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.model.Internship;
import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

public class RecruiterInternshipDetailActivity extends AppCompatActivity {

    private ImageView ivCover;
    private TextView tvTitle, tvCompany, tvLocation, tvDuration, tvStipend, tvDeadline, tvDescription, tvRequirements;
    private ImageButton btnMap;
    private MaterialButton btnStatus;
    private Internship internship;
    private InternshipDAO internshipDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_internship_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Ánh xạ view
        ivCover = findViewById(R.id.ivCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvCompany = findViewById(R.id.tvCompany);
        tvLocation = findViewById(R.id.tvLocation);
        tvDuration = findViewById(R.id.tvDuration);
        tvStipend = findViewById(R.id.tvStipend);
        tvDeadline = findViewById(R.id.tvDeadline);
        tvDescription = findViewById(R.id.tvDescription);
        tvRequirements = findViewById(R.id.tvRequirements);
        btnMap = findViewById(R.id.btnMap);
        btnStatus = findViewById(R.id.btnClose);

        // Lấy internship_id từ intent
        int internshipId = getIntent().getIntExtra("internship_id", -1);

        if (internshipId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin internship.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        internshipDAO = new InternshipDAO(new DatabaseHelper(this).getWritableDatabase());
        internship = internshipDAO.getInternshipById(internshipId);

        if (internship == null) {
            Toast.makeText(this, "Không tìm thấy internship.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindInternshipData();

        btnMap.setOnClickListener(v -> openLocationOnMap());
        setupStatusButton();
    }

    private void setupStatusButton() {
        // Kiểm tra trạng thái hiện tại, cập nhật nút và sự kiện
        if ("close".equalsIgnoreCase(internship.getStatus())) {
            btnStatus.setText("Mở nhận hồ sơ");
            btnStatus.setEnabled(true);
            btnStatus.setOnClickListener(v -> reopenInternship());
        } else {
            btnStatus.setText("Dừng nhận hồ sơ");
            btnStatus.setEnabled(true);
            btnStatus.setOnClickListener(v -> closeInternship());
        }
    }

    private void closeInternship() {
        int rows = internshipDAO.updateStatus(internship.getId(), "close");
        if (rows > 0) {
            internship.setStatus("close");
            Toast.makeText(this, "Đã dừng nhận hồ sơ!", Toast.LENGTH_SHORT).show();
            btnStatus.setText("Mở nhận hồ sơ");
            btnStatus.setOnClickListener(v -> reopenInternship());
        } else {
            Toast.makeText(this, "Có lỗi khi cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
        }
    }

    private void reopenInternship() {
        int rows = internshipDAO.updateStatus(internship.getId(), "open");
        if (rows > 0) {
            internship.setStatus("open");
            Toast.makeText(this, "Đã mở lại nhận hồ sơ!", Toast.LENGTH_SHORT).show();
            btnStatus.setText("Dừng nhận hồ sơ");
            btnStatus.setOnClickListener(v -> closeInternship());
        } else {
            Toast.makeText(this, "Có lỗi khi cập nhật trạng thái!", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindInternshipData() {
        ivCover.setImageResource(R.drawable.img_internship_cover);
        tvTitle.setText(internship.getTitle());
        tvCompany.setText(internship.getCompany());
        tvLocation.setText(internship.getLocation());
        tvDuration.setText(internship.getDuration());
        tvStipend.setText(
                (internship.getStipend() == null || internship.getStipend().isEmpty()) ? "Không lương" : internship.getStipend()
        );
        tvDeadline.setText(internship.getDeadline());
        tvDescription.setText(internship.getDescription());
        tvRequirements.setText(internship.getRequirements());
    }

    private void openLocationOnMap() {
        String location = internship.getLocation();
        double latitude = internship.getLatitude();
        double longitude = internship.getLongitude();
        Intent intent;
        if (latitude != 0 && longitude != 0) {
            String uri = String.format("geo:%f,%f?q=%f,%f(%s)", latitude, longitude, latitude, longitude, location);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        } else if (location != null && !location.isEmpty()) {
            String uri = "geo:0,0?q=" + Uri.encode(location);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        } else {
            Toast.makeText(this, "Không có thông tin địa điểm.", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
}