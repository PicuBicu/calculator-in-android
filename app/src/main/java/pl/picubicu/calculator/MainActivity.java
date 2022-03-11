package pl.picubicu.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button aboutButton;
    private Button exitButton;
    private Button simpleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleAboutActivity();
        handleExit();
        handleSimpleActivity();
    }

    private void handleAboutActivity() {
        this.aboutButton = findViewById(R.id.aboutButton);
        this.aboutButton.setOnClickListener((view) -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });
    }

    private void handleExit() {
        this.exitButton = findViewById(R.id.exitButton);
        this.exitButton.setOnClickListener((view) -> finish());
    }

    private void handleSimpleActivity() {
        this.simpleButton = findViewById(R.id.simpleButton);
        this.simpleButton.setOnClickListener((view) -> {
            startActivity(new Intent(MainActivity.this, SimpleCalculatorActivity.class));
        });
    }
}