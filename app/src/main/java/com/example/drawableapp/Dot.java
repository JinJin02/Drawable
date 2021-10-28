package com.example.drawableapp;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Dot extends Drawable {
	private final Paint pen = new Paint();
	private final Path path = new Path();

	// Purpose: Constructor!
	// Arguments: float diameter
	public Dot(float diameter) {
		this.pen.setAntiAlias(true);
		this.pen.setColor(0xff000000);
		this.pen.setStyle(Paint.Style.STROKE);
		this.pen.setStrokeCap(Paint.Cap.ROUND);
		this.pen.setStrokeJoin(Paint.Join.ROUND);

		this.setRadius(diameter);
	}

	// Purpose: This is what happends when Android redraws the Dot.
	// Arguments: Canvas canvas
	// Return: -
	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(this.path, this.pen);
	}

	// Purpose: When the bounds change (mainly at start), re-center the path.
	// Arguments: Rect bounds
	// Return: -
	@Override
	protected void onBoundsChange(Rect bounds) {
		// According to documentation, width() and height() can be negative.
		float midX = Math.abs(bounds.width()) * 0.5f;
		float midY = Math.abs(bounds.height()) * 0.5f;

		this.path.reset();
		this.path.moveTo(midX - 0.05f, midX - 0.05f);
		this.path.lineTo(midY + 0.05f, midY + 0.05f);
	}

	// Purpose: This must be implemented, so here it is.
	// Arguments: int alpha
	// Return: -
	@Override
	public void setAlpha(int alpha) {}

	// Purpose: This must be implemented, so here it is.
	// Arguments: ColorFilter colorFilter
	// Return: -
	@Override
	public void setColorFilter(ColorFilter colorFilter) {}

	// Purpose: I'm not sure what this does. xD
	// Arguments: -
	// Return: int
	@Override
	public int getOpacity() {
		// Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE:
		return PixelFormat.OPAQUE;
	}

	// Purpose: Updates the "diameter" of the dot and schedules a draw().
	// Arguments: float diameter
	// Return: -
	public void setRadius(float diameter) {
		this.pen.setStrokeWidth(diameter);
		this.invalidateSelf();
	}
}

