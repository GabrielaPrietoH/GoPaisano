package com.example.proyectopaisanogo.Adapter;// CalendarAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.Presentation.DayOfTheMonth;
import com.example.proyectopaisanogo.R;
import java.util.ArrayList;

/**
 * Adaptador para el calendario que muestra los días del mes en un RecyclerView.
 *
 * Este adaptador maneja la visualización de los días del mes y destaca los días
 * que tienen citas programadas.
 */
public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {
    private final ArrayList<DayOfTheMonth> daysOfMonth;
    private final OnItemListener onItemListener;

    /**
     * Constructor sobrecargado del adaptador del calendario.
     *
     * @param daysOfMonth    La lista de días del mes.
     * @param onItemListener El listener para manejar los eventos de clic en los días.
     */
    public CalendarAdapter(ArrayList<DayOfTheMonth> daysOfMonth, OnItemListener onItemListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemListener = onItemListener;
    }

    /**
     * Método que crea un nuevo ViewHolder cuando no hay suficientes ViewHolders
     * existentes que puedan ser reutilizados.
     *
     * @param parent   El ViewGroup padre en el que se añadirá la nueva vista.
     * @param viewType El tipo de la vista nueva.
     * @return Un nuevo CalendarViewHolder que contiene la vista inflada.
     */
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        return new CalendarViewHolder(view, onItemListener);
    }

    /**
     * Método que vincula los datos de un día del mes al ViewHolder.
     *
     * @param holder   El ViewHolder que se actualizará con los datos del día.
     * @param position La posición del día en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        String day = daysOfMonth.get(position).getDay();
        boolean hasCita = daysOfMonth.get(position).hasCita();
        holder.dayOfMonth.setText(day);
        if (hasCita) {
            holder.dayOfMonth.setBackgroundResource(R.drawable.circle_background);
        } else {
            holder.dayOfMonth.setBackgroundResource(android.R.color.transparent);
        }
    }

    /**
     * Método que devuelve el número total de días en la lista.
     *
     * @return El número total de días en la lista.
     */
    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    /**
     * Interfaz para manejar los eventos de clic en los días del calendario.
     */
    public interface OnItemListener {
        /**
         * Método que se llama cuando se hace clic en un día del calendario.
         *
         * @param position La posición del día en la lista.
         * @param dayText  El texto del día que se ha clicado.
         */
        void onItemClick(int position, String dayText);
    }

}

