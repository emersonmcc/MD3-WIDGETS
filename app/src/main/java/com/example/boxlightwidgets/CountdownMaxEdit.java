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


    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        WidgetController countdownMaxEditController = new WidgetController();
        countdownMaxEditController.CountdownMaxEditController(exitFullScrn, playBtn, hoursPicker, minutesPicker, secondsPicker, CountdownMaxEdit.class, this);
    }

    public void youreDone() {
        finish();
        System.out.println("TRYING TO DESTROY");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }

}
