package com.example.proyectopaisanogo.Presentation;

import static com.example.proyectopaisanogo.R.id.editTextDireccionCLiente;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Patterns;
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
import com.example.proyectopaisanogo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class registroCliente extends Fragment {
    Button registroC;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    EditText nombreText, direccionText, cpText, telefonoText, emailText, passwordText;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registro_cliente, container, false);

        // Inicializar Firebase Auth y Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Referencia de los campos de entrada
        nombreText = rootView.findViewById(R.id.editTextUsuarioCliente);
        direccionText = rootView.findViewById(editTextDireccionCLiente);
        cpText = rootView.findViewById(R.id.editTextCpCliente);
        telefonoText = rootView.findViewById(R.id.editTextPhoneCliente);
        emailText = rootView.findViewById(R.id.editTextEmailCliente);
        passwordText = rootView.findViewById(R.id.editTextPasswordCliente);

        // Botón de registro
        registroC = rootView.findViewById(R.id.buttonResgistroCliente);
        registroC.setOnClickListener(v -> registerClient());

        return rootView;
    }

    // Método para registrar un cliente
    private void registerClient() {
        String nombreCliente = nombreText.getText().toString().trim();
        String direccion = direccionText.getText().toString().trim();
        String cp = cpText.getText().toString().trim();
        String telefono = telefonoText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        // Validaciones
        if (nombreCliente.isEmpty()) {
            nombreText.setError("Nombre es obligatorio");
            nombreText.requestFocus();
            return;
        }
        if (direccion.isEmpty()) {
            direccionText.setError("Dirección es obligatoria");
            direccionText.requestFocus();
            return;
        }
        if (cp.isEmpty() || !cp.matches("\\d{5}")) {
            cpText.setError("Código Postal inválido");
            cpText.requestFocus();
            return;
        }
        if (telefono.isEmpty() || !telefono.matches("\\d{9}")) {
            telefonoText.setError("Teléfono inválido");
            telefonoText.requestFocus();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("Correo electrónico inválido");
            emailText.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordText.setError("La contraseña debe tener al menos 6 caracteres");
            passwordText.requestFocus();
            return;
        }

        // Crear usuario en Firebase Auth
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String userEmail = user.getEmail();
                            Map<String, Object> cliente = new HashMap<>();
                            cliente.put("nombreCliente", nombreCliente);
                            cliente.put("direccion", direccion);
                            cliente.put("cp", cp);
                            cliente.put("telefono", telefono);
                            cliente.put("email", userEmail);
                            cliente.put("userID", uid);

                            db.collection("registroCliente").document(uid).set(cliente)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                            fragmentManager.beginTransaction()
                                                    .replace(R.id.fragment_container, new mainCliente())
                                                    .setReorderingAllowed(true)
                                                    .addToBackStack(null)
                                                    .commit();
                                        } else {
                                            Toast.makeText(getContext(), "Error al registrar cliente", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}