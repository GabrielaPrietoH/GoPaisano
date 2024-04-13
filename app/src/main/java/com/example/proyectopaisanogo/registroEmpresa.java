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

public class registroEmpresa extends Fragment {
    Button registroE;
    private FirebaseAuth mAuth;
    EditText cifText, nombreText, direccionText, cpText, telefonoText, emailText, passwordText;


    private RegistroEmpresaViewModel mViewModel;

    public static registroEmpresa newInstance() {
        return new registroEmpresa();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registro_empresa, container, false);
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
        return rootView;


        //REGISTRO
        mAuth = FirebaseAuth.getInstance();
        //Creo la referencia de las cajas
        cifText = findViewById(R.id.editTextCifEmpresa);
        nombreText = findViewById(R.id.editTextNomEmpresa);
        direccionText = findViewById(R.id.editTextDireccionEmpresa);
        cpText = findViewById(R.id.editTextCpEmpresa);
        telefonoText = findViewById(R.id.editTextTelefonoEmpresa);
        emailText = findViewById(R.id.editTextEmailEmpresa);
        passwordText = findViewById(R.id.editTextTextPassword3);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegistroEmpresaViewModel.class);
        // TODO: Use the ViewModel
    }


    registroE = findViewById(R.id.buttonRegistroEmpresa");
    registroE.setOnClickListener(new View.OnClickListener() { //Esto se pod´ria hacer con fn lambda.
            @Override
            public void onClick(View v) {

                String cif = cifText.getText().toString();
                String nombreEmpresa = nombreText.getText().toString();
                String direccion = direccionText.getText().toString();
                String cp = cpText.getText().toString();
                String telefono = telefonoText.getText().toString();
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();

                mAuth.createUserEmpresa(cif, nombreEmpresa, direccion, cp, telefono, email, password)
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {


                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information

                                   //TOAST
                                   // Toast.makeText(Login.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    //ntent intent = new Intent(Login.this, MainActivity.class);
                                    //startActivity(intent);
                                } else {
                                    // If sign in fails, display a message to the user.

                                    //Toast.makeText(Login.this, "Authentication failed.",Toast.LENGTH_SHORT).show();

                                }
                            }
                        });




            }
         });

}