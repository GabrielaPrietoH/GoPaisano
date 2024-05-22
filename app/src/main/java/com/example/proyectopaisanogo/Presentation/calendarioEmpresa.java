package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.Adapter.CalendarAdapter;
import com.example.proyectopaisanogo.Model.Cliente;
import com.example.proyectopaisanogo.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class calendarioEmpresa extends Fragment  implements NavigationView.OnNavigationItemSelectedListener, CalendarAdapter.OnItemListener  {

    private CalendarView calendarView;
    private FirebaseFirestore db;
    private String userID;

    private CalendarAdapter adapter;
    private ArrayList<String> daysOfMonth;
    private TextView informacion;
    private TextView monthYearText;
    private Calendar calendar;

    public calendarioEmpresa() {
        // Requerido por Android
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Obtener el userID del usuario activo
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_empresa, container, false);
        View v = inflater.inflate(R.layout.calendar_dialog, container, false);
        calendarView = v.findViewById(R.id.calendarViewDialog);

        setupRecyclerView(view);
        setupMonthNavigation(view);
        informacion = view.findViewById(R.id.tvCitaInfoE); // Asegúrate de que este ID sea correcto en tu XML
        monthYearText = view.findViewById(R.id.monthYearTV);
        calendar = Calendar.getInstance();

        db = FirebaseFirestore.getInstance();

        updateCalendar();
        setupCalendarListener();
        setupToolbar(view);
        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarCalendarioEmpresa);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Calendario Empresa");
        }

        toolbar.setNavigationOnClickListener(v -> {
            // Manejo de la flecha de retroceso
            requireActivity().onBackPressed();
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            requireActivity().onBackPressed();
            return true;
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
                .whereEqualTo("empresaId", userID) // Filtrar por userID de la empresa
                .whereGreaterThanOrEqualTo("fecha", new Timestamp(startDate))
                .whereLessThan("fecha", new Timestamp(endDate))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder citasDetails = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Date citaDate = Objects.requireNonNull(document.getTimestamp("fecha")).toDate();
                            String citaID = document.getId();
                            citasDetails.append("Cita ID: ").append(citaID)
                                    .append(", Fecha: ").append(citaDate)
                                    .append("\n");
                        }
                        informacion.setText(citasDetails.toString());
                        informacion.setVisibility(View.VISIBLE);
                    } else {
                        informacion.setText(R.string.error_cargando_citas);
                        informacion.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.calendarEmpresaRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7)); // 7 columnas para los días de la semana

        daysOfMonth = new ArrayList<>();
        adapter = new CalendarAdapter(daysOfMonth, this, userID); // Pasar el userID al adapter
        recyclerView.setAdapter(adapter);
    }

    private void setupMonthNavigation(View view) {
        Button previousMonthButton = view.findViewById(R.id.previousMonthButton);
        Button nextMonthButton = view.findViewById(R.id.nextMonthButton);

        previousMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextMonthButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        monthYearText.setText(sdf.format(calendar.getTime()));

        daysOfMonth.clear();
        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < firstDayOfMonth; i++) {
            daysOfMonth.add("");
        }

        for (int i = 1; i <= daysInMonth; i++) {
            daysOfMonth.add(String.valueOf(i));
        }

        adapter.notifyDataSetChanged();

        for (int i = 1; i <= daysInMonth; i++) {
            String dayText = String.valueOf(i);
            onItemClick(i - 1, dayText);
        }
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (informacion != null) {
            informacion.setText(String.format("%s%s", getString(R.string.dia_seleccionado), dayText));
        }

        db.collection("Citas")
                .whereEqualTo("empresaId", userID) // Filtrar por userID de la empresa
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder citasInfo = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("fecha");
                            if (timestamp != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("d 'de' MMMM 'de' yyyy, h:mm a", Locale.getDefault());
                                String fechaFormateada = sdf.format(timestamp.toDate());
                                String diaCita = new SimpleDateFormat("d", Locale.getDefault()).format(timestamp.toDate());

                                if (diaCita.equals(dayText)) {
                                    String clienteId = document.getString("userID");
                                    assert clienteId != null;
                                    db.collection("registroCliente").document(clienteId).get().addOnSuccessListener(clienteDoc -> {
                                        if (clienteDoc.exists()) {
                                            Cliente cliente = clienteDoc.toObject(Cliente.class);
                                            assert cliente != null;
                                            String detallesEmpresa = String.format("Cliente: %s\nTeléfono: %s",
                                                    cliente.getNombreCliente(), cliente.getTelefono());
                                            citasInfo.append(fechaFormateada).append("\n").append(detallesEmpresa).append("\n");
                                            informacion.setText(String.format("%s%s\nCitas:\n%s", getString(R.string.dia_seleccionado), dayText, citasInfo));
                                        }
                                    }).addOnFailureListener(e -> Log.e("calendarioEmpresa", "Error al cargar datos del cliente", e));
                                }
                            }
                        }
                        if (citasInfo.length() == 0) {
                            informacion.setText(String.format("%s%s\nNo hay citas.", getString(R.string.dia_seleccionado), dayText));
                        }
                    } else {
                        informacion.setText(String.format("%s%s\nNo hay citas.", getString(R.string.dia_seleccionado), dayText + getString(R.string.error_al_buscar_citas)));
                    }
                });
    }
}
