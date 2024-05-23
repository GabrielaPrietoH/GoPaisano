package com.example.proyectopaisanogo.Presentation;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginEmpresa extends Fragment {
    Button loginE, registroE;
    private FirebaseAuth mAuth;
    //signUp
    EditText emailText, passText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_empresa, container, false);
        ImageButton botonLogout = rootView.findViewById(R.id.botonLogoutEmpresa);
        //FIREBASE AUTHENTICATOR
        mAuth = FirebaseAuth.getInstance();
        emailText = rootView.findViewById(R.id.cajaCorreo);
        passText = rootView.findViewById(R.id.cajaPass);

        loginE = rootView.findViewById(R.id.buttonLoginE);
        registroE = rootView.findViewById(R.id.buttonRegistroE);

        loginE.setOnClickListener(v -> {


            //Firebase AUTH
            String email = emailText.getText().toString();
            String password = passText.getText().toString();


            //VALIDACIONES-lOGIN
            if (email.isEmpty()) {
                emailText.setError("Campo obligatorio");
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                emailText.setError("Correo incorrecto");

            }else if(password.isEmpty()){
                passText.setError("Campo obligatorio");
            }else if(password.length() < 6){
                passText.setError("Mínimo 6 caracteres");

            }else{

                /*
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {

                                // Crear un nuevo fragmento y transacción
                                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, mainEmpresa.class, null)
                                        .setReorderingAllowed(true)
                                        .addToBackStack("nombre") // El nombre puede ser nulo
                                        .commit();

                            } else {

                                Toast.makeText(getContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
                            }
                        });
                    */

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    // Consulta a Firestore para obtener el documento del usuario
                                    FirebaseFirestore.getInstance().collection("registroEmpresa").document(uid)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful() && task1.getResult() != null) {
                                                    String userRole = task1.getResult().getString("role");
                                                    // Verifica si el rol es 'empresa'
                                                    if ("empresa".equals(userRole)) {
                                                        // Si es empresa, permitir acceso
                                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                        fragmentManager.beginTransaction()
                                                                .replace(R.id.fragment_container, MainEmpresa.class, null)
                                                                .setReorderingAllowed(true)
                                                                .addToBackStack("nombre")
                                                                .commit();
                                                        Toast.makeText(getContext(), "¡Estás dentro! Explora todas las novedades que tenemos para ti", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        // Si no es empresa, mostrar mensaje y no permitir acceso
                                                        Toast.makeText(getContext(), "¡Acceso permitido solo para empresas!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    // Manejo de errores o documento no encontrado
                                                    Toast.makeText(getContext(), "Empresa no registrada", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Mostrar un Toast indicando que las credenciales son incorrectas
                                Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
                            }
                        });


            }



        });

        // Listener para el botón de transición al fragmento de registro
        registroE.setOnClickListener(v -> {
            // Crear un nuevo fragmento y transacción para ir al fragmento de registro
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegistroEmpresa.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();
        });
        botonLogout.setOnClickListener(v -> {

            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FragmentLogin.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();
        });
        return rootView;
    }

}