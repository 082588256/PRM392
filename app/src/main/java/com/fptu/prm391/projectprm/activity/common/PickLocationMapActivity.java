package com.fptu.prm391.projectprm.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fptu.prm391.projectprm.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PickLocationMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLatLng = null;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

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

        // Mặc định zoom vào Việt Nam
        LatLng defaultLatLng = new LatLng(21.028511, 105.804817);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 6f));
    }

//    @Override
//    public void onBackPressed() {
//        if (selectedLatLng != null) {
//            Intent data = new Intent();
//            data.putExtra("LATITUDE", selectedLatLng.latitude);
//            data.putExtra("LONGITUDE", selectedLatLng.longitude);
//            setResult(RESULT_OK, data);
//            finish();
//        } else {
//            Toast.makeText(this, "Vui lòng chọn vị trí trên bản đồ!", Toast.LENGTH_SHORT).show();
//        }
//    }
}