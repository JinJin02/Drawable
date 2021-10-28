package com.example.drawableapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class SizeSeeker {
	private static SizeSeeker sizeSeeker;
	private SizeSeekerListener listener;
	private Context context;
	private Dialog dialog;
	private ImageView sizeImage;
	private SeekBar seekBar;
	private int minValue;

	// Purpose: The interface's purpose is to force the calling activity to implement callback
	// methods.
	public interface SizeSeekerListener {
		public void onSizeSeekerOkClick(int acceptedSize);
	}

	// Purpose: This creates and/or returns a reference to the only object.
	// Arguments: -
	// Returns: SizeSeeker
	public static SizeSeeker get() {
		if (SizeSeeker.sizeSeeker == null) {
			SizeSeeker.sizeSeeker = new SizeSeeker();
		}

		return SizeSeeker.sizeSeeker;
	}

	private int toDip(int value) {
		return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, this.context.getResources().getDisplayMetrics()));
	}

	private GradientDrawable makeBackgroundShape(int cornerRadius, int color) {
		GradientDrawable shape = new GradientDrawable();
		shape.setCornerRadius(cornerRadius);
		shape.setColor(color);

		return shape;
	}

	private void drawSizeImageDot(float size) {
		if (sizeImage.getDrawable() == null) {
			sizeImage.setImageDrawable(new Dot(size));
		} else {
			((Dot) sizeImage.getDrawable()).setRadius(size);
		}
	}

	private void createDialog(int minValue, int maxValue) {
		// Set some layout sizes. These use context and cannot be in the constructor.
		int sizeLarge = this.toDip(48);
		int sizeMedium = this.toDip(24);
		int sizeSmall = this.toDip(16);

		// The dialog:
		this.dialog = new Dialog(this.context);
		this.dialog.setTitle(R.string.size_seeker_title);
		this.dialog.setCanceledOnTouchOutside(false);
		this.dialog.getWindow().setBackgroundDrawable(this.makeBackgroundShape(0, 0xffcfcfcf));

		// The layout:
		ConstraintLayout layout = new ConstraintLayout(this.context);
		int layoutId = layout.generateViewId();
		layout.setId(layoutId);
		this.dialog.addContentView(layout, new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

		// The OK button:
		Button okButton = new Button(this.context);
		int okButtonId = okButton.generateViewId();
		okButton.setId(okButtonId);
		okButton.setText(android.R.string.ok);
		ConstraintLayout.LayoutParams okParams = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
		okParams.endToEnd = layoutId;
		okParams.rightMargin = sizeSmall;
		okParams.bottomToBottom = layoutId;
		okParams.bottomMargin = sizeSmall;
		okButton.setLayoutParams(okParams);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				listener.onSizeSeekerOkClick(minValue + seekBar.getProgress());
				dialog.dismiss();
			}
		});
		layout.addView(okButton);

		// The cancel button:
		Button cancelButton = new Button(this.context);
		int cancelButtonId = cancelButton.generateViewId();
		cancelButton.setId(cancelButtonId);
		cancelButton.setText(android.R.string.cancel);
		ConstraintLayout.LayoutParams cancelParams = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
		cancelParams.endToStart = okButtonId;
		cancelParams.rightMargin = sizeSmall;
		cancelParams.bottomToBottom = layoutId;
		cancelParams.bottomMargin = sizeSmall;
		cancelButton.setLayoutParams(cancelParams);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		layout.addView(cancelButton);

		// The size image:
		sizeImage = new ImageView(this.context);
		int sizeImageId = sizeImage.generateViewId();
		sizeImage.setId(sizeImageId);

		ConstraintLayout.LayoutParams imageParams = new ConstraintLayout.LayoutParams(sizeLarge, sizeLarge);
		imageParams.topToTop = layoutId;
		imageParams.bottomToTop = okButtonId;
		imageParams.startToStart = layoutId;
		imageParams.leftMargin = sizeMedium;

		sizeImage.setLayoutParams(imageParams);

		layout.addView(sizeImage);

		// The seek bar:
		seekBar = new SeekBar(this.context);
		seekBar.setMax(maxValue - minValue);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				drawSizeImageDot((float) (progress + minValue));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onStopTrackingTouch(SeekBar seekBar) {}
		});

		ConstraintLayout.LayoutParams seekParams = new ConstraintLayout.LayoutParams(this.toDip(224), sizeLarge);
		seekParams.topToTop = layoutId;
		seekParams.endToEnd = layoutId;
		seekParams.bottomToTop = okButtonId;
		seekParams.startToEnd = sizeImageId;
		seekParams.topMargin = sizeMedium;
		seekParams.rightMargin = sizeMedium;
		seekParams.bottomMargin = sizeMedium;
		seekParams.leftMargin = sizeMedium;

		seekBar.setLayoutParams(seekParams);

		layout.addView(seekBar);
	}

	// Purpose: Use this method to open/create a dialog window.
	// Arguments: Context context, int minValue, int maxValue, int currentValue
	// Returns: -
	public void show(Context context, int minValue, int maxValue, int currentValue) {
		// If this is called from a different activity, create a new dialog.
		if (context != this.context) {
			this.context = context;
			this.createDialog(minValue, maxValue);
		}

		try {
			// Cast the host (activity) and bind it for communication.
			this.listener = (SizeSeekerListener) context;
		} catch (ClassCastException e) {
			// The host (activity) doesn't implement the interface.
			throw new ClassCastException(this.context.getClass().getName() + " must implement SizeSeekerListener.");
		}

		this.drawSizeImageDot((float) currentValue);
		this.seekBar.setProgress(currentValue - minValue);
		this.dialog.show();
	}
}

