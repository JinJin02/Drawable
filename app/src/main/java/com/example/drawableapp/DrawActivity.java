package com.example.drawableapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class DrawActivity extends AppCompatActivity {
	private Art art;
	private ImageButton penButton;
	private ImageButton eraserButton;
	private ImageButton saveButton;
	private Art projectArt;

	FirebaseStorage storage = FirebaseStorage.getInstance();
	StorageReference storageRef = storage.getReference();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the app label.
		this.getSupportActionBar().hide();

		this.setContentView(R.layout.activity_draw);

		//this.art = (Art) this.findViewById(R.id.art);
		this.penButton = (ImageButton) this.findViewById(R.id.penButton);
		this.eraserButton = (ImageButton) this.findViewById(R.id.eraserButton);
		this.saveButton = (ImageButton) this.findViewById(R.id.save_button);

		projectArt = (Art) findViewById(R.id.art);

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
				if(projectArt.getName().isEmpty()){
					saveProjectDialog();
				} else {
					uploadImage(projectArt.getName());
				}

			}
		});
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
					projectArt.setName(name);
					uploadImage(name);
					dialog.dismiss();
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
		// Create a reference to "mountains.jpg"
		StorageReference projectRef = storageRef.child(name + ".jpg");

		//ändra art viewn till en jpeg
		projectArt.setDrawingCacheEnabled(true);
		projectArt.buildDrawingCache();

		Bitmap bitmap = projectArt.getDrawingCache();
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
				// taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
				// ...
				projectArt.setDrawingCacheEnabled(false);
			}
		});
	}

	public void deleteProject(String name){
		StorageReference projectRef = storageRef.child(name +".jpg");

// Delete the file
		projectRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
			@Override
			public void onSuccess(Void aVoid) {
				// File deleted successfully
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Uh-oh, an error occurred!
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
		((ColorDrawable) this.penButton.getBackground()).setColor(0xffbfbfbf);
		((ColorDrawable) this.eraserButton.getBackground()).setColor(0xffdfdfdf);

		this.art.selectPen();
	}

	public void selectEraser(View view) {
		((ColorDrawable) this.penButton.getBackground()).setColor(0xffdfdfdf);
		((ColorDrawable) this.eraserButton.getBackground()).setColor(0xffbfbfbf);

		this.art.selectEraser();
	}
}
