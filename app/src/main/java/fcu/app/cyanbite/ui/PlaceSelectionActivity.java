package fcu.app.cyanbite.ui;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import fcu.app.cyanbite.R;

public class PlaceSelectionActivity extends AppCompatActivity {

    private MapView mapView;
    private GoogleMap googleMap;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Marker marker;
    private String initialAddress, result, city, district, road, number;
    private Button btnReturn, btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_place_selection);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        initialAddress = getIntent().getStringExtra("initial_address");
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap map) {
                googleMap = map;
                LatLng initLatLng = new LatLng(24.179261330095628, 120.64925653126724);
                if (initialAddress != null) {
                    try {
                        Geocoder geocoder = new Geocoder(PlaceSelectionActivity.this, new Locale("zh", "TW"));
                        List<Address> addresses = null;
                        addresses = geocoder.getFromLocationName(initialAddress, 1);
                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            double lat = address.getLatitude();
                            double lng = address.getLongitude();
                            initLatLng = new LatLng(lat, lng);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initLatLng, 16));
                marker = googleMap.addMarker(new MarkerOptions().position(initLatLng).title("選擇的位置"));
                googleMap.setOnMapClickListener(latLng -> {
                    try {
                        if (marker != null) {
                            marker.remove();
                        }
                        marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("選擇的位置"));

                        Geocoder geocoder = new Geocoder(PlaceSelectionActivity.this, new Locale("zh", "TW"));
                        List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                        if (addresses != null && !addresses.isEmpty()) {
                            Address address = addresses.get(0);

                            city = address.getAdminArea();                 // 市
                            district = address.getSubAdminArea();          // 區
                            road = address.getThoroughfare();              // 路
                            number = address.getSubThoroughfare();         // 號碼

                            result = "";
                            if (city != null) result += city;
                            if (district != null) result += district;
                            if (road != null) result += road;
                            if (number != null) result += number;
                        } else {
                            result = "";
                            city = null;
                            district = null;
                            road = null;
                            number = null;
                        }
                        Toast.makeText(PlaceSelectionActivity.this, result, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });

        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(view -> {
            if (result.isEmpty()) {
                Toast.makeText(PlaceSelectionActivity.this, "請選擇地址", Toast.LENGTH_LONG).show();
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("address", result);
            resultIntent.putExtra("city", city);
            resultIntent.putExtra("district", district);
//            resultIntent.putExtra("road", road);
//            resultIntent.putExtra("number", number);

            setResult(RESULT_OK, resultIntent);
            finish();
        });

        btnReturn = findViewById(R.id.btn_return);
        btnReturn.setOnClickListener(view -> {
            finish();
        });
    }

    // 以下為 MapView 生命週期方法
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
}