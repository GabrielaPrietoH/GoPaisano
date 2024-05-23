package com.example.proyectopaisanogo.Presentation;

public class DayOfTheMonth {
    private String day;
    private boolean hasCita;
    private String month;

    public DayOfTheMonth(String day, boolean hasCita, String month) {
        this.day = day;
        this.hasCita = hasCita;
        this.month = month;
    }

    public boolean hasCita() {
        return hasCita;
    }

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

    // Getters and Setters can be added here
}
