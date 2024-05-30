package com.example.proyectopaisanogo.Presentation.Empresa;

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

public class LoginEmpresa extends Fragment {
    Button loginE, registroE;
    private FirebaseAuth mAuth;
    EditText emailText, passText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_empresa, container, false);
        ImageButton botonLogout = rootView.findViewById(R.id.botonLogoutEmpresa);

        mAuth = FirebaseAuth.getInstance();
        emailText = rootView.findViewById(R.id.cajaCorreo);
        passText = rootView.findViewById(R.id.cajaPass);

        loginE = rootView.findViewById(R.id.buttonLoginE);
        registroE = rootView.findViewById(R.id.buttonRegistroE);

        loginE.setOnClickListener(v -> {
            if (LoginUtils.validateEmail(emailText) && LoginUtils.validatePassword(passText)) {
                String email = emailText.getText().toString();
                String password = passText.getText().toString();
                LoginUtils.loginUser(email, password, this, mAuth, FirebaseFirestore.getInstance(), "registroEmpresa", MainEmpresa.class, "empresa");
            }
        });

        registroE.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegistroEmpresa.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre")
                    .commit();
        });

        botonLogout.setOnClickListener(v -> LoginUtils.logoutUser(this));

        return rootView;
    }
}
