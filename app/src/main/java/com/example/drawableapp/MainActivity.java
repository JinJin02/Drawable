package com.example.drawableapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the app label.
		this.getSupportActionBar().hide();

		this.setContentView(R.layout.activity_main);

		Button drawActivityButton = (Button) this.findViewById(R.id.drawActivityButton);
		drawActivityButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, DrawActivity.class);
				startActivity(intent);
			}
		});

		Button galleryActivityButton = (Button) this.findViewById(R.id.galleryActivityButton);
		galleryActivityButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
				startActivity(intent);
			}
		});


	}
}

