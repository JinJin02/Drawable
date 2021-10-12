package com.example.drawableapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
//import android.view.ViewGroup;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import static com.example.drawableapp.MainActivity.path;
import static com.example.drawableapp.MainActivity.paintBrush;

public class Art extends View {
	public static ArrayList<Path> paths = new ArrayList<Path>();
//    public ViewGroup.LayoutParams params;

//											(current_brush)
//    public static int selectedColor = Color.BLACK;

	public Art(Context context) {
		super(context);
		init(context);
	}

	public Art(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public Art(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		paintBrush.setAntiAlias(true);
		paintBrush.setColor(Color.BLACK);
		paintBrush.setStyle(Paint.Style.STROKE);
		paintBrush.setStrokeCap(Paint.Cap.ROUND);
		paintBrush.setStrokeJoin(Paint.Join.ROUND);
		paintBrush.setStrokeWidth(8.0f);

//        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	//	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(x, y);
				this.invalidate();
				return true;
			case MotionEvent.ACTION_MOVE:
				path.lineTo(x, y);
				paths.add(path);
				this.invalidate();
				return true;
			default:
				return false;
		}
	}

	//	@Override
	protected void onDraw(Canvas canvas) {
		for (int i = 0; i < paths.size(); i++) {
			paintBrush.setColor(Color.BLACK);
			canvas.drawPath(paths.get(i), paintBrush);
			this.invalidate();
		}
	}
}

