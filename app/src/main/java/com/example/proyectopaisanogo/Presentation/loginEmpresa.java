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

public class loginEmpresa extends Fragment {
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
                                        .replace(R.id.fragment_container, mainEmpresa.class, null)
                                        .setReorderingAllowed(true)
                                        .addToBackStack("nombre") // El nombre puede ser nulo
                                        .commit();

                            } else {

                                /*
                                Toast.makeText(getContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(loginEmpresa.this , "Authentication failed.",Toast.LENGTH_SHORT).show();
                                 */
                                // Mostrar un Toast indicando que las credenciales son incorrectas
                                Toast.makeText(getContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
                            }
                        });
            }



        });

        // Listener para el botón de transición al fragmento de registro
        registroE.setOnClickListener(v -> {
            // Crear un nuevo fragmento y transacción para ir al fragmento de registro
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, registroEmpresa.class, null)
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
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        return rootView;
    }

}