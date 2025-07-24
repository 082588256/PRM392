package com.fptu.prm391.projectprm.activity.student;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fptu.prm391.projectprm.activity.common.ChatActivity;
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

    private static final String TAG = "InternshipDetailActivity";

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
        Log.d(TAG, "Received internshipId: " + internshipId);
        if (internshipId == -1) {
            Toast.makeText(this, "Invalid Internship ID", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid Internship ID received in Intent");
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
        Log.d(TAG, "Current studentId: " + studentId);

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
                        Log.d(TAG, "ApplyActivity returned RESULT_OK. Update apply button.");
                        updateApplyButton();
                    }
                }
        );

        // Lần đầu load dữ liệu và bind UI
        loadAndBindInternship();

        // Xử lý nút bản đồ
        btnMap.setOnClickListener(v -> {
            if (internship == null) {
                Log.e(TAG, "Internship is null when clicking btnMap");
                return;
            }
            Log.d(TAG, "Map button clicked. Lat: " + internship.getLatitude() + ", Lng: " + internship.getLongitude());
            Intent intent = new Intent(InternshipDetailActivity.this,
                    com.fptu.prm391.projectprm.activity.common.MapActivity.class);
            intent.putExtra("LATITUDE", internship.getLatitude());
            intent.putExtra("LONGITUDE", internship.getLongitude());
            intent.putExtra("LOCATION_NAME", internship.getLocation());
            Log.d(TAG, "Sending intent to MapActivity with LATITUDE: " + internship.getLatitude()
                    + ", LONGITUDE: " + internship.getLongitude()
                    + ", LOCATION_NAME: " + internship.getLocation());
            startActivity(intent);
        });

        // Xử lý nút chat
        fabMessage.setOnClickListener(v -> {
            if (internship == null) {
                Log.e(TAG, "Internship is null when clicking fabMessage");
                return;
            }
            int recruiterId = internship.getRecruiterId();
            String recruiterName = internship.getCompany();
            Log.d(TAG, "Chat button clicked. RecruiterId: " + recruiterId + ", RecruiterName: " + recruiterName);

            Intent intent = new Intent(InternshipDetailActivity.this, ChatActivity.class);
            intent.putExtra("SENDER_ID", studentId);
            intent.putExtra("RECEIVER_ID", recruiterId);
            intent.putExtra("RECEIVER_NAME", recruiterName);
            startActivity(intent);
        });
    }

    private void loadAndBindInternship() {
        internship = internshipDAO.getInternshipById(internshipId);
        Log.d(TAG, "Loaded internship: " + (internship != null ? internship.toString() : "null"));
        if (internship == null) {
            Toast.makeText(this, "Internship not found", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No internship found for id: " + internshipId);
            finish();
            return;
        }
        bindInternshipData(internship);
        updateApplyButton();
    }

    // Cập nhật trạng thái nút Apply dựa theo internship status và application
    private void updateApplyButton() {
        if (internship == null) {
            Log.e(TAG, "updateApplyButton: internship is null");
            return;
        }
        // Refresh lại status phòng trường hợp recruiter vừa đóng/mở internship
        internship = internshipDAO.getInternshipById(internshipId);

        Log.d(TAG, "updateApplyButton: internship status: " + internship.getStatus());
        if ("close".equalsIgnoreCase(internship.getStatus())) {
            btnApply.setText("Đã dừng nhận hồ sơ");
            btnApply.setEnabled(false);
            btnApply.setOnClickListener(null);
        } else {
            boolean hasApplied = applicationDAO.hasApplied(studentId, internshipId);
            Log.d(TAG, "updateApplyButton: hasApplied = " + hasApplied);
            if (hasApplied) {
                btnApply.setText("Already Applied");
                btnApply.setEnabled(false);
                btnApply.setOnClickListener(null);
            } else {
                btnApply.setText("Apply Now");
                btnApply.setEnabled(true);
                btnApply.setOnClickListener(v -> {
                    Log.d(TAG, "Apply button clicked. Launching ApplyActivity.");
                    Intent intent = new Intent(InternshipDetailActivity.this, ApplyActivity.class);
                    intent.putExtra("INTERNSHIP_ID", internshipId);
                    applyLauncher.launch(intent);
                });
            }
        }
    }

    private void bindInternshipData(Internship internship) {
        Log.d(TAG, "Binding internship data to UI: " + internship.toString());
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
        Log.d(TAG, "onResume called");
        loadAndBindInternship();
    }
}