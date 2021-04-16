package com.ceng319.n01231625.javapersonifyapp;

//import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UploadPhotoActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

    Button uploadPhotoBtn;
    TextView messageView;
    ImageView uploadPhotoView;
    Button backBtn;
    TextView currentPhotoLbl;
    Button selectPhotoBtn;

    String postPhotoURL;

    private static int RESULT_LOAD_IMAGE = 1;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {loadSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        setTitle(R.string.upload_photo);

        uploadPhotoBtn = findViewById(R.id.uploadPhotoBtn);
        messageView = findViewById(R.id.messageLbl);
        uploadPhotoView = findViewById(R.id.uploadPhotoView);
        backBtn = findViewById(R.id.backBtn);
        currentPhotoLbl = findViewById(R.id.currentPhotoLbl);
        selectPhotoBtn = findViewById(R.id.selectPhotoBtn);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance();

        storageReference = mStorage.getReference();

        currentPhotoLbl.setText("");
        messageView.setText(R.string.fetch_post);
        uploadPhotoBtn.setEnabled(true);
        selectPhotoBtn.setEnabled(true);


        Map<String, Object> userDetails = new HashMap<>();

        userDetails.put("POST_PHOTO_URL", "URL");

            mDatabase.child("SERVER_DATA").child("USERS").child(mAuth.getUid()).child("STORAGE").child("POSTS").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("POST_ID").exists()){
                    postPhotoURL = dataSnapshot.child("POST_PHOTO_URL").getValue().toString();


                    Log.d("POST-URL",postPhotoURL);

                    StorageReference postURLReference = mStorage.getReferenceFromUrl(postPhotoURL);

                    try {
                        File localFile = File.createTempFile("POST_PHOTO", "jpg");
//                        postURLReference.getFile(localFile).addOnSuccessListener(new On {
//                            @Override
//                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                Log.d("SUCCESS","Profile photo downloaded!");
//                            }
//                        });
                            postURLReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Log.d("SUCCESS", "Profile photo downloaded!");
                                    currentPhotoLbl.setText(R.string.current_photo);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    uploadPhotoView.setImageBitmap(bmp);

                                    selectPhotoBtn.setEnabled(true);


                                mDatabase.child("SERVER_DATA/POSTS/DATA").child(dataSnapshot.child("POST_ID").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                        if((postSnapshot.child("numOfYes").exists()) || (postSnapshot.child("numOfNo").exists())){
                                            //messageView.setText(dataSnapshot.child("RATING").toString());
                                            Log.d("Exists","Exists");

                                            int numOfYes;
                                            int numOfNo;

                                            if (postSnapshot.child("numOfYes").exists()){
                                                numOfYes = Integer.valueOf(postSnapshot.child("numOfYes").getValue().toString());
                                            }else {
                                                numOfYes = 0;
                                            }

                                            if (postSnapshot.child("numOfNo").exists()){
                                                numOfNo = Integer.valueOf(postSnapshot.child("numOfNo").getValue().toString());
                                            }else {
                                                numOfNo = 0;
                                            }

                                            messageView.setText("Current votes: Yes: " + numOfYes + " & No: " + numOfNo);
                                        }else {
                                            messageView.setText(R.string.Rating_not);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }else{
                    messageView.setText("No photo to fetch.");
                    selectPhotoBtn.setEnabled(true);

                }

//                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            uploadPhotoView.setImageBitmap(bmp);
                //Log.d("SUCCESS","Profile photo downloaded!");




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UploadPhotoActivity.this,databaseError.getDetails(),Toast.LENGTH_SHORT);
            }
        });


        selectPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);

}


        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadPhotoBtn.setEnabled(true);

                final String postId = mDatabase.push().getKey();


                Bitmap bitmap = ((BitmapDrawable) uploadPhotoView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                byte[] data = baos.toByteArray();


                StorageMetadata uploadMetadata = new StorageMetadata.Builder().setCustomMetadata("UID",mAuth.getUid()).setCustomMetadata("POST_ID",postId).build();

                final StorageReference uploadRef = storageReference.child("SERVER_DATA").child("PENDING_DATA").child("POSTS").child(postId + ".jpg");

                final UploadTask uploadTask = uploadRef.putBytes(data,uploadMetadata);


                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        messageView.setText(e.getLocalizedMessage());
                        uploadPhotoBtn.setEnabled(true);
                    }
                }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.d("Photo-Upload","Upload completed!");
                        //String uploadPath = taskSnapshot.getUploadSessionUri().toString();
//                        String path =   task.getResult().getUploadSessionUri().toString();
//                        Log.d("Storage-Upload-Path",path);


                        uploadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("This_URL",""+uri.toString());

                                Map<String, Object> uploadData = new HashMap<>();
                                uploadData.put("postId", postId);
                                uploadData.put("UID",mAuth.getUid());
                                uploadData.put("PhotoURL", uri.toString());

                                mDatabase.child("SERVER_DATA").child("PENDING_DATA").child("POSTS/DATA").child(mAuth.getUid()).setValue(uploadData);
                                Toast.makeText(UploadPhotoActivity.this,"Post upload success!",Toast.LENGTH_SHORT).show();
                            }
                        });




                    }
                });


            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(UploadPhotoActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(UploadPhotoActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(UploadPhotoActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        RESULT_OK);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            uploadPhotoView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            uploadPhotoBtn.setEnabled(true);

        }
    }

    private void loadSetting() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String saveLang = sharedPreferences.getString("saveLang", "");
        Locale locale = new Locale(saveLang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        String savecolor = sharedPreferences.getString("color", "");
        switch (savecolor) {
            case "blue":   // Blue
                setTheme(R.style.Blue);
                break;
            case "yellow":     // Yellow
                setTheme(R.style.AppThemeYellow);
                break;
            case "purple":     // Purple
                setTheme(R.style.AppThemePurple);
                break;
            case "red":     // Red
                setTheme(R.style.AppTheme_Red);
                break;
            case "green":    // Green
                setTheme(R.style.AppThemeGreen);
                break;

            default:
                setTheme(R.style.AppTheme_NoActionBar);

                break;


        }
    }
}
