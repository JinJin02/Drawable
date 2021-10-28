package com.example.drawableapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class GalleryActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		// Hide the app label.
		this.getSupportActionBar().hide();

		this.setContentView(R.layout.activity_gallery);

		ImageButton backButton = (ImageButton) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {


			@Override
			public void onClick(View view) {
				Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}
}

