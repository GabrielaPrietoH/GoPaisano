package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class settingCliente extends Fragment implements NavigationView.OnNavigationItemSelectedListener {


    //Settings
    EditText nombreText, direccionText, cpText, telefonoText, emailText, passwordText;
    Button btnSaveChanges;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting_cliente, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Referencia de las cajas
        nombreText = rootView.findViewById(R.id.editTextUsuarioCliente);
        direccionText = rootView.findViewById(R.id.editTextDireccionCLiente);
        cpText = rootView.findViewById(R.id.editTextCpCliente);
        telefonoText = rootView.findViewById(R.id.editTextPhoneCliente);
        emailText = rootView.findViewById(R.id.editTextEmailCliente);
        passwordText = rootView.findViewById(R.id.editTextPasswordCliente);


        btnSaveChanges = rootView.findViewById(R.id.buttonResgistroCliente);

        btnSaveChanges.setOnClickListener(v -> {
            // Recoger los valores de las cajas de texto
            String nombreCliente = nombreText.getText().toString().trim();
            String direccion = direccionText.getText().toString().trim();
            String cp = cpText.getText().toString().trim();
            String telefono = telefonoText.getText().toString().trim();
            String email = emailText.getText().toString().trim();
            String newPassword = passwordText.getText().toString().trim();

            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {

                // Actualizar los datos en Firestore
                DocumentReference docRef = db.collection("registroCliente").document(user.getUid());
                Map<String, Object> updates = new HashMap<>();
                updates.put("nombreCliente", nombreCliente);
                updates.put("direccion", direccion);
                updates.put("cp", cp);
                updates.put("telefono", telefono);
                updates.put("email", email);

                docRef.update(updates)
                        .addOnSuccessListener(aVoid -> {
                            // Toast
                        })
                        .addOnFailureListener(e -> {
                            //Toast
                        });

                // Verificar si la contraseña fue ingresada para cambiarla
                if (!newPassword.isEmpty()) {
                    // Cambiar la contraseña del usuario
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast
                            } else {
                                // Si el cambio de contraseña falla, podría ser necesario reautenticar al usuario
                                //Toast
                            }
                        }
                    });
                }

            } else {
                // El usuario no está autenticado o ha ocurrido un error
                //Toast
            }
        });
        setupToolbar(rootView);
        return rootView;

    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Setting Cliente");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Manejar el clic en el ícono de retroceso en la barra de herramientas
        if (menuItem.getItemId() == android.R.id.home) {
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mainCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}