package com.example.boxlightwidgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

public class WidgetController {

    private View view;
    private WindowManager windowManager;
    private CountdownMaxEdit countdownMaxEdit = new CountdownMaxEdit();

    private WindowManager.LayoutParams params;

    public void CountdownMinEditController(ImageView closeBtn, ImageView fullscreenBtn, ImageView playBtn, NumberPicker hours, NumberPicker minutes, NumberPicker seconds,
                                           View view, WindowManager windowManager, WindowManager.LayoutParams params, Class from, Context context) {
        this.view = view;
        this. windowManager = windowManager;
        EnableMovement(view, windowManager, params);
        MinGeneral(closeBtn, fullscreenBtn, view, windowManager, from);
        NumberPickers(hours, minutes, seconds);
        StartCountdown(playBtn, hours, minutes, seconds, from, context);

    }

    public void CountdownMaxEditController(ImageView minimise, ImageView playBtn, NumberPicker hours, NumberPicker minutes, NumberPicker seconds,
                                           Class from, Context context) {
        NumberPickers(hours, minutes, seconds);
        StartCountdown(playBtn, hours, minutes, seconds, from, context);
        MaxGeneral(minimise, from, context);

    }

    public void CountdownMinController(ImageView pause, ImageView stop, ImageView fullscreen, ImageView close, Class from, Context context) {
                MediaControlls(pause, stop);
    }

    private void MediaControlls(ImageView pause, ImageView stop) {

    }

    private void StartCountdown(ImageView playBtn, final NumberPicker hour, final NumberPicker minutes, final NumberPicker seconds, final Class from,
                                final Context context) {
        final CountdownLogic cntdwn = new CountdownLogic();
        playBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    cntdwn.CalculateTime(hour.getValue(), minutes.getValue(), seconds.getValue());
                    if (cntdwn.getTotalTime() <= 0) {
                        Toast errorMessage = Toast.makeText(context, "Countdown time needs to be greater than 0 seconds", Toast.LENGTH_SHORT);
                        errorMessage.show();
                    } else {
                        Intent intent = new Intent(v.getContext(), CountdownMin.class);
                        DataHolder.getInstance().setTotalTime(cntdwn.getTotalTime());
                        DataHolder.getInstance().setMasterTotalTime(cntdwn.getTotalTime()); //Keep track of original total time for the progress bar animation on CountdownMax.java
                        DataHolder.getInstance().setIsPaused(false);
                        DataHolder.getInstance().setOnStart(true);
                        if(from == CountdownMinEdit.class) {
                            NewService(CountdownMin.class, v.getContext());
                            onDestroy(view, windowManager);
                        } else if(from == CountdownMaxEdit.class) {
                            NewActivity(CountdownMax.class, v.getContext());
                            countdownMaxEdit.youreDone();
                        }
                    }
                }
                return false;
            }
        });
    }

    private void NewService(Class to, Context context) {
        Intent intent = new Intent(context, to);
        context.stopService(intent);
        context.startService(intent);

    }

    private void NewActivity(Class to, Context context) {
        Intent intent = new Intent(context, to);
        context.startActivity(intent);
    }

    private void MaxGeneral(ImageView minimise, final Class from, final Context context) {
        minimise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println(from.toString());
                    if (from == CountdownMaxEdit.class) {
                        System.out.println("OK, in right if statement");
                        NewService(CountdownMinEdit.class, v.getContext());
                        System.out.println(context.toString());
                        ((Activity)context).finish();

                    }
                }
                return false;
            }
        });
    }

    private void MinGeneral(ImageView closeBtn, ImageView fullscreenBtn, final View view, final WindowManager windowManager, final Class from) {
        closeBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onDestroy(view, windowManager);
                    DataHolder.getInstance().setCountdownOpened(false);
                }
                return false;
            }
        });

        fullscreenBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(v.getContext(), CountdownMaxEdit.class);
                    onDestroy(view, windowManager);
                    if (from == CountdownMinEdit.class) {
                        NewActivity(CountdownMaxEdit.class, v.getContext());
                    } else if (from == CountdownMin.class) {
                        NewActivity(CountdownMax.class, v.getContext());
                    }
                }
                return false;
            }
        });
    }

    private void NumberPickers(NumberPicker hours, NumberPicker minutes, NumberPicker seconds) {
        hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        minutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        seconds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }



    private void EnableMovement(final View view, final WindowManager windowManager, final WindowManager.LayoutParams params) {
        this.params = params;
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

    private void onDestroy(View view, WindowManager windowManager) {

        if (view != null) {

            windowManager.removeView(view);

            view = null;
        }
    }


}
