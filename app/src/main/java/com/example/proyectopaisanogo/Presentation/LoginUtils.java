package com.example.proyectopaisanogo.Presentation;

import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginUtils {
    public static boolean validateEmail(EditText emailText) {
        String email = emailText.getText().toString();
        if (email.isEmpty()) {
            emailText.setError("Campo obligatorio");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Correo incorrecto");
            return false;
        }
        return true;
    }

    public static boolean validatePassword(EditText passText) {
        String password = passText.getText().toString();
        if (password.isEmpty()) {
            passText.setError("Campo obligatorio");
            return false;
        } else if (password.length() < 6) {
            passText.setError("Mínimo 6 caracteres");
            return false;
        }
        return true;
    }

    public static void loginUser(String email, String password, Fragment fragment, FirebaseAuth mAuth,
                                 FirebaseFirestore db, String collection, Class<?> nextFragment, String role) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            db.collection(collection).document(uid).get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful() && task1.getResult() != null) {
                                            String userRole = task1.getResult().getString("role");
                                            if (role.equals(userRole)) {
                                                FragmentManager fragmentManager = fragment.requireActivity().getSupportFragmentManager();
                                                fragmentManager.beginTransaction()
                                                        .replace(R.id.fragment_container, (Class<? extends Fragment>) nextFragment, null)
                                                        .setReorderingAllowed(true)
                                                        .addToBackStack("nombre")
                                                        .commit();
                                                Toast.makeText(fragment.getContext(), "¡Estás dentro! Explora todas las novedades que tenemos para ti", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(fragment.getContext(), "¡Acceso permitido solo para " + role + "s!", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(fragment.getContext(), role + " no registrado", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(fragment.getContext(), "Error de autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void logoutUser(Fragment fragment) {
        FragmentManager fragmentManager = fragment.requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentLogin.class, null)
                .setReorderingAllowed(true)
                .addToBackStack("nombre")
                .commit();
        Toast.makeText(fragment.getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
    }
}
