package com.example.drawableapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

public class DrawActivity extends AppCompatActivity implements ColorPicker.ColorPickerListener, SizeSeeker.SizeSeekerListener {

	private Art art;
	private boolean penDialog = true;
	private ImageButton penButton;
	private ImageButton eraserButton;
	private ImageButton saveButton;
	private ImageButton deleteButton;
	private TextView nameTextView;


	FirebaseStorage storage = FirebaseStorage.getInstance();
	StorageReference storageRef = storage.getReference();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the app label.
		this.getSupportActionBar().hide();

		this.setContentView(R.layout.activity_draw);

		this.art = (Art) this.findViewById(R.id.art);
		this.penButton = (ImageButton) this.findViewById(R.id.penButton);
		this.eraserButton = (ImageButton) this.findViewById(R.id.eraserButton);
		this.saveButton = (ImageButton) this.findViewById(R.id.save_button);
		this.deleteButton = (ImageButton) this.findViewById(R.id.delete_button);
		this.nameTextView = (TextView) this.findViewById(R.id.name_text_view);

		this.drawSizeImageDot(this.art.getPenSize());

		ImageButton backButton = (ImageButton) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(DrawActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});

		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//kollar om project redan har ett namn och sparar isåfall över den filen
				//annars öppnar den en dialog för att spara ett projekt för första gången
				if(art.getName().isEmpty()){
					saveProjectDialog();
				} else {
					Log.i("info", art.getName());
					uploadImage(art.getName());
				}

			}
		});

		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//kollar om project redan har ett namn och sparar isåfall över den filen
				//annars öppnar den en dialog för att spara ett projekt för första gången
				if(!art.getName().isEmpty()){
					deleteProjectDialog(art.getName());
				}

			}
		});

		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null) {
			this.art.setBackgroundColor(bundle.getInt("BACKGROUND_COLOR"));
		}
	}

	@Override
	public void onColorPickerOkClick(int pickedColor) {
		if (this.penDialog) {
			this.art.setPenColor(pickedColor);
		} else {
			this.art.setBackgroundColor(pickedColor);
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
					nameTextView.setText(name);
					uploadImage(name);
					dialog.dismiss();
				}
				Toast toast=Toast.makeText(getApplicationContext(),"Please enter a name!",Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

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



	public void showColorPickerPen(View view) {
		this.penDialog = true;
		ColorPicker.get().show(this, ColorPicker.Mode.ALTER, this.art.getPenColor());
	}

	public void uploadImage(String name){
		// Create a reference to "mountains.jpg"
		StorageReference projectRef = storageRef.child(name + ".jpg");

		//ändra art viewn till en jpeg
		art.setDrawingCacheEnabled(true);
		art.buildDrawingCache();

		Bitmap bitmap = art.getDrawingCache();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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

	public void setNameExist(String name){

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
		this.penDialog = false;
		ColorPicker.get().show(this, ColorPicker.Mode.ALTER, this.art.getBackgroundColor());
	}
}

