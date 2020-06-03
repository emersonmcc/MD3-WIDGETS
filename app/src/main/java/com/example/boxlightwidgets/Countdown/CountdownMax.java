package com.example.boxlightwidgets.Countdown;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boxlightwidgets.Helper.DataHolder;
import com.example.boxlightwidgets.R;
import com.example.boxlightwidgets.Helper.WidgetController;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_max);

        //Extract time remaining from DataHolder
        totalTime = DataHolder.getInstance().getTotalTime() * 1000;
        masterTotalTime = DataHolder.getInstance().getMasterTotalTime();
        InitialiseScreenObjects();
    }

    public void InitialiseScreenObjects() {
        // Grab reference to buttons and text on activity
        countdownText = findViewById(R.id.maxCountdownText);
        pauseBtn = findViewById(R.id.pauseBtn);
        stopBtn = findViewById(R.id.stopBtn);
        exitFullScrn = findViewById(R.id.exitFllscreenBtn);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(1000);

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
                if (progressBar.getProgress() != 0) {
                    progressBar.setProgress(0);
                }
                MediaPlayer finishSound = MediaPlayer.create(getApplicationContext(),R.raw.countdown_finish_sound);
                finishSound.start();
                Toast completeMessage = Toast.makeText(getApplicationContext(), "Countdown complete!", Toast.LENGTH_SHORT);
                completeMessage.show();
                Intent intent = new Intent(getApplicationContext(), CountdownMaxEdit.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
