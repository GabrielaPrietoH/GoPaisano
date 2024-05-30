package com.example.proyectopaisanogo.Presentation.Cliente;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragmento para el registro de nuevos clientes.
 * Este fragmento permite a los clientes registrarse proporcionando su información personal
 * y creando una cuenta en Firebase Authentication. Los datos del cliente se almacenan en Firestore.
 */
public class RegistroCliente extends Fragment {
    Button registroC;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String role;
    EditText nombreText, direccionText, cpText, telefonoText, emailText, passwordText;


    /**
     * Método que inicializa la vista del fragmento.
     *
     * @param inflater El LayoutInflater que se usa para inflar la vista del fragmento.
     * @param container  El ViewGroup padre al que se adjunta la vista del fragmento.
     * @param savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
     * @return La vista inflada del fragmento.
     */
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registro_cliente, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nombreText = rootView.findViewById(R.id.editTextUsuarioCliente);
        direccionText = rootView.findViewById(editTextDireccionCLiente);
        cpText = rootView.findViewById(R.id.editTextCpCliente);
        telefonoText = rootView.findViewById(R.id.editTextPhoneCliente);
        emailText = rootView.findViewById(R.id.editTextEmailCliente);
        passwordText = rootView.findViewById(R.id.editTextPasswordCliente);

        registroC = rootView.findViewById(R.id.buttonResgistroCliente);
        registroC.setOnClickListener(v -> registerClient());

        role = "cliente";
        setupToolbar(rootView);
        return rootView;
    }

    /**
     * Método para registrar un cliente.
     * Este método realiza las validaciones de los campos y, si son correctos, crea una cuenta
     * en Firebase Authentication y guarda los datos del cliente en Firestore.
     */
    private void registerClient() {
        String nombreCliente = nombreText.getText().toString().trim();
        String direccion = direccionText.getText().toString().trim();
        String cp = cpText.getText().toString().trim();
        String telefono = telefonoText.getText().toString().trim();
        String email = emailText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();

        if (!validateClientInputs(nombreCliente, direccion, cp, telefono, email, password)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            String userEmail = user.getEmail();
                            Map<String, Object> cliente = createClienteMap(nombreCliente, direccion, cp, telefono, userEmail, uid);

                            registerClienteInFirestore(cliente, uid);
                        }
                    } else {
                        handleClientRegistrationFailure(task);
                    }
                });
    }
    /**
     * Método que realiza las validaciones
     */
    private boolean validateClientInputs(String nombreCliente, String direccion, String cp, String telefono, String email, String password) {
        if (nombreCliente.isEmpty()) {
            nombreText.setError("Nombre es obligatorio");
            nombreText.requestFocus();
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            passwordText.setError("La contraseña debe tener al menos 6 caracteres");
            passwordText.requestFocus();
            return false;
        }
        if (telefono.isEmpty() || !telefono.matches("\\d{9}")) {
            telefonoText.setError("Teléfono inválido");
            telefonoText.requestFocus();
            return false;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            emailText.setError("Correo electrónico inválido");
            emailText.requestFocus();
            return false;
        }
        if (direccion.isEmpty()) {
            direccionText.setError("Dirección es obligatoria");
            direccionText.requestFocus();
            return false;
        }
        if (cp.isEmpty() || !cp.matches("\\d{5}")) {
            cpText.setError("Código Postal inválido");
            cpText.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Método que crea un mapper con todos los campos del cliente.
     */
    private Map<String, Object> createClienteMap(String nombreCliente, String direccion, String cp, String telefono, String userEmail, String uid) {
        Map<String, Object> cliente = new HashMap<>();
        cliente.put("nombreCliente", nombreCliente);
        cliente.put("direccion", direccion);
        cliente.put("cp", cp);
        cliente.put("telefono", telefono);
        cliente.put("email", userEmail);
        cliente.put("userID", uid);
        cliente.put("role", role);
        return cliente;
    }

    /**
     * Método que registra un nuevo cliente en Firebase Authentication y Firestore.
     */
    private void registerClienteInFirestore(Map<String, Object> cliente, String uid) {
        db.collection("registroCliente").document(uid).set(cliente)
                .addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Toast.makeText(getContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                        goToMainClienteFragment();
                    } else {
                        Toast.makeText(getContext(), "Error al registrar cliente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Método que gestiona las posibles excepciones
     */
    private void handleClientRegistrationFailure(Task<AuthResult> task) {
        String errorMessage;
        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            errorMessage = "Correo electrónico inválido.";
            emailText.setError(errorMessage);
            emailText.requestFocus();
        } catch (FirebaseAuthUserCollisionException e) {
            errorMessage = "El correo electrónico ya está en uso.";
            emailText.setError(errorMessage);
            emailText.requestFocus();
        } catch (Exception e) {
            errorMessage = "Error al registrar: " + e.getMessage();
        }
        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Método que navega al fragmento principal de empresa.
     */
    private void goToMainClienteFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new MainCliente())
                .setReorderingAllowed(true)
                .addToBackStack("nombre")
                .commit();
    }

    /**
     * Método que configura la barra de herramientas (Toolbar) para el fragmento.
     *
     * @param view La vista raíz del fragmento.
     */
    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarRegistroCliente);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setTitle(" Registro Clientes");
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }
}