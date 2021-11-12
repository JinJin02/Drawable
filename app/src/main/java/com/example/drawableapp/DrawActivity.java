package com.example.drawableapp;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class DrawActivity extends AppCompatActivity implements
	ColorPicker.ColorPickerListener, SizeSeeker.SizeSeekerListener, NavigationView.OnNavigationItemSelectedListener {
	private Art art;
	private ImageButton penButton, eraserButton;
	private DrawerLayout burgerRoot;
	private ActionBarDrawerToggle burgerToggle;
	private ActionBar actionbar;
	private ResetAction resetAction;

	public enum ResetAction {NEW_PROJECT, DELETE_PROJECT}

	FirebaseStorage storage = FirebaseStorage.getInstance();
	StorageReference storageRef = storage.getReference();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_draw);

		getSupportActionBar().setTitle("");
		this.art = (Art) this.findViewById(R.id.art);
		this.penButton = (ImageButton) this.findViewById(R.id.penButton);
		this.eraserButton = (ImageButton) this.findViewById(R.id.eraserButton);

		this.drawSizeImageDot(this.art.getPenSize());

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
//			ConstraintLayout test = findViewById(R.id.art_constraint);
			this.art.setBackgroundColor(bundle.getInt("BACKGROUND_COLOR"));
//			this.art.setName(bundle.getString("IMAGE_NAME"));
//			getSupportActionBar().setTitle(bundle.getString("IMAGE_NAME"));
//			Bitmap b = BitmapFactory.decodeByteArray(
//					getIntent().getByteArrayExtra("byteArray"),0,getIntent().getByteArrayExtra("byteArray").length);
//			this.art.setImageBitmap(b);
//			Glide.with(test.getContext()).load(bundle.getString("IMAGE_URL")).into(art);
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
				art.undo();
				System.out.println("R.id.action_undo");
				return true;
			case R.id.action_redo:
				art.redo();
				System.out.println("R.id.action_redo");
				return true;
			case R.id.action_share:
				share();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.nav_new:
				resetProjectDialog(ResetAction.NEW_PROJECT);
				break;
			case R.id.nav_save:
				if(art.getName().isEmpty()){
					saveProjectDialog();
				} else {
					uploadImage(art.getName());
				}
				break;
			case R.id.nav_open_gallery:
				Intent intent = new Intent(this, GalleryActivity.class);
				startActivity(intent);
				break;
			case R.id.nav_delete:
				if (!art.getName().isEmpty()) {
					resetProjectDialog(ResetAction.DELETE_PROJECT);
				}
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

	private void saveProjectDialog(){
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.view_save_project);

		Button cancelBtn = dialog.findViewById(R.id.cancel_button);
		Button saveBtn = dialog.findViewById(R.id.save_button);
		EditText nameEditText = dialog.findViewById(R.id.name_edit_text);

		saveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String name = nameEditText.getText().toString();

				if(!name.isEmpty()) {
					checkIfNameTaken(name);
					dialog.dismiss();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(), "Please enter a name!", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
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

	public void checkIfNameTaken(String name){
		StorageReference projectRef = storageRef.child(name + ".jpg");
		projectRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
			@Override
			public void onSuccess(Uri uri) {
				Toast toast = Toast.makeText(getApplicationContext(), "This name already exists", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				uploadImage(name);
			}
		});
	}

	public void uploadImage(String name){
		// For creating the file
		//StorageReference fileRef = storageRef.child(userID + "/");
		//StorageReference projectRef = fileRef.child(name + ".jpg");

		StorageReference projectRef = storageRef.child(name + ".jpg");
			//ändra art viewn till en jpeg
			art.setDrawingCacheEnabled(true);
			art.buildDrawingCache();
			Bitmap bitmap = art.getDrawingCache();

			//bitmap = getBitmapFromView(art);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();


			//ladda upp bilden
			UploadTask uploadTask = projectRef.putBytes(data);
			uploadTask.addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception exception) {
					// Handle unsuccessful uploads
					Toast toast = Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
					//baos.reset();

					Toast toast=Toast.makeText(getApplicationContext(),"Saved successfully!",Toast.LENGTH_LONG);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					art.setName(name);
					getSupportActionBar().setTitle(name);
					art.setDrawingCacheEnabled(false);
				}
			});
	}

	public void resetProjectDialog(ResetAction resetAction){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//Setting message manually and performing action on button click
		this.resetAction = resetAction;

		switch (this.resetAction) {
			case NEW_PROJECT:
				builder.setMessage(R.string.reset_action_new).setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							resetProject();
							dialog.cancel();
						}
					});
				break;
			case DELETE_PROJECT:
				builder.setMessage(R.string.reset_action_delete).setCancelable(false)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							deleteProject();
							dialog.cancel();
						}
					});
				break;
		}

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//  Action for 'NO' Button
				dialog.cancel();
			}
		});
		//Creating dialog box
		AlertDialog alert = builder.create();

		alert.show();
	}

	public void deleteProject(){

		StorageReference projectRef = storageRef.child(art.getName() +".jpg");
		projectRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				resetProject();
				Toast toast = Toast.makeText(getApplicationContext(), "Deleted succesfully!", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Toast toast = Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
			}
		});
	}

	public void resetProject(){
		this.art.reset(0xffffffff);
		this.art.setName("");
		getSupportActionBar().setTitle(this.art.getName());
		ColorPicker.get().show(this, ColorPicker.Mode.CREATE, 0xffffffff, 0xff000000);
	}

	private void share(){

		StorageReference projectRef = storageRef.child(art.getName() + ".jpg");
		projectRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
			@Override
			public void onSuccess(Uri uri) {
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
				shareIntent.setType("image/jpeg");
				startActivity(Intent.createChooser(shareIntent, "Share"));

			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Toast toast = Toast.makeText(getApplicationContext(), "Please save project!", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

			}
		});
	}

	private Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable =view.getBackground();
		if (bgDrawable!=null) {
			//has background drawable, then draw it on the canvas
			bgDrawable.draw(canvas);
		}   else{
			//does not have background drawable, then draw white background on the canvas
			canvas.drawColor(Color.WHITE);
		}
		view.draw(canvas);
		return returnedBitmap;
	}


	public void showColorPickerPen(View view) {
		ColorPicker.get().show(this, ColorPicker.Mode.ALTER_PEN, this.art.getPenColor(), this.art.getBackgroundColor());
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

//	public void showColorPickerBackground(View view) {
//		ColorPicker.get().show(this, ColorPicker.Mode.ALTER_BACKGROUND, this.art.getBackgroundColor());
//	}
}

