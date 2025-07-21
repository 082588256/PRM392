package com.fptu.prm391.projectprm.activity.student;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.ApplicationDAO;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InterviewDAO;
import com.fptu.prm391.projectprm.model.Application;
import com.fptu.prm391.projectprm.model.Interview;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

public class ApplyActivity extends AppCompatActivity {
    private static final int MAX_FILE_SIZE_MB = 5;

    private Uri selectedCVUri = null;
    private String selectedCVFileName = null;

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

        LinearLayout layoutUploadCV = findViewById(R.id.layoutUploadCV);
        TextView tvUploadCV = findViewById(R.id.tvUploadCV);
        EditText edtCoverLetter = findViewById(R.id.edtCoverLetter);
        Button btnApply = findViewById(R.id.btnApply);

        ActivityResultLauncher<String[]> filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null && isValidCV(uri)) {
                        selectedCVUri = uri;
                        selectedCVFileName = getFileName(uri);
                        tvUploadCV.setText("Đã chọn: " + selectedCVFileName);
                    }
                });

        layoutUploadCV.setOnClickListener(v -> {
            filePickerLauncher.launch(new String[]{"application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"});
        });

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
            Log.d("ApplyActivity", "Bắt đầu nộp đơn thực tập cho internshipId = " + internshipId);

            long result = dao.insertApplication(app);
            if (result != -1) {
                // ✅ Tạo lịch phỏng vấn trống sau khi nộp đơn thành công
                Interview interview = new Interview();
                interview.setApplicationId((int) result); // ID ứng tuyển vừa tạo
                interview.setScheduledTime(null); // Sẽ cập nhật sau
                interview.setStatus("Proposed");  // ✅ Trạng thái khởi tạo
                interview.setNotes("Chưa có");   // ✅ Ghi chú mặc định

                InterviewDAO interviewDAO = new InterviewDAO(db);
                long interviewResult = interviewDAO.insertInterview(interview);
                Log.d("ApplyActivity", "Kết quả insert Interview ID: " + interviewResult);

                if (interviewResult == -1) {
                    Toast.makeText(this, "Lỗi khi tạo lịch phỏng vấn!", Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(this, "Nộp đơn thành công!", Toast.LENGTH_SHORT).show();
                finish(); // Hoặc chuyển về màn lịch sử
            } else {
                Toast.makeText(this, "Lỗi khi nộp đơn!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidCV(Uri uri) {
        String fileName = getFileName(uri);
        if (fileName == null) return false;
        boolean validType = fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx");
        if (!validType) {
            Toast.makeText(this, "Định dạng file không hợp lệ!", Toast.LENGTH_SHORT).show();
            return false;
        }
        long size = getFileSize(uri);
        if (size > MAX_FILE_SIZE_MB * 1024 * 1024) {
            Toast.makeText(this, "File vượt quá 5MB!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
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
