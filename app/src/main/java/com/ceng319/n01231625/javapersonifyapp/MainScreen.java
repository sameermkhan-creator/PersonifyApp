package com.ceng319.n01231625.javapersonifyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.FirebaseStorage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MainScreen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage mStorage;



    Button yesBtn;
    Button noBtn;
    Button logOutBtn;
    Button Fab;
    Button uploadPhotoBtn;
    TextView nameLbl;

    ImageView mainPhotoView;
    String currentPostID;

    ArrayList<Post> posts = new ArrayList<>();
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {loadSetting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        yesBtn = findViewById(R.id.likeBtn);
        noBtn = findViewById(R.id.dislikeBtn);
        logOutBtn = findViewById(R.id.logOutBtn);
        uploadPhotoBtn = findViewById(R.id.uploadPhotoBtn);
        Fab = findViewById(R.id.buttonSetting);
        final TextView message = findViewById(R.id.doesItWorkLbl);
        message.setText(R.string.fetching_outfits);

        yesBtn.setText(R.string.Yes);
        noBtn.setText(R.string.No);
        nameLbl = findViewById(R.id.nameLbl);
        mainPhotoView =findViewById(R.id.outfitView);


        nameLbl.setVisibility(View.INVISIBLE);
        noBtn.setEnabled(false);
        yesBtn.setEnabled(false);


            //Fetch the outfits
            mDatabase.child("SERVER_DATA/POSTS/DATA").limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        //There there are posts
                        Log.d("POSTS",""+ dataSnapshot);





                        for (final DataSnapshot snapshot: dataSnapshot.getChildren()) {



                            Log.d("COMPARE_UID_POST", "Current user UID: " + mAuth.getUid() + " & post UID: " + snapshot.child("UID").getValue().toString());

                            if((snapshot.child("UID").getValue()).equals(mAuth.getUid())){
                                Log.d("POST_ERROR","Post belongs to current user!");
                            }else {
                                //NEED TO CHANGE NODE

                                            Post post = new Post();

                                            post.postId = snapshot.getKey();
                                            post.postURL = snapshot.child("POST_PHOTO_URL").getValue().toString();
                                            post.time = (long) snapshot.child("TIMESTAMP").getValue();
                                            post.UID = snapshot.child("UID").getValue().toString();
                                            post.firstName = snapshot.child("firstName").getValue().toString();

                                            posts.add(post);
                            }
                        }


                        Log.d(getString(R.string.post),getString(R.string.number)+getString(R.string.post)  + posts.size());
                        Log.d(getString(R.string.post),""+posts.toString());





                        for (final Post post: posts){
                            mDatabase.child("SERVER_DATA/POSTS/RATINGS").child(post.postId).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if( !dataSnapshot.exists()){


                                            //for (final Post post: posts) {


                                                Log.d("Fetching_post_image", "" + post.postURL);
                                                StorageReference postURLReference = mStorage.getReferenceFromUrl(post.postURL);

                                                postURLReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        Log.d("SUCCESS", "Post photo downloaded");

                                                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                        post.setPhoto(bmp);
                                                        nameLbl.setText(posts.get(0).firstName);
                                                        nameLbl.setVisibility(View.VISIBLE);
                                                        mainPhotoView.setImageBitmap(posts.get(0).photo);
                                                        currentPostID = posts.get(0).postId;
                                                        message.setText(R.string.what_you_think);
                                                        yesBtn.setEnabled(true);
                                                        noBtn.setEnabled(true);



                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(getString(R.string.error), getString(R.string.exception) + e);
                                                    }
                                                });
                                            //}

                                        }else {

                                            posts.remove(post);
                                            posts.trimToSize();
                                            if(posts.size() == 0){
                                                message.setText(R.string.nothing_fetch);
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                        }

                        //Log.d("Downloaded_posts",""+posts.toString());


                        //Present the first post







                    }else {
                        Toast.makeText(MainScreen.this,"There are no posts! This might be an error?",Toast.LENGTH_SHORT).show();
                    }
                }





                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }



            });



        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Map<String, Object> ratingDetails = new HashMap<>();
                ratingDetails.put("UID",mAuth.getUid());
                ratingDetails.put("PostId", currentPostID);
                ratingDetails.put("RATING", "NO");


                mDatabase.child("SERVER_DATA/PENDING_DATA/POSTS/RATINGS").push().setValue(ratingDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if(posts.size() > 1){
                            posts.trimToSize();
                            posts.remove(0);
                            posts.trimToSize();
                            currentPostID = posts.get(0).postId;
                            nameLbl.setText(posts.get(0).firstName);
                            mainPhotoView.setImageBitmap(posts.get(0).photo);



                        }else {
                            posts.trimToSize();
                            posts.remove(0);
                            Log.d("Message!","Fetching new posts.");
                            yesBtn.setEnabled(false);
                            noBtn.setEnabled(false);
                            message.setText(R.string.fetch_new_outfits);

                            mDatabase.child("SERVER_DATA/POSTS/DATA").limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        //There there are posts
                                        Log.d("POSTS",""+ dataSnapshot);



                                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {



                                            Log.d("COMPARE_UID_POST", "Current user UID: " + mAuth.getUid() + " & post UID: " + snapshot.child("UID").getValue().toString());

                                            if((snapshot.child("UID").getValue()).equals(mAuth.getUid())){
                                                Log.d("POST_ERROR","Post belongs to current user!");
                                            }else {

                                                Post post = new Post();

                                                post.postId = snapshot.getKey();
                                                post.postURL = snapshot.child("POST_PHOTO_URL").getValue().toString();
                                                post.time = (long) snapshot.child("TIMESTAMP").getValue();
                                                post.UID = snapshot.child("UID").getValue().toString();
                                                post.firstName = snapshot.child("firstName").getValue().toString();

                                                posts.add(post);
                                            }





                                        }

                                        for (final Post post: posts) {

                                            mDatabase.child("SERVER_DATA/POSTS/RATINGS").child(post.postId).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if( !dataSnapshot.exists()){
                                                        Log.d("Fetching_post_image", "" + post.postURL);
                                                        StorageReference postURLReference = mStorage.getReferenceFromUrl(post.postURL);

                                                        postURLReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                            @Override
                                                            public void onSuccess(byte[] bytes) {
                                                                Log.d("SUCCESS", "Post photo downloaded");

                                                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                post.setPhoto(bmp);


                                                                mainPhotoView.setImageBitmap(posts.get(0).photo);
                                                                currentPostID = posts.get(0).postId;
                                                                message.setText(R.string.what_you_think);
                                                                yesBtn.setEnabled(true);
                                                                noBtn.setEnabled(true);
                                                                nameLbl.setText(posts.get(0).firstName);



                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("ERROR", "Exception: " + e);
                                                            }
                                                        });
                                                    }else {
                                                        posts.remove(post);
                                                        Log.d("LOG",""+Arrays.toString(posts.toArray()));
                                                        if(posts.size() == 0){
                                                            String uri = "@drawable/clothes";  // where myresource (without the extension) is the file

                                                            int imageResource = getResources().getIdentifier(uri, null, getPackageName());


                                                            Drawable res = getResources().getDrawable(imageResource);
                                                            mainPhotoView.setImageDrawable(res);
                                                            nameLbl.setVisibility(View.INVISIBLE);
                                                            message.setText(R.string.nothing_left);
                                                        }
                                                    }

                                                    }


                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });



                                        }

                                        if(posts.size() == 0){
                                           String uri = "@drawable/clothes";  // where myresource (without the extension) is the file

                                            int imageResource = getResources().getIdentifier(uri, null, getPackageName());


                                           Drawable res = getResources().getDrawable(imageResource);
                                           mainPhotoView.setImageDrawable(res);
                                           nameLbl.setVisibility(View.INVISIBLE);

                                       }

                                        Log.d("Downloaded_posts",""+posts.toString());


                                        //Present the first post







                                    }else {
                                        Toast.makeText(MainScreen.this,"There are no posts! This might be an error?",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });





                        }



                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("RATE_NO","DONE");
                    }
                });
            }
        });



        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Map<String, Object> ratingDetails = new HashMap<>();
                ratingDetails.put("UID",mAuth.getUid());
                ratingDetails.put("PostId", currentPostID);
                ratingDetails.put("RATING", "YES");


                mDatabase.child("SERVER_DATA/PENDING_DATA/POSTS/RATINGS").push().setValue(ratingDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        if(posts.size() > 1){
                            posts.trimToSize();
                            posts.remove(0);
                            posts.trimToSize();
                            currentPostID = posts.get(0).postId;
                            nameLbl.setText(posts.get(0).firstName);
                            mainPhotoView.setImageBitmap(posts.get(0).photo);



                        }else {
                            posts.trimToSize();
                            posts.remove(0);
                            Log.d("Message!","Fetching new posts.");
                            yesBtn.setEnabled(false);
                            noBtn.setEnabled(false);
                            message.setText(R.string.fetch_new_outfits);

                            mDatabase.child("SERVER_DATA/POSTS/DATA").limitToFirst(20).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        //There there are posts
                                        Log.d("POSTS",""+ dataSnapshot);



                                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {



                                            Log.d("COMPARE_UID_POST", "Current user UID: " + mAuth.getUid() + " & post UID: " + snapshot.child("UID").getValue().toString());

                                            if((snapshot.child("UID").getValue()).equals(mAuth.getUid())){
                                                Log.d("POST_ERROR","Post belongs to current user!");
                                            }else {

                                                Post post = new Post();

                                                post.postId = snapshot.getKey();
                                                post.postURL = snapshot.child("POST_PHOTO_URL").getValue().toString();
                                                post.time = (long) snapshot.child("TIMESTAMP").getValue();
                                                post.UID = snapshot.child("UID").getValue().toString();
                                                post.firstName = snapshot.child("firstName").getValue().toString();

                                                posts.add(post);
                                            }





                                        }

                                        for (final Post post: posts) {

                                            mDatabase.child("SERVER_DATA//POSTS/RATINGS").child(post.postId).child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if( !dataSnapshot.exists()){
                                                        Log.d("Fetching_post_image", "" + post.postURL);
                                                        StorageReference postURLReference = mStorage.getReferenceFromUrl(post.postURL);

                                                        postURLReference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                            @Override
                                                            public void onSuccess(byte[] bytes) {
                                                                Log.d("SUCCESS", "Post photo downloaded");

                                                                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                post.setPhoto(bmp);


                                                                mainPhotoView.setImageBitmap(posts.get(0).photo);
                                                                currentPostID = posts.get(0).postId;
                                                                message.setText(R.string.what_you_think);
                                                                yesBtn.setEnabled(true);
                                                                noBtn.setEnabled(true);
                                                                nameLbl.setText(posts.get(0).firstName);



                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d("ERROR", "Exception: " + e);
                                                            }
                                                        });
                                                    }else {
                                                        posts.remove(post);
                                                       Log.d("LOG",""+Arrays.toString(posts.toArray()));
                                                        if(posts.size() == 0){
                                                            String uri = "@drawable/clothes";  // where myresource (without the extension) is the file

                                                            int imageResource = getResources().getIdentifier(uri, null, getPackageName());


                                                            Drawable res = getResources().getDrawable(imageResource);
                                                            mainPhotoView.setImageDrawable(res);
                                                            nameLbl.setVisibility(View.INVISIBLE);
                                                        message.setText(R.string.nothing_left);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });



                                        }
