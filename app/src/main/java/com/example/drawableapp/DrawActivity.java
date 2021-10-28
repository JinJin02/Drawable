package com.example.drawableapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class DrawActivity extends AppCompatActivity implements ColorPicker.ColorPickerListener, SizeSeeker.SizeSeekerListener {
	private Art art;
	private boolean penDialog = true;
	private ImageButton penButton;
	private ImageButton eraserButton;
	private ImageButton saveButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the app label.
		this.getSupportActionBar().hide();

		this.setContentView(R.layout.activity_draw);

		this.art = (Art) this.findViewById(R.id.art);
		this.penButton = (ImageButton) this.findViewById(R.id.penButton);
		this.eraserButton = (ImageButton) this.findViewById(R.id.eraserButton);
		this.saveButton = (ImageButton) this.findViewById(R.id.saveButton);

		this.drawSizeImageDot(this.art.getPenSize());

		ImageButton backButton = (ImageButton) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(DrawActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveArtDialog();
			}
		});

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			this.art.setBackgroundColor(bundle.getInt("BACKGROUND_COLOR"));
		}
	}

	@Override
	public void onColorPickerOkClick(int pickedColor) {
		if (this.penDialog) {
			this.art.setPenColor(pickedColor);
		} else {
			this.art.setBackgroundColor(pickedColor);
		}
	}

	@Override
	public void onSizeSeekerOkClick(int acceptedSize) {
		this.drawSizeImageDot((float) acceptedSize);
		this.art.setPenSize(acceptedSize);
	}

	private void drawSizeImageDot(float size) {
		ImageView sizeImage = (ImageView) this.findViewById(R.id.sizeImage);

		if (sizeImage.getDrawable() == null) {
			sizeImage.setImageDrawable(new Dot(size));
		} else {
			((Dot) sizeImage.getDrawable()).setRadius(size);
		}
	}

	private void saveArtDialog(){
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.view_save_project);

		Button cancelBtn = dialog.findViewById(R.id.cancel_button);
		Button saveBtn = dialog.findViewById(R.id.save_button);

		saveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void showColorPickerPen(View view) {
		this.penDialog = true;
		ColorPicker.get().show(this, ColorPicker.Mode.ALTER, this.art.getPenColor());
	}

	public void selectPen(View view) {
		((ColorDrawable) view.getBackground()).setColor(0xffbfbfbf);
		((ColorDrawable) this.eraserButton.getBackground()).setColor(0xffdfdfdf);

		this.art.selectPen();
	}

	public void showSizeSeeker(View view) {
		SizeSeeker.get().show(this, Math.round(Art.PEN_MIN_SIZE), Math.round(Art.PEN_MAX_SIZE), Math.round(this.art.getPenSize()));
	}

	public void selectEraser(View view) {
		((ColorDrawable) this.penButton.getBackground()).setColor(0xffdfdfdf);
		((ColorDrawable) view.getBackground()).setColor(0xffbfbfbf);

		this.art.selectEraser();
	}

	public void showColorPickerBackground(View view) {
		this.penDialog = false;
		ColorPicker.get().show(this, ColorPicker.Mode.ALTER, this.art.getBackgroundColor());
	}
}

