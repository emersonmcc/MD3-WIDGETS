package com.boxlight.widgets.Timer;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.boxlight.widgets.Helper.DataHolder;
import com.boxlight.widgets.Helper.Preferences;
import com.boxlight.widgets.R;
import com.boxlight.widgets.Helper.WidgetController;

import java.io.File;

import static java.lang.Integer.getInteger;
import static java.lang.Integer.parseInt;

public class CountdownMin extends Service {

    private static final String TAG = CountdownMinEdit.class.getSimpleName();

    private WindowManager windowManager;

    private View minimisedCountdownView;
    private Button closeBtn;
    private Button fullscreenBtn;
    private Button pauseBtn;
    private Button stopBtn;
    private int totalTime;
    private CountDownTimer countDownTimer;
    private TextView countdownText;
    private WindowManager.LayoutParams params;
    private CountdownLogic countdownLogic = new CountdownLogic();
    private WidgetController countdownMinController = new WidgetController();
    private FrameLayout layout;
    private Boolean soundActive;
    private Boolean customSound;
    private String customSoundFile;
    private MediaPlayer finishAudio;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate() {
        super.onCreate();
        totalTime = DataHolder.getInstance().getTotalTime(); // Set total time

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
         //Add the countdown overlay to the view

        if(!DataHolder.getInstance().getIsPaused()) {
            StartCountdown();
            addCountdownMinimisedView();
        } else {
            pauseBtn.setBackgroundResource(R.drawable.ic_start);
            countdownText.setText(countdownLogic.upDateTimer(totalTime / 1000));
        }
        CheckPreferences();
    }

    public int CalculateTime() {return totalTime = countdownLogic.getTotalTime() * 1000;}

    public void StartCountdown() {
        countDownTimer = new CountDownTimer(DataHolder.getInstance().getTotalTime() * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownText.setText(countdownLogic.upDateTimer((int) millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (soundActive) {
                    finishAudio.start();
                }
                Toast completeMessage = Toast.makeText(getApplicationContext(), "Countdown complete!", Toast.LENGTH_SHORT);
                completeMessage.show();
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

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 270, getResources().getDisplayMetrics());

        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
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
            layout = minimisedCountdownView.findViewById(R.id.frameLayout);
            MinimisedCountdownController();
        }
        else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }
    }

    private void MinimisedCountdownController() {
        final WidgetController countdownMinController = new WidgetController();
        countdownMinController.EnableMovement(minimisedCountdownView, windowManager, params);

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
                countdownMinController.NewService(CountdownMinEdit.class, v.getContext());
                onDestroy(minimisedCountdownView);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                onDestroy(minimisedCountdownView);
            }
        });

        fullscreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                countdownLogic.SaveCountdownProgress(DataHolder.getInstance().getTotalTime());
                countdownMinController.NewActivity(CountdownMax.class, v.getContext());
                onDestroy(minimisedCountdownView);
            }
        });
    }

    public CountDownTimer getCountDownTimer() {return countDownTimer;}

    public void setCountDownTimer(CountDownTimer countDownTimer) {this.countDownTimer = countDownTimer;}
    public void setCountdownText(TextView countdownText) {this.countdownText = countdownText;}

    private void CheckPreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.boxlight.widgets.TIMER_PREFERENCES", Context.MODE_PRIVATE);
        layout.setBackgroundColor(Preferences.GetTimerBackgroundColor(sharedPreferences));
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

    public void onDestroy(View view) {

        super.onDestroy();

        if (view != null) {

            windowManager.removeView(view);

            view = null;
        }
    }
}
