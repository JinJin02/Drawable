package com.example.drawableapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

// This is used for TextView value manipulation.
//import android.widget.TextView;

import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This function sets activity_draw.xml as the starting activity.
        setContentView(R.layout.activity_draw);
//        setContentView(R.layout.activity_main);
    }

    public void selectPen(View view) {
        Art.get().selectPen();
    }

    public void selectEraser(View view) {
        Art.get().selectEraser();
    }

    // THIS IS FOR DEBUGGING PURPOSES ONLY!!
    public void updatePathSize(View view) {
        // Select a layout.
//        setContentView(R.layout.activity_draw);

        // Find and manipulate the element.
//        TextView tvPathSize = (TextView) findViewById(R.id.tvPathSize);
//        tvPathSize.setText(String.valueOf(paths.size()));
    }
}