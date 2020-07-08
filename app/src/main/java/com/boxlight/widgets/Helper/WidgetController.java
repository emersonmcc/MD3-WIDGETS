package com.boxlight.widgets.Helper;

import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.boxlight.widgets.Timer.CountdownMin;

public class WidgetController {

    public int calculatateTime(int hours, int minutes, int seconds) {
        int totalTime = (hours * 3600) + (minutes * 60) + (seconds);
        return totalTime;
    }

    public boolean isTimeValid(int totalTime, Context context) {
        if (totalTime <= 0) {
            Toast errorMessage = Toast.makeText(context, "Countdown time needs to be greater than 0 seconds", Toast.LENGTH_SHORT);
            errorMessage.show();
            return false;
        } else {
            DataHolder.getInstance().setTotalTime(totalTime);
            DataHolder.getInstance().setMasterTotalTime(totalTime); //Keep track of original total time for the progress bar animation on CountdownMax.java
            DataHolder.getInstance().setIsPaused(false);
            DataHolder.getInstance().setOnStart(true);
            return true;
        }
    }

    public void NewService(Class to, Context context) {
        Intent intent = new Intent(context, to);
        context.stopService(intent);
        context.startService(intent);

    }

    public void NewActivity(Class to, Context context) {
        Intent intent = new Intent(context, to);
        context.startActivity(intent);
    }

    public void EnableMovement(final View view, final WindowManager windowManager, final WindowManager.LayoutParams params) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private WindowManager.LayoutParams updatedParameters = params;
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
                        windowManager.updateViewLayout(view, updatedParameters);
                        System.out.println("3");
                        return true;
                }
                return false;
            }
        });

    }
}
