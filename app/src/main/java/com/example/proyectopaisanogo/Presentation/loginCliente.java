package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectopaisanogo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginCliente extends Fragment {

    Button loginC, registroC;
    TextView registro;
    private LoginClienteViewModel mViewModel;

    public static loginCliente newInstance() {
        return new loginCliente();
    }

    @SuppressLint("ResourceType")

    //FIREBASE AUTHENTICATOR.
    private FirebaseAuth mAuth;
    //signUp
    EditText emailText, passText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_cliente, container, false);

        //FIREBASE AUTHENTICATOR
        mAuth = FirebaseAuth.getInstance();

        emailText = rootView.findViewById(R.id.cajaCorreo);
        passText = rootView.findViewById(R.id.cajaPass);


        loginC = rootView.findViewById(R.id.buttonLoginC);
        loginC.setOnClickListener(new View.OnClickListener() {

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
                                                .replace(R.id.fragment_container, mainCliente.class, null)
                                                .setReorderingAllowed(true)
                                                .addToBackStack("nombre") // El nombre puede ser nulo
                                                .commit();
                                    /*
                                    //Toast Comunicación entre activities
                                        //Usa el INtent para pasar la info
                                        EditText nombreUsuario = rootView.findViewById(R.id.cajaCorreo);
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
                                        Toast.makeText(loginCliente.this , "Authentication failed.",Toast.LENGTH_SHORT).show();

                                         */

                                    }
                                }
                            });
                }
            }

        });

        registroC = rootView.findViewById(R.id.buttonRegistroC);
        registroC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un nuevo fragmento y transacción para ir al fragmento de registro
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, registroCliente.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("nombre") // El nombre puede ser nulo
                        .commit();
            }
        });










        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginClienteViewModel.class);
        // TODO: Use the ViewModel
    }

}