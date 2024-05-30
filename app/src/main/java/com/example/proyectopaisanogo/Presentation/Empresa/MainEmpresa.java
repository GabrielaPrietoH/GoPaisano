package com.example.proyectopaisanogo.Presentation.Empresa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Fragmento principal para la visualización de los usuarios de tipo empresa.

 * Este fragmento muestra la información de la empresa usuaria, con algunos
 * contadores sobre su interacción con los clientes y el acceso a otros fragmentos
 * como el calendario
 */
public class MainEmpresa extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

        private DrawerLayout drawerLayout;
        private FirestoreRecyclerAdapter<Empresa, HelperViewHolder> firestoreAdapter;
        private StorageReference storageRef;
        private FirebaseFirestore db;
        private TextView llamadas, direciones, emails;
        private FirebaseAuth mAuth;
        private String userID;

        /**
         * Método que se llama cuando se crea el fragmento.
         */
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
            mAuth = FirebaseAuth.getInstance();
            storageRef = FirebaseStorage.getInstance().getReference();

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                userID = currentUser.getUid();
            }
        }

        /**
         * Método que inicializa la vista del fragmento.
         *
         * @param inflater           El LayoutInflater que se usa para inflar la vista del fragmento.
         * @param container          El ViewGroup padre al que se adjunta la vista del fragmento.
         * @param savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
         * @return La vista inflada del fragmento.
         */
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_main_empresa, container, false);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            RecyclerView recyclerView = v.findViewById(R.id.RvEmpresa);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

            Toolbar toolbar = v.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

            llamadas = v.findViewById(R.id.textLlamadas);
            emails = v.findViewById(R.id.textEmails);
            direciones = v.findViewById(R.id.textDirection);
            db = FirebaseFirestore.getInstance();
            drawerLayout = v.findViewById(R.id.drawerLayout);
            NavigationView navigationView = v.findViewById(R.id.navView);
            navigationView.setNavigationItemSelectedListener(this);

            Query query = firestore.collection("registroEmpresa").whereEqualTo("userID", userID);
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
                }

                @NonNull
                @Override
                public HelperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
                    return new HelperViewHolder(view);
                }
            };

            fetchAndDisplayData(userID);

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
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();

            if (id == R.id.setting) {
                navigateToFragment(SettingEmpresa.class, "Setting");
            } else if (id == R.id.calendar) {
                navigateToFragment(CalendarioEmpresa.class, "Calendario");
            } else if (id == R.id.logout) {
                mAuth.signOut();
                navigateToFragment(LoginEmpresa.class, "Logout");
                Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawers();
            return true;
        }

        /**
         * Método que navega al fragmento especificado
         *
         * @param fragmentClass La clase del fragmento al que se va a navegar.
         * @param tag           La etiqueta para la transacción del fragmento.
         */
        private void navigateToFragment(Class<? extends Fragment> fragmentClass, String tag) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentClass, null)
                    .setReorderingAllowed(true)
                    .addToBackStack(tag)
                    .commit();
        }

        /**
         * Método que configura la barra de herramientas (Toolbar) para el fragmento.
         *
         * @param view La vista raíz del fragmento.
         */
        private void setupToolbar(View view) {
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar,
                    R.string.open, R.string.close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        /**
         * Método que carga una imagen desde Firebase Storage en un ImageView utilizando Glide.
         *
         * @param context   El contexto actual.
         * @param userID    El ID del usuario cuya imagen se va a cargar.
         * @param imageView El ImageView donde se va a cargar la imagen.
         */
        private void loadImage(Context context, String userID, ImageView imageView) {
            StorageReference imageRef = storageRef.child("images/" + userID);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(context)
                    .load(uri)
                    .into(imageView)).addOnFailureListener(exception -> imageView.setVisibility(View.GONE));
        }

        /**
         * Método que Obtiene y muestra los datos de la empresa desde Firestore.
         *
         * @param userID El ID del usuario actual.
         */
        private void fetchAndDisplayData(String userID) {
            db.collection("registroEmpresa").document(userID)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Long contadorLlamadas = document.getLong("contadorLlamadas");
                                Long contadorEmails = document.getLong("contadorEmails");
                                Long contadorDirecciones = document.getLong("contadorDirecciones");

                                llamadas.setText(String.valueOf(contadorLlamadas != null ? contadorLlamadas : 0));
                                emails.setText(String.valueOf(contadorEmails != null ? contadorEmails : 0));
                                direciones.setText(String.valueOf(contadorDirecciones != null ? contadorDirecciones : 0));
                            } else {
                                llamadas.setText("0");
                                emails.setText("0");
                                direciones.setText("0");
                            }
                        } else {
                            llamadas.setText(R.string.error);
                            emails.setText(R.string.error);
                            direciones.setText(R.string.error);
                        }
                    });
        }

}

