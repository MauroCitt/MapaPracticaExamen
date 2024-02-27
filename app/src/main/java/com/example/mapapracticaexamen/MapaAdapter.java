package com.example.mapapracticaexamen;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MapaAdapter extends RecyclerView.Adapter<MapaAdapter.ViewHolder> {
    public List<Coordenadas> listaCoordenadas;
    private Context context;
    private DatabaseReference databaseReference;

    public MapaAdapter(Context context, DatabaseReference databaseReference, List<Coordenadas> listaCoordenadas) {
        this.context = context;
        this.databaseReference = databaseReference;
        this.listaCoordenadas = listaCoordenadas;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView latitudText;
        public TextView longitudText;

        @SuppressLint("WrongViewCast")
        public ViewHolder(View itemView) {
            super(itemView);
            latitudText = itemView.findViewById(R.id.latitudText);
            longitudText = itemView.findViewById(R.id.longitudText);
            itemView.setOnClickListener(this); // Per escoltar els clicks (no oblidar!)
        }

        @Override
        public void onClick(View v) {
            mostraPopupMenu(v, this);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coordenadas coordenada = listaCoordenadas.get(position);
        holder.latitudText.setText(coordenada.getLatitud());
        holder.longitudText.setText(coordenada.getLongitud());
        holder.itemView.setTag(holder);
    }

    @Override
    public int getItemCount() {
        return listaCoordenadas != null ? listaCoordenadas.size() : 0;
    }



    private void mostraPopupMenu(View view, ViewHolder holder) {
        PopupMenu popupMenu = new PopupMenu(this.context, view);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new Menu(holder));
        popupMenu.show();
    }

    public class Menu implements PopupMenu.OnMenuItemClickListener {
        ViewHolder holder;

        public Menu(ViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int pos = holder.getAdapterPosition();
            switch (menuItem.getItemId()) {
                case R.id.borrar:
                    System.out.println(pos);
                    databaseReference.child(listaCoordenadas.get(pos).getKey()).removeValue();
                    listaCoordenadas.remove(pos);
                    notifyDataSetChanged();
                    break;
                case R.id.abrirMapa:
                    Intent intent = new Intent(context, MapaActivity.class);
                    intent.putExtra("latitud", listaCoordenadas.get(pos).getLatitud());
                    intent.putExtra("longitud", listaCoordenadas.get(pos).getLongitud());
                    context.startActivity(intent);
                    break;
            }
            return false;
        }
    }
}