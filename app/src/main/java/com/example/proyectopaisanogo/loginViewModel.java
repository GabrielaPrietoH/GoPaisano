package com.example.proyectopaisanogo;

import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

public class loginViewModel extends ViewModel {
    @NotNull
    public final Object uiState;

    public loginViewModel(@NotNull Object uiState) {
        this.uiState = uiState;
    }

    @NotNull
    public loginViewModel invoke() {
        return null;
    }
    // TODO: Implement the ViewModel
}