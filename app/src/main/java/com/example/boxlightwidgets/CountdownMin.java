package com.example.boxlightwidgets;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;

public class CountdownMin extends Service {

    private static final String TAG = CountdownMinEdit.class.getSimpleName();

    private WindowManager windowManager;

    private View minimisedCountdownView;
    private ImageView closeBtn;
    private ImageView fullscreenBtn;
    private ImageView pauseBtn;
    private ImageView stopBtn;
    private int totalTime;
    private CountDownTimer countDownTimer;
    private TextView countdownText;
    private WindowManager.LayoutParams params;

    private CountdownLogic cntdwn;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        cntdwn = new CountdownLogic();
        totalTime = DataHolder.getInstance().getTotalTime(); // Set total time

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        addCountdownMinimisedView(); //Add the countdown overlay to the view

        if(!DataHolder.getInstance().getIsPaused()) {
            StartCountdown();
        } else {
            pauseBtn.setImageResource(R.drawable.ic_start);
            countdownText.setText(cntdwn.upDateTimer(totalTime / 1000));
        }
    }

    public void CalculateTime() {totalTime = cntdwn.getTotalTime() * 1000;}

    public void StartCountdown() {

        countDownTimer = new CountDownTimer(totalTime + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
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
                Intent intent = new Intent(getApplicationContext(), CountdownMinEdit.class);
                stopService(intent);
                startService(intent);
                onDestroy(minimisedCountdownView);
            }
        }.start();
    }

    private void addCountdownMinimisedView() {

        int layoutParamsType;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            layoutParamsType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParamsType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        FrameLayout interceptorLayout = new FrameLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    // Check if the HOME button is pressed
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        return true;
                    }
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event);
            }
        };

        LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        if (inflater != null) {
            minimisedCountdownView = inflater.inflate(R.layout.countdown_minimised, interceptorLayout);
            windowManager.addView(minimisedCountdownView, params);
            countdownText = minimisedCountdownView.findViewById(R.id.countdownText);
            closeBtn = minimisedCountdownView.findViewById(R.id.closeBtn);
            pauseBtn = minimisedCountdownView.findViewById(R.id.pauseBtn);
            stopBtn = minimisedCountdownView.findViewById(R.id.stopBtn);
            fullscreenBtn = minimisedCountdownView.findViewById(R.id.fullscreenBtn);
            MinimisedCountdownController();
        }
        else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }
    }

    private void MinimisedCountdownController() {
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onDestroy(minimisedCountdownView);
                }
                return false;
            }
        });

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
                    Intent intent = new Intent(getApplicationContext(), CountdownMinEdit.class);
                    stopService(intent);
                    startService(intent);
                    onDestroy(minimisedCountdownView);
                }
                return false;
            }
        });

        fullscreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CountdownMax.class);
                countDownTimer.cancel();
                CalculateTime();
                System.out.println("before: " + totalTime);
                DataHolder.getInstance().setTotalTime(totalTime);
                startActivity(intent);
                onDestroy(minimisedCountdownView);
            }
        });

        minimisedCountdownView.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updatedParameters = params;
            int x, y;
            float touchX, touchY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    //When detects one finger, mode is set to none for basic movement.
                    case MotionEvent.ACTION_DOWN:
                        x = updatedParameters.x;
                        y = updatedParameters.y;
                        touchX = event.getRawX();
                        touchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        updatedParameters.x = (int) (x + (event.getRawX() - touchX));
                        updatedParameters.y = (int) (y + (event.getRawY() - touchY));
                        windowManager.updateViewLayout(minimisedCountdownView, updatedParameters);
                        System.out.println("3");
                        return true;
                }
                return false;
            }
        });
    }

    public void onDestroy(View view) {

        super.onDestroy();

        if (view != null) {

            windowManager.removeView(view);

            view = null;
        }
    }
}
