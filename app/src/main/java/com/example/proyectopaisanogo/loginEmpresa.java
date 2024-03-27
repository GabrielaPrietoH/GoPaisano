package com.example.proyectopaisanogo;

import static android.content.ContentValues.TAG;
import static com.example.proyectopaisanogo.R.id.buttonLoginC;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class loginEmpresa extends Fragment {
    Button loginE;
    TextView registro;
    //FIREBASE AUTHENTICATOR. Declaración de variable.
    private FirebaseAuth mAuth;
    //signUp
    EditText emailText, passText;
    private LoginEmpresaViewModel mViewModel;

    public static loginEmpresa newInstance() {
        return new loginEmpresa();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_empresa, container, false);

        //FIREBASE AUTHENTICATOR
        mAuth = FirebaseAuth.getInstance();
        emailText = rootView.findViewById(R.id.cajaCorreo);
        passText = rootView.findViewById(R.id.cajaPass);

        loginE = rootView.findViewById(R.id.buttonLoginE);
        loginE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


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

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        // Crear un nuevo fragmento y transacción
                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                        fragmentManager.beginTransaction()
                                                .replace(R.id.fragment_container, mainEmpresa.class, null)
                                                .setReorderingAllowed(true)
                                                .addToBackStack("nombre") // El nombre puede ser nulo
                                                .commit();


                                        //Intent intent = new Intent(Login.this, MainActivity.class);

                                        /*
                                        //Toast Comunicación entre activities
                                        //Usa el INtent para pasar la info
                                        EditText nombreUsuario = findViewById(R.id.cajaCorreo);
                                        String cadena = nombreUsuario.getText().toString();
                                        String[] partes = cadena.split("@");
                                        String nombre = partes[0];
                                        intent.putExtra("nombre", nombre);


                                        startActivity(intent);

                                         */

                                    } else {
                                        /*
                                        //Un toast para avisar que las credenciales son incorrectas
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(loginEmpresa.this , "Authentication failed.",Toast.LENGTH_SHORT).show();

                                         */

                                    }
                                }
                            });
                }



            }

        });

        registro = rootView.findViewById(R.id.textViewRegistroE);
        registro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                String email = emailText.getText().toString();
                String password = passText.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {


                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // Crear un nuevo fragmento y transacción
                                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction()
                                            .replace(R.id.fragment_container, registroEmpresa.class, null)
                                            .setReorderingAllowed(true)
                                            .addToBackStack("nombre") // El nombre puede ser nulo
                                            .commit();
                                    /*
                                    Toast.makeText(loginEmpresa.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    //Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);

                                     */
                                } else {
                                    // If sign in fails, display a message to the user.

                                    //Toast.makeText(loginEmpresa.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

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
        mViewModel = new ViewModelProvider(this).get(LoginEmpresaViewModel.class);
        // TODO: Use the ViewModel
    }

}