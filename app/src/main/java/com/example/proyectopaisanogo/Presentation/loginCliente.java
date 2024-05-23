package com.example.proyectopaisanogo.Presentation;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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

public class loginCliente extends Fragment {

    Button loginC, registroC;

    @SuppressLint("ResourceType")

    //FIREBASE AUTHENTICATOR.
    private FirebaseAuth mAuth;
    //signUp
    EditText emailText, passText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_cliente, container, false);
        ImageButton botonLogout = rootView.findViewById(R.id.botonLogoutCliente);

        //FIREBASE AUTHENTICATOR
        mAuth = FirebaseAuth.getInstance();

        emailText = rootView.findViewById(R.id.cajaCorreo);
        passText = rootView.findViewById(R.id.cajaPass);

        loginC = rootView.findViewById(R.id.buttonLoginC);
        loginC.setOnClickListener(v -> {


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
                                        .replace(R.id.fragment_container, mainCliente.class, null)
                                        .setReorderingAllowed(true)
                                        .addToBackStack("nombre") // El nombre puede ser nulo
                                        .commit();

                            } else {
                                //Un toast para avisar que las credenciales son incorrectas
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.",Toast.LENGTH_SHORT).show();
                            }
                        });

                 */

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    // Consulta a Firestore para obtener el documento del usuario y su rol
                                    FirebaseFirestore.getInstance().collection("registroCliente").document(uid)
                                            .get()
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful() && task1.getResult() != null) {
                                                    String userRole = task1.getResult().getString("role");

                                                    if ("cliente".equals(userRole)) {

                                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                        fragmentManager.beginTransaction()
                                                                .replace(R.id.fragment_container, mainCliente.class, null)
                                                                .setReorderingAllowed(true)
                                                                .addToBackStack("nombre")
                                                                .commit();
                                                        Toast.makeText(getContext(), "¡Estás dentro! Explora todas las novedades que tenemos para ti", Toast.LENGTH_SHORT).show();
                                                    } else {

                                                        Toast.makeText(getContext(), "¡Acceso permitido solo para clientes!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {

                                                    Toast.makeText(getContext(), "Usuario no registrado", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Manejo de error de autenticación
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        registroC = rootView.findViewById(R.id.buttonRegistroC);
        registroC.setOnClickListener(v -> {
            // Crear un nuevo fragmento y transacción para ir al fragmento de registro
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, registroCliente.class, null)
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
            // Mostrar un Toast indicando que se ha cerrado la sesión
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }

}