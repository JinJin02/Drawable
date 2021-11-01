package com.example.drawableapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Art extends ImageView {
	private String name;
	private Paint pen = new Paint();
	private Path path;
	private ArrayList<Integer> colors = new ArrayList<Integer>();
	private ArrayList<Float> sizes = new ArrayList<Float>();
	private ArrayList<Path> paths = new ArrayList<Path>();
	private int backgroundColor = 0xffffffff; // White!
	private int penColor = 0xff000000; // Black! This variable is used to toggle pen/eraser mode.
	private float penSize = 12.0f;
	private boolean erasing = false;
	public static final float PEN_MIN_SIZE = 4.0f;
	public static final float PEN_MAX_SIZE = 64.0f;

	// Purpose: Constructor!
	// Arguments: Context context
	public Art(Context context) {
		super(context);
		this.init();
	}

	// Purpose: Constructor!
	// Arguments: Context context, @Nullable AttributeSet attrs
	public Art(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	// Purpose: Constructor!
	// Arguments: Context context, @Nullable AttributeSet attrs, int defStyleAttr
	public Art(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init();
	}

	private void init() {
		this.pen.setAntiAlias(true);
		this.pen.setColor(this.penColor);
		this.pen.setStyle(Paint.Style.STROKE);
		this.pen.setStrokeCap(Paint.Cap.ROUND);
		this.pen.setStrokeJoin(Paint.Join.ROUND);
		this.pen.setStrokeWidth(this.penSize);
		this.name = "";
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	// Purpose: This method is called when the object is touched. (Someone is attempting to draw.)
	// Arguments: MotionEvent event
	// Return: boolean
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				this.colors.add(this.pen.getColor());
				this.sizes.add(this.pen.getStrokeWidth());
				this.path = new Path();
				this.paths.add(this.path);
				// Draw a dot upon tap. 0.1f was enough of a difference in the emulated environment.
				this.path.moveTo(x - 0.05f, y - 0.05f);
				this.path.lineTo(x + 0.05f, y + 0.05f);
				break;
			case MotionEvent.ACTION_MOVE:
				this.path.lineTo(x, y);
				break;
			default:
				return false;
		}

		// This method is used to trigger a redraw of the element.
		this.invalidate();
		return true;
	}

	// Purpose: Do this when Android runs its draw methods - for all paths, select the appropriate
	// pen color and draw the path.
	// Arguments: Canvas canvas
	// Return: -
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawARGB((this.backgroundColor >> 24) & 0xff, (this.backgroundColor >> 16) & 0xff, (this.backgroundColor >> 8) & 0xff, (this.backgroundColor) & 0xff);

		for (int i = 0; i < this.paths.size(); i++) {
			this.pen.setColor(this.colors.get(i));
			this.pen.setStrokeWidth(this.sizes.get(i));
			canvas.drawPath(this.paths.get(i), this.pen);
		}

		// Reset the pen's properties after drawing.
		this.pen.setColor(this.erasing == false ? this.penColor : this.backgroundColor);
		this.pen.setStrokeWidth(this.penSize);
	}

	// Purpose: Set the color pf the pen.
	// Arguments: int color
	// Return: -
	public void setPenColor(int color) {
		this.penColor = color;

		if (this.erasing == false) {
			this.pen.setColor(this.penColor);
		}
	}

	// Purpose: Get the (saved) color of the pen.
	// Arguments: -
	// Return: int
	public int getPenColor() {
		return this.penColor;
	}

	// Purpose: Select pen mode by setting the pen's color to the selected color.
	// Arguments: -
	// Return: -
	public void selectPen() {
		this.erasing = false;
		this.pen.setColor(this.penColor);
	}

	// Purpose: Set the pen stroke width (size).
	// Arguments: float size
	// Return: -
	public void setPenSize(float size) {
		this.penSize = size;
		this.pen.setStrokeWidth(this.penSize);
	}

	// Purpose: Get the pen stroke width (size).
	// Arguments: -
	// Return: float
	public float getPenSize() {
		return this.penSize;
	}

	// Purpose: Select eraser mode by setting the pen's color to the background color.
	// Arguments: -
	// Return: -
	public void selectEraser() {
		this.erasing = true;
		this.pen.setColor(this.backgroundColor);
	}

	// Purpose: Set the background color!
	// Arguments: int color
	// Return: -
	public void setBackgroundColor(int color) {
		// Loop through already used colors and if they match the old background, set them to the new
		// one to preserve the illusion of deleting.
		for (int i = 0; i < this.colors.size(); i++) {
			if (this.colors.get(i) == this.backgroundColor) {
				this.colors.set(i, color);
			}
		}

		this.backgroundColor = color;
		this.invalidate();
	}

	// Purpose: Retrieve the background color, duh.
	// Arguments: -
	// Return: int
	public int getBackgroundColor() {
		return this.backgroundColor;
	}
}

