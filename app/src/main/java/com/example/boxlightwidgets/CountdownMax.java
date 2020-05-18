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
    private String getCountdownRemaining;
    private TextView countdownText;
    private ImageView pauseBtn;
    private ImageView stopBtn;
    private ImageView exitFullScrn;
    private CountDownTimer countDownTimer;
    private boolean isPlaying = true;
    private int longNum;
    private int totalTime;
    private String hoursText;
    private String minutesText;
    private String secondsText;
    private ProgressBar progressBar;
    private int progress;
    private int masterTotalTime;

    private CountdownLogic cntdwn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_max);
        cntdwn = new CountdownLogic();

        //Extract time remaining from DataHolder
        totalTime = cntdwn.getTotalTime();
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

        if(!DataHolder.getInstance().getIsPaused()) {
            MaxCountdownController(); //Set controls
            StartCountdown();
        } else {
            pauseBtn.setImageResource(R.drawable.ic_start);
            isPlaying = false;
            System.out.println("b: " + totalTime);
            cntdwn.upDateTimer(totalTime / 1000);
            MaxCountdownController(); //Set controls
            System.out.println("a: " + totalTime);
        }
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
                        CalculateTime();
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
        cntdwn.CalculateTime(Integer.parseInt(hoursText), Integer.parseInt(minutesText), Integer.parseInt(secondsText));
        totalTime = cntdwn.getTotalTime();
    }

    public void StartCountdown() {
        countDownTimer = new CountDownTimer(totalTime + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//                masterTotalTime = DataHolder.getInstance().getMasterTotalTime();
//                secondsLeft = (int) millisUntilFinished / 1000;
//                masterTotalTime = masterTotalTime / 1000;
//                float test = secondsLeft / masterTotalTime * 100;
//                int progressT = (int) test;
//
//                //progressBar.setProgress(progressT);
//                System.out.println("P: " + progressT + " S: " + secondsLeft + " MTT: " + masterTotalTime );
//                System.out.println(millisUntilFinished / masterTotalTime * 100);
                countdownText.setText(cntdwn.upDateTimer((int) millisUntilFinished / 1000));
            }
            @Override
            public void onFinish() {
                MediaPlayer finishSound = MediaPlayer.create(getApplicationContext(),R.raw.countdown_finish_sound);
                finishSound.start();
                Toast completeMessage = Toast.makeText(getApplicationContext(), "Countdown complete!", Toast.LENGTH_SHORT);
                completeMessage.show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), CountdownMaxEdit.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
}
