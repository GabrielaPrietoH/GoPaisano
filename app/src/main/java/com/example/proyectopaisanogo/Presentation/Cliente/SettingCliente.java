package com.example.proyectopaisanogo.Presentation.Cliente;

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

import com.example.proyectopaisanogo.Presentation.FragmentLogin;
import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragmento para ajustar la configuración de cada usuario de tipo cliente.
 * Este fragmento permite a los clientes actualizar su información y modificar su contraseña.
 *
 */
public class SettingCliente extends Fragment {
    EditText nombreText, direccionText, cpText, telefonoText, passwordText;
    Button btnSaveChanges;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Método llamado cuando se crea el fragmento.
     *
     * @param savedInstanceState Si el fragmento se está recreando a partir de un estado guardado
     *                           anteriormente, este es el estado.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Método que inicializa la vista del fragmento.
     *
     * @param inflater El LayoutInflater que se usa para inflar la vista del fragmento.
     * @param container  El ViewGroup padre al que se adjunta la vista del fragmento.
     * @param savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
     * @return La vista inflada del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_cliente, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nombreText = rootView.findViewById(R.id.editTextUsuarioCliente);
        direccionText = rootView.findViewById(R.id.editTextDireccionCLiente);
        cpText = rootView.findViewById(R.id.editTextCpCliente);
        telefonoText = rootView.findViewById(R.id.editTextPhoneCliente);
        passwordText = rootView.findViewById(R.id.editTextPasswordCliente);
        btnSaveChanges = rootView.findViewById(R.id.buttonGuardarCambiosCliente);
        Button btnEliminarCuentaEmpresa = rootView.findViewById(R.id.buttonEliminarCuentaCliente);
        btnSaveChanges.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
            String nombreCliente = nombreText.getText().toString().trim();
            String direccion = direccionText.getText().toString().trim();
            String cp = cpText.getText().toString().trim();
            String telefono = telefonoText.getText().toString().trim();
            String newPassword = passwordText.getText().toString().trim();

                // Validaciones
                if (nombreCliente.isEmpty()) {
                    nombreText.setError("Nombre es obligatorio");
                    nombreText.requestFocus();
                    return;
                }
                if (direccion.isEmpty()) {
                    direccionText.setError("Dirección es obligatoria");
                    direccionText.requestFocus();
                    return;
                }
                if (cp.isEmpty() || !cp.matches("\\d{5}")) {
                    cpText.setError("Código Postal inválido");
                    cpText.requestFocus();
                    return;
                }
                if (telefono.isEmpty() || !telefono.matches("\\d{9}")) {
                    telefonoText.setError("Teléfono inválido");
                    telefonoText.requestFocus();
                    return;
                }
                if (!newPassword.isEmpty() && newPassword.length() < 6) {
                    passwordText.setError("La contraseña debe tener al menos 6 caracteres");
                    passwordText.requestFocus();
                    return;
                }