//                                          Does not work
                                       if(posts.size() == 0){
                                           message.setText("Nothing left!");
                                       }

                                        //Log.d("Downloaded_posts",""+posts.toString());


                                        //Present the first post







                                    }else {
                                        Toast.makeText(MainScreen.this,"There are no posts! This might be an error?",Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });





                        }



                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("RATE_YES","DONE");
                    }
                });
            }
        });







Fab.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);


        startActivity(intent);

    }
});
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("SERVER_DATA").child("USERS").child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("TEST",dataSnapshot.getValue().toString());
                setTitle(getString(R.string.welcome) + dataSnapshot.child("firstName").getValue().toString() + "!");


                /*
                    If status is D.O.A means that the username is not available
                    everything will work but the user will need to change it eventually
                */
                //Log.d("ERROR", "USERNAME IS IN USE!" + dataSnapshot.child("status").getValue().toString());
                if((dataSnapshot.child("status").getValue().toString()).equals("DOA")){
                    Toast.makeText(MainScreen.this, "The username provided is unavailable! You are still able to use the app tho.", Toast.LENGTH_LONG).show();
                    Log.d("ERROR", "USERNAME IS IN USE!" + dataSnapshot.child("status").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ERROR", databaseError.getMessage());
            }
        });

        mDatabase.child("SERVER_DATA").child("POSTS");

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();

            }
        });

        //Upload photo
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent uploadPhotoIntent = new Intent(MainScreen.this,UploadPhotoActivity.class);
                startActivity(uploadPhotoIntent);
            }
        });







    }


//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(getIntent());
//    }

    private class Post {


        String postId;
        Long time;
        String postURL;
        String UID;
        String firstName;
        Bitmap photo;


        public void setPhoto(Bitmap photo) {
            this.photo = photo;
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

