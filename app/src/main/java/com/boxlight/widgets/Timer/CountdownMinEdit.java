package com.boxlight.widgets.Timer;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.boxlight.widgets.Helper.DataHolder;
import com.boxlight.widgets.Helper.Preferences;
import com.boxlight.widgets.R;
import com.boxlight.widgets.Helper.WidgetController;

public class CountdownMinEdit extends Service {

    private static final String TAG = CountdownMinEdit.class.getSimpleName();

    private WindowManager windowManager;

    private View minCountdownEditView;
    private Button closeBtn, fullscreenBtn, playBtn, modeChangeBtn;
    private TextView modeTitle;
    private NumberPicker secondsPicker;
    private NumberPicker minutesPicker;
    private NumberPicker hoursPicker;
    private WindowManager.LayoutParams params;
    private int mode = 0; // 0 = Countdown, 1 = Stop Watch
    private FrameLayout layout;

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
        CountdownEditController();
        CheckPreferences();
    }

    private void addCountdownEditView() {
        int layoutParamsType;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            layoutParamsType = LayoutParams.TYPE_PHONE;
        }

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

        params = new WindowManager.LayoutParams(
                width,
                height,
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
            minCountdownEditView = inflater.inflate(R.layout.countdown_min_edit, interceptorLayout);
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
            modeChangeBtn = minCountdownEditView.findViewById(R.id.modeChange);
            modeTitle = minCountdownEditView.findViewById(R.id.modeTitle);
            layout = minCountdownEditView.findViewById(R.id.minEditFrameLayout);
        }
        else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }
    }

    private void CountdownEditController() {
        final WidgetController countdownEditController = new WidgetController();

        countdownEditController.EnableMovement(minCountdownEditView, windowManager, params);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDestroy(minCountdownEditView);
            }
        });

        fullscreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdownEditController.NewActivity(CountdownMaxEdit.class, v.getContext());
                onDestroy(minCountdownEditView);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalTime = countdownEditController.calculatateTime(hoursPicker.getValue(), minutesPicker.getValue(), secondsPicker.getValue());
                boolean isValid = countdownEditController.isTimeValid(totalTime, getApplicationContext());
                if (mode == 0) {
                    if (!isValid) {
                        System.out.println("Time not valid --- re-enter value");
                    } else if (isValid) {
                        countdownEditController.NewService(CountdownMin.class, getApplicationContext());
                        onDestroy(minCountdownEditView);
                    } else {
                        System.out.println("isValid not initialised.");
                    }
                } else if (mode == 1) {
                    countdownEditController.NewService(Stopwatch.class, getApplicationContext());
                    onDestroy(minCountdownEditView);
                }


            }
        });

        modeChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mode == 0) {
                    //Mode == Countdown
                    modeTitle.setText("Stopwatch");
                    mode = 1;
                } else if (mode == 1) {
                    //Mode == Stop watch
                    modeTitle.setText("Countdown");
                    mode = 0;
                }

            }
        });
    }

    private void CheckPreferences() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.boxlight.widgets.TIMER_PREFERENCES", Context.MODE_PRIVATE);
        layout.setBackgroundColor(Preferences.GetTimerBackgroundColor(sharedPreferences));
    }

    private void onDestroy(View view) {

        if (view != null) {

            windowManager.removeView(view);

            view = null;
        }
    }
}
