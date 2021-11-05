package com.example.drawableapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;

public class DrawActivity extends AppCompatActivity implements
	ColorPicker.ColorPickerListener, SizeSeeker.SizeSeekerListener, NavigationView.OnNavigationItemSelectedListener {
	private Art art;
	private ImageButton penButton;
	private ImageButton eraserButton;

	private DrawerLayout burgerRoot;
	private ActionBarDrawerToggle burgerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_draw);

		this.art = (Art) this.findViewById(R.id.art);
		this.penButton = (ImageButton) this.findViewById(R.id.penButton);
		this.eraserButton = (ImageButton) this.findViewById(R.id.eraserButton);

		this.drawSizeImageDot(this.art.getPenSize());

//		ImageButton backButton = (ImageButton) this.findViewById(R.id.backButton);
//		backButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				Intent intent = new Intent(DrawActivity.this, MainActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
//			}
//		});

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			this.art.setBackgroundColor(bundle.getInt("BACKGROUND_COLOR"));
		}

		this.burgerRoot = this.findViewById(R.id.burger_root);
		this.burgerToggle = new ActionBarDrawerToggle(this, this.burgerRoot, R.string.nav_open, R.string.nav_close);

		this.burgerRoot.addDrawerListener(this.burgerToggle);
		this.burgerToggle.syncState();

		NavigationView navigationView = (NavigationView) this.findViewById(R.id.burger_view);
		navigationView.setNavigationItemSelectedListener(this);

		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.menu_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (this.burgerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
			case R.id.action_undo:
				// TO BE IMPLEMENTED!
				System.out.println("R.id.action_undo");
				return true;
			case R.id.action_redo:
				// TO BE IMPLEMENTED!
				System.out.println("R.id.action_redo");
				return true;
			case R.id.action_share:
				// TO BE IMPLEMENTED!
				System.out.println("R.id.action_share");
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.nav_new:
				ColorPicker.get().show(this, ColorPicker.Mode.CREATE, 0xffffffff);
				break;
			case R.id.nav_save:
				saveArtDialog();
				break;
			case R.id.nav_load:
				Intent intent = new Intent(this, GalleryActivity.class);
				startActivity(intent);
				break;
			case R.id.nav_export:
				// TO BE IMPLEMENTED!
				System.out.println("R.id.nav_export");
				break;
			case R.id.nav_import:
				// TO BE IMPLEMENTED!
				System.out.println("R.id.nav_import");
				break;
		}

		this.burgerRoot.closeDrawer(GravityCompat.START);

		return true;
	}

	@Override
	public void onBackPressed() {
		if (this.burgerRoot.isDrawerOpen(GravityCompat.START)) { // If the navigation menu is fully open, close it.
			this.burgerRoot.closeDrawer(GravityCompat.START);
		} else if (!this.burgerRoot.isDrawerVisible(GravityCompat.START)) { // If it's not animated, use default.
			super.onBackPressed();
		}
		// If it's animated, do nothing.
	}

	@Override
	public void onColorPickerOkClick(ColorPicker.Mode mode, int pickedColor) {
		switch (mode) {
			case CREATE:
				this.art.reset(pickedColor);
				this.selectPen(this.findViewById(R.id.penButton));
				this.drawSizeImageDot(this.art.getPenSize());
				break;
			case ALTER_PEN:
				this.art.setPenColor(pickedColor);
				break;
			case ALTER_BACKGROUND:
				this.art.setBackgroundColor(pickedColor);
				break;
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
		dialog.setCanceledOnTouchOutside(false);

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
		ColorPicker.get().show(this, ColorPicker.Mode.ALTER_PEN, this.art.getPenColor());
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
		ColorPicker.get().show(this, ColorPicker.Mode.ALTER_BACKGROUND, this.art.getBackgroundColor());
	}
}

