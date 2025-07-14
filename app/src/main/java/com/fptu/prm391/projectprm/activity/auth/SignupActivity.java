package com.fptu.prm391.projectprm.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.UserDAO;
import com.fptu.prm391.projectprm.model.User;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etUniversity, etCompany;
    private TextInputLayout tilUniversity, tilCompany;
    private RadioGroup radioRole;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Khởi tạo database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        userDAO = new UserDAO(dbHelper.getWritableDatabase());

        // Ánh xạ view
        initViews();

        // Xử lý thay đổi role
        radioRole.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioStudent) {
                tilUniversity.setVisibility(TextView.VISIBLE);
                tilCompany.setVisibility(TextView.GONE);
            } else {
                tilUniversity.setVisibility(TextView.GONE);
                tilCompany.setVisibility(TextView.VISIBLE);
            }
        });

        // Xử lý đăng ký
        Button btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(v -> handleSignup());

        // Chuyển sang màn hình đăng nhập
        TextView tvLogin = findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void initViews() {
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etUniversity = findViewById(R.id.etUniversity);
        etCompany = findViewById(R.id.etCompany);
        tilUniversity = findViewById(R.id.tilUniversity);
        tilCompany = findViewById(R.id.tilCompany);
        radioRole = findViewById(R.id.radioRole);
    }

    private void handleSignup() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String university = etUniversity.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String role = ((RadioButton) findViewById(radioRole.getCheckedRadioButtonId())).getText().toString();

        if (!validateInput(name, email, password, role)) {
            return;
        }

        // Tạo user mới
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role.toLowerCase());

        if (role.equalsIgnoreCase("student")) {
            user.setUniversity(university);
        } else {
            user.setCompany(company);
        }

        // Thêm user vào database
        long result = userDAO.insertUser(user);

        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInput(String name, String email, String password, String role) {
        if (name.isEmpty()) {
            etName.setError("Vui lòng nhập họ tên");
            etName.requestFocus();
            return false;
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return false;
        }

        if (password.isEmpty() || password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        if (role.equalsIgnoreCase("student") && etUniversity.getText().toString().trim().isEmpty()) {
            etUniversity.setError("Vui lòng nhập trường học");
            etUniversity.requestFocus();
            return false;
        }

        if (role.equalsIgnoreCase("recruiter") && etCompany.getText().toString().trim().isEmpty()) {
            etCompany.setError("Vui lòng nhập công ty");
            etCompany.requestFocus();
            return false;
        }

        return true;
    }
}
