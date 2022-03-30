package pl.picubicu.calculator;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mariuszgromada.math.mxparser.Expression;

public class SimpleCalculatorActivity extends AppCompatActivity {

    private final String MATH_OPERATOR_PATTERN_STR = "[*+\\/-]+|[A-Za-z]+";
    private TextView resultTextView;
    private boolean isDotPlaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        Button clickedButton = (Button) view;
        String sign = clickedButton.getText().toString();
        switch (sign) {
            case "0":
                addZero();
                break;
            case ".":
                addDotSign();
                break;
            case "=":
                calculateExpression();
                break;
            case "c":
                resetExpression();
                break;
            case "bksp":
                removeSignFromExpression(); // TODO: sprawdzić znak i ustawić flage
                break;
            case "+/-":
                changeNumberSign();
                break;
            default:
                addSignToExpression(clickedButton);
                break;
        }
    }

    private void changeNumberSign() {
        String expression = this.resultTextView.getText().toString();
        if (!expression.isEmpty()) {
            String[] tokens = expression.split(MATH_OPERATOR_PATTERN_STR);
            String lastToken = tokens[tokens.length - 1];
            int position = expression.lastIndexOf(lastToken);
            // Getting element before last token
            if (position - 1 > 0) {
                char charBefore = this.resultTextView.getText().charAt(position - 1);
                char sign = ' ';
                if (charBefore == '-') {
                    sign = '+';
                }
                else if (charBefore == '+') {
                    sign = '-';
                }
                else {
                    return;
                }
                StringBuilder stringBuilder = new StringBuilder(expression);
                stringBuilder.setCharAt(position - 1, sign);
                this.resultTextView.setText(stringBuilder.toString());
            }
        }
    }

    private void addZero() {
        String expression = this.resultTextView.getText().toString();
        int length = expression.length();
        // If no signs have been put already
        if (length == 0) {
            this.resultTextView.append("0");
            return;
        }
        // If last char is dot or number but not 0 or operator
        char lastChar = expression.charAt(length - 1);
        if ((Character.isDigit(lastChar) && lastChar != '0')
                || (isDot(lastChar) && hasDotBeenPlacedAlready())
                || isMathOperator(lastChar)
                || (Character.isDigit(lastChar) && hasDotBeenPlacedAlready())) {
            this.resultTextView.append("0");
        }
    }

    private void addDotSign() {
        String expression = this.resultTextView.getText().toString();
        if (!expression.isEmpty()) {
            String[] tokens = expression.split(MATH_OPERATOR_PATTERN_STR);
            String lastToken = tokens[tokens.length - 1];
            if (Character.isDigit(expression.charAt(expression.length() - 1))) {
                try {
                    Float.parseFloat(lastToken + ".");
                    this.resultTextView.append(".");
                    this.isDotPlaced = true;
                } catch (NumberFormatException e) {
                    makeToast("Dot is already in current number");
                }
            }
        }
    }

    private void removeSignFromExpression() {
        String expression = this.resultTextView.getText().toString();
        if (!expression.isEmpty()) {
            int length = expression.length();
            char lastChar = expression.charAt(length - 1);
            if (isDot(lastChar)) {
                isDotPlaced = false;
            }
            String newExpression = expression.substring(0, length - 1);
            this.resultTextView.setText(newExpression);
        }
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
        return sign == '+' ||
                sign == '-' ||
                sign == '*' ||
                sign == '/';
    }

    private void addSignToExpression(Button button) {
        CharSequence newText = button.getText();
        int length = this.resultTextView.length();
        // No signs have been put yet
        if (length == 0) {
            if (Character.isDigit(newText.charAt(0))) {
                this.resultTextView.append(newText);
            }
            return;
        }
        char lastChar = this.resultTextView.getText().charAt(length - 1);
        // If sign is number
        if (Character.isDigit(newText.charAt(0))) {
            if (Character.isDigit(lastChar)
                    || (isDot(lastChar) && hasDotBeenPlacedAlready())
                    || isMathOperator(lastChar)
            ) {
                this.resultTextView.append(newText);
            }
        }
        // If sign is math operator
        else if (Character.isDigit(lastChar)) {
            this.resultTextView.append(newText);
            // because now we can place dot in new float number after math operator
            isDotPlaced = false;
        }
    }

    private void calculateExpression() {
        String textViewExpression = this.resultTextView.getText().toString();
        if (!textViewExpression.isEmpty()) {
            Expression expression = new Expression(textViewExpression);
            double expressionResult = expression.calculate();
            if (Double.isNaN(expressionResult)) {
                makeToast(getResources().getString(R.string.expression_not_valid));
            } else {
                this.resultTextView.setText(String.valueOf(expressionResult));
                this.isDotPlaced = true;
            }
        }
    }

    private void makeToast(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }
}