            updateUserData(user, nombreCliente, direccion, cp, telefono, newPassword);
                navigateToFragment(MainCliente.class);
            } else {
                showToast("Usuario no autenticado o ha ocurrido un error");
            }
        });
        btnEliminarCuentaEmpresa.setOnClickListener(v -> deleteAccount());
        passwordText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String nombreCliente = nombreText.getText().toString().trim();
                    String direccion = direccionText.getText().toString().trim();
                    String cp = cpText.getText().toString().trim();
                    String telefono = telefonoText.getText().toString().trim();
                    String newPassword = passwordText.getText().toString().trim();
                    showPasswordDialog(user, nombreCliente, direccion, cp, telefono, newPassword);
                }
            }
        });
        setupToolbar(rootView);
        loadUserData();
        return rootView;
    }

    /**
     * Método que configura la barra de herramientas (Toolbar) para el fragmento.
     *
     * @param view La vista raíz del fragmento.
     */
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

    /**
     * Método que muestra un cuadro de diálogo para la reautenticación del usuario.
     *
     * @param user           El usuario actual.
     * @param nombreCliente  El nombre del cliente.
     * @param direccion      La dirección del cliente.
     * @param cp             El código postal del cliente.
     * @param telefono       El teléfono del cliente.
     * @param newPassword    La nueva contraseña del cliente.
     */
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
                showToast("Contraseña correcta");
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

    /**
     * Método que reautentica al usuario con la contraseña actual.
     *
     * @param user           El usuario actual.
     * @param currentPassword La contraseña actual del usuario.
     * @param nombreCliente  El nombre del cliente.
     * @param direccion      La dirección del cliente.
     * @param cp             El código postal del cliente.
     * @param telefono       El teléfono del cliente.
     * @param newPassword    La nueva contraseña del cliente.
     */
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

    /**
     * Método que actualiza los datos del usuario en Firestore.
     *
     * @param user           El usuario actual.
     * @param nombreCliente  El nombre del cliente.
     * @param direccion      La dirección del cliente.
     * @param cp             El código postal del cliente.
     * @param telefono       El teléfono del cliente.
     * @param newPassword    La nueva contraseña del cliente.
     */
    private void updateUserData(FirebaseUser user, String nombreCliente, String direccion, String cp, String telefono, String newPassword) {

        DocumentReference docRef = db.collection("registroCliente").document(user.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreCliente", nombreCliente);
        updates.put("direccion", direccion);
        updates.put("cp", cp);
        updates.put("telefono", telefono);

        docRef.update(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showToast("Datos actualizados exitosamente");
                if (!newPassword.isEmpty()) {
                    user.updatePassword(newPassword).addOnCompleteListener(passwordTask -> {
                        if (passwordTask.isSuccessful()) {
                            showToast("Contraseña actualizada exitosamente");
                        } else {
                            showToast("Error al actualizar la contraseña: " + passwordTask.getException().getMessage());
                        }
                    });
                }
            } else {
                showToast("Error al actualizar los datos: " + task.getException().getMessage());
            }
        });
    }

    /**
     * Método que carga los datos del usuario desde Firestore.
     */
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

    /**
     * Método que navega al fragmento principal del cliente.
     */

    private void navigateToFragment(Class<? extends Fragment> fragmentClass) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment fragment = null;
        try {
            fragment = fragmentClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            showToast("Error al crear instancia del fragmento");
        } catch (java.lang.InstantiationException e) {
            throw new RuntimeException(e);
        }
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    /**
     * Método que elimina la cuenta del usuario actual.
     */
    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
                    .setPositiveButton("Sí", (dialog, which) -> reauthenticateBeforeDelete(user))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            showToast("El usuario no está autenticado");
        }
    }

    /**
     * Método que comprueba si es el usuario antes de eliminar la cuenta.
     */
    private void reauthenticateBeforeDelete(FirebaseUser user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.password_dialog, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.password_input);
        final Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        final Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnConfirm.setOnClickListener(v -> {
            String currentPassword = input.getText().toString().trim();
            if (!currentPassword.isEmpty()) {
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                user.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        deleteUserAccount(user);
                        dialog.dismiss();
                    } else {
                        showToast("Error de reautenticación: " + task.getException().getMessage());
                    }
                });
            } else {
                showToast("La contraseña no puede estar vacía");
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Método que elimina la cuenta de usuario de Firebase Authentication y Firestore.
     */
    private void deleteUserAccount(FirebaseUser user) {
        String uid = user.getUid();

        db.collection("registroCliente").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String documentUid = document.getId();
                    if (documentUid.equals(uid)) {
                        db.collection("registroCliente").document(uid).delete().addOnCompleteListener(deleteTask -> {
                            if (deleteTask.isSuccessful()) {
                                user.delete().addOnCompleteListener(deleteAuthTask -> {
                                    if (deleteAuthTask.isSuccessful()) {
                                        showToast("Cuenta eliminada correctamente");
                                        navigateToFragment(FragmentLogin.class);
                                    } else {
                                        showToast("Error al eliminar la cuenta: " + deleteAuthTask.getException().getMessage());
                                    }
                                });
                            } else {
                                showToast("Error al eliminar los datos del cliente: " + deleteTask.getException().getMessage());
                            }
                        });
                    } else {
                        showToast("El UID del documento no coincide con el UID del usuario autenticado.");
                    }
                } else {
                    showToast("El documento no existe.");
                }
            } else {
                showToast("Error al obtener el documento: " + task.getException().getMessage());
            }
        });

    }

    /**
     * Método que muestra un Toast con el mensaje proporcionado.
     *
     * @param message El mensaje que se desea mostrar.
     */
    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}

