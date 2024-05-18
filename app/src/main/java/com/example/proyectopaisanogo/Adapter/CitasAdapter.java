package com.example.proyectopaisanogo.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;

import com.example.proyectopaisanogo.Model.Cita;
import com.example.proyectopaisanogo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

    private List<Cita> citasList;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());

    public CitasAdapter() {
        this.citasList = new ArrayList<>();
    }

    @Override
    public CitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cita_card, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);
        holder.nombreUsuario.setText(cita.getNombreUsuario());
        holder.fecha.setText(formatter.format(cita.getFecha().toDate()));
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    public void updateData(List<Cita> nuevasCitas) {
        citasList.clear();
        citasList.addAll(nuevasCitas);
        notifyDataSetChanged();
    }

    static class CitaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreUsuario;
        TextView fecha;
        CardView cardView;

        CitaViewHolder(View itemView) {
            super(itemView);
            nombreUsuario = itemView.findViewById(R.id.nombreUsuario);
            fecha = itemView.findViewById(R.id.fechaCita);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
