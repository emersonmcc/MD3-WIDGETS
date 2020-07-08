package com.boxlight.widgets.Calculator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import com.boxlight.widgets.Helper.DataHolder;
import com.boxlight.widgets.R;
import com.boxlight.widgets.Helper.WidgetController;

public class Calculator extends Service {


    private WindowManager windowManager;
    private View calculatorView;
    private WindowManager.LayoutParams params;
    private Button closeBtn, btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnEquals, btnPlus, btnSquareRoot, btnSin, btnPower, btnClear,
            btnDivide, btnMultiply, btnMinus, btnDecimal, btnBracketOpen, btnBracketClose, btnBackspace, btnLog, btnCos, btnTan;
    private TextView formulaInput, result;
    private Expression expression;
    private String stringNumber, stringSpecial;
    private double value=0;
    private boolean numberClicked = false;
    private int charBracketOpenCount=0, charBracketCloseCount=0, charInExceed=0, dotCount=0;
    private char bracketOpen = '(', bracketClose = ')';


    private final View.OnKeyListener formulaOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_ENTER:
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                    }
                    return true;
            }
            return false;
        }
    };


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

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getResources().getDisplayMetrics());
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

        params = new WindowManager.LayoutParams(
                width,
                height,
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
            windowManager.addView(calculatorView, params);
            closeBtn = calculatorView.findViewById(R.id.btn_close);
            btn0 = calculatorView.findViewById(R.id.btn_0);
            btn1 = calculatorView.findViewById(R.id.btn_1);
            btn2 = calculatorView.findViewById(R.id.btn_2);
            btn3 = calculatorView.findViewById(R.id.btn_3);
            btn4 = calculatorView.findViewById(R.id.btn_4);
            btn5 = calculatorView.findViewById(R.id.btn_5);
            btn6 = calculatorView.findViewById(R.id.btn_6);
            btn7 = calculatorView.findViewById(R.id.btn_7);
            btn8 = calculatorView.findViewById(R.id.btn_8);
            btn9 = calculatorView.findViewById(R.id.btn_9);
            btnEquals = calculatorView.findViewById(R.id.btn_equals);
            btnPlus = calculatorView.findViewById(R.id.btn_plus);
            formulaInput = calculatorView.findViewById(R.id.formula);
            result = calculatorView.findViewById(R.id.result);
            btnSquareRoot = calculatorView.findViewById(R.id.btn_root);
            btnPower = calculatorView.findViewById(R.id.btn_power);
            btnClear = calculatorView.findViewById(R.id.btn_clear);
            btnDivide = calculatorView.findViewById(R.id.btn_divide);
            btnMultiply = calculatorView.findViewById(R.id.btn_multiply);
            btnMinus = calculatorView.findViewById(R.id.btn_minus);
            btnDecimal = calculatorView.findViewById(R.id.btn_decimal);
            btnBracketOpen = calculatorView.findViewById(R.id.btn_bracket_open);
            btnBracketClose = calculatorView.findViewById(R.id.btn_bracket_close);
            btnBackspace = calculatorView.findViewById(R.id.btn_backspace);
        } else {
            Log.e("SAW-example", "Layout Inflater Service is null; can't inflate and display R.layout.floating_view");
        }
    }

    public void CalculatorController() {
        WidgetController calculatorMove = new WidgetController();
        calculatorMove.EnableMovement(calculatorView, windowManager, params);
    }

    public void ClickButtonClose(View v) {
        onDestroy(calculatorView);
        DataHolder.getInstance().setCalculatorOpened(false);
    }

    public void ClickButton0(View v) {
        formulaInput.append("0");
        result.append("0");
        numberClicked = true;
    }

    public void ClickButton1(View v) {
        formulaInput.append("1");
        result.append("1");
        numberClicked = true;
    }

    public void ClickButton2(View v) {
        formulaInput.append("2");
        result.append("2");
        numberClicked = true;
    }

    public void ClickButton3(View v) {
        formulaInput.append("3");
        result.append("3");
        numberClicked = true;
    }

    public void ClickButton4(View v) {
        formulaInput.append("4");
        result.append("4");
        numberClicked = true;
    }

    public void ClickButton5(View v) {
        formulaInput.append("5");
        result.append("5");
        numberClicked = true;
    }

    public void ClickButton6(View v) {
        formulaInput.append("6");
        result.append("6");
        numberClicked = true;
    }

    public void ClickButton7(View v) {
        formulaInput.append("7");
        result.append("7");
        numberClicked = true;
    }

    public void ClickButton8(View v) {
        formulaInput.append("8");
        result.append("8");
        numberClicked = true;
    }

    public void ClickButton9(View v) {
        formulaInput.append("9");
        result.append("9");
        numberClicked = true;
    }

    public void ClickButtonPlus(View v) {
        checkNumberDisplay();
        stringSpecial = formulaInput.getText().toString();
        if(stringSpecial.isEmpty()) {

        } else if(stringSpecial.charAt(stringSpecial.length()-1)=='+' || stringSpecial.charAt(stringSpecial.length()-1)=='-' || stringSpecial.charAt(stringSpecial.length()-1)=='*' || stringSpecial.charAt(stringSpecial.length()-1)=='/') {

        } else {
            formulaInput.append("+");
            result.setText(null);
            numberClicked=false;
            charBracketCloseCount=0;
            charBracketCloseCount=0;
            charBracketOpenCount=0;
            dotCount=0;
        }
    }

    public void ClickButtonSquareRoot(View v) {
        result.append("√(");
        formulaInput.append("sqrt(");
        numberClicked=false;
        charBracketOpenCount=0;
        charBracketCloseCount=0;
        charInExceed=0;
        dotCount=0;
    }

    public void ClickButtonPower(View v) {
        stringSpecial = result.getText().toString();
        if(!numberClicked) {

        } else if(stringSpecial.endsWith("^")) {

        } else {
            result.append("^");
            formulaInput.append("^");
            numberClicked = false;
        }
    }

    public void ClickButtonClear(View v) {
        numberClicked = false;
        result.setText("");
        formulaInput.setText("");
    }

    public void ClickButtonDivide(View v) {
        checkNumberDisplay();
        stringSpecial = formulaInput.getText().toString();

        if(stringSpecial.isEmpty()) {

        } else if(stringSpecial.charAt(stringSpecial.length()-1)=='(') {

        } else if(stringSpecial.charAt(stringSpecial.length()-1)=='+' || stringSpecial.charAt(stringSpecial.length()-1)=='-' || stringSpecial.charAt(stringSpecial.length()-1)=='*' || stringSpecial.charAt(stringSpecial.length()-1)=='/') {

        } else {
            numberClicked=false;
            formulaInput.setText(formulaInput.getText() + "/");
            result.setText(null);
            numberClicked=false;
            charBracketCloseCount=0;
            charBracketCloseCount=0;
            charBracketOpenCount=0;
            dotCount=0;
        }
    }

    public void ClickButtonMultiply(View v) {
        checkNumberDisplay();
        stringSpecial = formulaInput.getText().toString();

        if(stringSpecial.isEmpty()) {

        } else if(stringSpecial.charAt(stringSpecial.length()-1)=='(') {

        } else if(stringSpecial.charAt(stringSpecial.length()-1)=='+' || stringSpecial.charAt(stringSpecial.length()-1)=='-' || stringSpecial.charAt(stringSpecial.length()-1)=='X' || stringSpecial.charAt(stringSpecial.length()-1)=='/') {

        } else {
            formulaInput.append("*");
            result.setText(null);
            numberClicked=false;
            charBracketCloseCount=0;
            charBracketCloseCount=0;
            charBracketOpenCount=0;
            dotCount=0;
        }
    }

    public void ClickButtonSubtract(View v) {
        checkNumberDisplay();
        stringSpecial = formulaInput.getText().toString();

        if(stringSpecial.endsWith("sqrt(")) {

        } else {
            formulaInput.append("-");
            result.setText("-");
            numberClicked = false;
            charBracketCloseCount = 0;
            charBracketCloseCount = 0;
            charBracketOpenCount = 0;
            dotCount = 0;
        }
    }

    public void ClickButtonDecimal(View v) {
        stringSpecial = formulaInput.getText().toString();
        if (!numberClicked) {

        } else if (stringSpecial.endsWith("X") || stringSpecial.endsWith("/") || stringSpecial.endsWith("-") || stringSpecial.endsWith("+") || stringSpecial.endsWith("(")) {

        } else {
            if(dotCount != 1) {
                dotCount++;
                formulaInput.append(".");
                if (!result.getText().toString().contains(".")) {
                    result.append(".");
                }
            }
        }
    }

    public void ClickButtonBracketOpen(View v) {
        result.append("(");
        formulaInput.append("(");
        dotCount = 0;
        numberClicked = false;
    }

    public void ClickButtonBracketClose(View v) {
        stringSpecial = formulaInput.getText().toString();
        if(stringSpecial.isEmpty()) {

        } else if (stringSpecial.substring(stringSpecial.length()-1).equals("(")) {

        } else if(stringSpecial.substring(stringSpecial.length()-1).equals("+") ||
                stringSpecial.substring(stringSpecial.length()-1).equals("-") ||
                stringSpecial.substring(stringSpecial.length()-1).equals("*") ||
                stringSpecial.substring(stringSpecial.length()-1).equals("/")) {

        } else if(!stringSpecial.contains("(")) {

        } else {
            formulaInput.append(")");
            result.append(")");
            dotCount=0;
        }
    }

    public void ClickButtonBackspace(View v) {
        if ((formulaInput.getText().toString() != null) && formulaInput.getText().toString().length() > 0) {
            if (formulaInput.getText().toString().equals(result.getText().toString())) {
                result.setText(result.getText().toString().substring(0, result.getText().toString().length() - 1));
            } else {
                result.setText("");
            }
            formulaInput.setText(formulaInput.getText().toString().substring(0, formulaInput.getText().toString().length() - 1));
        }
    }

    public void ClickButtonLog(View v) {
        result.append("log(");
        formulaInput.append("log(");
        numberClicked=false;
        charBracketOpenCount=0;
        charBracketCloseCount=0;
        charInExceed=0;
        dotCount=0;
    }

    public void ClickButtonCos(View v) {
        result.append("cos(");
        formulaInput.append("cos(");
        numberClicked=false;
        charBracketOpenCount=0;
        charBracketCloseCount=0;
        charInExceed=0;
        dotCount=0;
    }

    public void ClickButtonEquals(View v) {
        if(numberClicked==false) {

        } else {
            stringNumber = formulaInput.getText().toString();
            checkBracketsNumber();

            if(charBracketCloseCount > charBracketOpenCount) {
                result.setText("Invalid expression");
            } else if(stringNumber.contains("Infinity")) {
                result.setText("Infinity");
            } else {
                expression = new ExpressionBuilder(stringNumber).build();
                System.out.println("Here");

                try {
                    value = expression.evaluate();
                    result.setText(Double.toString(value));
                } catch (ArithmeticException e) {
                    result.setText("Can't divide by 0");
                }
            }
        }
    }

    private void checkNumberDisplay() {
        stringSpecial = result.getText().toString();
        if(Double.toString(value).equals(stringSpecial)) {
            formulaInput.setText(stringSpecial);
        }
    }

    public void checkBracketsNumber() {
        charBracketCloseCount=0;
        charBracketOpenCount=0;
        charInExceed=0;

        for (int i = 0; i < stringNumber.length(); i++) {
            if (stringNumber.charAt(i) == bracketOpen)
                charBracketOpenCount++;

            else if (stringNumber.charAt(i) == bracketClose)
                charBracketCloseCount++;
        }

        if (charBracketOpenCount == charBracketCloseCount) {

        } else if (charBracketOpenCount > charBracketCloseCount) {
            charInExceed = charBracketOpenCount - charBracketCloseCount;

            for (int j = 0; j < charInExceed; j++) {
                stringNumber = stringNumber + ")";
            }
        }
    }

    public void onDestroy(View view) {

        super.onDestroy();

        if (view != null) {

            windowManager.removeView(view);

            view = null;
        }
    }
}
