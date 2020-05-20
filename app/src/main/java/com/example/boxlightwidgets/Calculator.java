package com.example.boxlightwidgets;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class Calculator extends Service {

    private WindowManager windowManager;
    private View calculatorView;
    private WindowManager.LayoutParams params;
    private Button closeBtn;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        AddCalculatorView();
        CalculatorController();
    }

    public void AddCalculatorView() {
        int layoutParamsType;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            layoutParamsType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                500,
                500,
                layoutParamsType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.CENTER;
        params.x = 0;
        params.y = 0;

        LinearLayout interceptorLayout = new LinearLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                // Only fire on the ACTION_DOWN event, or you'll get two events (one for _DOWN, one for _UP)
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    // Check if the HOME button is pressed
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                        Log.v("info", "BACK Button Pressed");

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
            calculatorView = inflater.inflate(R.layout.calculator_layout, interceptorLayout);
            calculatorView.setBackgroundResource(R.drawable.frame_layout_background);
            windowManager.addView(calculatorView, params);
            closeBtn = calculatorView.findViewById(R.id.btn_close);
        } else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }
    }

    public void CalculatorController() {
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onDestroy(calculatorView);
                    DataHolder.getInstance().setCalculatorOpened(false);
                }
                return false;
            }
        });

        WidgetController calculatorMove = new WidgetController();
        //calculatorMove.CountdownEditController(calculatorView, windowManager, params);
    }

    public void onDestroy(View view) {

        super.onDestroy();

        if (view != null) {

            windowManager.removeView(view);

            view = null;
        }
    }

}
