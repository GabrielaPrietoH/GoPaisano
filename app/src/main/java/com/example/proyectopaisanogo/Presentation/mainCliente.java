package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class mainCliente extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirestoreRecyclerAdapter<Empresa, HelperViewHolder> firestoreAdapter;
    private StorageReference storageRef;

    private FirebaseAuth mAuth;
    private int contadorCall, contadorEmail, contadorDirection = 0;

    private TextView llamadas, emails, direccion;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        storageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_cliente, container, false);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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

                viewHolder.botonLlamar.setVisibility(View.VISIBLE);
                // Configurar OnClickListener para el botón de llamada
                viewHolder.botonLlamar.setOnClickListener(v -> {
                    String telefono = empresa.getTelefono();
                    realizarLlamada(telefono);
                    incrementarContadorLlamadas();
                });

                viewHolder.botonCorreo.setVisibility(View.VISIBLE);
                // Configurar OnClickListener para el botón de correo electrónico
                viewHolder.botonCorreo.setOnClickListener(v -> {
                    String email = empresa.getEmail();
                    enviarCorreo(email);
                    incrementarContadorEmails();
                });

                viewHolder.botonDireccion.setVisibility(View.VISIBLE);
                // Configurar OnClickListener para el botón de abrir dirección en Google Maps
                viewHolder.botonDireccion.setOnClickListener(v -> {
                    String direccion = empresa.getDireccion();
                    abrirDireccionEnMapas(direccion);
                    incrementarContadorDireccion();
                });

                viewHolder.botonAgenda.setVisibility(View.VISIBLE);
                // Configurar OnClickListener para el botón de abrir dirección en Google Maps
                viewHolder.botonAgenda.setOnClickListener(v -> {
                   // String direccion = empresa.getDireccion();
                   // incrementarContadorDireccion();
                });
            }

            @NonNull
            @Override
            public HelperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
                return new HelperViewHolder(view);
            }

            private void realizarLlamada(String telefono) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telefono));
                requireContext().startActivity(intent);
            }

            private void enviarCorreo(String correo) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + correo));
                requireContext().startActivity(intent);
            }

            private void abrirDireccionEnMapas(String direccion) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                requireContext().startActivity(mapIntent);
            }
        };
        setupToolbar(v);
        recyclerView.setAdapter(firestoreAdapter);
        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreAdapter.stopListening();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.setting) {
            navigateToFragment(settingCliente.class, "Setting");
        } else if (id == R.id.calendar) {
            navigateToFragment(calendarioCliente.class, "Calendario");
        } else if (id == R.id.logout) {
            mAuth.signOut();
            navigateToFragment(loginCliente.class, "Logout");
        }

        mAuth = FirebaseAuth.getInstance();
        drawerLayout.closeDrawers();
        return true;

    }

    private void setupToolbar(View view) {
        Toolbar toolbar;
        toolbar = view.findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void navigateToFragment(Class fragmentClass, String tag) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragmentClass, null)
                .setReorderingAllowed(true)
                .addToBackStack(tag)
                .commit();
    }

    // Método para cargar la imagen desde Firebase Storage usando Glide
    private void loadImage(Context context, String userID, ImageView imageView) {
        StorageReference imageRef = storageRef.child("images/" + userID);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Glide
            Glide.with(context)
                    .load(uri)
                    .into((ImageView) imageView.findViewById(R.id.imagen_empresa));
        }).addOnFailureListener(exception -> imageView.setVisibility(View.GONE));
    }

    private void incrementarContadorLlamadas(){
        contadorCall ++;
        llamadas.setText(String.format("%sClientes", contadorCall));
    }
    private void incrementarContadorEmails(){
        contadorEmail ++;
        emails.setText(String.format("%sClientes", contadorEmail));
    }
    private void incrementarContadorDireccion(){
        contadorDirection ++;
        direccion.setText(String.format("%sClientes", contadorDirection));
    }
}