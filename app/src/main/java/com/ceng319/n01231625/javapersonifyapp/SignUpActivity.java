package com.ceng319.n01231625.javapersonifyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.view.inputmethod.InputMethodManager;
import android.view.View;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;




import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import java.lang.String;
import java.lang.Boolean;

import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.TextUtils;
import android.widget.Toast;
import android.os.Bundle;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {


    TextView emailField;
    TextView usernameField;
    TextView passwordField;
    TextView confirmPasswordField;
    TextView firstNameField;
    Button signUpBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseFunctions mFunctions;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {        loadSetting();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        setTitle(R.string.sign_up);

        emailField = findViewById(R.id.emailField);
        firstNameField = findViewById(R.id.firstNameField);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        confirmPasswordField = findViewById(R.id.confirmField);
        signUpBtn = findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validateForm()) {
                    //Everything filled out and passwords match
                    //Send to Firebase for AUTH

                    mAuth.createUserWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("Sign-Up", "createUserWithEmail:success");

                                        Map<String, Object> userDetails = new HashMap<>();

                                        userDetails.put("username", usernameField.getText().toString());
                                        userDetails.put("firstName", firstNameField.getText().toString());

                                        mDatabase = FirebaseDatabase.getInstance().getReference();


                                        mDatabase.child("SERVER_DATA").child("PENDING_DATA").child("NEW_USERS").child(mAuth.getCurrentUser().getUid()).setValue(userDetails);


                                        finish();


                                        //Intent mainScreenIntent = new Intent(SignUpActivity.this, MainScreen.class);

                                        //startActivity(mainScreenIntent);


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("Sign-Up", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpActivity.this, "Authentication failed! " + task.getException().getLocalizedMessage(),
                                                Toast.LENGTH_LONG).show();

                                    }

                                    // [START_EXCLUDE]

                                    // [END_EXCLUDE]
                                }
                            });


                } else {
                    Log.d("ERROR", "ERROR");
                }

            }
        });


    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String confirmPassword = confirmPasswordField.getText().toString();
        String firstName = firstNameField.getText().toString();
        Log.w("Sign-Up-Values", "These are the values: " + email + username + password + confirmPassword);

        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.required));
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (TextUtils.isEmpty(firstName)) {
            firstNameField.setError(getString(R.string.required));
            valid = false;
        } else {
            firstNameField.setError(null);
        }


        if (TextUtils.isEmpty(username)) {
            usernameField.setError(getString(R.string.required));
            valid = false;
        } else {
            usernameField.setError(null);
        }
        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.required));
            valid = false;
        } else {
            passwordField.setError(null);
        }


        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordField.setError(getString(R.string.required));
            valid = false;
        } else {
            confirmPasswordField.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Does not match password!");
            valid = false;
        } else {
            confirmPasswordField.setError(null);
        }


        return valid;
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

