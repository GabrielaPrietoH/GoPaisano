package com.example.proyectopaisanogo.Presentation.Empresa;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
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

import com.example.proyectopaisanogo.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragmento para el registro de empresas.
 * Este fragmento maneja el registro de nuevas empresas, incluyendo la subida de una imagen
 * de la empresa a Firebase Storage y la creación de un nuevo usuario en Firebase Authentication.
 */
public class RegistroEmpresa extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText cifText, nombreText, direccionText, cpText, telefonoText, emailText, passwordText;
    private Uri filePath;
    private StorageReference storageRef;
    private ImageView imageView;
    private String role;

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
        View rootView = inflater.inflate(R.layout.fragment_registro_empresa, container, false);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        cifText = rootView.findViewById(R.id.editTextCifEmpresa);
        nombreText = rootView.findViewById(R.id.editTextNomEmpresa);
        direccionText = rootView.findViewById(R.id.editTextDireccionEmpresa);
        cpText = rootView.findViewById(R.id.editTextCpEmpresa);
        telefonoText = rootView.findViewById(R.id.editTextTelefonoEmpresa);
        emailText = rootView.findViewById(R.id.editTextEmailEmpresa);
        passwordText = rootView.findViewById(R.id.editTextTextPassword3);
        imageView = rootView.findViewById(R.id.imageEmpresa);

        Button btnSelectImage = rootView.findViewById(R.id.subirImagen);
        Button btnCancel = rootView.findViewById(R.id.cancelarImagen);
        btnSelectImage.setOnClickListener(v -> selectImage());
        btnCancel.setOnClickListener(v -> cancelUpload());

        Button registroE = rootView.findViewById(R.id.buttonRegistroEmpresa);
        registroE.setOnClickListener(v -> registerCompany());

        role = "empresa";
        setupToolbar(rootView);
        return rootView;
    }


    /**
     * Método que abre un selector de imágenes para que el usuario pueda elegir una imagen
     * desde su galería.
     */
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    /**
     * Método que cancela la subida de la imagen, eliminando la vista previa y
     * limpiando la Uri de la imagen seleccionada.
     */
    private void cancelUpload() {
        filePath = null;
        imageView.setImageURI(null);
        Toast.makeText(getContext(), "Subida cancelada", Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "Upload canceled", Toast.LENGTH_SHORT).show();
    }

    /**
     * Método que registra una nueva empresa en Firebase Authentication y Firestore.
     */
    private void registerCompany() {
        String cif = cifText.getText().toString().trim();
        String nombreEmpresa = nombreText.getText().toString().trim();
        String direccion = direccionText.getText().toString().trim();
        String cp = cpText.getText().toString().trim();
        String telefono = telefonoText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (!validateInputs(cif, nombreEmpresa, direccion, cp, telefono, email, password)) {
            return;
        }

        if (filePath == null) {
            showImageError();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String userEmail = user.getEmail();
                            Map<String, Object> empresa = createEmpresaMap(cif, nombreEmpresa, direccion, cp, telefono, userEmail, uid);

                            registerEmpresaInFirestore(empresa, uid);
                        }
                    } else {
                        handleRegistrationFailure(task);
                    }
                });
    }

    /**
     * Método que realiza las validaciones
     */
    private boolean validateInputs(String cif, String nombreEmpresa, String direccion, String cp, String telefono, String email, String password) {
        if (cif.isEmpty()) {
            cifText.setError("CIF es obligatorio");
            cifText.requestFocus();
            return false;
        }
        if (nombreEmpresa.isEmpty()) {
            nombreText.setError("Nombre de la empresa es obligatorio");
            nombreText.requestFocus();
            return false;
        }
        if (direccion.isEmpty()) {
            direccionText.setError("Dirección es obligatoria");
            direccionText.requestFocus();
            return false;
        }
        if (cp.isEmpty() || !cp.matches("\\d{5}")) {
            cpText.setError("Código Postal inválido");
            cpText.requestFocus();
            return false;
        }
        if (telefono.isEmpty() || !telefono.matches("\\d{9}")) {
            telefonoText.setError("Teléfono inválido");
            telefonoText.requestFocus();
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Correo electrónico inválido");
            emailText.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordText.setError("La contraseña debe tener al menos 6 caracteres");
            passwordText.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Método que comprueba que se haya subido una imagen.
     */
    private void showImageError() {
        imageView.requestFocus();
        imageView.setBackgroundResource(R.drawable.error_background);
        Toast.makeText(getContext(), "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
    }

    /**
     * Método que crea un mapper con todos los campos de la empresa.
     */
    private Map<String, Object> createEmpresaMap(String cif, String nombreEmpresa, String direccion, String cp, String telefono, String userEmail, String uid) {
        Map<String, Object> empresa = new HashMap<>();
        empresa.put("cif", cif);
        empresa.put("nombreEmpresa", nombreEmpresa);
        empresa.put("nombreEmpresaLowerCase", nombreEmpresa.toLowerCase());
        empresa.put("direccion", direccion);
        empresa.put("cp", cp);
        empresa.put("telefono", telefono);
        empresa.put("email", userEmail);
        empresa.put("userID", uid);
        empresa.put("role", role);
        return empresa;
    }

    /**
     * Método que registra una nueva empresa en Firebase Authentication y Firestore.
     */
    private void registerEmpresaInFirestore(Map<String, Object> empresa, String uid) {
        db.collection("registroEmpresa").document(uid).set(empresa)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        uploadImage(uid);
                    } else {
                        Toast.makeText(getContext(), "Error al registrar la empresa", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Método que gestiona las posibles excepciones
     */
    private void handleRegistrationFailure(Task<AuthResult> task) {
        String errorMessage;
        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            errorMessage = "Correo electrónico inválido.";
            emailText.setError(errorMessage);
            emailText.requestFocus();
        } catch (FirebaseAuthUserCollisionException e) {
            errorMessage = "El correo electrónico ya está en uso.";
            emailText.setError(errorMessage);
            emailText.requestFocus();
        } catch (Exception e) {
            errorMessage = "Error al registrar: " + e.getMessage();
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }


    /**
     * Método que sube la imagen seleccionada a Firebase Storage y actualiza la URL
     * de la imagen en Firestore.
     *
     * @param uid El ID del usuario.
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
                                        Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                                        goToMainEmpresaFragment();
                                    } else {
                                        Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                    goToMainEmpresaFragment();
                } else {
                    Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageView.requestFocus();
            imageView.setBackgroundResource(R.drawable.error_background);
            Toast.makeText(getContext(), "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método que navega al fragmento principal de empresa.
     */
    private void goToMainEmpresaFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new MainEmpresa())
                .setReorderingAllowed(true)
                .addToBackStack("nombre")
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
            imageView.setBackgroundResource(0);
        }
    }

    /**
     * Configura la barra de herramientas (Toolbar) para el fragmento de registro de empresas.
     * Este método inicializa la barra de herramientas y configura el comportamiento del botón
     * de navegación para permitir que el usuario regrese a la pantalla anterior.
     *
     * @param view La vista raíz del fragmento.
     */
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarRegistroEmpresa);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(" Registro Empresas");
        }

        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }
}