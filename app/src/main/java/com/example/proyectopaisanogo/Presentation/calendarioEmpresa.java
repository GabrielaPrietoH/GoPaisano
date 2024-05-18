package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.util.Log;
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

import com.example.proyectopaisanogo.Adapter.CitasAdapter;
import com.example.proyectopaisanogo.Model.Cita;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;


public class calendarioEmpresa extends Fragment  implements NavigationView.OnNavigationItemSelectedListener {

    private CalendarView calendarView;
    private TextView tvCitaInfo;
    private FirebaseFirestore db;
    private final Empresa empresa = new Empresa();

    private CitasAdapter citasAdapter;
    private RecyclerView recyclerView;


    public calendarioEmpresa() {
        // Requerido por Android
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_empresa, container, false);
        calendarView = view.findViewById(R.id.empresaCalendarView);
        tvCitaInfo = view.findViewById(R.id.tvCitaInfo);

        db = FirebaseFirestore.getInstance();
        setupCalendarListener();

        setupToolbar(view);

        //CALENDARIO
        recyclerView = view.findViewById(R.id.recyclerCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        citasAdapter = new CitasAdapter();
        recyclerView.setAdapter(citasAdapter);
        // Cargar las citas al abrir la vista
        fetchCitas();


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
                .whereEqualTo("empresaId", empresa.getUserID())
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

                        }
                        tvCitaInfo.setText(citasDetails.toString());
                        tvCitaInfo.setVisibility(View.VISIBLE);
                    } else {
                        tvCitaInfo.setText(R.string.error_cargando_citas);
                        tvCitaInfo.setVisibility(View.VISIBLE);
                    }
                });
    }

    //CALENDARIO
    private void fetchCitas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String empresaId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Citas")
                .whereEqualTo("empresaId", empresaId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Cita> nuevasCitas = new ArrayList<>();
                        AtomicInteger remainingCalls = new AtomicInteger(task.getResult().size()); // Contador para gestionar subconsultas asincrónicas

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userID = document.getString("userID");
                            Timestamp fecha = document.getTimestamp("fecha");  // Extraer la fecha directamente de cada documento de cita

                            // Subconsulta para obtener los detalles del cliente
                            db.collection("registroCliente").document(userID).get().addOnSuccessListener(clientSnapshot -> {
                                String nombreCliente = clientSnapshot.getString("nombreCliente");
                                String telefonoCliente = clientSnapshot.getString("telefono");

                                // Crear un objeto Cita con toda la información necesaria
                                Cita cita = new Cita(nombreCliente, fecha, telefonoCliente);
                                nuevasCitas.add(cita);

                                // Verificar si todas las subconsultas han terminado
                                if (remainingCalls.decrementAndGet() == 0) {
                                    // Actualizar el adaptador aquí si todas las consultas han finalizado
                                    citasAdapter.updateData(nuevasCitas);
                                }
                            }).addOnFailureListener(e -> {
                                Log.w("calendarioEmpresa", "Error al obtener detalles del cliente", e);
                                if (remainingCalls.decrementAndGet() == 0) {
                                    // Actualizar el adaptador aquí si todas las consultas han finalizado
                                    citasAdapter.updateData(nuevasCitas);
                                }
                            });
                        }
                    } else {
                        Log.w("calendarioEmpresa", "Error getting documents.", task.getException());
                    }
                });
    }



}