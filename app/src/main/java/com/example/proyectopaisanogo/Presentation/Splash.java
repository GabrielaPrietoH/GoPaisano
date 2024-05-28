package com.example.proyectopaisanogo.Presentation;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectopaisanogo.R;
/**
 * Actividad de pantalla de presentación (Splash Screen).
 *
 * Esta actividad muestra una pantalla de presentación con una animación, una barra de progreso y
 * reproduce un sonido mientras se prepara la aplicación. Después de unos segundos,
 * se inicia la actividad principal.
 */
public class Splash extends AppCompatActivity {
    public ProgressBar splash_screenProgressBar;
    public int MAX_VALUE = 30;

    /**
     * Método que se llama cuando se crea la actividad.
     *
     * @param savedInstanceState Si la actividad se está recreando a partir de un estado guardado
     *                           anteriormente, este es el estado.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.modern_proyect);
        mediaPlayer.start();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_splash);

        ImageView txt = (ImageView) findViewById(R.id.imageView2);
        Animation aniSlide = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.anim1);
        txt.startAnimation(aniSlide);

        splash_screenProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        splash_screenProgressBar.setMax(MAX_VALUE);

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
                Intent mainIntent = new Intent().setClass(Splash.this, MainActivity.class);
                startActivity(mainIntent);

                finish();
            }

        }.start();


    }
}