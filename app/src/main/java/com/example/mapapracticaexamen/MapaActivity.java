package com.example.mapapracticaexamen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final float DEFAULT_ZOOM = 15.0f; // Adjust the zoom level as needed
    private static final LatLng defaultLocation = new LatLng(41.12, 1.243988);
    private String apiKey;
    private GoogleMap mapa;
    String latitud;
    String longitud;
    LatLng coordenadas;
    private final int PERMISO_LOCALIZACION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        latitud = intent.getStringExtra("latitud");
        longitud = intent.getStringExtra("longitud");

        latitud = latitud.replace("_",".");
        longitud = longitud.replace("_",".");

        coordenadas = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
    }



    @Override
    public void onMapClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;

        CameraPosition camPos = new CameraPosition.Builder()
                .target(coordenadas)
                .zoom(10)
                .bearing(0)
                .tilt(70)
                .build();

        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
        mapa.animateCamera(camUpdate);

        mapa.setOnMapClickListener(this);
        mapa.setOnMapLongClickListener(this);
        habilitaLocalitzacio();
    }
    public void habilitaLocalitzacio() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            mapa.setMyLocationEnabled(true);
        } else {
            // Demanem a l'usuari que ens doni permís per localitzar-se a ell mateix
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISO_LOCALIZACION);
        }
    }

}