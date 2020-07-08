package com.boxlight.widgets.ScreenRecorder;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.boxlight.widgets.R;

public class ScreenRecorderActive extends Service {

    private View recorderOverlay;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;

    private Button pauseBtn;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        AddRecorderOverlay();
    }

    private void AddRecorderOverlay() {
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
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);


        params.gravity = Gravity.BOTTOM | Gravity.START;
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
            recorderOverlay = inflater.inflate(R.layout.screen_recorder_active, interceptorLayout);
            windowManager.addView(recorderOverlay, params);
            pauseBtn = recorderOverlay.findViewById(R.id.btn_close);
        } else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }
    }
}
