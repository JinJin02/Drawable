package com.example.drawableapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class GalleryActivity extends AppCompatActivity {

	FirebaseStorage storage = FirebaseStorage.getInstance();
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
		Button downLoad = (Button) this.findViewById(R.id.button);
		StorageReference storageRef = storage.getReference();
		StorageReference pathRef = storageRef.child("try7.jpg");
		ImageView image = (ImageView) this.findViewById(R.id.imageView);
		Glide.with(this /* context */)
				.load(pathRef)
				.into(image);

		downLoad.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
	}
}

