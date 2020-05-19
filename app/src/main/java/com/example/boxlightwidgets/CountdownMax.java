package com.example.boxlightwidgets;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CountdownMax extends Activity {
    private TextView countdownText;
    private ImageView pauseBtn;
    private ImageView stopBtn;
    private ImageView exitFullScrn;
    private CountDownTimer countDownTimer;
    private int totalTime;
    private ProgressBar progressBar;
    private double masterTotalTime;

    private CountdownLogic cntdwn;
    private int prevProgress = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_max);
        cntdwn = new CountdownLogic();

        //Extract time remaining from DataHolder
        totalTime = DataHolder.getInstance().getTotalTime();
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
            MaxCountdownController(); //Set controls
            StartCountdown();
        } else {
            pauseBtn.setImageResource(R.drawable.ic_start);
            System.out.println("b: " + totalTime);
            countdownText.setText(cntdwn.upDateTimer(totalTime / 1000));
            MaxCountdownController(); //Set controls
            System.out.println("a: " + totalTime);
        }
    }

    public void UpdateProgress(int milliseconds) {
        if (prevProgress == 200) {
            prevProgress = 1000;
        }
        masterTotalTime = DataHolder.getInstance().getMasterTotalTime();
        double progress = milliseconds / masterTotalTime * 1000;
        progressBar.setProgress((int) progress);
    }

    public void MaxCountdownController() {
        pauseBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!DataHolder.getInstance().getIsPaused()) {
                        countDownTimer.cancel();
                        pauseBtn.setImageResource(R.drawable.ic_start);
                        DataHolder.getInstance().setIsPaused(true);
                        CalculateTime();
                        DataHolder.getInstance().setTotalTime(totalTime);
                    } else {
                        StartCountdown();
                        pauseBtn.setImageResource(R.drawable.ic_pause);
                        DataHolder.getInstance().setIsPaused(false);
                    }

                }
                return false;
            }
        });

        stopBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    countDownTimer.cancel();
                    Intent intent = new Intent(v.getContext(), CountdownMaxEdit.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        exitFullScrn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    countDownTimer.cancel();
                    CalculateTime();
                    Intent svc = new Intent(v.getContext(), CountdownMin.class);
                    DataHolder.getInstance().setTotalTime(totalTime);
                    stopService(svc);
                    startService(svc);
                    finish();
                }
                return false;
            }
        });
    }

    public void CalculateTime() {
        totalTime = cntdwn.getTotalTime() * 1000;
    }

    public void StartCountdown() {
        countDownTimer = new CountDownTimer(totalTime + 100, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText(cntdwn.upDateTimer((int) millisUntilFinished / 1000));
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
