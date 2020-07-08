package com.boxlight.widgets.Timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.boxlight.widgets.Helper.DataHolder;
import com.boxlight.widgets.R;
import com.boxlight.widgets.Helper.WidgetController;
import com.boxlight.widgets.Helper.Preferences;

public class CountdownMaxEdit extends FragmentActivity {

    private NumberPicker secondsPicker;
    private NumberPicker minutesPicker;
    private NumberPicker hoursPicker;
    private Button playBtn;
    private Button exitFullScrn;
    private Button settingsBtn;
    private SharedPreferences sharedPreferences;
    private FrameLayout layout;

    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("info"));
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        DataHolder.getInstance().setIsPaused(false);
        setContentView(R.layout.countdown_edit_max);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.boxlight.widgets.TIMER_PREFERENCES", Context.MODE_PRIVATE);
        InitialiseScreenObjects();
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("info"));
        CheckPreferences();
    }

    private void InitialiseScreenObjects() {
        secondsPicker = findViewById(R.id.secondsPicker);
        String[] numbers = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
                "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
                "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"};
        secondsPicker.setMinValue(0);
        secondsPicker.setMaxValue(59);
        secondsPicker.setDisplayedValues(numbers);
        minutesPicker = findViewById(R.id.minutesPicker);
        minutesPicker.setMinValue(0);
        minutesPicker.setMaxValue(59);
        minutesPicker.setDisplayedValues(numbers);
        hoursPicker = findViewById(R.id.hoursPicker);
        hoursPicker.setMinValue(0);
        hoursPicker.setMaxValue(59);
        hoursPicker.setDisplayedValues(numbers);
        playBtn = findViewById(R.id.playBtn);
        exitFullScrn = findViewById(R.id.exitFllscreenBtn);
        settingsBtn = findViewById(R.id.settingsBtn);
        layout = (FrameLayout) findViewById(R.id.max_frameLayout);
        CountdownMaxEditController();
    }

    private void CountdownMaxEditController() {
        final WidgetController countdownMaxEditController = new WidgetController();
        //countdownMaxEditController.CountdownMaxEditController(exitFullScrn, playBtn, hoursPicker, minutesPicker, secondsPicker, CountdownMaxEdit.class, this);

        exitFullScrn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countdownMaxEditController.NewService(CountdownMinEdit.class, v.getContext());
                finish();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalTime = countdownMaxEditController.calculatateTime(hoursPicker.getValue(), minutesPicker.getValue(), secondsPicker.getValue());
                boolean isValid = countdownMaxEditController.isTimeValid(totalTime, v.getContext());
                if (!isValid) {
                    System.out.println("Time not valid --- re-enter value");
                } else if (isValid) {
                    countdownMaxEditController.NewActivity(CountdownMax.class, v.getContext());
                    finish();
                } else {
                    System.out.println("isValid not initialised.");
                }

            }
        });

        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    private void openDialog() {
        TimerSettings timerSettings = new TimerSettings();
        timerSettings.show(getSupportFragmentManager(), "timer settings dialog");
    }


    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            CheckPreferences();
        }
    };

    private void CheckPreferences() {
        System.out.println("Background Color: " + sharedPreferences.getInt("BACKGROUND_COLOR", Color.rgb(0, 94, 86)));


        layout.setBackgroundColor(Preferences.GetTimerBackgroundColor(sharedPreferences));

    }

}
