package com.example.proyectopaisanogo.Adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class HelperAdapter extends RecyclerView.Adapter<HelperAdapter.ViewHolder> {
    private final List<Empresa> empresas;
    private final Context mContext;
    private final StorageReference storageRef;

    public HelperAdapter(List<Empresa> empresas, Context context) {
        this.empresas = empresas;
        mContext = context;
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Empresa empresa = empresas.get(position);
        holder.nombreEmpresa.setText(empresa.getNombreEmpresa());
        holder.cif.setText(empresa.getCif());
        holder.cp.setText(empresa.getCp());
        holder.direccion.setText(empresa.getDireccion());
        holder.email.setText(empresa.getEmail());
        holder.telefono.setText(empresa.getTelefono());
        holder.userID.setText(empresa.getUserID());

        // Cargar la imagen desde Firebase Storage usando Glide
        loadImage(empresa.getUserID(), holder.imageView);
        // Configurar OnClickListener para el botón de llamada
        holder.botonLlamar.setOnClickListener(v -> {
            String telefono = empresa.getTelefono();
            realizarLlamada(telefono);
        });

        // Configurar OnClickListener para el botón de correo electrónico
        holder.botonCorreo.setOnClickListener(v -> {
            String email = empresa.getEmail();
            enviarCorreo(email);
        });

        // Configurar OnClickListener para el botón de abrir dirección en Google Maps
        holder.botonDireccion.setOnClickListener(v -> {
            String direccion = empresa.getDireccion();
            abrirDireccionEnMapas(direccion);
        });
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreEmpresa, cif, cp, direccion, email, telefono, userID;
        public ImageView botonLlamar, botonCorreo, botonDireccion, imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreEmpresa = itemView.findViewById(R.id.textnombreEmpresa);
            cif = itemView.findViewById(R.id.textCif);
            cp = itemView.findViewById(R.id.textCP);
            direccion = itemView.findViewById(R.id.textDireccion);
            email = itemView.findViewById(R.id.textEmail);
            telefono = itemView.findViewById(R.id.textTelefono);
            userID = itemView.findViewById(R.id.textUserID);
            botonLlamar = itemView.findViewById(R.id.botonLLamada);
            botonCorreo = itemView.findViewById(R.id.botonCorreo);
            botonDireccion = itemView.findViewById(R.id.botonLocalizacion);
            imageView = itemView.findViewById(R.id.imagen_empresa);
        }
    }

    private void loadImage(String userID, ImageView imageView) {
        // Obtener la referencia de la imagen desde Firebase Storage
        StorageReference imageRef = storageRef.child("images/" + userID);

        // Cargar la imagen usando Glide
        Glide.with(mContext)
                .load(imageRef)
                .placeholder(R.drawable.paisano)
                .error(R.drawable.ic_launcher_background)
                .into((ImageView) imageView.findViewById(R.id.imagen_empresa));
    }

    private void realizarLlamada(String telefono) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + telefono));
        mContext.startActivity(intent);
    }

    private void enviarCorreo(String correo) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + correo));
        mContext.startActivity(intent);
    }

    private void abrirDireccionEnMapas(String direccion) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContext.startActivity(mapIntent);
    }
}
