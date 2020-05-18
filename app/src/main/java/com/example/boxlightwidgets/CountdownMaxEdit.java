package com.example.boxlightwidgets;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

public class CountdownMaxEdit extends Activity {

    public static final String INTENT_EXTRA = "com.example.boxlightwidgets.MESSAGE";

    private NumberPicker secondsPicker;
    private NumberPicker minutesPicker;
    private NumberPicker hoursPicker;
    private ImageView playBtn;
    private ImageView exitFullScrn;
    CountdownLogic cntdwn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataHolder.getInstance().setIsPaused(false);
        setContentView(R.layout.countdown_edit_max);
        InitialiseScreenObjects();
        cntdwn = new CountdownLogic();
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
        CountdownMaxEditController();
    }

    private void CountdownMaxEditController() {
        playBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cntdwn.CalculateTime(hoursPicker.getValue(), minutesPicker.getValue(), secondsPicker.getValue());
                    if (cntdwn.getTotalTime() <= 0) {
                        Toast errorMessage = Toast.makeText(getApplicationContext(), "Countdown time needs to be greater than 0 seconds", Toast.LENGTH_SHORT);
                        errorMessage.show();
                    } else {
                        Intent intent = new Intent(v.getContext(), CountdownMax.class);
                        DataHolder.getInstance().setTotalTime(cntdwn.getTotalTime());
                        DataHolder.getInstance().setIsPaused(false);
                        DataHolder.getInstance().setOnStart(true);
                        startActivity(intent);
                        finish();
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

        exitFullScrn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent svc = new Intent(v.getContext(), Countdown.class);

                    stopService(svc);
                    startService(svc);
                    finish();
                }
                return false;
            }
        });
    }

}
