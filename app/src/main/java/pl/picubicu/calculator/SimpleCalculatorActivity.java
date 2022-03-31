package pl.picubicu.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

public class SimpleCalculatorActivity extends AppCompatActivity {

    private final String MATH_OPERATOR_PATTERN_STR = "[*+\\/-]+";
    private TextView resultTextView;
    private boolean isDotPlaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mXparser.enableAlmostIntRounding();
        setContentView(R.layout.activity_simple_calculator);
        this.resultTextView = findViewById(R.id.smp_equationTextView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("expressionText", this.resultTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.resultTextView.setText(savedInstanceState.getString("expressionText"));
    }

    public void handleButton(View view) {
        try {
            String btnValue = ((Button) view).getText().toString();
            String exprValue = this.resultTextView.getText().toString();
            if (exprValue.isEmpty() && (Character.isDigit(btnValue.charAt(0)) || btnValue.charAt(0) == '-')) {
                this.resultTextView.append(btnValue);
            } else if (!exprValue.isEmpty()) {
                if (btnValue.equals("0")) {
                    addZero(exprValue);
                } else if (btnValue.equals(".")) {
                    addDot(exprValue);
                } else if (btnValue.equals("=")) {
                    calculateExpression(exprValue);
                } else if (btnValue.equals("c")) {
                    resetExpression();
                } else if (btnValue.equals("bksp")) {
                    removeSign(exprValue);
                } else if (btnValue.equals("+/-")) {
                    changeNumberSign(exprValue);
                } else if (Character.isDigit(btnValue.charAt(0))) {
                    addDigit(exprValue, btnValue);
                } else {
                    addMathOperator(exprValue, btnValue);
                }
            }
        } catch (Exception exp) {
            makeToast(getString(R.string.expression_not_valid));
        }

    }

    private String parseLastToken(String exprValue) {
        String[] tokens = exprValue.split(MATH_OPERATOR_PATTERN_STR);
        return tokens.length > 0 ? tokens[tokens.length - 1] : "";
    }

    private char determineChar(char charBefore) {
        if (charBefore == '-') {
            return '+';
        } else if (charBefore == '+') {
            return '-';
        }
        return '-';
    }

    private void changeNumberSign(String exprValue) {
        String[] tokens = exprValue.split(MATH_OPERATOR_PATTERN_STR);
        if ((tokens.length == 1 || tokens.length == 2) && exprValue.charAt(0) == '-') {
            exprValue = exprValue.replace("-", "");
            this.resultTextView.setText(exprValue);
        } else if (tokens.length == 1 && Character.isDigit(exprValue.charAt(0))) {
            StringBuilder stringBuilder = new StringBuilder(exprValue);
            stringBuilder.insert(0, '-');
            this.resultTextView.setText(stringBuilder.toString());
        } else {
            String lastToken = parseLastToken(exprValue);
            int position = exprValue.lastIndexOf(lastToken);
            char charBefore = exprValue.charAt(position - 1);
            StringBuilder stringBuilder = new StringBuilder(exprValue);
            if (charBefore == '-' && (exprValue.charAt(position - 2) == '/' || exprValue.charAt(position - 2) == '*')) {
                stringBuilder.replace(position - 1, position, "");
            } else if (charBefore == '+' || charBefore == '-') {
                stringBuilder.setCharAt(position - 1, determineChar(charBefore));
            } else {
                stringBuilder.insert(position, '-');
            }
            this.resultTextView.setText(stringBuilder.toString());
        }
    }

    private void addZero(String exprValue) {
        String lastToken = parseLastToken(exprValue);
        char lastChar = exprValue.charAt(exprValue.length() - 1);
        if ((Character.isDigit(lastChar) && lastToken.charAt(0) != '0')
                || (isDot(lastChar) && hasDotBeenPlacedAlready())
                || isMathOperator(lastChar)
                || (Character.isDigit(lastChar) && hasDotBeenPlacedAlready())) {
            this.resultTextView.append("0");
        }
    }

    private void addDot(String exprValue) {
        String lastToken = parseLastToken(exprValue);
        if (Character.isDigit(exprValue.charAt(exprValue.length() - 1))) {
            try {
                Float.parseFloat(lastToken + ".");
                this.resultTextView.append(".");
                this.isDotPlaced = true;
            } catch (NumberFormatException e) {
                makeToast("Dot is already in current number");
            }
        }
    }

    private void removeSign(String exprValue) {
        int length = exprValue.length();
        char lastChar = exprValue.charAt(length - 1);
        if (isDot(lastChar)) {
            isDotPlaced = false;
        }
        String newExpression = exprValue.substring(0, length - 1);
        this.resultTextView.setText(newExpression);
    }

    private void resetExpression() {
        this.resultTextView.setText("");
        this.isDotPlaced = false;
    }

    private boolean isDot(char sign) {
        return sign == '.';
    }

    private boolean hasDotBeenPlacedAlready() {
        return this.isDotPlaced;
    }

    private boolean isMathOperator(char sign) {
        return (sign + "").matches("[*+\\/-]+|[A-Za-z]+");
    }

    private void addDigit(String exprValue, String btnValue) {
        int length = exprValue.length();
        char lastChar = exprValue.charAt(length - 1);
        if (Character.isDigit(lastChar)
                || (isDot(lastChar) && hasDotBeenPlacedAlready())
                || isMathOperator(lastChar)
        ) {
            this.resultTextView.append(btnValue);
        }
    }

    private void addMathOperator(String exprValue, String btnValue) {
        char lastChar = exprValue.charAt(exprValue.length() - 1);
        if (Character.isDigit(lastChar)) {
            this.resultTextView.append(btnValue);
            isDotPlaced = false;
        }
    }

    private void calculateExpression(String exprValue) {
        Expression expression = new Expression(exprValue);
        double expressionResult = expression.calculate();
        if (Double.isNaN(expressionResult)) {
            makeToast(getResources().getString(R.string.expression_not_valid));
        } else {
            this.resultTextView.setText(String.valueOf(expressionResult));
            this.isDotPlaced = true;
        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}