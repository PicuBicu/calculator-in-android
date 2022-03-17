package pl.picubicu.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mariuszgromada.math.mxparser.Expression;

public class SimpleCalculatorActivity extends AppCompatActivity {

    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_calculator);
        this.resultTextView = findViewById(R.id.smp_equationTextView);
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
        this.resultTextView.setText(newExpression);
    }

    private void resetExpression() {
        this.resultTextView.setText("");
    }

    private void addSignToExpression(Button button) {
        String previousText = this.resultTextView.getText().toString();
        String newText = previousText + button.getText();
        this.resultTextView.setText(newText);
    }

    private void calculateExpression() {
        String textViewExpression = this.resultTextView.getText().toString();
        Expression expression = new Expression(textViewExpression);
        double expressionResult = expression.calculate();
        if (Double.isNaN(expressionResult)) {
            Toast.makeText(
                    this,
                    "XD",
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            this.resultTextView.setText(String.valueOf(expressionResult));
        }
    }
}