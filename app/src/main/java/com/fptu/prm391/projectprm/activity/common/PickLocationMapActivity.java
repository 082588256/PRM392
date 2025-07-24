package com.fptu.prm391.projectprm.activity.common;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fptu.prm391.projectprm.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PickLocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1003;
    private GoogleMap mMap;
    private LatLng selectedLatLng = null;
    private Marker marker;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Toast.makeText(this, "Chạm vào bản đồ để chọn vị trí, nhấn BACK để xác nhận", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(latLng -> {
            if (marker != null) marker.remove();
            marker = mMap.addMarker(new com.google.android.gms.maps.model.MarkerOptions()
                    .position(latLng)
                    .title("Vị trí đã chọn"));
            selectedLatLng = latLng;
        });

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                            } else {
                                // Không lấy được location, fallback về Việt Nam
                                LatLng defaultLatLng = new LatLng(21.028511, 105.804817);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 6f));
                            }
                        })
                        .addOnFailureListener(e -> {
                            LatLng defaultLatLng = new LatLng(21.028511, 105.804817);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 6f));
                        });
            } else {
                // Xin quyền nếu chưa có
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
                // Zoom tạm về Việt Nam
                LatLng defaultLatLng = new LatLng(21.028511, 105.804817);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 6f));
            }
        } catch (SecurityException e) {
            // Bị lỗi quyền, fallback về Việt Nam
            LatLng defaultLatLng = new LatLng(21.028511, 105.804817);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 6f));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Được cấp quyền thì thử lấy lại location
                if (mMap != null && fusedLocationProviderClient != null) {
                    try {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationProviderClient.getLastLocation()
                                    .addOnSuccessListener(this, location -> {
                                        if (location != null) {
                                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                                        }
                                    });
                        }
                    } catch (SecurityException e) {
                        Toast.makeText(this, "Không đủ quyền truy cập vị trí!", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "Không thể lấy vị trí hiện tại, bản đồ sẽ tự động zoom về Việt Nam.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (selectedLatLng != null) {
            Intent data = new Intent();
            data.putExtra("LATITUDE", selectedLatLng.latitude);
            data.putExtra("LONGITUDE", selectedLatLng.longitude);
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(this, "Vui lòng chọn vị trí trên bản đồ!", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
}