package com.mojo.com;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.AnyThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.mojo.com.R.id.email;
import static com.mojo.com.R.id.start;

public class RegisterActivity extends AppCompatActivity {

    private ImageView iv_backToLogin;
    private Intent intent_showLoginActivity;
    private EditText et_username, et_name, et_mail, et_password, et_telefonnummer;
    private LinearLayout ll_register;
    private String name, username, password, mail, telefonnummer;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private FirebaseUser FBuser;
    public int yx = 0;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;    // Request code für READ_CONTACTS, beliebige zahl > 0.
    private Contacts kontakte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iv_backToLogin = (ImageView) findViewById(R.id.iv_backToLogin);
        et_username = (EditText) findViewById(R.id.et_username);
        et_name = (EditText) findViewById(R.id.et_name);
        et_password = (EditText) findViewById(R.id.et_password);
        et_mail = (EditText) findViewById(R.id.et_mail);
        ll_register = (LinearLayout) findViewById(R.id.ll_register);
        et_telefonnummer = (EditText) findViewById(R.id.et_telefonnummer);

        // auth
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        // datenbank
        databaseReference = FirebaseDatabase.getInstance().getReference().getRoot();


        // Zurück zum Login
        intent_showLoginActivity = new Intent(this, LoginActivity.class);
        iv_backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent_showLoginActivity);
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
            contactPermisson();
            while (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            }
            ;
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
                                writeContactsInDB();
                                Toast.makeText(RegisterActivity.this, "Gratuliere, Sie sind nun Teil unserer Sekte!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            } else {        //error nachricht
                                Toast.makeText(RegisterActivity.this, "Sorry, die Registrierung war aufgrund Netzwerkproblemen nicht möglich.", Toast.LENGTH_LONG).show();
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


    // schaut, ob berechtigung für kontakte auslesen bereits erteilt -> ab android 6.0 neues modell, müssen immer live abgefragt werden
    private void contactPermisson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
         //  writeContactsInDB();
        }
    }

    // listener, während der abfrage ob permission erteilt wird (kontakte)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
              //  writeContactsInDB();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // schreibt kontakte in db
    private void writeContactsInDB() {
        kontakte = new Contacts();
        ContentResolver cr = RegisterActivity.this.getContentResolver(); //Activity/Application android.content.Context
        Map<String, String> al_contacts = kontakte.ReadKontakte(cr);        // holt kontakte aus adressbuch
        //Map<String, String> al_downloaded_contacts = kontakte.GetRegistredOnes(al_contacts);   // holt alle telefonnummern vom server; alle nutzer
        kontakte.GetRegistredOnes(al_contacts, FBuser.getUid());   // holt alle telefonnummern vom server; alle nutzer
       // Map<String, Object> al_contacts_registred = kontakte.GetYourRegistredOnes(al_contacts, al_downloaded_contacts); // holt kontakte, welche sowohl in adressbuch als auch auf server sind
        databaseReference.child("users").child("telefonnummern").child(telefonnummer).setValue(FBuser.getUid());        // eigene nummer in telefonummern tabelle
       // databaseReference.child("users").child(FBuser.getUid()).child("contacts").setValue(al_contacts_registred);
    }

    private boolean fehlerEingabe() {
        if (!(mail.contains("@")) || !(password.length() > 6)) {
            return false;
        } else {
            return true;
        }
    }



}
