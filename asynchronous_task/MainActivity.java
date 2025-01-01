package com.example.asynchronous_applicatio;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView bannerText;
    private Button startButton, stopButton;
    private BannerAsyncTask bannerTask;
    private boolean isMoving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bannerText = findViewById(R.id.bannerText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMoving) {
                    isMoving = true;
                    bannerTask = new BannerAsyncTask();
                    bannerTask.execute();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMoving && bannerTask != null) {
                    bannerTask.cancel(true);
                    isMoving = false;
                }
            }
        });
    }

    private class BannerAsyncTask extends AsyncTask<Void, Float, Void> {
        private float currentX;

        @Override
        protected void onPreExecute() {
            currentX = bannerText.getX();
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                currentX -= 5; // Move 5 pixels left

                // Reset position when text moves off screen
                if (currentX < -bannerText.getWidth()) {
                    currentX = getWindow().getDecorView().getWidth();
                }

                publishProgress(currentX);

                try {
                    Thread.sleep(50); // Control speed of movement
                } catch (InterruptedException e) {
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Float... values) {
            bannerText.setX(values[0]);
        }

        @Override
        protected void onCancelled() {
            // Reset position when stopped
            bannerText.setX(20);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerTask != null) {
            bannerTask.cancel(true);
        }
    }
}