package com.example.proyectopaisanogo.Presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Date;

public class calendarioEmpresa extends Fragment  implements NavigationView.OnNavigationItemSelectedListener {

    private CalendarView calendarView;
    private TextView tvCitaInfo;
    private FirebaseFirestore db;
    private final Empresa empresa = new Empresa();

    public calendarioEmpresa() {
        // Requerido por Android
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_empresa, container, false);
        calendarView = view.findViewById(R.id.empresaCalendarView);
        tvCitaInfo = view.findViewById(R.id.tvCitaInfo);

        db = FirebaseFirestore.getInstance();
        setupCalendarListener();

        setupToolbar(view);
        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarCalendarioEmpresa);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Calendario Empresa");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Manejar el clic en el ícono de retroceso en la barra de herramientas
        if (menuItem.getItemId() == android.R.id.home) {
            // Navegar hacia atrás
            requireActivity().onBackPressed();
            return true; // Devolver true para indicar que el evento fue manejado
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void setupCalendarListener() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar date = Calendar.getInstance();
            date.set(year, month, dayOfMonth, 0, 0, 0);
            date.set(Calendar.MILLISECOND, 0);
            Date startDate = date.getTime(); // Midnight of the selected day
            date.add(Calendar.DATE, 1);
            Date endDate = date.getTime(); // Midnight of the next day

            loadCitasForDate(startDate, endDate);
        });
    }

    private void loadCitasForDate(Date startDate, Date endDate) {

        db.collection("Citas")
                .whereEqualTo("empresaId", empresa.getUserID()) // Cambiar por el ID real de la empresa
                .whereGreaterThanOrEqualTo("fecha", new Timestamp(startDate))
                .whereLessThan("fecha", new Timestamp(endDate))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder citasDetails = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Date citaDate = document.getTimestamp("fecha").toDate();
                            citasDetails.append("Cita ID: ").append(document.getId())
                                    .append(", Fecha: ").append(citaDate)
                                    .append("\n");
                            // Agrega más detalles según lo que esté almacenado en Firestore
                        }
                        tvCitaInfo.setText(citasDetails.toString());
                        tvCitaInfo.setVisibility(View.VISIBLE);
                    } else {
                        tvCitaInfo.setText(R.string.error_cargando_citas);
                        tvCitaInfo.setVisibility(View.VISIBLE);
                    }
                });
    }

}