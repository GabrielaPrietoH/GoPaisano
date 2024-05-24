package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.Adapter.CalendarAdapter;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CalendarioCliente extends Fragment implements CalendarAdapter.OnItemListener {

    private CalendarAdapter adapter;
    private ArrayList<DayOfTheMonth> daysOfMonth;
    private TextView informacion;
    private TextView monthYearText;
    private FirebaseFirestore db;
    private Calendar calendar;
    private String userID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Obtener el userID del usuario activo
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_cliente, container, false);

        setupToolbar(view);
        setupRecyclerView(view);
        setupMonthNavigation(view);

        informacion = view.findViewById(R.id.tvCitaInfoC); // Asegúrate de que este ID sea correcto en tu XML
        monthYearText = view.findViewById(R.id.monthYearTV);

        updateCalendar();

        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarCalendarioCliente);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Calendario Cliente");
        }

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.calendarRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 7)); // 7 columnas para los días de la semana

        // Inicializar la lista de días del mes
        daysOfMonth = new ArrayList<>();
        adapter = new CalendarAdapter(daysOfMonth, this); // Pasar el userID al adapter
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

    private void fetchCitas() {
        db.collection("registroCliente").document(userID).get()
                .addOnCompleteListener(taskCliente -> {
                    if (taskCliente.isSuccessful() && taskCliente.getResult().exists()) {
                        fetchCitasDelCliente();
                    } else {
                        // Manejar el caso donde el usuario no se encuentra en ninguna colección
                        Log.e("fetchCitas", "Usuario no encontrado en registroEmpresa ni en registroCliente");
                    }
                });
    }

    private void updateCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.getDefault());
        monthYearText.setText(sdf.format(calendar.getTime()));
        daysOfMonth.clear();

        Calendar tempCalendar = (Calendar) calendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        String currentMonth = String.valueOf(tempCalendar.get(Calendar.MONTH) + 1);
        int firstDayOfMonth = tempCalendar.get(Calendar.DAY_OF_WEEK);
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (int i = 1; i < firstDayOfMonth; i++) {
            daysOfMonth.add(new DayOfTheMonth("", false, currentMonth));
        }

        for (int i = 1; i <= daysInMonth; i++) {
            daysOfMonth.add(new DayOfTheMonth(String.valueOf(i), false, currentMonth));
        }

        fetchCitas();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void fetchCitasDelCliente() {
        db.collection("Citas").whereEqualTo("userID", userID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Timestamp timestamp = document.getTimestamp("fecha");
                            if (timestamp != null) {
                                SimpleDateFormat sdf = new SimpleDateFormat("d", Locale.getDefault());
                                SimpleDateFormat sdf2 = new SimpleDateFormat("M", Locale.getDefault());
                                String day = sdf.format(timestamp.toDate());
                                String month = sdf2.format(timestamp.toDate());
                                for (int i = 0; i < daysOfMonth.size(); i++) {
                                    String dayOfMonth = daysOfMonth.get(i).getDay();
                                    if (dayOfMonth.equals(day)) {
                                        daysOfMonth.get(i).setCita(daysOfMonth.get(i).getMonth().equals(month));
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if (informacion != null) {
            informacion.setText(String.format("%s%s", getString(R.string.dia_seleccionado), dayText));
        }

        // Fetch appointments from Firestore
        db.collection("Citas").whereEqualTo("userID", userID).get()
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
                                    String empresaId = document.getString("empresaId");
                                    assert empresaId != null;
                                    db.collection("registroEmpresa").document(empresaId).get()
                                            .addOnSuccessListener(empresaDoc -> {
                                                if (empresaDoc.exists()) {
                                                    Empresa empresa = empresaDoc.toObject(Empresa.class);
                                                    assert empresa != null;
                                                    String detallesEmpresa = String.format("Empresa: %s Teléfono: %s", empresa.getNombreEmpresa(), empresa.getTelefono());
                                                    citasInfo.append(fechaFormateada).append("\n").append(detallesEmpresa).append("\n");
                                                    informacion.setText(String.format("%s%s\nCitas:\n%s", getString(R.string.dia_seleccionado), dayText, citasInfo));
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("calendarioCliente", "Error al cargar datos de la empresa", e));
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

