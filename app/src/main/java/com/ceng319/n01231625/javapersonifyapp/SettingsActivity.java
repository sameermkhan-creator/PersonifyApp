package com.ceng319.n01231625.javapersonifyapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    Button refresh, nameid,share,mail,lang,display,hardwareData;
    TextView nameText;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    private Object DisplayHardware;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(this);
        updateResources(sharedPreferences.getString("saveLang", ""));
        setsTheme(sharedPreferences.getString("color", ""));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        refresh =  findViewById(R.id.button4);
        nameid = findViewById(R.id.button3);
        share = findViewById(R.id.button7);
        mail = findViewById(R.id.button8);
        lang = findViewById(R.id.button6);
        display = findViewById(R.id.button5);
        nameText = findViewById(R.id.textView);
        hardwareData = findViewById(R.id.HardwareData);


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();



        mDatabase.child("SERVER_DATA/USERS").child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("firstName").exists()){
                    String name = dataSnapshot.child("firstName").getValue().toString();
                    nameText.setText(name);

                }else {
                    nameText.setText(R.string.error);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SettingsActivity.this,databaseError.getDetails(),Toast.LENGTH_SHORT).show();
            }
        });



        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lang();
            }
        });
        nameid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NameProf();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpemail();
            }
        });
        display.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appearance();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                recreate();
            }
        });
        hardwareData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHardwareData();
            }
        });

    }

    private void NameProf() {
        nameText = findViewById(R.id.textView);
        final EditText naming = new EditText(this);

        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle(R.string.your_name)
                .setMessage(R.string.new_name)
                .setView(naming)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }})
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nameNew = naming.getText().toString();
                        nameText.setText(nameNew);

                        Map<String, Object> userDetails = new HashMap<>();

                        userDetails.put("firstName", nameNew);

                        mDatabase.child("SERVER_DATA/USERS").child(mAuth.getUid()).updateChildren(userDetails).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SettingsActivity.this, "Name successfully updated.",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }})

                .show();
    }

    public void lang(){
        // changing language
        final String [] lang = {"English","Spanish","French"};

        AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("select language");
        builder.setItems(lang, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    updateResources("en");
                }
                else if(i==1) {
                    updateResources("es");
                }
                else if(i==2){

                    updateResources("fr");
                }
            }
        });
        builder.show();

    }

    public void appearance(){
        //change theme and colours
        final String [] theme = {"Green","Blue","Yellow","Purple","Red"};
        AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle("select theme");
        builder.setItems(theme, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int cl) {
                if(cl==0){

                    setsTheme(theme[0].toLowerCase());
                }
                else if(cl==1){

                    setsTheme(theme[1].toLowerCase());
                }
                else if(cl==2) {

                    setsTheme(theme[2].toLowerCase());
                }
                else if(cl==3){

                    setsTheme(theme[3].toLowerCase());
                }
                else if(cl==4) {

                    setsTheme(theme[4].toLowerCase());
                }


            }});
        builder.show();
    }
    public void helpemail(){

        new AlertDialog.Builder(SettingsActivity.this)
                .setTitle("Request help or send feedback")
                .setMessage("Who would you like to email")

                .setPositiveButton("Philbert Fontenelle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String[] emailaddress= {"ordell_p@hotmail.com"};
                        sendMail(emailaddress );
                    }
                })
                .setNegativeButton("Dominik Kabala", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] emailaddress={"dominikkabala11@gmail.com"} ;
                        sendMail(emailaddress);
                    }})
                .setNeutralButton("Sameer Khan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String [] emailaddress={"jookiee9@gmail.com"};
                        sendMail(emailaddress);
                    }})
                .create()
                .show();

    }
    public void sendMail(String[] emailaddress){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL  ,emailaddress);
        i.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        i.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
            finish();
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e("vineleaf", "no email");

        }


    }
    private void share(){
        try{
            String message = "send where";
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }catch (SecurityException error)
        {
            Log.e("MapleLeaf", "Cancelled");
        }
    }

    private void   updateResources(String language) {
        Log.d("MapleLeaf", "Language given is: " + language);
        editor = sharedPreferences.edit();

        editor.putString("saveLang", language );
        editor.apply();

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();

        config.locale = locale;

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

    }


    private void setsTheme(String saveTheme) {

        Log.d("MapleLeaf", "Color Index is: " + saveTheme);

        editor.putString("color", saveTheme );
        editor.apply();

        switch (saveTheme) {
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

    public void viewHardwareData() {
        Intent intent = new Intent(this, DisplayHardware.class);
        startActivity(intent);
    }
}


