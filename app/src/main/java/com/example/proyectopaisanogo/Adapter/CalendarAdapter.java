package com.example.proyectopaisanogo.Adapter;// CalendarAdapter.java

import static java.sql.DriverManager.println;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<String> daysOfMonth;
    private final ArrayList<String> currentMonth;
    private final OnItemListener onItemListener;
    private final FirebaseFirestore db;
    private final String userID;
    private Map<String, String> citasMap; // Para almacenar días con citas del cliente activo

    public CalendarAdapter(ArrayList<String> daysOfMonth, ArrayList<String> currentMonth,  OnItemListener onItemListener, String userID) {
        this.daysOfMonth = daysOfMonth;
        this.currentMonth = currentMonth;
        this.onItemListener = onItemListener;
        this.db = FirebaseFirestore.getInstance();
        this.userID = userID;
        this.citasMap = new HashMap<>();

    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position);
        holder.dayOfMonth.setText(day);
        citasMap = new HashMap<>();
        fetchCitas();
        // Marcar el día si hay una cita para el cliente activo
        if (citasMap.containsKey(day)) {
            holder.dayOfMonth.setBackgroundResource(R.drawable.circle_background);
        } else {
            holder.dayOfMonth.setBackgroundResource(android.R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    private void fetchCitas() {
        // Primero, determinar el rol del usuario
        db.collection("registroEmpresa").document(userID).get()
                .addOnCompleteListener(taskEmpresa -> {
                    if (taskEmpresa.isSuccessful() && taskEmpresa.getResult().exists()) {
                        // Si existe en registroEmpresa, es una empresa
                        String role = "empresa";
                        fetchCitasByRole(role);
                    } else {
                        // Si no existe en registroEmpresa, verificar en registroCliente
                        db.collection("registroCliente").document(userID).get()
                                .addOnCompleteListener(taskCliente -> {
                                    if (taskCliente.isSuccessful() && taskCliente.getResult().exists()) {
                                        // Si existe en registroCliente, es un cliente
                                        String role = "cliente";
                                        fetchCitasByRole(role);
                                    } else {
                                        // Manejar el caso donde el usuario no se encuentra en ninguna colección
                                        Log.e("fetchCitas", "Usuario no encontrado en registroEmpresa ni en registroCliente");
                                    }
                                });
                    }
                });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCitasByRole(String role) {
        if ("empresa".equals(role)) {
            db.collection("Citas")
                    .whereEqualTo("empresaId", userID) // Filtrar por el ID de la empresa
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timestamp timestamp = document.getTimestamp("fecha");
                                if (timestamp != null) {
                                    // Convertir Timestamp a una cadena de fecha formateada
                                    SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
                                    String day = sdf.format(timestamp.toDate());
                                    citasMap.put(day, document.getId()); // Almacenar el ID de la cita
                                }
                            }
                            notifyDataSetChanged(); // Refrescar el adaptador después de cargar todas las citas
                        }
                    });
        } else if ("cliente".equals(role)) {
            db.collection("Citas")
                    .whereEqualTo("userID", userID) // Filtrar por el ID del cliente
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Timestamp timestamp = document.getTimestamp("fecha");
                                if (timestamp != null) {
                                    // Convertir Timestamp a una cadena de fecha formateada
                                    SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
                                    SimpleDateFormat sdf2 = new SimpleDateFormat("M", Locale.getDefault());
                                    String day = sdf.format(timestamp.toDate());
                                    String month = sdf2.format(timestamp.toDate());
                                    if(currentMonth.contains(month)){
                                        citasMap.put(day, document.getId()); // Almacenar el ID de la cita
                                    }

                                }
                            }
                            notifyDataSetChanged(); // Refrescar el adaptador después de cargar todas las citas
                        }
                    });
        }
    }


    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }
}
