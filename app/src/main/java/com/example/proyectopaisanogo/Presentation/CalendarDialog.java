package com.example.proyectopaisanogo.Presentation;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

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

/*
    Propósito: Un DialogFragment que permite a los usuarios seleccionar una fecha y hora para una
    cita con una empresa específica. El diálogo facilita la interacción con un CalendarView y un
    TimePickerDialog. Una vez seleccionada la fecha y hora, la cita se guarda en Firestore bajo
    la colección "Citas".
 */
public class CalendarDialog extends DialogFragment {

    Empresa empresa;
    private final FirebaseFirestore db;
    private final Calendar selectedDate = Calendar.getInstance();  // Almacenar la fecha y hora seleccionada

    public CalendarDialog(Empresa empresa) {
        this.empresa = empresa;
        this.db = FirebaseFirestore.getInstance(); // Asegúrate de que Firebase esté inicializado
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.calendar_dialog, null);
        dialog.setContentView(view);

        CalendarView calendarView = view.findViewById(R.id.calendarViewDialog);
        Button confirmButton = view.findViewById(R.id.btnConfirmDate);

        // Establecer listener para cambios de fecha
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            // Se ajusta la fecha tan pronto como se selecciona en el calendario.
            selectedDate.set(year, month, dayOfMonth);

            // El TimePickerDialog se abre con los valores iniciales del día seleccionado.
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (timeView, hourOfDay, minute) -> {
                // Se actualizan la hora y minutos solo después de confirmar en el TimePicker.
                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectedDate.set(Calendar.MINUTE, minute);
                confirmButton.setEnabled(true); // Habilita el botón una vez seleccionado el tiempo completo.
            }, selectedDate.get(Calendar.HOUR_OF_DAY), selectedDate.get(Calendar.MINUTE), false);

            timePickerDialog.show();
        });

        confirmButton.setOnClickListener(v -> {
            Map<String, Object> cita = new HashMap<>();
            cita.put("empresaId", empresa.getUserID());
            cita.put("fecha", selectedDate.getTime());  // Se guarda la fecha y hora seleccionada.
            createCita(cita);
            dialog.dismiss();
        });

        return dialog;
    }

    private void createCita(Map<String, Object> cita) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userID = currentUser.getUid();  // Obtiene el ID del usuario autenticado
            cita.put("userID", userID);  // Guarda el userID con la cita

            Log.d("CalendarDialog", "Guardando cita con empresa ID: " + empresa.getUserID() + ", Usuario ID: " + userID);
            db.collection("Citas").add(cita)
                    .addOnSuccessListener(documentReference -> Log.d("CalendarDialog", "Cita guardada con éxito"))
                    .addOnFailureListener(e -> Log.w("CalendarDialog", "Error al guardar cita", e));
        }
    }


}
