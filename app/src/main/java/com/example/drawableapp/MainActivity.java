package com.example.drawableapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements ColorPicker.ColorPickerListener {
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
				ColorPicker.get().show(MainActivity.this, ColorPicker.Mode.CREATE, 0xffffffff);
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

	@Override
	public void onColorPickerOkClick(int pickedColor) {
		Intent intent = new Intent(MainActivity.this, DrawActivity.class);
		intent.putExtra("BACKGROUND_COLOR", pickedColor);
		startActivity(intent);
	}
}

