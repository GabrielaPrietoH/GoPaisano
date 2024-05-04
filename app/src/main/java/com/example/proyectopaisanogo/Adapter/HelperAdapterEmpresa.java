package com.example.proyectopaisanogo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HelperAdapterEmpresa extends FirestoreRecyclerAdapter<Empresa, HelperAdapterEmpresa.ViewHolder> {
    private final Context mContext;
    private final StorageReference storageRef;

    public HelperAdapterEmpresa(@NonNull FirestoreRecyclerOptions<Empresa> options, Context context) {
        super(options);
        mContext = context;
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Empresa empresa) {
        holder.nombreEmpresa.setText(empresa.getNombreEmpresa());
        holder.cif.setText(empresa.getCif());
        holder.cp.setText(empresa.getCp());
        holder.direccion.setText(empresa.getDireccion());
        holder.email.setText(empresa.getEmail());
        holder.telefono.setText(empresa.getTelefono());
        holder.userID.setText(empresa.getUserID());

        // Cargar la imagen desde Firebase Storage usando Glide
        loadImage(empresa.getUserID(), holder.imageView);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empresa, parent, false);
        return new HelperAdapterEmpresa.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreEmpresa, cif, cp, direccion, email, telefono, userID;
        public ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreEmpresa = itemView.findViewById(R.id.textnombreEmpresa);
            cif = itemView.findViewById(R.id.textCif);
            cp = itemView.findViewById(R.id.textCP);
            direccion = itemView.findViewById(R.id.textDireccion);
            email = itemView.findViewById(R.id.textEmail);
            telefono = itemView.findViewById(R.id.textTelefono);
            userID = itemView.findViewById(R.id.textUserID);
            imageView = itemView.findViewById(R.id.imagen_empresa_e);
        }
    }

    private void loadImage(String userID, ImageView imageView) {
        if (imageView == null) return; // Verificar si la imageView es nula

        StorageReference imageRef = storageRef.child("images/" + userID);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Glide
            if (mContext != null) {
                Glide.with(mContext)
                        .load(uri)
                        .placeholder(R.drawable.paisano) // Placeholder mientras se carga
                        .error(R.drawable.ic_launcher_background) // Imagen de error si falla la carga
                        .into(imageView);
            }
        }).addOnFailureListener(exception -> {
            // Handle any errors
            // Por ejemplo, puedes mostrar una imagen de error
            if (mContext != null) {
                Glide.with(mContext)
                        .load(R.drawable.img_5)
                        .into(imageView);
            }
        });
    }
}

