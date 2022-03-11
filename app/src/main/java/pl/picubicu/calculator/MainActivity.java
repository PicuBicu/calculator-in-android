package pl.picubicu.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button aboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handleAboutActivity();
    }

    private void handleAboutActivity() {
        this.aboutButton = findViewById(R.id.aboutButton);
        this.aboutButton.setOnClickListener((view) -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });
    }
}