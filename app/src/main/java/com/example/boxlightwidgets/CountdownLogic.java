package com.example.boxlightwidgets;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.widget.Toast;

public class CountdownLogic {

    private boolean onStart = false;
    private int totalTime;
    public int getTotalTime() {return totalTime;}
    private int longNum;
    public int getLongNum() {return longNum;}

    private String countdownText;
    public String getCountdownText() {return countdownText;}

    public void CalculateTime(int hours, int minutes, int seconds) {
        totalTime = (hours * 3600000) + (minutes * 60000) + (seconds * 1000);
    }

//    public void CalculateTimeOnStart() {
//        CountdownMaxEdit tt = new CountdownMaxEdit();
//        totalTime = tt.getTotalTime();
//    }

    public String upDateTimer(int secondsLeft) {
        longNum = secondsLeft;
        totalTime = secondsLeft;
        int hours = longNum / 3600;
        longNum = longNum - hours * 3600;
        int minutes = longNum / 60;
        longNum = longNum - minutes * 60;
        int seconds = longNum;
        String secondsString = Integer.toString(seconds);
        String minutesString = Integer.toString(minutes);

        //Check if seconds is <9
        if (secondsString.length() == 1) {
            secondsString = "0" + Integer.toString(seconds);
        }
        //Check if minutes is <9
        if (minutesString.length() == 1) {
            minutesString = "0" + Integer.toString(minutes);
        }

        countdownText = Integer.toString(hours) + ":" + minutesString + ":" + secondsString;
        System.out.println("Still doing stuff..");
        return countdownText;

    }

}
