package com.example.proyectopaisanogo.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class HelperAdapter extends FirestoreRecyclerAdapter<Empresa, HelperAdapter.ViewHolder> {


    public HelperAdapter(@NonNull FirestoreRecyclerOptions<Empresa> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Empresa empresa) {
        viewHolder.nombreEmpresa.setText(Empresa.getNombreEmpresa());
        viewHolder.cif.setText(Empresa.getCif());
        viewHolder.cp.setText(Empresa.getCp());
        viewHolder.direccion.setText(Empresa.getDireccion());
        viewHolder.email.setText(Empresa.getEmail());
        viewHolder.telefono.setText(Empresa.getTelefono());
        viewHolder.userID.setText(Empresa.getUserID());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
      return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombreEmpresa;
        TextView cif;
        TextView cp;
        TextView direccion;
        TextView email;
        TextView telefono;
        TextView userID;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nombreEmpresa = itemView.findViewById(R.id.textnombreEmpresa);
            cif = itemView.findViewById(R.id.textCif);
            telefono = itemView.findViewById(R.id.textTelefono);
            direccion = itemView.findViewById(R.id.textDireccion);
            cp = itemView.findViewById(R.id.textCP);
            email = itemView.findViewById(R.id.textEmail);
            userID = itemView.findViewById(R.id.textUserID);
        }
    }
    }

