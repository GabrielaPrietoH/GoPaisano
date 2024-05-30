package com.example.proyectopaisanogo.Presentation.Empresa;

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
import com.example.proyectopaisanogo.Presentation.FragmentLogin;
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
     * Método que inicializa la vista del fragmento.
     *
     * @param inflater           El LayoutInflater que se usa para inflar la vista del fragmento.
     * @param container          El ViewGroup padre al que se adjunta la vista del fragmento.
     * @param savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
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
        Button btnEliminarCuentaEmpresa = rootView.findViewById(R.id.buttonEliminarCuentaEmpresa);

        loadCompanyData();

        btnSaveChanges.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                saveChanges();
            } else {
                showToast("Usuario no autenticado o ha ocurrido un error");
            }
        });


        btnSelectImage.setOnClickListener(v -> selectImage());
        btnCancel.setOnClickListener(v -> cancelUpload());
        btnEliminarCuentaEmpresa.setOnClickListener(v -> deleteAccount());

        passwordText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String cif = cifText.getText().toString().trim();
                    String nombreEmpresa = nombreText.getText().toString().trim();
                    String direccion = direccionText.getText().toString().trim();
                    String cp = cpText.getText().toString().trim();
                    String telefono = telefonoText.getText().toString().trim();
                    String newPassword = passwordText.getText().toString().trim();
                    showPasswordDialog(user, cif, nombreEmpresa, direccion, cp, telefono, newPassword);
                }
            }
        });
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
     * @param user            El usuario actual.
     * @param nombreEmpresa   El nombre de la empresa.
     * @param direccion       La dirección de la empresa.
     * @param cp              El código postal de la empresa.
     * @param telefono        El teléfono de la empresa.
     * @param newPassword     La nueva contraseña del usuario.
     */
    private void showPasswordDialog(FirebaseUser user, String cif, String nombreEmpresa, String direccion, String cp, String telefono, String newPassword) {

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
                reauthenticateUser(user, currentPassword, cif, nombreEmpresa, direccion, cp, telefono, newPassword);
                dialog.dismiss();
            } else {
                showToast("La contraseña no puede estar vacía");
            }
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
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
    private void reauthenticateUser(FirebaseUser user, String currentPassword, String cif, String nombreEmpresa, String direccion, String cp, String telefono, String newPassword) {
        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential).addOnCompleteListener(reauthTask -> {
            if (reauthTask.isSuccessful()) {
                updateCompanyData(user, cif, nombreEmpresa, direccion, cp, telefono, newPassword);
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
    private void updateCompanyData(FirebaseUser user, String cif, String nombreEmpresa, String direccion, String cp, String telefono, String newPassword) {
        DocumentReference docRef = db.collection("registroEmpresa").document(user.getUid());
        Map<String, Object> updates = new HashMap<>();
        updates.put("cif", cif);
        updates.put("nombreEmpresa", nombreEmpresa);
        updates.put("nombreEmpresaLowerCase", nombreEmpresa.toLowerCase());
        updates.put("direccion", direccion);
        updates.put("cp", cp);
        updates.put("telefono", telefono);

        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    showToast("Datos actualizados correctamente");
                    if (!newPassword.isEmpty()) {
                        showToast("Contraseña actualizada correctamente");
                    }
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
            updates.put("nombreEmpresaLowerCase", nombreEmpresa.toLowerCase());
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
                navigateToFragment(MainEmpresa.class);
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
                                        navigateToFragment(MainEmpresa.class);
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
     * Re-authenticacion previa a la eliminación del usuario
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
     * Eliminar cuenta del usuario en Firebase Authentication y Firestore.
     */
    private void deleteUserAccount(FirebaseUser user) {
        String uid = user.getUid();

        db.collection("registroEmpresa").document(uid).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.delete().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        showToast("Cuenta eliminada correctamente");
                       navigateToFragment(FragmentLogin.class);
                    } else {
                        showToast("Error al eliminar la cuenta");
                    }
                });
            } else {
                showToast("Error al eliminar los datos de la empresa");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

