package com.example.drawableapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.constraintlayout.widget.ConstraintLayout;

public class ColorPicker {
	private static ColorPicker colorPicker;
	private ColorPickerListener listener;
	private Context context;
	private Dialog dialog;
	private Mode dialogMode;
	private final int[] buttonColors = {
		0xff000000, 0xff7f7f7f, 0xffffffff, 0xffff0000, 0xffff7f00,
		0xffffff00, 0xff7fff00, 0xff00ff00, 0xff00ff7f, 0xff00ffff,
		0xff007fff, 0xff0000ff, 0xff7f00ff, 0xffff00ff, 0xffff007f };
	private int[] buttonIds;
	private final int colorsPerRow = 5;
	private int pickedId;
	private int cornerRadius;

	// Purpose: This enumerator is used to select the mode/style of the generated dialog.
	public enum Mode {CREATE, ALTER_PEN, ALTER_BACKGROUND}

	// Purpose: The interface's purpose is to force the calling activity to implement callback
	// methods.
	public interface ColorPickerListener {
		public void onColorPickerOkClick(Mode mode, int pickedColor);
	}

	// Purpose: Constructor!
	// Arguments: -
	private ColorPicker() {
		this.buttonIds = new int[this.buttonColors.length];
	}

	// Purpose: This creates and/or returns a reference to the only object.
	// Arguments: -
	// Returns: ColorPicker
	public static ColorPicker get() {
		if (ColorPicker.colorPicker == null) {
			ColorPicker.colorPicker = new ColorPicker();
		}

		return ColorPicker.colorPicker;
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

	private void createDialog() {
		// Set some layout sizes. These use context and cannot be in the constructor.
		int sizeLarge = this.toDip(48);
		int sizeMedium = this.toDip(24);
		int sizeSmall = this.toDip(16);

		this.cornerRadius = sizeMedium;

		// The dialog:
		this.dialog = new Dialog(this.context);
		int title = 0;
		switch (this.dialogMode) {
			case CREATE:
				title = R.string.color_picker_create;
				break;
			case ALTER_PEN:
			case ALTER_BACKGROUND:
				title = R.string.color_picker_alter;
				break;
		}
		this.dialog.setTitle(title);
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
				listener.onColorPickerOkClick(dialogMode, (int) (dialog.findViewById(pickedId)).getTag());
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

		// The color buttons:
		for (int i = 0; i < this.buttonColors.length; i++) {
			ImageButton button = new ImageButton(this.context);
			this.buttonIds[i] = button.generateViewId();
			button.setId(this.buttonIds[i]);
			button.setTag(this.buttonColors[i]);
			button.setBackgroundColor(this.buttonColors[i]);
			ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(sizeLarge, sizeLarge);

			// The first button on each row:
			if (i % colorsPerRow == 0) {
				params.startToStart = layoutId;
				params.leftMargin = sizeMedium;
			} else {
				params.startToEnd = this.buttonIds[i - 1];
				params.leftMargin = sizeSmall;
			}

			// Top row buttons:
			if (i < colorsPerRow) {
				params.topToTop = layoutId;
				params.topMargin = sizeMedium;
			} else {
				params.topToBottom = this.buttonIds[i - colorsPerRow];
				params.topMargin = sizeSmall;
			}

			// The last button on each full row:
			if ((i + 1) % colorsPerRow == 0) {
				params.endToEnd = layoutId;
				params.rightMargin = sizeMedium;
			}

			// The very last button:
			if (i == buttonColors.length - 1) {
				params.bottomToTop = okButtonId;
				params.bottomMargin = sizeMedium;
			}

			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					selectButton((ImageButton) view);
				}
			});

			button.setLayoutParams(params);
			layout.addView(button);
		}

		// Set pickedId so that it's not undefined later.
		this.pickedId = this.buttonIds[0];
	}

	private void selectButton(ImageButton newButton) {
		ImageButton previousButton = (ImageButton) this.dialog.findViewById(this.pickedId);
		previousButton.setBackgroundColor((int) previousButton.getTag());

		newButton.setBackgroundDrawable(this.makeBackgroundShape(this.cornerRadius, (int) newButton.getTag()));

		this.pickedId = newButton.getId();
	}

	// Purpose: Use this method to open/create a dialog window.
	// Arguments: Context context, ColorPicker.Mode mode, int currentColor
	// Returns: -
	public void show(Context context, Mode mode, int currentColor) {
		// If no colors are defined, this class won't work. On the other hand, it won't work without
		// a bunch of other information and I'm only checking this, so …
		if (this.buttonColors.length == 0) {
			return;
		}

		this.dialogMode = mode;

		// If this is called from a different activity, create a new dialog.
		if (context != this.context) {
			this.context = context;
			this.createDialog();
		}

		try {
			// Cast the host (activity) and bind it for communication.
			this.listener = (ColorPickerListener) context;
		} catch (ClassCastException e) {
			// The host (activity) doesn't implement the interface.
			throw new ClassCastException(this.context.getClass().getName() + " must implement ColorPickerListener.");
		}

		for (int i = 0; i < this.buttonIds.length; i++) {
			ImageButton button = (ImageButton) this.dialog.findViewById(this.buttonIds[i]);
			if (currentColor == (int) button.getTag()) {
				this.selectButton(button);
				break;
			}
		}
		this.dialog.show();
	}
}

