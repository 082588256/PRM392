package com.fptu.prm391.projectprm.activity.student;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InterviewDAO;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.Interview;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

public class ApplyActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1001;
    private static final int MAX_FILE_SIZE_MB = 5;

    private Uri selectedCVUri = null;
    private String selectedCVFileName = null;

    private LinearLayout layoutUploadCV;
    private TextView tvUploadCV;
    private EditText edtCoverLetter;
    private Button btnApply;

    private ActivityResultLauncher<String[]> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_apply);

        int internshipId = getIntent().getIntExtra("INTERNSHIP_ID", -1);
        if (internshipId == -1) {
            Toast.makeText(this, "Thiếu thông tin thực tập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        layoutUploadCV = findViewById(R.id.layoutUploadCV);
        tvUploadCV = findViewById(R.id.tvUploadCV);
        edtCoverLetter = findViewById(R.id.edtCoverLetter);
        btnApply = findViewById(R.id.btnApply);

        // Init file picker
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null && isValidCV(uri)) {
                        selectedCVUri = uri;
                        selectedCVFileName = getFileName(uri);
                        tvUploadCV.setText("Đã chọn: " + selectedCVFileName);
                    } else {
                        Toast.makeText(this, "File không hợp lệ (.pdf, .doc, .docx, <=5MB)", Toast.LENGTH_SHORT).show();
                    }
                });

        layoutUploadCV.setOnClickListener(v -> requestStoragePermission());

        btnApply.setOnClickListener(v -> {
            if (selectedCVUri == null) {
                Toast.makeText(this, "Vui lòng tải lên file CV", Toast.LENGTH_SHORT).show();
                return;
            }

            String coverLetter = edtCoverLetter.getText().toString().trim();
            if (coverLetter.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập thư giới thiệu", Toast.LENGTH_SHORT).show();
                return;
            }

            int studentId = SharedPrefManager.getInstance(this).getUser().getId();

            SQLiteDatabase db = new DatabaseHelper(this).getWritableDatabase();
            ApplicationDAO dao = new ApplicationDAO(db);

            Application app = new Application();
            app.setStudentId(studentId);
            app.setInternshipId(internshipId);
            app.setResumeFile(selectedCVUri.toString());
            app.setCoverLetter(coverLetter);
            app.setNote("");

            long result = dao.insertApplication(app);
            if (result != -1) {
                Interview interview = new Interview();
                interview.setApplicationId((int) result);
                interview.setScheduledTime(null);
                interview.setStatus("Proposed");
                interview.setNotes("Chưa có");

                InterviewDAO interviewDAO = new InterviewDAO(db);
                long interviewResult = interviewDAO.insertInterview(interview);

                if (interviewResult == -1) {
                    Toast.makeText(this, "Lỗi khi tạo lịch phỏng vấn!", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(this, "Nộp đơn thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi nộp đơn!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VIDEO,
                                Manifest.permission.READ_MEDIA_AUDIO
                        },
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            // Android <= 12
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openFilePicker();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (granted) {
                openFilePicker();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền để chọn CV!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openFilePicker() {
        filePickerLauncher.launch(new String[]{
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        });
    }

    private boolean isValidCV(Uri uri) {
        String fileName = getFileName(uri);
        if (fileName == null) return false;
        boolean validType = fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx");
        if (!validType) return false;
        return getFileSize(uri) <= MAX_FILE_SIZE_MB * 1024 * 1024;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private long getFileSize(Uri uri) {
        long size = 0;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex);
                    }
                }
            }
        }
        return size;
    }
}
