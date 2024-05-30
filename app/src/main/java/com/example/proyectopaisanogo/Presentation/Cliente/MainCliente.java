package com.example.proyectopaisanogo.Presentation.Cliente;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectopaisanogo.Adapter.HelperViewHolder;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.Presentation.CalendarDialog;
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;

/**
 * Fragmento principal para la visualización de los clientes.
 * Este fragmento muestra la información de las empresas registradas y permite a los clientes
 * interactuar con ellas a través de llamadas, correos electrónicos y direcciones. Además,
 * maneja la navegación a otros fragmentos como configuración y calendario.
 */
public class MainCliente extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirestoreRecyclerAdapter<Empresa, HelperViewHolder> firestoreAdapter;
    private StorageReference storageRef;
    private TextView llamadas, emails, direccion;
    private FirebaseAuth mAuth;
    private  FirebaseFirestore firestore;
    private long contadorCall, contadorEmail, contadorDirection = 0;

    /**
     * Método llamado cuando se crea el fragmento.
     *
     * @param savedInstanceState Si el fragmento se está recreando a partir de un estado guardado
     *                           anteriormente, este es el estado.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Método que inicializa la vista del fragmento.
     *
     * @param inflater El LayoutInflater que se usa para inflar la vista del fragmento.
     * @param container  El ViewGroup padre al que se adjunta la vista del fragmento.
     * @param savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
     * @return La vista inflada del fragmento.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_cliente, container, false);

        firestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = v.findViewById(R.id.RvEmpresas);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        drawerLayout = v.findViewById(R.id.drawerLayout);
        NavigationView navigationView = v.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        View layoutMainEmpresa = inflater.inflate(R.layout.fragment_main_empresa, container, false);
        llamadas = layoutMainEmpresa.findViewById(R.id.textLlamadas);
        emails = layoutMainEmpresa.findViewById(R.id.textEmails);
        direccion = layoutMainEmpresa.findViewById(R.id.textDirection);

        EditText editTextEmpresa = v.findViewById(R.id.editTextEmpresa);
        ImageButton buttonBuscar = v.findViewById(R.id.buttonBuscar);

        buttonBuscar.setOnClickListener(view -> {
            String query = editTextEmpresa.getText().toString();
            buscarEmpresas(query);
        });

        Query query = firestore.collection("registroEmpresa");
        FirestoreRecyclerOptions<Empresa> options = new FirestoreRecyclerOptions.Builder<Empresa>()
                .setQuery(query, Empresa.class)
                .build();

        firestoreAdapter = new FirestoreRecyclerAdapter<Empresa, HelperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HelperViewHolder viewHolder, int i, @NonNull Empresa empresa) {
                viewHolder.nombreEmpresa.setText(empresa.getNombreEmpresa());
                viewHolder.cif.setText(empresa.getCif());
                viewHolder.cp.setText(empresa.getCp());
                viewHolder.direccion.setText(empresa.getDireccion());
                viewHolder.email.setText(empresa.getEmail());
                viewHolder.telefono.setText(empresa.getTelefono());

                loadImage(requireContext(), empresa.getUserID(), viewHolder.imageView);


                obtenerContadoresEmpresa(empresa.getUserID());

                viewHolder.botonLlamar.setVisibility(View.VISIBLE);
                viewHolder.botonLlamar.setOnClickListener(v -> {
                    String telefono = empresa.getTelefono();
                    realizarLlamada(telefono);
                    incrementarContadorLlamadas();
                    actualizarContadorEmpresa(empresa.getUserID(), "contadorLlamadas");
                });

                viewHolder.botonCorreo.setVisibility(View.VISIBLE);
                viewHolder.botonCorreo.setOnClickListener(v -> {
                    String email = empresa.getEmail();
                    enviarCorreo(email);
                    incrementarContadorEmails();
                    actualizarContadorEmpresa(empresa.getUserID(), "contadorEmails");
                });

                viewHolder.botonDireccion.setVisibility(View.VISIBLE);
                viewHolder.botonDireccion.setOnClickListener(v -> {
                    String direccion = empresa.getDireccion();
                    abrirDireccionEnMapas(direccion);
                    incrementarContadorDireccion();
                    actualizarContadorEmpresa(empresa.getUserID(), "contadorDirecciones");
                });

                viewHolder.botonAgenda.setVisibility(View.VISIBLE);
                viewHolder.botonAgenda.setOnClickListener(v -> onEmpresaSelected(empresa));
            }

            @NonNull
            @Override
            public HelperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
                return new HelperViewHolder(view);
            }
        };
        setupToolbar(v);
        recyclerView.setAdapter(firestoreAdapter);
        return v;
    }

    /**
     * Método llamado cuando el fragmento se vuelve visible para el usuario.
     */
    @Override
    public void onStart() {
        super.onStart();
        firestoreAdapter.startListening();
    }

    /**
     * Método llamado cuando el fragmento deja de ser visible para el usuario.
     */
    @Override
    public void onStop() {
        super.onStop();
        firestoreAdapter.stopListening();
    }

    /**
     * Método que maneja la selección de elementos en el NavigationView.
     *
     * @param menuItem El elemento del menú seleccionado.
     * @return True si el elemento del menú seleccionado fue manejado con éxito.
     */
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.setting) {
            navigateToFragment(SettingCliente.class, "Setting");
        } else if (id == R.id.calendar) {
            navigateToFragment(CalendarioCliente.class, "Calendario");
        } else if (id == R.id.logout) {
            mAuth.signOut();
            navigateToFragment(LoginCliente.class, "Logout");
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, LoginCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Logout")
                    .commit();
            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();

        }
        mAuth = FirebaseAuth.getInstance();
        drawerLayout.closeDrawers();
        return true;
    }

    /**
     * Método que configura la barra de herramientas (Toolbar) para el fragmento.
     *
     * @param view La vista raíz del fragmento.
     */
    private void setupToolbar(View view) {
        Toolbar toolbar;
        toolbar = view.findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Método que navega al fragmento especificado.
     *
     * @param fragmentClass La clase del fragmento al que se desea navegar.
     * @param tag           La etiqueta de la transacción del fragmento.
     */
    private void navigateToFragment(Class fragmentClass, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentClass, null)
                .setReorderingAllowed(true)
                .addToBackStack(tag)
                .commit();
    }

    private void buscarEmpresas(String nombreEmpresa) {
        Query query;
        if (nombreEmpresa.isEmpty()) {
            query = firestore.collection("registroEmpresa");
        } else {
            // Hacer la búsqueda insensible a mayúsculas y minúsculas
            query = firestore.collection("registroEmpresa")
                    .orderBy("nombreEmpresaLowerCase")
                    .startAt(nombreEmpresa.toLowerCase())
                    .endAt(nombreEmpresa.toLowerCase() + "\uf8ff");
        }

        FirestoreRecyclerOptions<Empresa> options = new FirestoreRecyclerOptions.Builder<Empresa>()
                .setQuery(query, Empresa.class)
                .build();

        firestoreAdapter.updateOptions(options);
    }



    /**
     * Método que carga la imagen de la empresa desde Firebase Storage.
     *
     * @param context  El contexto de la aplicación.
     * @param userID   El ID de usuario de la empresa.
     * @param imageView El ImageView donde se cargará la imagen.
     */
    private void loadImage(Context context, String userID, ImageView imageView) {
        StorageReference imageRef = storageRef.child("images/" + userID);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context)
                .load(uri)
                .into(imageView)).addOnFailureListener(exception -> imageView.setVisibility(View.GONE));
    }

    /**
     * Método que realiza una llamada al número de teléfono proporcionado.
     *
     * @param telefono El número de teléfono al que se desea llamar.
     */
    private void realizarLlamada(String telefono) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + telefono));
        requireContext().startActivity(intent);
    }

    /**
     * Método que envía un correo electrónico a la dirección proporcionada.
     *
     * @param correo La dirección de correo electrónico a la que se desea enviar un mensaje.
     */
    private void enviarCorreo(String correo) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + correo));
        requireContext().startActivity(intent);
    }

    /**
     * Método que abre la dirección proporcionada en la aplicación de mapas.
     *
     * @param direccion La dirección que se desea abrir en la aplicación de mapas.
     */
    private void abrirDireccionEnMapas(String direccion) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        requireContext().startActivity(mapIntent);
    }

    /**
     * Método que incrementa el contador de llamadas y actualiza la vista.
     */
    private void incrementarContadorLlamadas() {
        contadorCall++;
        llamadas.setText(String.valueOf(contadorCall));
    }

    /**
     * Método que incrementa el contador de correos electrónicos y actualiza la vista.
     */
    private void incrementarContadorEmails() {
        contadorEmail++;
        emails.setText(String.valueOf(contadorEmail));
    }

    /**
     * Método que incrementa el contador de direcciones y actualiza la vista.
     */
    private void incrementarContadorDireccion() {
        contadorDirection++;
        direccion.setText(String.valueOf(contadorDirection));
    }


    /**
     * Método que obtiene el valor del contador correspondiente.
     *
     * @param campoContador El campo del contador cuyo valor se desea obtener.
     * @return El valor del contador correspondiente.
     */
    private long getContador(String campoContador) {
        switch (campoContador) {
            case "contadorLlamadas":
                return contadorCall;
            case "contadorEmails":
                return contadorEmail;
            case "contadorDirecciones":
                return contadorDirection;
            default:
                return 0;
        }
    }

    /**
     * Método que maneja la selección de una empresa y muestra el diálogo de calendario.
     *
     * @param empresa La empresa seleccionada.
     */
    public void onEmpresaSelected(Empresa empresa) {
        Log.d("Debug", "Pasando empresa con ID: " + empresa.getUserID() + " al diálogo.");
        CalendarDialog dialog = new CalendarDialog(empresa);
        dialog.show(getChildFragmentManager(), "CalendarDialog");
    }

    /**
     * Método que obtiene los contadores de la empresa desde Firestore.
     *
     * @param empresaID El ID de la empresa cuyos contadores se desean obtener.
     */
    private void obtenerContadoresEmpresa(String empresaID) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference contadorEmpresaRef = firestore.collection("registroEmpresa").document(empresaID);

        contadorEmpresaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Long contadorLlamadas = documentSnapshot.getLong("contadorLlamadas");
                Long contadorEmails = documentSnapshot.getLong("contadorEmails");
                Long contadorDirecciones = documentSnapshot.getLong("contadorDirecciones");
                Date fechaUltimaActualizacion = documentSnapshot.getDate("fechaUltimaActualizacion");

                // Obtén la fecha actual
                Date fechaActual = new Date();
                Calendar calFechaActual = Calendar.getInstance();
                calFechaActual.setTime(fechaActual);

                Calendar calFechaUltimaActualizacion = Calendar.getInstance();
                if (fechaUltimaActualizacion != null) {
                    calFechaUltimaActualizacion.setTime(fechaUltimaActualizacion);
                }

                // Compara si la fecha actual es diferente a la fecha de la última actualización
                if (calFechaActual.get(Calendar.DAY_OF_YEAR) != calFechaUltimaActualizacion.get(Calendar.DAY_OF_YEAR) ||
                        calFechaActual.get(Calendar.YEAR) != calFechaUltimaActualizacion.get(Calendar.YEAR)) {
                    // Resetea los contadores si ha pasado un día
                    contadorLlamadas = 0L;
                    contadorEmails = 0L;
                    contadorDirecciones = 0L;
                    resetearContadores(empresaID);
                }

                Long finalContadorLlamadas = contadorLlamadas;
                Long finalContadorEmails = contadorEmails;
                Long finalContadorDirecciones = contadorDirecciones;
                requireActivity().runOnUiThread(() -> {
                    llamadas.setText(String.valueOf(finalContadorLlamadas));
                    emails.setText(String.valueOf(finalContadorEmails));
                    direccion.setText(String.valueOf(finalContadorDirecciones));
                });
            }
        }).addOnFailureListener(e -> Log.e("mainCliente", "Error al cargar datos del cliente", e));
    }

    private void resetearContadores(String empresaID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference empresaRef = db.collection("registroEmpresa").document(empresaID);

        empresaRef.update("contadorLlamadas", 0, "contadorEmails", 0, "contadorDirecciones", 0, "fechaUltimaActualizacion", new Date())
                .addOnFailureListener(e -> Log.e("resetContadores", "Error al resetear los contadores", e));
    }

    /**
     * Método que actualiza el contador de interacciones de la empresa en Firestore.
     *
     * @param empresaID    El ID de la empresa cuyo contador se va a actualizar.
     * @param campoContador El campo del contador que se va a actualizar.
     */

    private void actualizarContadorEmpresa(String empresaID, String campoContador) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference empresaRef = db.collection("registroEmpresa").document(empresaID);

        Date fechaActual = new Date();

        empresaRef.update(campoContador, getContador(campoContador), "fechaUltimaActualizacion", fechaActual)
                .addOnFailureListener(e ->Log.e("actualizarContadorEmpresa", "Error al actualizar los contadores", e));
    }


}