package com.example.proyectopaisanogo.Presentation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


/**
 * Fragmento para ajsutar la configuración de cada usuario de tipo empresa.

 * Este fragmento permite a las empresas actualizar su información y modificar su contraseña.
 */
public class SettingEmpresa extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    EditText cifText, nombreText, direccionText, cpText, telefonoText, passwordText;
    Button btnSaveChanges, btnSelectImage, btnCancel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private ImageView imageView;
    private Uri filePath;
    private boolean imageUpdated = false;
    private String currentImageUrl;

    /**
     * Constructor por defecto.
     */
    public SettingEmpresa() {

    }

    /**
     * Método que se llama cuando se crea el fragmento.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    /**
     * Método que nicializa la vista del fragmento.
     *
     * @param %%inflater           El LayoutInflater que se usa para inflar la vista del fragmento.
     * @param %%container          El ViewGroup padre al que se adjunta la vista del fragmento.
     * @param %%savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
     * @return La vista inflada del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_empresa, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        cifText = rootView.findViewById(R.id.editTextCifEmpresa);
        nombreText = rootView.findViewById(R.id.editTextNomEmpresa);
        direccionText = rootView.findViewById(R.id.editTextDireccionEmpresa);
        cpText = rootView.findViewById(R.id.editTextCpEmpresa);
        telefonoText = rootView.findViewById(R.id.editTextTelefonoEmpresa);
        passwordText = rootView.findViewById(R.id.editTextTextPassword3);
        imageView = rootView.findViewById(R.id.imageEmpresa);

        btnSaveChanges = rootView.findViewById(R.id.buttonResgistroEmpresa);
        btnSelectImage = rootView.findViewById(R.id.subirImagen);
        btnCancel = rootView.findViewById(R.id.cancelarImagen);

        loadCompanyData();

        // ASignación de listeners para los botones
        btnSaveChanges.setOnClickListener(v -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        // Obtener datos actualizados del formulario
                        String cif = cifText.getText().toString().trim();
                        String nombreEmpresa = nombreText.getText().toString().trim();
                        String direccion = direccionText.getText().toString().trim();
                        String cp = cpText.getText().toString().trim();
                        String telefono = telefonoText.getText().toString().trim();
                        String newPassword = passwordText.getText().toString().trim();

                        // Mostrar diálogo para la contraseña actual
                        showPasswordDialog(user, nombreEmpresa, direccion, cp, telefono, newPassword);
                    } else {
                        showToast("Usuario no autenticado o ha ocurrido un error");
                    }
                });

        btnSelectImage.setOnClickListener(v -> selectImage());
        btnCancel.setOnClickListener(v -> cancelUpload());
        saveChanges();
        setupToolbar(rootView);
        return rootView;
    }

    /**
     * Método que Carga los datos de la empresa desde Firestore y los muestra en los campos
     * correspondientes.
     */
    private void loadCompanyData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("registroEmpresa").document(user.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    cifText.setText(documentSnapshot.getString("cif"));
                    nombreText.setText(documentSnapshot.getString("nombreEmpresa"));
                    direccionText.setText(documentSnapshot.getString("direccion"));
                    cpText.setText(documentSnapshot.getString("cp"));
                    telefonoText.setText(documentSnapshot.getString("telefono"));
                    currentImageUrl = documentSnapshot.getString("imageUrl");

                    if (currentImageUrl != null && !currentImageUrl.isEmpty()) {
                        Glide.with(this).load(currentImageUrl).into(imageView);
                    }
                } else {
                    showToast("No se encontraron datos de la empresa");
                }
            }).addOnFailureListener(e -> showToast("Error al cargar datos de la empresa"));
        }
    }

    /**
     * Método que muestra un cuadro de diálogo para reautenticación del usuario.
     *
     * @param %user            El usuario actual.
     * @param %nombreEmpresa   El nombre de la empresa.
     * @param %direccion       La dirección de la empresa.
     * @param %cp              El código postal de la empresa.
     * @param %telefono        El teléfono de la empresa.
     * @param %newPassword     La nueva contraseña del usuario.
     */
    private void showPasswordDialog(FirebaseUser user, String nombreEmpresa, String direccion, String cp, String telefono, String newPassword) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.password_dialog, null);
        builder.setView(dialogView);

        final EditText input = dialogView.findViewById(R.id.password_input);

        builder.setPositiveButton("Aceptar", null);
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String currentPassword = input.getText().toString().trim();
            if (!currentPassword.isEmpty()) {
                reauthenticateUser(user, currentPassword, nombreEmpresa, direccion, cp, telefono, newPassword);
                dialog.dismiss();
            } else {
                showToast("La contraseña no puede estar vacía");
            }
        });
        negativeButton.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * Método para autenticar de nuevo al usuario con la contraseña actual y
     * actualizar los datos de la empresa.
     *
     * @param user            El usuario actual.
     * @param currentPassword La contraseña actual del usuario.
     * @param nombreEmpresa   El nombre de la empresa.
     * @param direccion       La dirección de la empresa.
     * @param cp              El código postal de la empresa.
     * @param telefono        El teléfono de la empresa.
     * @param newPassword     La nueva contraseña del usuario.
     */
    private void reauthenticateUser(FirebaseUser user, String currentPassword, String nombreEmpresa, String direccion, String cp, String telefono, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                updateCompanyData( user, nombreEmpresa, direccion, cp, telefono, newPassword);
            } else {
                showToast("Error de reautenticación: " + reauthTask.getException().getMessage());
            }
        });
    }

    /**
     * Método aue actualiza los datos de la empresa en Firestore y la contraseña del usuario
     * si se ha proporcionado una nueva.
     *
     * @param user            El usuario actual.
     * @param nombreEmpresa   El nombre de la empresa.
     * @param direccion       La dirección de la empresa.
     * @param cp              El código postal de la empresa.
     * @param telefono        El teléfono de la empresa.
     * @param newPassword     La nueva contraseña del usuario.
     */
    private void updateCompanyData(FirebaseUser user, String nombreEmpresa, String direccion, String cp, String telefono, String newPassword) {
        DocumentReference docRef = db.collection("registroEmpresa").document(user.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("nombreEmpresa", nombreEmpresa);
        updates.put("direccion", direccion);
        updates.put("cp", cp);
        updates.put("telefono", telefono);

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    showToast("Datos actualizados correctamente");
                    if (!newPassword.isEmpty()) {
                        showToast("Contraseña actualizada correctamente");
                    }
                    navigateToFragment();
                })
                .addOnFailureListener(e -> showToast("Error al actualizar los datos: " + e.getMessage()));
    }

    /**
     * Método que guarda los cambios realizados en la información de la empresa.
     */
    private void saveChanges() {
        String cif = cifText.getText().toString().trim();
        String nombreEmpresa = nombreText.getText().toString().trim();
        String direccion = direccionText.getText().toString().trim();
        String cp = cpText.getText().toString().trim();
        String telefono = telefonoText.getText().toString().trim();
        String newPassword = passwordText.getText().toString().trim();

        // Validaciones
        if (cif.isEmpty()) {
            cifText.setError("CIF es obligatorio");
            cifText.requestFocus();
            return;
        }
        if (nombreEmpresa.isEmpty()) {
            nombreText.setError("Nombre de la empresa es obligatorio");
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

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("registroEmpresa").document(user.getUid());
            Map<String, Object> updates = new HashMap<>();
            updates.put("cif", cif);
            updates.put("nombreEmpresa", nombreEmpresa);
            updates.put("direccion", direccion);
            updates.put("cp", cp);
            updates.put("telefono", telefono);

            docRef.update(updates)
                    .addOnSuccessListener(aVoid -> showToast("Datos actualizados correctamente"))
                    .addOnFailureListener(e -> showToast("Error al actualizar los datos"));

            if (!newPassword.isEmpty()) {
                user.updatePassword(newPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Contraseña actualizada correctamente");
                    } else {
                        showToast("Error al actualizar la contraseña");
                    }
                });
            }

            if (imageUpdated) {
                uploadImage(user.getUid());
            } else {
                navigateToFragment();
            }
        } else {
            showToast("El usuario no está autenticado");
        }
    }

    /**
     * Método que abre un selector de imágenes para que el usuario pueda elegir una imagen de la galería.
     */
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    private void cancelUpload() {
        filePath = null;
        imageView.setImageURI(null);
        showToast("Subida cancelada");
    }

    /**
     * Método que cancela la subida de la imagen, eliminando la vista previa y limpiando
     * la Uri de la imagen seleccionada.
     */
    private void uploadImage(String uid) {
        if (filePath != null) {
            StorageReference imageRef = storageRef.child("images/" + uid);
            UploadTask uploadTask = imageRef.putFile(filePath);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        db.collection("registroEmpresa").document(uid).update("imageUrl", imageUrl)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        showToast("Imagen subida exitosamente");
                                        navigateToFragment();
                                    } else {
                                        showToast("Error al guardar URL de la imagen");
                                    }
                                });
                    });
                } else {
                    showToast("Error al subir la imagen");
                }
            });
        }
    }

    /**
     * Método que navega al fragmento principal de empresa.
     */
    private void navigateToFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MainCliente.class, null)
                .setReorderingAllowed(true)
                .commit();
    }

    /**
     * Método que maneja el resultado de la actividad iniciada por el selector de imágenes.
     *
     * @param requestCode El código de solicitud pasado a startActivityForResult().
     * @param resultCode  El código de resultado devuelto por la actividad secundaria.
     * @param data        Un Intent que puede devolver datos de resultado a la llamada.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imageView.setImageURI(filePath);
            imageUpdated = true;
        }
    }

    /**
     * Método que configura la barra de herramientas (Toolbar) para el fragmento de configuración
     * de empresas. Inicializa la barra de herramientas y configura el comportamiento del botón
     * de navegación para permitir que el usuario regrese a la pantalla anterior.
     *
     * @param view La vista raíz del fragmento.
     */
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarSettingEmpresa);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle("Configuración Empresa");
        }

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

