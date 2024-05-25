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

/**
 * Fragmento para el inicio de sesión de empresas.
 *
 * Este fragmento maneja el inicio de sesión de las empresas y permite la autenticación
 * mediante Firebase Authentication. Además, redirige a las empresas registradas a la pantalla
 * principal de la empresa.
 */
public class LoginEmpresa extends Fragment {
    Button loginE, registroE;
    private FirebaseAuth mAuth;
    EditText emailText, passText;

    /**
     * Método que inicializa la vista del fragmento.
     *
     * @param inflater El LayoutInflater que se usa para inflar la vista del fragmento.
     * @param container  El ViewGroup padre al que se adjunta la vista del fragmento.
     * @param savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
     * @return La vista inflada del fragmento.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_empresa, container, false);
        ImageButton botonLogout = rootView.findViewById(R.id.botonLogoutEmpresa);

        mAuth = FirebaseAuth.getInstance();
        emailText = rootView.findViewById(R.id.cajaCorreo);
        passText = rootView.findViewById(R.id.cajaPass);

        loginE = rootView.findViewById(R.id.buttonLoginE);
        registroE = rootView.findViewById(R.id.buttonRegistroE);

        // Configuración del listener para el botón de inicio de sesión
        loginE.setOnClickListener(v -> {
            String email = emailText.getText().toString();
            String password = passText.getText().toString();

            // Validaciones para el Login
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
                                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                                        fragmentManager.beginTransaction()
                                                                .replace(R.id.fragment_container, MainEmpresa.class, null)
                                                                .setReorderingAllowed(true)
                                                                .addToBackStack("nombre")
                                                                .commit();
                                                        Toast.makeText(getContext(), "¡Estás dentro! Explora todas las novedades que tenemos para ti", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getContext(), "¡Acceso permitido solo para empresas!", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(getContext(), "Empresa no registrada", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Toast.makeText(getContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        // Configuración del listener para el botón de registro
        registroE.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RegistroEmpresa.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre")
                    .commit();
        });
        // Configuración del listener para el botón de Logout
        botonLogout.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, FragmentLogin.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre")
                    .commit();
        });
        return rootView;
    }

}