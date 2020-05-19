package com.example.boxlightwidgets;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;

public class ProgressBarAnimation extends Animation {

    private ProgressBar progressBar;
    private double from;
    private double to;

    public ProgressBarAnimation(ProgressBar progressBar, double from, double to) {
        super();
        this.progressBar = progressBar;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        double value = from + (to - from) * interpolatedTime;
        progressBar.setProgress((int) value);
    }
}
