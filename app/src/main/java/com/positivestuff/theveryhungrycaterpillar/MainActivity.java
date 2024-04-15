package com.positivestuff.theveryhungrycaterpillar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button startGameScreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startGameScreenButton = findViewById(R.id.buttonStart);
        startGameScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGameScreen();
            }
        });
    }

    public void openGameScreen() {
        Intent intent = new Intent(this, CaterpillarGameActivity.class);
        startActivity(intent);
    }
}