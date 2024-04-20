package com.example.proyectopaisanogo;

import android.content.Intent;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registroEmpresa extends Fragment {

    //Registro empresa
    Button registroE;
    private FirebaseAuth mAuth;

    //Add datos en colección
    private FirebaseFirestore db;
    private String idUser, userEmail;

    EditText cifText, nombreText, direccionText, cpText, telefonoText, emailText, passwordText;


    private RegistroEmpresaViewModel mViewModel;

    public static registroEmpresa newInstance() {
        return new registroEmpresa();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registro_empresa, container, false);

        //REGISTRO
        mAuth = FirebaseAuth.getInstance();

        //ADD datos a FirebaseFirestore
        db = FirebaseFirestore.getInstance();
       // idUser = mAuth.getCurrentUser().getUid();
       // userEmail = mAuth.getCurrentUser().getEmail();

        //Referencia de las cajas
        cifText = rootView.findViewById(R.id.editTextCifEmpresa);
        nombreText = rootView.findViewById(R.id.editTextNomEmpresa);
        direccionText = rootView.findViewById(R.id.editTextDireccionEmpresa);
        cpText = rootView.findViewById(R.id.editTextCpEmpresa);
        telefonoText = rootView.findViewById(R.id.editTextTelefonoEmpresa);
        emailText = rootView.findViewById(R.id.editTextEmailEmpresa);
        passwordText = rootView.findViewById(R.id.editTextTextPassword3);



        /*
        registroE = rootView.findViewById(R.id.buttonRegistroEmpresa);
        registroE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Crear un nuevo fragmento y transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mainEmpresa.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("nombre") // El nombre puede ser nulo
                        .commit();
            }

        });
        */

        //Registro empresa
        registroE = rootView.findViewById(R.id.buttonRegistroEmpresa);
        registroE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                //Registro
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    //Tras registro, obtener usuario y email.
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        String uid = user.getUid();
                                        String userEmail = user.getEmail();

                                        // Preparar los datos de la empresa para Firestore
                                        Map<String, Object> empresa = new HashMap<>();
                                        empresa.put("cif", cifText.getText().toString().trim());
                                        empresa.put("nombreEmpresa", nombreText.getText().toString().trim());
                                        empresa.put("direccion", direccionText.getText().toString().trim());
                                        empresa.put("cp", cpText.getText().toString().trim());
                                        empresa.put("telefono", telefonoText.getText().toString().trim());
                                        empresa.put("email", userEmail); // Usar el email del registro
                                        empresa.put("userID", uid);  //user ID del registro



                                        // Agregar información a Firestore con UID como ID del documento
                                        db.collection("registroEmpresa").document(uid).set(empresa)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Datos añadidos correctamente
                                                    // Toast
                                                    // Cambiar a otro fragmento/activity después del registro exitoso
                                                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                    fragmentManager.beginTransaction()
                                                            .replace(R.id.fragment_container, mainEmpresa.class, null)
                                                            .setReorderingAllowed(true)
                                                            .addToBackStack("nombre") // El nombre puede ser nulo
                                                            .commit();
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Manejar el error aquí
                                                    //Toast
                                                });



                                    }

                                } else {
                                        // If sign in fails, display a message to the user.

                                        //Toast fallo auth

                                }
                            }
                        });
            }
        });


        return rootView;



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegistroEmpresaViewModel.class);
        // TODO: Use the ViewModel
    }








}