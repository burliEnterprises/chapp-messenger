package com.mojo.com;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mojo.com.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private TextView backToLogin;
    private EditText et_username, et_name, et_mail, et_password, et_telefonnummer;
    private LinearLayout ll_register;
    private String name, username, password, mail, telefonnummer;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseUser FBuser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        backToLogin = (TextView) findViewById(R.id.tv_backtologin);
        et_username = (EditText) findViewById(R.id.et_username);
        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);
        et_mail = (EditText) findViewById(R.id.et_mail);
        ll_register = (LinearLayout) findViewById(R.id.ll_register);
        et_telefonnummer = (EditText) findViewById(R.id.et_telefon);

        // auth
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();


        // Zurück zum Login
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });



        // Button Klick "Registrieren"
        ll_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterUser();
            }
        });

    }

    // Registrieren nach Button Klick
    private void RegisterUser() {
        // https://firebase.google.com/docs/auth/android/password-auth
        username = et_username.getText().toString();
        name = et_name.getText().toString();
        mail = et_mail.getText().toString();
        password = et_password.getText().toString();
        telefonnummer = et_telefonnummer.getText().toString();
        //telefonnummer = kontakte.standardisiereAUT(telefonnummer);      // standardisiert eingegebene nummer

        if (fehlerEingabe() == true) {
            // ABLEHNEN HANDLING FEHLT!
            progressDialog.setMessage("Bitte haben Sie einen Moment Geduld...");
            progressDialog.show();

            // Registrierung in der Auth-Table
            firebaseAuth.createUserWithEmailAndPassword(mail, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {      //activity wechsel + nachricht bei erfolgreicher registierung
                                InsertUserInDB(username, password, mail, name, telefonnummer);
                                Toast.makeText(RegisterActivity.this, "Willkommen bei Chapp :)", Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = getSharedPreferences("CHAPP_PREFS", Context.MODE_PRIVATE).edit();
                                editor.putString("email", mail);
                                editor.putString("name", name);
                                editor.putString("username", username);
                                editor.putString("telefonnummer", telefonnummer);
                                editor.putString("password", password);
                                editor.commit();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            } else {        //error nachricht
                                Toast.makeText(RegisterActivity.this, "Sorry, ein Fehler ist aufgetreten :(.", Toast.LENGTH_LONG).show();
                            }
                            ;
                            progressDialog.dismiss();
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "Fehler bei der Registrierung. Überprüfe deine Daten!", Toast.LENGTH_LONG).show();
        }
    }

    // Eintrag in die DB in den user-Zweig, Username = ID so quasi
    private void InsertUserInDB(String usernam, String pwd, String email, String namee, String telef) {
        FBuser = firebaseAuth.getCurrentUser();
        User user = new User(usernam, pwd, email, namee, telef);
        databaseReference.child("users").child(FBuser.getUid()).setValue(user);
    }


    private boolean fehlerEingabe() {
        if (!(mail.contains("@")) || !(password.length() > 6)) {
            return false;
        } else {
            return true;
        }
    }



}
