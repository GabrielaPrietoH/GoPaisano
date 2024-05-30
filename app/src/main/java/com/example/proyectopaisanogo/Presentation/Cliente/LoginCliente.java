package com.example.proyectopaisanogo.Presentation.Cliente;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.Presentation.LoginUtils;
import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginCliente extends Fragment {

    Button loginC, registroC;
    @SuppressLint("ResourceType")

    private FirebaseAuth mAuth;
    EditText emailText, passText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_cliente, container, false);
        ImageButton botonLogout = rootView.findViewById(R.id.botonLogoutCliente);

        mAuth = FirebaseAuth.getInstance();

        emailText = rootView.findViewById(R.id.cajaCorreo);
        passText = rootView.findViewById(R.id.cajaPass);

        loginC = rootView.findViewById(R.id.buttonLoginCliente);
        loginC.setOnClickListener(v -> {
            if (LoginUtils.validateEmail(emailText) && LoginUtils.validatePassword(passText)) {
                String email = emailText.getText().toString();
                String password = passText.getText().toString();
                LoginUtils.loginUser(email, password, this, mAuth, FirebaseFirestore.getInstance(), "registroCliente", MainCliente.class, "cliente");
            }
        });

        registroC = rootView.findViewById(R.id.buttonRegistroCliente);
        registroC.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegistroCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre")
                    .commit();
        });

        botonLogout.setOnClickListener(v -> LoginUtils.logoutUser(this));

        return rootView;
    }
}
