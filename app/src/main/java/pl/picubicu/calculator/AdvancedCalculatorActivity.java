package pl.picubicu.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.mXparser;

public class AdvancedCalculatorActivity extends AppCompatActivity {

    private final String MATH_OPERATOR_PATTERN_STR = "[*+\\/-]+";
    private TextView resultTextView;
    private boolean isDotPlaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mXparser.enableAlmostIntRounding();
        setContentView(R.layout.activity_advanced_calculator);
        this.resultTextView = findViewById(R.id.adv_equationTextView);
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
            if (exprValue.isEmpty() && btnValue.matches("sin|cos|tan|log|ln|sqrt")) {
                this.resultTextView.append(btnValue + "(");
            } else if (exprValue.isEmpty() && (Character.isDigit(btnValue.charAt(0)) || btnValue.charAt(0) == '-')) {
                this.resultTextView.append(btnValue);
            } else if (!exprValue.isEmpty()) {
                char lastChar = exprValue.charAt(exprValue.length() - 1);
                if (btnValue.equals("x^2") && (Character.isDigit(lastChar) || lastChar == ')')) {
                    this.resultTextView.append("^2");
                } else if (btnValue.equals("x^y") && (Character.isDigit(lastChar) || lastChar == ')')) {
                    this.resultTextView.append("^");
                } else if (btnValue.equals("0")) {
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
                } else if (btnValue.equals(")")) {
                    addBracket(exprValue);
                } else if (isMathOperator(btnValue.charAt(0))) {
                    addMathOperator(exprValue, btnValue);
                } else if (btnValue.matches("sin|cos|tan|log|ln|sqrt")
                        && (isMathOperator(lastChar) || lastChar == '(')) {
                    this.resultTextView.append(btnValue + "(");
                }
            }
        } catch (Exception exp) {
            makeToast(getResources().getString(R.string.expression_not_valid));
        }
    }

    private void addBracket(String exprValue) {
        long numOfLeftBracket = exprValue.chars().filter(sign -> sign == '(').count();
        long numOfRightBracket = exprValue.chars().filter(sign -> sign == ')').count();
        if (numOfLeftBracket > numOfRightBracket) {
            this.resultTextView.append(")");
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
            if (charBefore == '*' || charBefore == '/') {
                stringBuilder.insert(position, '-');
            } else if (charBefore == '-' && (exprValue.charAt(position - 2) == '/' || exprValue.charAt(position - 2) == '*')) {
                stringBuilder.replace(position - 1, position, "");
            } else if (exprValue.charAt(position - 2) == '(' && charBefore == '-') {
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
                || (Character.isDigit(lastChar) && hasDotBeenPlacedAlready())
                || lastChar == '(') {
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
        return (sign + "").matches("[*+\\/-]");
    }

    private void addDigit(String exprValue, String btnValue) {
        int length = exprValue.length();
        char lastChar = exprValue.charAt(length - 1);
        if (Character.isDigit(lastChar)
                || (isDot(lastChar) && hasDotBeenPlacedAlready())
                || isMathOperator(lastChar)
                || lastChar == '('
                || lastChar == '^'
        ) {
            this.resultTextView.append(btnValue);
        }
    }

    private void addMathOperator(String exprValue, String btnValue) {
        char lastChar = exprValue.charAt(exprValue.length() - 1);
        if (Character.isDigit(lastChar) || lastChar == ')') {
            this.resultTextView.append(btnValue);
            isDotPlaced = false;
        }
    }

    private String prepareExpression(String exprValue) {
        long numOfLeftBracket = exprValue.chars().filter(sign -> sign == '(').count();
        long numOfRightBracket = exprValue.chars().filter(sign -> sign == ')').count();
        StringBuilder exprValueBuilder = new StringBuilder(exprValue);
        for (int i = 0; i < numOfLeftBracket - numOfRightBracket; i++) {
            exprValueBuilder.append(")");
        }
        exprValue = exprValueBuilder.toString().replace("log(", "log(10,");
        return exprValue;
    }

    private void calculateExpression(String exprValue) {
        exprValue = prepareExpression(exprValue);
        System.out.println(exprValue);
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