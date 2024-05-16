// CalendarAdapter.java
package com.example.proyectopaisanogo.Adapter;

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
    private final OnItemListener onItemListener;
    private final FirebaseFirestore db;
    private final Map<String, Boolean> citasMap; // Para almacenar días con citas

    public CalendarAdapter(ArrayList<String> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
        this.db = FirebaseFirestore.getInstance();
        this.citasMap = new HashMap<>();
        fetchCitas();
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

        // Marca el día si hay una cita
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
        db.collection("Citas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("fecha");
                            if (timestamp != null) {
                                // Convert Timestamp to a formatted date string
                                SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
                                String day = sdf.format(timestamp.toDate());
                                citasMap.put(day, true);
                            }
                        }
                        notifyDataSetChanged(); // Refresca el adaptador después de cargar las citas
                    }
                });
    }

    public interface OnItemListener {
        void onItemClick(int position, String dayText);
    }

    public void clearCitasMap() {
        citasMap.clear();
    }
}
