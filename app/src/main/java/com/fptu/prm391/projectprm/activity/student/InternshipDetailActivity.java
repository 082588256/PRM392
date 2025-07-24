package com.fptu.prm391.projectprm.activity.student;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.model.Internship;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

public class InternshipDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvCompany, tvLocation, tvDuration,
            tvStipend, tvDeadline, tvDescription, tvRequirements;

    private MaterialButton btnApply;
    private ImageButton btnMap;
    private FloatingActionButton fabMessage;

    // Field để lưu lại các biến quan trọng cho onResume
    private int internshipId;
    private int studentId;
    private Internship internship;
    private ApplicationDAO applicationDAO;
    private InternshipDAO internshipDAO;
    private DatabaseHelper dbHelper;

    // ActivityResultLauncher để nhận kết quả từ ApplyActivity
    private ActivityResultLauncher<Intent> applyLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        internshipId = getIntent().getIntExtra("INTERNSHIP_ID", -1);
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
        btnMap = findViewById(R.id.btnMap);
        fabMessage = findViewById(R.id.fabMessage);

        // Lấy studentId từ SharedPrefManager
        studentId = SharedPrefManager.getInstance(this).getUser().getId();

        // Database, giữ reference để dùng trong onResume
        dbHelper = new DatabaseHelper(this);
        internshipDAO = new InternshipDAO(dbHelper.getReadableDatabase());
        applicationDAO = new ApplicationDAO(dbHelper.getReadableDatabase());

        // Đăng ký ActivityResultLauncher để nhận kết quả từ ApplyActivity
        applyLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Sau khi Apply xong, cập nhật trạng thái nút ngay lập tức
                        updateApplyButton();
                    }
                }
        );

        // Lần đầu load dữ liệu và bind UI
        loadAndBindInternship();

        // Xử lý nút bản đồ
        btnMap.setOnClickListener(v -> {
            if (internship == null) return;
            Intent intent = new Intent(InternshipDetailActivity.this,
                    com.fptu.prm391.projectprm.activity.common.MapActivity.class);
            intent.putExtra("LATITUDE", internship.getLatitude());
            intent.putExtra("LONGITUDE", internship.getLongitude());
            intent.putExtra("LOCATION_NAME", internship.getLocation());
            startActivity(intent);
        });

        // Xử lý nút chat
        fabMessage.setOnClickListener(v -> {
            if (internship == null) return;
            int recruiterId = internship.getRecruiterId();
            String recruiterName = internship.getCompany();

            Intent intent = new Intent(InternshipDetailActivity.this, ChatActivity.class);
            intent.putExtra("SENDER_ID", studentId);
            intent.putExtra("RECEIVER_ID", recruiterId);
            intent.putExtra("RECEIVER_NAME", recruiterName);
            startActivity(intent);
        });
    }

    private void loadAndBindInternship() {
        internship = internshipDAO.getInternshipById(internshipId);
        if (internship == null) {
            Toast.makeText(this, "Internship not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        bindInternshipData(internship);
        updateApplyButton();
    }

    // Cập nhật trạng thái nút Apply dựa theo internship status và application
    private void updateApplyButton() {
        if (internship == null) return;
        // Refresh lại status phòng trường hợp recruiter vừa đóng/mở internship
        internship = internshipDAO.getInternshipById(internshipId);

        if ("close".equalsIgnoreCase(internship.getStatus())) {
            btnApply.setText("Đã dừng nhận hồ sơ");
            btnApply.setEnabled(false);
            btnApply.setOnClickListener(null);
        } else {
            boolean hasApplied = applicationDAO.hasApplied(studentId, internshipId);
            if (hasApplied) {
                btnApply.setText("Already Applied");
                btnApply.setEnabled(false);
                btnApply.setOnClickListener(null);
            } else {
                btnApply.setText("Apply Now");
                btnApply.setEnabled(true);
                btnApply.setOnClickListener(v -> {
                    Intent intent = new Intent(InternshipDetailActivity.this, ApplyActivity.class);
                    intent.putExtra("INTERNSHIP_ID", internshipId);
                    applyLauncher.launch(intent);
                });
            }
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

    // Luôn cập nhật trạng thái nút mỗi lần quay về màn hình này
    @Override
    protected void onResume() {
        super.onResume();
        loadAndBindInternship();
    }
}