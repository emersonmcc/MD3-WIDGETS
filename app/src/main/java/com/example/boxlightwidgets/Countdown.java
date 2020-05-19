package com.example.boxlightwidgets;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

public class Countdown extends Service {

    private static final String TAG = Countdown.class.getSimpleName();
    public static final String INTENT_EXTRA = "com.example.boxlightwidgets.MESSAGE";

    private WindowManager windowManager;

    private View minCountdownEditView;
    private ImageView closeBtn;
    private ImageView fullscreenBtn;
    private ImageView playBtn;
    private NumberPicker secondsPicker;
    private NumberPicker minutesPicker;
    private NumberPicker hoursPicker;
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
        DataHolder.getInstance().setIsPaused(false);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        addCountdownEditView();
        cntdwn = new CountdownLogic();
    }

    private void addCountdownEditView() {
        int layoutParamsType;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            layoutParamsType = LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                500,
                LayoutParams.WRAP_CONTENT,
                layoutParamsType,
                LayoutParams.FLAG_NOT_FOCUSABLE,
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

                        Log.v(TAG, "BACK Button Pressed");

                        // As we've taken action, we'll return true to prevent other apps from consuming the event as well
                        return true;
                    }
                }

                // Otherwise don't intercept the event
                return super.dispatchKeyEvent(event);
            }
        };

        LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        if (inflater != null) {
            minCountdownEditView = inflater.inflate(R.layout.countdown_layout, interceptorLayout);
            windowManager.addView(minCountdownEditView, params);
            closeBtn = minCountdownEditView.findViewById(R.id.closeBtn);
            playBtn = minCountdownEditView.findViewById(R.id.playBtn);
            secondsPicker = minCountdownEditView.findViewById(R.id.secondsPicker);
            String[] numbers = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
                    "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
                        "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
            secondsPicker.setMinValue(0);
            secondsPicker.setMaxValue(59);
            secondsPicker.setDisplayedValues(numbers);
            minutesPicker = minCountdownEditView.findViewById(R.id.minutesPicker);
            minutesPicker.setMinValue(0);
            minutesPicker.setMaxValue(59);
            minutesPicker.setDisplayedValues(numbers);
            hoursPicker = minCountdownEditView.findViewById(R.id.hoursPicker);
            hoursPicker.setMinValue(0);
            hoursPicker.setMaxValue(59);
            hoursPicker.setDisplayedValues(numbers);
            fullscreenBtn = minCountdownEditView.findViewById(R.id.fullscreenBtn);
        }
        else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }
        CountdownEditController();

    }

    private void CountdownEditController() {
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onDestroy(minCountdownEditView);
                }
                return false;
            }
        });

        playBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cntdwn.CalculateTime(hoursPicker.getValue(), minutesPicker.getValue(), secondsPicker.getValue());
                    if (cntdwn.getTotalTime() <= 0) {
                        Toast errorMessage = Toast.makeText(getApplicationContext(), "Countdown time needs to be greater than 0 seconds", Toast.LENGTH_SHORT);
                        errorMessage.show();
                    } else {
                        Intent intent = new Intent(v.getContext(), CountdownMin.class);
                        DataHolder.getInstance().setTotalTime(cntdwn.getTotalTime());
                        DataHolder.getInstance().setMasterTotalTime(cntdwn.getTotalTime()); //Keep track of original total time for the progress bar animation on CountdownMax.java
                        DataHolder.getInstance().setIsPaused(false);
                        DataHolder.getInstance().setOnStart(true);
                        stopService(intent);
                        startService(intent);
                        onDestroy(minCountdownEditView);
                    }
                }
                return false;
            }
        });

        secondsPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        minutesPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        hoursPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fullscreenBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(v.getContext(), CountdownMaxEdit.class);
                    onDestroy(minCountdownEditView);
                    startActivity(intent);
                }
                return false;
            }
        });

        minCountdownEditView.setOnTouchListener(new View.OnTouchListener() {
            private LayoutParams updatedParameters = params;
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
                        windowManager.updateViewLayout(minCountdownEditView, updatedParameters);
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
