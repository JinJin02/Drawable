package com.example.drawableapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class GalleryActivity extends AppCompatActivity {
	ArrayList<String> imagelist;
	ArrayList<String> namelist;
	RecyclerView recyclerView;
	StorageReference root;
	ProgressBar progressBar;
	ImageAdapter adapter;
	FirebaseStorage storage = FirebaseStorage.getInstance();
	StorageReference storageRef = storage.getReference();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		this.getSupportActionBar().setTitle(R.string.app_gallery);

		this.setContentView(R.layout.activity_gallery);

//		ImageButton backButton = (ImageButton) this.findViewById(R.id.backButton);
//		backButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				finish();
//			}
//		});

		imagelist=new ArrayList<>();
		namelist=new ArrayList<>();
		recyclerView=findViewById(R.id.recycler_view);
		adapter=new ImageAdapter(imagelist,namelist,this);
		recyclerView.setLayoutManager(new LinearLayoutManager(null));
		progressBar=findViewById(R.id.progress);
		progressBar.setVisibility(View.VISIBLE);
		StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images");
		storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
			@Override
			public void onSuccess(ListResult listResult) {
				for(StorageReference file:listResult.getItems()){
					file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							//ProjectListData projectData = new ProjectListData(uri.toString(),file.getName());
							imagelist.add(uri.toString());
							namelist.add(projectName(file.getName()));
							Log.e("Itemvalue",uri.toString());
						}
					}).addOnSuccessListener(new OnSuccessListener<Uri>() {
						@Override
						public void onSuccess(Uri uri) {
							recyclerView.setAdapter(adapter);
							progressBar.setVisibility(View.GONE);
						}
					});
				}
			}
		});
	}

	public String projectName(String firebaseName){
		int length = firebaseName.length() - 4;
		String name = firebaseName.substring(0,length);
		return name;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		this.finish();
		return true;
	}
}

