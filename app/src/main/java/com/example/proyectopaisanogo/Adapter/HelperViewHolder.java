package com.example.proyectopaisanogo.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.R;

/**
 * Clase tipo ViewHolder para los elementos del RecyclerView que representan a las empresas.
 * Esta clase se encarga de mantener las referencias a las vistas de cada item del RecyclerView
 * para una empresa.
 */
public class HelperViewHolder extends RecyclerView.ViewHolder {
    public TextView nombreEmpresa, cif, cp, direccion, email, telefono;
    public ImageView botonLlamar, botonCorreo, botonDireccion, botonAgenda, imageView;

    /**
     * Constructor del HelperViewHolder.
     *
     * @param %itemview Vista del elemento del RecyclerView.
     */
    public HelperViewHolder(@NonNull View itemView) {
        super(itemView);
        nombreEmpresa = itemView.findViewById(R.id.textnombreEmpresa);
        cif = itemView.findViewById(R.id.textCif);
        cp = itemView.findViewById(R.id.textCP);
        direccion = itemView.findViewById(R.id.textDireccion);
        email = itemView.findViewById(R.id.textEmail);
        telefono = itemView.findViewById(R.id.textTelefono);
        botonLlamar = itemView.findViewById(R.id.botonLLamada);
        botonCorreo = itemView.findViewById(R.id.botonCorreo);
        botonDireccion = itemView.findViewById(R.id.botonLocalizacion);
        botonAgenda = itemView.findViewById(R.id.botonAgenda);
        imageView = itemView.findViewById(R.id.imagen_empresa);
    }
}

