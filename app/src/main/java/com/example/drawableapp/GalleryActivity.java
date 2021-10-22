package com.example.drawableapp;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class GalleryActivity extends AppCompatActivity {


    Button button5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        button5 = (Button)findViewById(R.id.button5);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}
