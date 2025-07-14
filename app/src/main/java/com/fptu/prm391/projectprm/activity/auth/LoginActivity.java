package com.fptu.prm391.projectprm.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.UserDAO;
import com.fptu.prm391.projectprm.model.User;
import com.fptu.prm391.projectprm.util.SharedPrefManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private UserDAO userDAO;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kiểm tra đăng nhập trước đó
        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            navigateToStudentActivity();
            return;
        }

        // Khởi tạo database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper.getWritableDatabase());

        // Ánh xạ view
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvSignup = findViewById(R.id.tvSignup);

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());

        // Chuyển sang màn hình đăng ký
        tvSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        // Kiểm tra thông tin đăng nhập
        User user = userDAO.getUserByEmail(email);

        if (user != null && user.getPassword().equals(password)) {
            // Lưu thông tin đăng nhập
            SharedPrefManager.getInstance(this).userLogin(user);

            // Chuyển đến màn hình chính
            navigateToStudentActivity();
        } else {
            Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void navigateToStudentActivity() {
        User user = SharedPrefManager.getInstance(this).getUser();
        if (user == null) {
            Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("student".equalsIgnoreCase(user.getRole())) {
            Intent intent = new Intent(this, com.fptu.prm391.projectprm.activity.student.InternshipListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if ("recruiter".equalsIgnoreCase(user.getRole())) {
            Intent intent = new Intent(this, com.fptu.prm391.projectprm.activity.recruiter.PostedInternshipsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Không xác định được vai trò người dùng", Toast.LENGTH_SHORT).show();
        }
    }

}