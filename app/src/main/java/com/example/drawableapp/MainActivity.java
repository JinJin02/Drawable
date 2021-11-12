package com.example.drawableapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity implements ColorPicker.ColorPickerListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the top app bar.
		this.getSupportActionBar().hide();

		this.setContentView(R.layout.activity_main);

		ConstraintLayout mainLayout = (ConstraintLayout) this.findViewById(R.id.main_layout);
		mainLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				ColorPicker.get().show(MainActivity.this, ColorPicker.Mode.CREATE, 0xffffffff, 0xff000000);
			}
		});
	}

	// Purpose: When a background color has been picked (OK) for the new project, control is
	// transferred to DrawActiviy.
	// Arguments: ColorPicker.Mode mode, int pickedColor
	// Returns: -
	@Override
	public void onColorPickerOkClick(ColorPicker.Mode mode, int pickedColor) {
		Intent intent = new Intent(this, DrawActivity.class);
		intent.putExtra("BACKGROUND_COLOR", pickedColor);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}
}

