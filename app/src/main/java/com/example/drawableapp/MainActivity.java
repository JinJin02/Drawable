package com.example.drawableapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Paint;
import android.view.View;

//import static com.example.drawableapp.Art.selectedColor;

public class MainActivity extends AppCompatActivity {
    public static Path path = new Path();
    public static Paint paintBrush = new Paint();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void selectPen(View view) {
        paintBrush.setColor(Color.BLACK);
//        com.example.drawableapp.Art.selectedColor = Color.BLACK;
        path = new Path();
    }
}