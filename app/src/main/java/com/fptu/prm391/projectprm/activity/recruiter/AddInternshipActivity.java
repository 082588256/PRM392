package com.fptu.prm391.projectprm.activity.recruiter;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fptu.prm391.projectprm.R;
import com.fptu.prm391.projectprm.db.DatabaseHelper;
import com.fptu.prm391.projectprm.db.InternshipDAO;
import com.fptu.prm391.projectprm.model.Internship;
import com.fptu.prm391.projectprm.util.SharedPrefManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddInternshipActivity extends AppCompatActivity {
    private EditText etTitle, etCompany, etLocation, etDuration, etField, etDescription, etRequirements, etStipend, etDeadline;
    private Spinner spinnerCurrency;
    private Button btnPickLocation, btnGetCurrentLocation, btnSubmit;
    private TextView tvLatLng;
    private InternshipDAO internshipDAO;
    private int recruiterId;

    private double selectedLatitude = 0;
    private double selectedLongitude = 0;

    private ActivityResultLauncher<Intent> pickLocationLauncher;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_LOCATION_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_internship_activity);

        etTitle = findViewById(R.id.etTitle);
        etCompany = findViewById(R.id.etCompany);
        etLocation = findViewById(R.id.etLocation);
        etDuration = findViewById(R.id.etDuration);
        etField = findViewById(R.id.etField);
        etDescription = findViewById(R.id.etDescription);
        etRequirements = findViewById(R.id.etRequirements);
        etStipend = findViewById(R.id.etStipend);
        etDeadline = findViewById(R.id.etDeadline);

        spinnerCurrency = findViewById(R.id.spinnerCurrency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.money, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);

        btnPickLocation = findViewById(R.id.btnPickLocation);
        btnGetCurrentLocation = findViewById(R.id.btnGetCurrentLocation);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvLatLng = findViewById(R.id.tvLatLng);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        internshipDAO = new InternshipDAO(dbHelper.getWritableDatabase());
        recruiterId = SharedPrefManager.getInstance(this).getUser().getId();

        etDeadline.setFocusable(false);
        etDeadline.setOnClickListener(v -> showDatePicker());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        pickLocationLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedLatitude = result.getData().getDoubleExtra("LATITUDE", 0);
                        selectedLongitude = result.getData().getDoubleExtra("LONGITUDE", 0);
                        tvLatLng.setText("Lat: " + selectedLatitude + "\nLng: " + selectedLongitude);                    }
                }
        );

        btnPickLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.fptu.prm391.projectprm.activity.common.PickLocationMapActivity.class);
            pickLocationLauncher.launch(intent);
        });

        btnGetCurrentLocation.setOnClickListener(v -> getCurrentLocation());

        btnSubmit.setOnClickListener(v -> submitInternship());
    }

    private void getCurrentLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                return;
            }

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            selectedLatitude = location.getLatitude();
                            selectedLongitude = location.getLongitude();
                            tvLatLng.setText("Lat: " + selectedLatitude + "\nLng: " + selectedLongitude);
                        } else {
                            requestNewLocationData();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AddInternshipActivity.this, "Không thể lấy vị trí hiện tại!", Toast.LENGTH_SHORT).show();
                    });
        } catch (SecurityException e) {
            Toast.makeText(this, "Không đủ quyền truy cập vị trí!", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestNewLocationData() {
        try {
            LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(1000)
                    .setNumUpdates(1);

            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        Toast.makeText(AddInternshipActivity.this, "Không thể lấy vị trí hiện tại!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        selectedLatitude = location.getLatitude();
                        selectedLongitude = location.getLongitude();
                        tvLatLng.setText("Lat: " + selectedLatitude + ", Lng: " + selectedLongitude);
                    } else {
                        Toast.makeText(AddInternshipActivity.this, "Không thể lấy vị trí hiện tại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, Looper.getMainLooper());
        } catch (SecurityException e) {
            Toast.makeText(this, "Không đủ quyền truy cập vị trí!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    etDeadline.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private boolean validateInput() {
        if (etTitle.getText().toString().trim().isEmpty()) {
            etTitle.setError("Vui lòng nhập tiêu đề");
            etTitle.requestFocus();
            return false;
        }
        if (etCompany.getText().toString().trim().isEmpty()) {
            etCompany.setError("Vui lòng nhập tên công ty");
            etCompany.requestFocus();
            return false;
        }
        if (etLocation.getText().toString().trim().isEmpty()) {
            etLocation.setError("Vui lòng nhập địa điểm");
            etLocation.requestFocus();
            return false;
        }
        if (etDuration.getText().toString().trim().isEmpty()) {
            etDuration.setError("Vui lòng nhập thời hạn thực tập");
            etDuration.requestFocus();
            return false;
        }
        if (etField.getText().toString().trim().isEmpty()) {
            etField.setError("Vui lòng nhập lĩnh vực");
            etField.requestFocus();
            return false;
        }
        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("Vui lòng nhập mô tả");
            etDescription.requestFocus();
            return false;
        }
        if (etRequirements.getText().toString().trim().isEmpty()) {
            etRequirements.setError("Vui lòng nhập yêu cầu");
            etRequirements.requestFocus();
            return false;
        }
        if (etDeadline.getText().toString().trim().isEmpty()) {
            etDeadline.setError("Vui lòng nhập hạn ứng tuyển");
            etDeadline.requestFocus();
            return false;
        }
        if (!validateDeadline()) {
            return false;
        }
        if (selectedLatitude == 0 || selectedLongitude == 0) {
            Toast.makeText(this, "Vui lòng chọn vị trí trên bản đồ hoặc lấy vị trí hiện tại!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etStipend.getText().toString().trim().isEmpty()) {
            etStipend.setError("Vui lòng nhập lương/phụ cấp");
            etStipend.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validateDeadline() {
        String deadline = etDeadline.getText().toString().trim();
        if (deadline.isEmpty()) {
            etDeadline.setError("Vui lòng nhập hạn ứng tuyển");
            etDeadline.requestFocus();
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);
        try {
            Date date = sdf.parse(deadline);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            if (date.before(today.getTime())) {
                etDeadline.setError("Hạn ứng tuyển phải là ngày hôm nay hoặc tương lai");
                etDeadline.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            etDeadline.setError("Định dạng ngày không hợp lệ (yyyy-MM-dd)");
            etDeadline.requestFocus();
            return false;
        }
        return true;
    }

    private void submitInternship() {
        if (!validateInput()) return;
        String title = etTitle.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String field = etField.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String requirements = etRequirements.getText().toString().trim();
        String stipend = etStipend.getText().toString().trim();
        String currency = spinnerCurrency.getSelectedItem().toString();
        String stipendRaw = stipend + "|" + currency; // Lưu vào DB ghép số và đơn vị tiền tệ
        String deadline = etDeadline.getText().toString().trim();

        Internship internship = new Internship(title, company, location, duration, field, description, requirements, stipendRaw, deadline, recruiterId, selectedLatitude, selectedLongitude, "open");

        long result = internshipDAO.insertInternship(internship);

        if (result != -1) {
            Toast.makeText(this, "Đăng internship thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Có lỗi xảy ra!", Toast.LENGTH_SHORT).show();
        }
    }
}