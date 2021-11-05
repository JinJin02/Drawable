package com.example.drawableapp;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class DrawActivity extends AppCompatActivity {
	private Art art;
	private Art projectArt;
	private ImageButton penButton;
	private ImageButton eraserButton;
	private ImageButton saveButton;
	private ImageButton shareButton;
	private ImageButton undoButton, redoButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide the app label.
		this.getSupportActionBar().hide();

		this.setContentView(R.layout.activity_draw);

		this.art = (Art) this.findViewById(R.id.art);
		this.penButton = (ImageButton) this.findViewById(R.id.penButton);
		this.eraserButton = (ImageButton) this.findViewById(R.id.eraserButton);
		this.saveButton = (ImageButton) this.findViewById(R.id.saveButton);
		this.undoButton = (ImageButton) this.findViewById(R.id.undoButton);
		this.redoButton = (ImageButton) this.findViewById(R.id.redoButton);
		this.shareButton = (ImageButton) this.findViewById(R.id.share_button);
		this.projectArt = (Art) this.findViewById(R.id.art);



		ImageButton backButton = (ImageButton) this.findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(DrawActivity.this, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});


		undoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				art.undo();
			}
		});

		redoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				art.redo();
			}
		});


		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				share();

			}
		});


		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				saveArtDialog();
			}
		});


	}

	private void saveArtDialog() {
		Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.view_save_project);

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


	private void share() {
		Bitmap bitmap = getBitmapFromView(projectArt);
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("Image/png");
		ByteArrayOutputStream bytes =new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
		String filePath = Environment.getExternalStorageDirectory()+File.separator + "temporary_file.jpg";
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

