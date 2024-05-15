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
                Toast.makeText(getContext(), "Por favor ingrese su correo electrónico", Toast.LENGTH_SHORT).show();
            }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                emailText.setError("Correo incorrecto");
                Toast.makeText(getContext(), "Por favor ingrese un correo electrónico válido", Toast.LENGTH_SHORT).show();
            }else if(password.isEmpty()){
                passText.setError("Campo obligatorio");
                Toast.makeText(getContext(), "Por favor ingrese su contraseña", Toast.LENGTH_SHORT).show();
            }else if(password.length() < 6){
                passText.setError("Mínimo 6 caracteres");
                Toast.makeText(getContext(), "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();

            }else{

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
                                Toast.makeText(getContext(), "Las credenciales son incorrectas. Por favor intente de nuevo.", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Redirigiendo al registro", Toast.LENGTH_SHORT).show();
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