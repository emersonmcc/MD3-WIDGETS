package com.example.boxlightwidgets.Countdown;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.example.boxlightwidgets.Helper.DataHolder;
import com.example.boxlightwidgets.R;
import com.example.boxlightwidgets.Helper.WidgetController;

public class CountdownMaxEdit extends Activity {

    public static final String INTENT_EXTRA = "com.example.boxlightwidgets.MESSAGE";

    private NumberPicker secondsPicker;
    private NumberPicker minutesPicker;
    private NumberPicker hoursPicker;
    private Button playBtn;
    private Button exitFullScrn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        DataHolder.getInstance().setIsPaused(false);
        setContentView(R.layout.countdown_edit_max);
        InitialiseScreenObjects();
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
    }

}
