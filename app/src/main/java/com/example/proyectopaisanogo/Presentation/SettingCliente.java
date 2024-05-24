package com.example.proyectopaisanogo.Presentation;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SettingCliente extends Fragment {

    // Settings
    EditText nombreText, direccionText, cpText, telefonoText, passwordText;
    Button btnSaveChanges;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_cliente, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencia de las cajas
        nombreText = rootView.findViewById(R.id.editTextUsuarioCliente);
        direccionText = rootView.findViewById(R.id.editTextDireccionCLiente);
        cpText = rootView.findViewById(R.id.editTextCpCliente);
        telefonoText = rootView.findViewById(R.id.editTextPhoneCliente);
        passwordText = rootView.findViewById(R.id.editTextPasswordCliente);
        btnSaveChanges = rootView.findViewById(R.id.buttonGuardarCambiosCliente);

        btnSaveChanges.setOnClickListener(v -> {
            // Recoger los valores de las cajas de texto
            String nombreCliente = nombreText.getText().toString().trim();
            String direccion = direccionText.getText().toString().trim();
            String cp = cpText.getText().toString().trim();
            String telefono = telefonoText.getText().toString().trim();
            String newPassword = passwordText.getText().toString().trim();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                // Mostrar diálogo para la contraseña actual
                showPasswordDialog(user, nombreCliente, direccion, cp, telefono, newPassword);
            } else {
                // El usuario no está autenticado o ha ocurrido un error
                showToast("Usuario no autenticado o ha ocurrido un error");
            }
        });
        setupToolbar(rootView);
        loadUserData();
        return rootView;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarSettingCliente);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Configuración Cliente");
        }

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void showPasswordDialog(FirebaseUser user, String nombreCliente, String direccion, String cp, String telefono, String newPassword) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.password_dialog, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.password_input);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button confirmButton = dialogView.findViewById(R.id.btnConfirm);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);

        confirmButton.setOnClickListener(v -> {
            String currentPassword = input.getText().toString().trim();
            if (!currentPassword.isEmpty()) {
                reauthenticateUser(user, currentPassword, nombreCliente, direccion, cp, telefono, newPassword);
                showToast("Contraseña cambiada exitosamente");
            } else {
                showToast("La contraseña no puede estar vacía");
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> {
            showToast("Operación cancelada");
            dialog.dismiss();
        });
    }

    private void reauthenticateUser(FirebaseUser user, String currentPassword, String nombreCliente, String direccion, String cp, String telefono, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                updateUserData(user, nombreCliente, direccion, cp, telefono, newPassword);
            } else {
                showToast("Error de reautenticación: " + reauthTask.getException().getMessage());
            }
        });
    }

    private void updateUserData(FirebaseUser user, String nombreCliente, String direccion, String cp, String telefono, String newPassword) {
        // Actualizar los datos del usuario en Firestore
        DocumentReference docRef = db.collection("registroCliente").document(user.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreCliente", nombreCliente);
        updates.put("direccion", direccion);
        updates.put("cp", cp);
        updates.put("telefono", telefono);

        docRef.update(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Datos actualizados exitosamente");
                // Verificar si se debe actualizar la contraseña
                if (!newPassword.isEmpty()) {
                    user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                        if (passwordTask.isSuccessful()) {
                            showToast("Contraseña actualizada exitosamente");
                        } else {
                            showToast("Error al actualizar la contraseña: " + passwordTask.getException().getMessage());
                        }
                    });
                }
                navigateToFragment();
            } else {
                showToast("Error al actualizar los datos: " + task.getException().getMessage());
            }
        });
    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("registroCliente").document(user.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nombreCliente = documentSnapshot.getString("nombreCliente");
                    String direccion = documentSnapshot.getString("direccion");
                    String cp = documentSnapshot.getString("cp");
                    String telefono = documentSnapshot.getString("telefono");

                    nombreText.setText(nombreCliente);
                    direccionText.setText(direccion);
                    cpText.setText(cp);
                    telefonoText.setText(telefono);
                } else {
                    showToast("No se encontraron datos del cliente");
                }
            }).addOnFailureListener(e -> showToast("Error al cargar datos: " + e.getMessage()));
        } else {
            showToast("Usuario no autenticado");
        }
    }

    private void navigateToFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainCliente.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    private void showToast(String message) {
        // Implementación de mostrar un toast con el mensaje proporcionado
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

