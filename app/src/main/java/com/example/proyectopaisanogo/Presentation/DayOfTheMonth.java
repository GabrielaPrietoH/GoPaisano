package com.example.proyectopaisanogo.Presentation;
/**
 * Clase que representa un día del mes en el calendario.
 *
 * Contiene información sobre el día, si tiene una cita y el mes al que pertenece.
 */
public class DayOfTheMonth {
    private String day;
    private boolean hasCita;
    private String month;
    /**
     * Constructor sobrecargado para crear una instancia de DayOfTheMonth.
     *
     * @param day     El día del mes.
     * @param hasCita Indica si el día tiene una cita.
     * @param month   El mes al que pertenece el día.
     */
    public DayOfTheMonth(String day, boolean hasCita, String month) {
        this.day = day;
        this.hasCita = hasCita;
        this.month = month;
    }
    /**
     * Método que verifica si el día tiene una cita.
     *
     * @return True si el día tiene una cita, False en caso contrario.
     */
    public boolean hasCita() {
        return hasCita;
    }

    //Getters & Setters
    public void setCita(boolean hasCita) {
        this.hasCita = hasCita;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

}
