package com.example.crm;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Initialize the ImageView for the app icon
        ImageView appIcon = findViewById(R.id.appIcon);
        TextView crmSplash = findViewById(R.id.crmSplash);

        // Load the fade-in animation for the image
        Animation fadeInImage = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        appIcon.startAnimation(fadeInImage);

        // Load the fade-in animation for the text
        Animation fadeInText = AnimationUtils.loadAnimation(this, R.anim.fade_in_text);

        // Set a listener to make the TextView visible after the image animation ends
        fadeInImage.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Do nothing
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                crmSplash.setVisibility(TextView.VISIBLE);
                crmSplash.startAnimation(fadeInText); // Start the text animation after image animation ends
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Do nothing
            }
        });

        // Use a handler to delay the transition to the main activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the main activity
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close the splash screen activity so it can't be returned to
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
