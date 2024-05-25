
package com.example.proyectopaisanogo.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.R;

/**
 * ViewHolder para los días del calendario mediante un RecyclerView.
 *
 * Este ViewHolder maneja la visualización de cada día del mes y gestiona
 * los eventos de clic en los días.
 */
public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    private final CalendarAdapter.OnItemListener onItemListener;

    /**
     * Constructor sobrecargado del ViewHolder del calendario.
     *
     * @param itemView        La vista del elemento del calendario.
     * @param onItemListener  El listener para manejar los eventos de clic en los días.
     */
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }

    /**
     * Método que maneja los clics en los días del calendario.
     *
     * @param view La vista que se ha clicado.
     */
    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(), (String) dayOfMonth.getText());
    }
}

