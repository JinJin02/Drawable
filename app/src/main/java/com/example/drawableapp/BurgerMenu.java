package com.example.drawableapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class BurgerMenu extends DrawerLayout {
	private final boolean SWIPE_OPEN_DISABLED = true;

	public BurgerMenu(Context context) {
		super(context);
	}

	public BurgerMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BurgerMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (SWIPE_OPEN_DISABLED && !this.isDrawerVisible(GravityCompat.START)) {
			return false;
		}

		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (SWIPE_OPEN_DISABLED && !this.isDrawerVisible(GravityCompat.START)) {
			return false;
		}

		return super.onTouchEvent(event);
	}
}

