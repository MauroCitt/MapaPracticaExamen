package com.example.mapapracticaexamen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ValueEventListener, ChildEventListener, View.OnClickListener {

    private static final String CHANNEL_ID = "1";
    private static final Object NOTIFICATION_ID = 1;
    private Button btnAdd;
    private EditText latitud;
    private EditText longitud;
    private List<Coordenadas> listaCoordenadas = new ArrayList<>();
    ArrayAdapter<String> adapter;
    private MapaAdapter mapaAdapter;
    private RecyclerView recyclerViewMapa;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseReference dbMapa = FirebaseDatabase.getInstance().getReference().child("mapa");

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        latitud = findViewById(R.id.latitud);
        longitud = findViewById(R.id.longitud);

        listaCoordenadas = new ArrayList<Coordenadas>();

        mapaAdapter = new MapaAdapter(this, dbMapa, listaCoordenadas);

        recyclerViewMapa = findViewById(R.id.recycler);

        recyclerViewMapa.setHasFixedSize(true);
        recyclerViewMapa.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMapa.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mapaAdapter = new MapaAdapter(this, dbMapa, listaCoordenadas);
        recyclerViewMapa.setAdapter(mapaAdapter);

        dbMapa.addChildEventListener(this);
        dbMapa.addValueEventListener(this);
    }

    @Override
    public void onClick(View v) {
        DatabaseReference dbMapa = null;

        if (R.id.btnAdd == v.getId()) {
            dbMapa = FirebaseDatabase.getInstance().getReference().child("mapa");
            Query query = dbMapa.orderByChild("latitud").startAt(latitud.getText().toString()).endAt(latitud.getText().toString() + "\uf8ff");
            DatabaseReference dbMapaFinal = dbMapa;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(MainActivity.this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show();
                    } else {
                        Coordenadas coordenadas = agregarCoordenadas();
                        if (coordenadas != null) {
                            String key = dbMapaFinal.push().getKey();
                            dbMapaFinal.child(key).setValue(coordenadas);
                        }
                        sendNotification("Coordenadas añadidas", "Coordenadas", latitud.getText().toString() + ", " + longitud.getText().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        listaCoordenadas.removeAll(listaCoordenadas);
        for (DataSnapshot element : snapshot.getChildren()) {
            Coordenadas coordenadas = new Coordenadas(
                    element.getKey(),
                    element.child("latitud").getValue().toString(),
                    element.child("longitud").getValue().toString()
            );
            listaCoordenadas.add(coordenadas);
            recyclerViewMapa.scrollToPosition(listaCoordenadas.size() - 1);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }

    public Coordenadas agregarCoordenadas() {
        Coordenadas coordeadas = new Coordenadas();
        if(latitud.getText().toString().equals("") || longitud.getText().toString().equals("")) {
            Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show();
            return null;
        }else if(!latitud.getText().toString().contains(".") || !longitud.getText().toString().contains(".")) {
            Toast.makeText(this, "Coordenadas inválidas", Toast.LENGTH_SHORT).show();
            return null;
        } else {
            String formatLat = "";
            String formatLong = "";
            if (latitud.getText().toString().contains(",") || longitud.getText().toString().contains(",")) {
                formatLat = latitud.getText().toString().replace(",", "_");
                formatLong = longitud.getText().toString().replace(",", "_");
            } else if(latitud.getText().toString().contains(".") || longitud.getText().toString().contains(".")) {
                formatLat = latitud.getText().toString().replace(".", "_");
                formatLong = longitud.getText().toString().replace(".", "_");
            }
            coordeadas.setLatitud(formatLat);
            coordeadas.setLongitud(formatLong);
        }
        Toast.makeText(this, "Coordenadas añadidas", Toast.LENGTH_SHORT).show();

        return coordeadas;
    }

    private void sendNotification(String textTitle, String textContent, String coordenadas) {
        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(textTitle)
                .setContentText(textContent + ": " + coordenadas) // Append matricula to the textContent
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify((Integer) NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}