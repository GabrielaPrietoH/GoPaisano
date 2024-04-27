package com.example.proyectopaisanogo.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import java.util.List;

public class HelperAdapter extends RecyclerView.Adapter<HelperAdapter.ViewHolder> {
    private List<Empresa> empresas;

    public HelperAdapter(List<Empresa> empresas) {
        this.empresas = empresas;
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
    }

    @Override
    public int getItemCount() {
        return empresas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreEmpresa;
        public TextView cif;
        public TextView cp;
        public TextView direccion;
        public TextView email;
        public TextView telefono;
        public TextView userID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreEmpresa = itemView.findViewById(R.id.textnombreEmpresa);
            cif = itemView.findViewById(R.id.textCif);
            cp = itemView.findViewById(R.id.textCP);
            direccion = itemView.findViewById(R.id.textDireccion);
            email = itemView.findViewById(R.id.textEmail);
            telefono = itemView.findViewById(R.id.textTelefono);
            userID = itemView.findViewById(R.id.textUserID);
        }
    }
}
