package com.example.drawableapp;

import android.content.Context;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Art extends ImageView {
	private Paint pen = new Paint();
	private Path path;
	private ArrayList<Path> paths = new ArrayList<Path>();
	private ArrayList<Path> undonePaths = new ArrayList<Path>();
	private ArrayList<Integer> colors = new ArrayList<Integer>();
	private int backgroundColor = 0xffffffff; // White!
	private int penColor = 0xff000000; // Black! This variable is needed to toggle pen/eraser mode.

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
		this.pen.setColor(this.penColor);
		this.pen.setStyle(Paint.Style.STROKE);
		this.pen.setStrokeCap(Paint.Cap.ROUND);
		this.pen.setStrokeJoin(Paint.Join.ROUND);
		this.pen.setStrokeWidth(10.0f);
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
				this.path = new Path();
				this.paths.add(this.path);
				this.path.moveTo(x, y);
				// Draw a dot upon tap. 0.1f was enough of a difference in the emulated environment.
				this.path.lineTo(x + 0.1f, y + 0.1f);
				this.colors.add(this.pen.getColor());
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
			canvas.drawPath(this.paths.get(i), this.pen);
		}
	}

	// Purpose: Select pen mode by setting the pen's color to the selected color.
	// Arguments: -
	// Return: -
	public void selectPen() {
		this.pen.setColor(this.penColor);
	}

	// Purpose: Select eraser mode by setting the pen's color to the background color.
	// Arguments: -
	// Return: -
	public void selectEraser() {
		this.pen.setColor(this.backgroundColor);
	}



	public void undo() {

		if (paths.size() > 0) {
			paths.add(paths.remove(paths.size()-1));
			invalidate();         //means the canvas redraws itself
		} else {

			Toast.makeText(getContext(),"Nothing to undo",Toast.LENGTH_LONG).show();
		}
	}

	public void redo() {

		if (undonePaths.size()>0) {
			paths.add(undonePaths.remove(undonePaths.size()-1));
			invalidate();
		} else {

			Toast.makeText(getContext(),"Nothing to redo",Toast.LENGTH_LONG).show();
		}
	}


}



