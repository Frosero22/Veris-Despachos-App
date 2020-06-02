package edmt.dev.verisdespachosapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;


public class SplashScreen extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
         img.animate().alpha(4000).setDuration(0);

         handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 Intent dsp = new Intent(SplashScreen.this,Login.class);

                 startActivity(dsp);
                 finish();
             }
         },4000);
    }
























    }
