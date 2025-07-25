package com.fptu.prm391.projectprm.activity.common;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitude;
    private double longitude;
    private String locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Đoạn kiểm tra API Key runtime
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String apiKey = ai.metaData.getString("com.google.android.geo.API_KEY");
            Log.d("GoogleMap", "API Key at runtime: " + apiKey);
        } catch (Exception e) {
            Log.e("GoogleMap", "Error getting API Key: " + e.getMessage());
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        latitude = getIntent().getDoubleExtra("LATITUDE", 0.0);
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0.0);
        locationName = getIntent().getStringExtra("LOCATION_NAME");

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapActivity", "onMapReady called. Lat: " + latitude + ", Lng: " + longitude + ", LocationName: " + locationName);
        LatLng internshipLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(internshipLocation)
                .title(locationName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(internshipLocation, 15f));
    }
}
