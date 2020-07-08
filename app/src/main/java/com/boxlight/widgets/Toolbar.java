package com.boxlight.widgets;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.boxlight.widgets.Calculator.Calculator;
import com.boxlight.widgets.Timer.CountdownMinEdit;
import com.boxlight.widgets.Helper.DataHolder;
import com.boxlight.widgets.ScreenRecorder.ScreenRecorder;

public class Toolbar extends Service {

    private WindowManager.LayoutParams params;
    private ImageView countdown;
    private ImageView calculator;
    private ImageView close;
    private ImageView screenRecord;
    private View toolbar;
    private WindowManager windowManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        addToolbarView();
        ToolbarController();
    }

    private void addToolbarView() {
        int layoutParamsType;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            layoutParamsType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                layoutParamsType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
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

                        Log.v("toolbar.class", "BACK Button Pressed");

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
            toolbar = inflater.inflate(R.layout.toolbar, interceptorLayout);
            windowManager.addView(toolbar, params);
            countdown = toolbar.findViewById(R.id.count);
            calculator = toolbar.findViewById(R.id.calc);
            close = toolbar.findViewById(R.id.closeBtn);
            screenRecord = toolbar.findViewById(R.id.camera);
        }
        else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }

    }

    private void ToolbarController() {
        close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onDestroy(toolbar);
                }
                return false;
            }
        });

        calculator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!DataHolder.getInstance().getCalculatorOpened()) {
                        Intent svc = new Intent(getApplicationContext(), Calculator.class);
                        stopService(svc);
                        startService(svc);
                        DataHolder.getInstance().setCalculatorOpened(true);
                    }
                }
                return false;
            }
        });

        countdown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(!DataHolder.getInstance().getCountdownOpened() | DataHolder.getInstance().getCountdownOpened()) {
                        Intent svc = new Intent(getApplicationContext(), CountdownMinEdit.class);
                        stopService(svc);
                        startService(svc);
                        DataHolder.getInstance().setCountdownOpened(true);
                    }

                }
                return false;
            }
        });

        screenRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent act = new Intent(getApplicationContext(), ScreenRecorder.class);
                    startActivity(act);
                }
                return false;
            }
        });

        toolbar.setOnTouchListener(new View.OnTouchListener() {
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
                        windowManager.updateViewLayout(toolbar, updatedParameters);
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
