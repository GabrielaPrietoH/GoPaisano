package com.example.proyectopaisanogo.Presentation;

import static com.example.proyectopaisanogo.R.color.colorPrimaryDark;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyectopaisanogo.R;

public class Splash extends AppCompatActivity {
    public ProgressBar splash_screenProgressBar;
    public int MAX_VALUE = 30;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.modern_proyect);
         mediaPlayer.start();

        // Establece la orientación vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_splash);

        ImageView txt = (ImageView) findViewById(R.id.imageView2);
        Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim1);
        txt.startAnimation(aniSlide);
        splash_screenProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        splash_screenProgressBar.setMax(MAX_VALUE);

        // Agrega el código para actualizar la barra de progreso aquí
        new CountDownTimer(5000, 100) {
            int progreso = 1;

            @Override
            public void onTick(long millisUntilFinished) {
                splash_screenProgressBar.setProgress(progreso);
                progreso += (1);
            }

            @Override
            public void onFinish() {
                splash_screenProgressBar.setProgress(MAX_VALUE);

                // Inicia la siguiente actividad
                Intent mainIntent = new Intent().setClass(Splash.this, MainActivity.class);
                startActivity(mainIntent);
                // Cierra la actividad para que el usuario no pueda volver a esta actividad presionando el botón Atrás
                finish();
            }

        }.start();


    }
}