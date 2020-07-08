package com.boxlight.widgets.Timer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boxlight.widgets.Helper.DataHolder;
import com.boxlight.widgets.R;
import com.boxlight.widgets.Helper.WidgetController;
import com.boxlight.widgets.Helper.Preferences;

import java.io.File;

public class CountdownMax extends Activity {
    private TextView countdownText;
    private Button pauseBtn;
    private Button stopBtn;
    private Button exitFullScrn;
    private CountDownTimer countDownTimer;
    private int totalTime;
    private ProgressBar progressBar;
    private double masterTotalTime;

    private CountdownLogic countdownLogic = new CountdownLogic();
    private WidgetController maxCountdownController = new WidgetController();
    private int prevProgress = 0;
    private SharedPreferences preferences;
    private View view;
    private Boolean soundActive;
    private Boolean customSound;
    private String customSoundFile;
    private MediaPlayer finishAudio;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_max);

        preferences = getApplicationContext().getSharedPreferences("com.boxlight.widgets.TIMER_PREFERENCES", MODE_PRIVATE);
        //Extract time remaining from DataHolder
        totalTime = DataHolder.getInstance().getTotalTime() * 1000;
        masterTotalTime = DataHolder.getInstance().getMasterTotalTime();
        InitialiseScreenObjects();
        CheckPreferences();
    }

    private void CheckPreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.boxlight.widgets.TIMER_PREFERENCES", Context.MODE_PRIVATE);
        view.setBackgroundColor(Preferences.GetTimerBackgroundColor(sharedPreferences));
        soundActive = Preferences.GetSoundActive(sharedPreferences);
        customSound = Preferences.GetCustomSoundEnabled(sharedPreferences);
        if (customSound) {
            File file = new File("/storage/emulated/0/bluetooth/old-car-engine_daniel_simion.mp3");
            if (file.exists()) {
                finishAudio = MediaPlayer.create(getApplicationContext(), Uri.parse(String.valueOf(file)));
            } else {
                finishAudio = MediaPlayer.create(getApplicationContext(), R.raw.default_timer);
            }
            System.out.println("Custom sound is enabled");
        } else if (!customSound) {
            System.out.println("Custom sound is disabled");
            finishAudio = MediaPlayer.create(getApplicationContext(), R.raw.default_timer);
        }
    }

    public void InitialiseScreenObjects() {
        // Grab reference to buttons and text on activity
        countdownText = findViewById(R.id.maxCountdownText);
        pauseBtn = findViewById(R.id.pauseBtn);
        stopBtn = findViewById(R.id.stopBtn);
        exitFullScrn = findViewById(R.id.exitFllscreenBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(1000);
        view = findViewById(R.id.max_frameLayout);

        if(!DataHolder.getInstance().getIsPaused()) {
            StartCountdown();
            MaxCountdownController();
        } else {
            pauseBtn.setBackgroundResource(R.drawable.ic_start);
            System.out.println("b: " + totalTime);
            countdownText.setText(countdownLogic.upDateTimer(totalTime / 1000));
            MaxCountdownController(); //Set controls
            System.out.println("a: " + totalTime);
        }
    }

    public void UpdateProgress(int milliseconds) {
        if (prevProgress == 200) {
            prevProgress = 1000;
        }
        masterTotalTime = DataHolder.getInstance().getMasterTotalTime();
        double progress = milliseconds / masterTotalTime;
        progressBar.setProgress((int) progress);
        System.out.println(progress);
        System.out.println("Bar P: " + progressBar.getProgress());
    }

    public void MaxCountdownController() {
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                if (!DataHolder.getInstance().getIsPaused()) {
                    pauseBtn.setBackgroundResource(R.drawable.ic_start);
                    DataHolder.getInstance().setIsPaused(true);
                } else if (DataHolder.getInstance().getIsPaused()) {
                    pauseBtn.setBackgroundResource(R.drawable.ic_pause);
                    DataHolder.getInstance().setIsPaused(false);
                    StartCountdown();
                }
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                maxCountdownController.NewActivity(CountdownMaxEdit.class, v.getContext());
                finish();
            }
        });

        exitFullScrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                countdownLogic.SaveCountdownProgress(DataHolder.getInstance().getTotalTime());
                maxCountdownController.NewService(CountdownMin.class, v.getContext());
                finish();
            }
        });
    }

    public void StartCountdown() {
        countDownTimer = new CountDownTimer(DataHolder.getInstance().getTotalTime() * 1000 + 100, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText(countdownLogic.upDateTimer((int) millisUntilFinished / 1000));
                UpdateProgress((int) millisUntilFinished);
            }
            @Override
            public void onFinish() {
                if (progressBar.getProgress() != 0) { progressBar.setProgress(0); }

                if (soundActive) {
                    finishAudio.start();
                }
                Toast completeMessage = Toast.makeText(getApplicationContext(), "Countdown complete!", Toast.LENGTH_SHORT);
                completeMessage.show();
                Intent intent = new Intent(getApplicationContext(), CountdownMaxEdit.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
