package com.example.proyectopaisanogo.Presentation;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText cifText, nombreText, direccionText, cpText, telefonoText, emailText, passwordText;
    private Uri filePath; // Uri de la imagen seleccionada
    private StorageReference storageRef;

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

        Button btnSelectImage = rootView.findViewById(R.id.subirImagen);
        Button btnCancel = rootView.findViewById(R.id.cancelarImagen);

        btnSelectImage.setOnClickListener(v -> selectImage());

        btnCancel.setOnClickListener(v -> cancelUpload());

        Button registroE = rootView.findViewById(R.id.buttonRegistroEmpresa);
        registroE.setOnClickListener(v -> registerCompany());

        return rootView;
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
    }

    private void cancelUpload() {
        filePath = null;
        Toast.makeText(getContext(), "Upload canceled", Toast.LENGTH_SHORT).show();
    }

    private void registerCompany() {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String userEmail = user.getEmail();

                            Map<String, Object> empresa = new HashMap<>();
                            empresa.put("cif", cifText.getText().toString().trim());
                            empresa.put("nombreEmpresa", nombreText.getText().toString().trim());
                            empresa.put("direccion", direccionText.getText().toString().trim());
                            empresa.put("cp", cpText.getText().toString().trim());
                            empresa.put("telefono", telefonoText.getText().toString().trim());
                            empresa.put("email", userEmail);
                            empresa.put("userID", uid);

                            db.collection("registroEmpresa").document(uid).set(empresa)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            if (filePath != null) {
                                                uploadImage(uid);
                                            } else {
                                                goToMainEmpresaFragment();
                                            }
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

    private void uploadImage(String uid) {
        if (filePath != null) {
            StorageReference imageRef = storageRef.child("images/" + uid);
            UploadTask uploadTask = imageRef.putFile(filePath);

            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    goToMainEmpresaFragment();
                } else {
                    Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            goToMainEmpresaFragment();
        }
    }

    private void goToMainEmpresaFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mainEmpresa.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("nombre")
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
        }
    }
}
