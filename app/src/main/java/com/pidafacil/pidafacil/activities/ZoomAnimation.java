package com.pidafacil.pidafacil.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.pidafacil.pidafacil.NavigationDrawer;
import com.pidafacil.pidafacil.R;
import com.pidafacil.pidafacil.fragments.SampleSlide;

/**
 * Created by developer on 4/22/16.
 */
public class ZoomAnimation extends AppIntro{

    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));
        addSlide(SampleSlide.newInstance(R.layout.intro5));
        addSlide(SampleSlide.newInstance(R.layout.intro6));
        addSlide(SampleSlide.newInstance(R.layout.intro7));
        Button skipButton = (Button)findViewById(R.id.skip);
        skipButton.setText("Saltar");
        Button doneButton = (Button)findViewById(R.id.done);
        doneButton.setText("Terminar");

        setZoomAnimation();
    }

    private void loadMainActivity(){
        goToMain();
       // Toast.makeText(getApplicationContext(), "Bienvenid@!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSkipPressed() {
        goToMain();
        //Toast.makeText(getApplicationContext(), "Gracias por preferirnos!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    @Override
    public void onSlideChanged() {

    }

    public void getStarted(View v){
        loadMainActivity();
    }


    private void goToMain() {
        Intent homeIntent = new Intent(getApplicationContext(), NavigationDrawer.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }
}
