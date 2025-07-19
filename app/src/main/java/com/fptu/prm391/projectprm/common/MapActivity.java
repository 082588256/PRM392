package com.fptu.prm391.projectprm.common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
        LatLng internshipLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(internshipLocation)
                .title(locationName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(internshipLocation, 15f));
    }
}
