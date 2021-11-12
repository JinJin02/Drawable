package com.example.drawableapp;

import android.content.Context;
import android.widget.Toast;

public class Toaster {
	private static Toast toast;

	// Purpose: Constructor!
	// Arguments: -
	private Toaster() {}

	// Purpose: This displays the message neater than otherwise.
	// Arguments: Context context, String message
	// Returns: -
	public static void show(Context context, String message) {
		// If a the toast is already active, cancel it.
		if (Toaster.toast != null) {
			Toaster.toast.cancel();
		}

		Toaster.toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		Toaster.toast.show();
	}
}

