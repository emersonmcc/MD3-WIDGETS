package com.boxlight.widgets.Helper;

public class DataHolder {
    private int totalTime;
    private boolean isPaused = false;
    private int masterTotalTime;
    private boolean calculatorOpened = false;
    private boolean countdownOpened = false;
    public boolean getCalculatorOpened() {return calculatorOpened;}
    public void setCalculatorOpened(boolean calculatorOpened) {this.calculatorOpened = calculatorOpened;}
    public boolean getCountdownOpened() {return countdownOpened;}
    public void setCountdownOpened(boolean countdownOpened) {this.countdownOpened = countdownOpened;}
    public int getTotalTime() {return totalTime;}
    public void setTotalTime(int totalTime) {this.totalTime = totalTime;}
    public boolean getIsPaused() {return isPaused;}
    public void setIsPaused(boolean isPaused) {this.isPaused = isPaused;}
    public int getMasterTotalTime() {return masterTotalTime;}
    public void setMasterTotalTime(int masterTotalTime) {this.masterTotalTime = masterTotalTime;}
    private boolean onStart;
    public boolean getOnStart() {return onStart;}
    public void setOnStart(boolean onStart) {this.onStart = onStart;}

    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
