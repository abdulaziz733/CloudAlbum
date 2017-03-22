package com.cloudalbum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 123;
    private Button buttonChoose, buttonUpload, buttonDownload;
    private ImageView imgView;
    private Uri filePath;

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonDownload = (Button) findViewById(R.id.buttonDownload);
        imgView = (ImageView) findViewById(R.id.imgView);

        buttonUpload.setOnClickListener(this);
        buttonChoose.setOnClickListener(this);
        buttonDownload.setOnClickListener(this);


        mStorageRef = FirebaseStorage.getInstance().getReference();

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar Mu!"), PICK_IMAGE_REQUEST
        );
    }

    private void uploadFile() {

        if (filePath != null) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference riversRef = mStorageRef.child("images/profile.jpg");

            riversRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Fle Uploaded", Toast.LENGTH_SHORT).show();
                            downloadFile();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage(((int) progress) + "% uploaded ...");
                        }
                    });
        } else {

        }
    }

    private void downloadFile() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Download...");
        progressDialog.setMessage("Download image");
        progressDialog.show();

        mStorageRef.child("images/profile.jpg").getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Toast.makeText(MainActivity.this, "Download Success", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();

                        Log.d("aziz", uri.toString());
                        Picasso.with(MainActivity.this)
                                .load(uri.toString())
                                .into(imgView);
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Gagal Download", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
//                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {

        if (v == buttonChoose) {
            //openfile chooser
            showFileChooser();
        } else if (v == buttonUpload) {
            //upload file to firebase storage
            uploadFile();
        } else if (v == buttonDownload){
            //download file
            downloadFile();
        }

    }
}
