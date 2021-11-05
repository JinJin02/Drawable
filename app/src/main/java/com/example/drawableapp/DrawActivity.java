package com.example.drawableapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DrawActivity extends AppCompatActivity implements
	ColorPicker.ColorPickerListener, SizeSeeker.SizeSeekerListener, NavigationView.OnNavigationItemSelectedListener {
	private Art art;
	private ImageButton penButton, eraserButton;
	private DrawerLayout burgerRoot;
	private ActionBarDrawerToggle burgerToggle;

	FirebaseStorage storage = FirebaseStorage.getInstance();
	StorageReference storageRef = storage.getReference();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_draw);

		this.art = (Art) this.findViewById(R.id.art);
		this.penButton = (ImageButton) this.findViewById(R.id.penButton);
		this.eraserButton = (ImageButton) this.findViewById(R.id.eraserButton);

		this.drawSizeImageDot(this.art.getPenSize());

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
				art.undo();
				System.out.println("R.id.action_undo");
				return true;
			case R.id.action_redo:
				art.redo();
				System.out.println("R.id.action_redo");
				return true;
			case R.id.action_share:
				share();
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
				saveProjectDialog();
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
			case R.id.nav_delete:
				// TO BE IMPLEMENTED!
				if(!art.getName().isEmpty()){
					deleteProjectDialog(art.getName());
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
					art.setName(name);
					//nameTextView.setText(name);
					uploadImage(name);
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

	public void uploadImage(String name){
		// For creating the file
		//StorageReference fileRef = storageRef.child(userID + "/");
		//StorageReference projectRef = fileRef.child(name + ".jpg");

		StorageReference projectRef = storageRef.child(name + ".jpg");

		//ändra art viewn till en jpeg
		art.setDrawingCacheEnabled(true);
		art.buildDrawingCache();

		Bitmap bitmap = art.getDrawingCache();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] data = baos.toByteArray();


		//ladda upp bilden
		UploadTask uploadTask = projectRef.putBytes(data);
		uploadTask.addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle unsuccessful uploads
				Log.i("info",exception.getMessage());
			}
		}).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
			@Override
			public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
				baos.reset();
				Toast toast=Toast.makeText(getApplicationContext(),"Saved successfully!",Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				// taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
				// ...
				art.setDrawingCacheEnabled(false);
			}
		});
	}

	public void doesNameExist(String name){

		StorageReference projectRef = storageRef.child(name + ".jpg");
		projectRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
			@Override
			public void onSuccess(Uri uri) {
				Log.i("info","file exist");

			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {

				Log.i("info","file not found");

			}
		});
	}

	public void deleteProjectDialog(String name){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//Setting message manually and performing action on button click
		builder.setMessage("Do you want to delete this project?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						deleteProject(name);
						Toast.makeText(getApplicationContext(),"you choose yes action for alertbox",
								Toast.LENGTH_SHORT).show();
						recreate();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//  Action for 'NO' Button
						dialog.cancel();
						Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
								Toast.LENGTH_SHORT).show();
					}
				});
		//Creating dialog box
		AlertDialog alert = builder.create();
		//Setting the title manually

		alert.show();
	}

	public void deleteProject(String name){

		StorageReference projectRef = storageRef.child(name +".jpg");

// Delete the file
		projectRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				Log.i("info", "it worked");

				/*art = (Art) findViewById(R.id.art);
				nameTextView = (TextView) findViewById(R.id.name_text_view);
				art.setName("");*/
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				Log.i("info", "uh-oh");
			}
		});
	}

	private void share() {
		Bitmap bitmap = getBitmapFromView(art);
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("Image/png");
		ByteArrayOutputStream bytes =new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
		String filePath = Environment.getExternalStorageDirectory()+ File.separator + "temporary_file.jpg";
		File f = new File (filePath);
		try {
			f.createNewFile();
			FileOutputStream fo =new FileOutputStream(f);
			fo.write(bytes.toByteArray());

		}catch (IOException e){
			e.printStackTrace();
		}
		share.putExtra(Intent.EXTRA_STREAM,Uri.parse(filePath));
		startActivity(Intent.createChooser(share,"Share Image"));
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

