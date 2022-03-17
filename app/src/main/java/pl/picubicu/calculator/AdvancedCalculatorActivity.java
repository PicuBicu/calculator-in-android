package pl.picubicu.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mariuszgromada.math.mxparser.Expression;

public class AdvancedCalculatorActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_calculator);
        this.resultTextView = findViewById(R.id.adv_equationTextView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        outState.putString("expressionText", this.resultTextView.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        this.resultTextView.setText(savedInstanceState.getString("expressionText"));
    }

    public void handleButton(View view) {
        Button clickedButton = (Button)view;
        String sign = clickedButton.getText().toString();
        switch (sign) {
            case "=": calculateExpression(); break;
            case "c": resetExpression(); break;
            case "bksp": removeLastSignFromExpression(); break;
            case "+/-": break;
            default: addSignToExpression(clickedButton); break;
        }
    }

    private void removeLastSignFromExpression() {
        String expression = this.resultTextView.getText().toString();
        int expressionLength = expression.length();
        String newExpression = expression.substring(0, expressionLength - 1);
        this.resultTextView.setText(newExpression.isEmpty() ? "" : newExpression);
    }

    private void resetExpression() {
        this.resultTextView.setText("");
    }

    private boolean isAddingBracketNeeded(String text) {
        switch (text) {
            case "sin":
            case "cos":
            case "tan":
            case "ln":
            case "sqrt":
            case "log":
                return true;
        }
        return false;
    }

    private boolean isPowerOfNumber(String text) {
        switch (text) {
            case "x^2":
            case "x^y":
                return true;
        }
        return false;
    }

    private String convertToProperPower(String text) {
        return text.replace("x", "").replace("y","");
    }

    private void addSignToExpression(Button button) {
        String previousText = this.resultTextView.getText().toString();
        String clickedText = button.getText().toString();
        if (isAddingBracketNeeded(clickedText)) {
            clickedText += "(";
        }
        else if (isPowerOfNumber(clickedText)) {
            clickedText = convertToProperPower(clickedText);
        }
        String newText = previousText + clickedText;
        this.resultTextView.setText(newText);
    }

    private void calculateExpression() {
        String textViewExpression = this.resultTextView.getText().toString();
        Expression expression = new Expression(textViewExpression);
        double expressionResult = expression.calculate();
        if (Double.isNaN(expressionResult)) {
            Toast.makeText(
                    this,
                    R.string.expression_not_valid,
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            this.resultTextView.setText(String.valueOf(expressionResult));
        }
    }
}