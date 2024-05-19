package com.example.proyectopaisanogo.Presentation;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;

public class registroEmpresa extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText cifText, nombreText, direccionText, cpText, telefonoText, emailText, passwordText;
    private Uri filePath; // Uri de la imagen seleccionada
    private StorageReference storageRef;
    private ImageView imageView; // ImageView para mostrar la imagen seleccionada

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registro_empresa, container, false);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // Referencia de los campos de entrada
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

        return rootView;
    }

    // Seleccionar imagen desde la galería
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    // Cancelar subida de imagen
    private void cancelUpload() {
        filePath = null;
        imageView.setImageURI(null); // Elimina la vista previa de la imagen
        Toast.makeText(getContext(), "Subida cancelada", Toast.LENGTH_SHORT).show();
    }

    // Método para registrar una empresa
    private void registerCompany() {
        String cif = cifText.getText().toString().trim();
        String nombreEmpresa = nombreText.getText().toString().trim();
        String direccion = direccionText.getText().toString().trim();
        String cp = cpText.getText().toString().trim();
        String telefono = telefonoText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

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
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Correo electrónico inválido");
            emailText.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordText.setError("La contraseña debe tener al menos 6 caracteres");
            passwordText.requestFocus();
            return;
        }
        if (filePath == null) {
            imageView.requestFocus();
            imageView.setBackgroundResource(R.drawable.error_background); // Cambiar el fondo para indicar error
            Toast.makeText(getContext(), "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String userEmail = user.getEmail();
                            Map<String, Object> empresa = new HashMap<>();
                            empresa.put("cif", cif);
                            empresa.put("nombreEmpresa", nombreEmpresa);
                            empresa.put("direccion", direccion);
                            empresa.put("cp", cp);
                            empresa.put("telefono", telefono);
                            empresa.put("email", userEmail);
                            empresa.put("userID", uid);

                            db.collection("registroEmpresa").document(uid).set(empresa)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            uploadImage(uid); // Intentar subir la imagen
                                        } else {
                                            Toast.makeText(getContext(), "Error al registrar la empresa", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Subir imagen a Firebase Storage
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
                                        Toast.makeText(getContext(), "Registro y subida de imagen exitosos", Toast.LENGTH_SHORT).show();
                                        goToMainEmpresaFragment();
                                    } else {
                                        Toast.makeText(getContext(), "Error al guardar URL de la imagen", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                } else {
                    Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            imageView.requestFocus();
            imageView.setBackgroundResource(R.drawable.error_background); // Cambiar el fondo para indicar error
            Toast.makeText(getContext(), "Debe seleccionar una imagen", Toast.LENGTH_SHORT).show();
        }
    }

    // Ir a la pantalla principal de empresa
    private void goToMainEmpresaFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new mainEmpresa())
                .setReorderingAllowed(true)
                .addToBackStack("nombre")
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            imageView.setImageURI(filePath); // Mostrar vista previa de la imagen
            imageView.setBackgroundResource(0); // Quitar el fondo de error si la imagen se selecciona correctamente
        }
    }
}

