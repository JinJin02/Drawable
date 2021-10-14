package com.example.drawableapp;

import android.content.Context;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Color;
import android.util.AttributeSet;
//import android.view.ViewGroup;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Art extends View {
	// TEMPORARY SOLUTION (?)
	private static Art art;

	private Path path;
	private ArrayList<Path> paths = new ArrayList<Path>();
	private Paint pen = new Paint();
	private int backgroundColor = Color.WHITE;
	private int selectedColor = Color.BLACK;
	private ArrayList<Integer> colors = new ArrayList<Integer>();
//    public ViewGroup.LayoutParams params;

	public Art(Context context) {
		super(context);
		this.init();
	}

	public Art(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		this.init();
	}

	public Art(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.init();
	}

	private void init() {
		this.pen.setAntiAlias(true);
		this.pen.setColor(this.selectedColor);
		this.pen.setStyle(Paint.Style.STROKE);
		this.pen.setStrokeCap(Paint.Cap.ROUND);
		this.pen.setStrokeJoin(Paint.Join.ROUND);
		this.pen.setStrokeWidth(8.0f);

//        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		Art.art = this;
	}

	// At some point, some part of the app creates an instance of Art. A reference to that is saved
	// in "art" and this method returns a reference to that instance.
	public static Art get() {
		return Art.art;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// On finger down, create a new path …
				this.path = new Path();
				// … and add it to the ArrayList.
				this.paths.add(this.path);
				this.path.moveTo(x, y);
				// Draw a dot upon tap. 0.1f was enough of a difference in the emulated environment.
				this.path.lineTo(x + 0.1f, y + 0.1f);
				// Add the current pen color to colors.
				this.colors.add(this.pen.getColor());
				// This method is used to trigger a redraw of the element.
				this.invalidate();
				return true;
			case MotionEvent.ACTION_MOVE:
				this.path.lineTo(x, y);
				this.invalidate();
				return true;
			default:
				return false;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < this.paths.size(); i++) {
			this.pen.setColor(this.colors.get(i));
			canvas.drawPath(this.paths.get(i), this.pen);
		}
	}

	public void selectPen() {
		this.pen.setColor(this.selectedColor);
	}

	public void selectEraser() {
		this.pen.setColor(this.backgroundColor);
	}
}

