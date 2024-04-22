package com.example.proyectopaisanogo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class settingCliente extends Fragment {



    //Settings
    EditText nombreText, direccionText, cpText, telefonoText, emailText, passwordText;
    Button btnSaveChanges;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    private SettingClienteViewModel mViewModel;

    public static settingCliente newInstance() {
        return new settingCliente();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            mAuth.signOut();
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        }else if(id == R.id.inicio){
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, mainCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        }else if(id == R.id.calendar){
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarioCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //????return inflater.inflate(R.layout.fragment_setting_cliente, container, false);


        View rootView = inflater.inflate(R.layout.fragment_registro_empresa, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Referencia de las cajas
        nombreText = rootView.findViewById(R.id.editTextNomEmpresa);
        direccionText = rootView.findViewById(R.id.editTextDireccionEmpresa);
        cpText = rootView.findViewById(R.id.editTextCpEmpresa);
        telefonoText = rootView.findViewById(R.id.editTextTelefonoEmpresa);
        emailText = rootView.findViewById(R.id.editTextEmailEmpresa);
        passwordText = rootView.findViewById(R.id.editTextTextPassword3);

        btnSaveChanges = rootView.findViewById(R.id.buttonRegistroEmpresa);

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });


        return rootView;






    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(SettingClienteViewModel.class);
        // TODO: Use the ViewModel
    }

}