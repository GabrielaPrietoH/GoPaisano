package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * DialogFragment para seleccionar una fecha y hora en el calendario.
 * Este diálogo permite al usuario seleccionar una fecha y una hora para agendar
 * una cita con una empresa específica. Faciltia la interacción con un calendarView y un
 * TimePickerDialog. Una vez seleccionada la fecha y hora, la cita se guarda en Firestore en
 * la colección "Citas".
 */
public class CalendarDialog extends DialogFragment {

    Empresa empresa;
    private final FirebaseFirestore db;
    private final Calendar selectedDate = Calendar.getInstance();  // Almacenar la fecha y hora seleccionada

    /**
     * Constructor sobrecargado
     *
     * @param empresa La empresa con la que se agendará la cita.
     */
    public CalendarDialog(Empresa empresa) {
        this.empresa = empresa;
        this.db = FirebaseFirestore.getInstance(); // Asegúrate de que Firebase esté inicializado
    }

    /**
     * Método llamado cuando se crea el fragmento.
     *
     * @param savedInstanceState Si el fragmento se está recreando a partir de un estado
     *                           guardado anteriormente, este es el estado.
     * @return El diálogo creado.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.calendar_dialog, null);
        dialog.setContentView(view);

        CalendarView calendarView = view.findViewById(R.id.calendarViewDialog);
        Button confirmButton = view.findViewById(R.id.btnConfirmDate);

        Calendar currentDate = Calendar.getInstance();
        calendarView.setMinDate(currentDate.getTimeInMillis());

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (timeView, hourOfDay, minute) -> {
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDate.set(Calendar.MINUTE, minute);
            }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), false);

            timePickerDialog.show();
        });

        confirmButton.setOnClickListener(v -> {

            if(selectedDate.before(Calendar.getInstance())){
                Toast.makeText(getContext(), "Seleccione una fecha/hora futura", Toast.LENGTH_SHORT).show();
            }
            else {
                confirmButton.setEnabled(true);
                Map<String, Object> cita = new HashMap<>();
                cita.put("empresaId", empresa.getUserID());
                cita.put("fecha", selectedDate.getTime());

                createCita(cita);
                dialog.dismiss();
                Toast.makeText(getContext(), "Cita creada correctamente", Toast.LENGTH_SHORT).show();
            }
        });

        return dialog;
    }

    /**
     * Método que crea una cita y la guarda en Firestore.
     *
     * @param cita Los detalles de la cita a ser guardados.
     */
    private void createCita(Map<String, Object> cita) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();
            cita.put("userID", userID);

            db.collection("Citas").add(cita)
                    .addOnSuccessListener(documentReference -> Log.d("CalendarDialog", "Cita guardada con éxito"))
                    .addOnFailureListener(e -> Log.w("CalendarDialog", "Error al guardar cita", e));
        } else {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}

