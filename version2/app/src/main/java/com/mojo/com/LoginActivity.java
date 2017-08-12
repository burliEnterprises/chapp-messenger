package com.mojo.com;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.firebase.database.ValueEventListener;
import com.mojo.com.R;


public class LoginActivity extends BaseActivity {

    DatabaseReference dbr_users;
    private String userName;        // beim start wird username aufgerufen
    public FirebaseAuth.AuthStateListener mAuthListener;
    public FirebaseAuth mAuth;
    String username, name, password, email, telefonnummer, pwd;

    LinearLayout ll_login;
    EditText et_mail, et_password;
    Intent intent_showRooms, intent_showAccountActivity;
    TextView tv_createAccount;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;    // Request code für READ_CONTACTS, beliebige zahl > 0.



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        /* User bereits eingeloggt? -> MainActivity, sonst Login */
        mAuth = FirebaseAuth.getInstance();
      //  mAuth.signOut();
        dbr_users = FirebaseDatabase.getInstance().getReference().getRoot().child("users");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("LoginTry", "onAuthStateChanged:signed_in:" + user.getUid());
                    getInformationFromFB(user.getUid());
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    // User is signed out
                    Log.d("LoginTry", "onAuthStateChanged:signed_out");
                }
            }
        };


        /* Login oder Register? */

        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        et_mail = (EditText) findViewById(R.id.et_mail);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_createAccount = (TextView) findViewById(R.id.tv_createAccount);

        ll_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginVersuch();
            }
        });

        tv_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    private void LoginVersuch() {
     // https://firebase.google.com/docs/auth/android/password-auth
        email = et_mail.getText().toString();
        pwd = et_password.getText().toString();
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("AndroidLoginVersuch", "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Sorry, der Login ist leider fehlgeschlagen.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("AndroidLoginVersuch", "signInWithEmail:onComplete:");
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }
                    }
                });

    }


    // takes user infos from firebase and stores it in shared preferences:
    private void getInformationFromFB(String UID) {
        dbr_users.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                email = (String) dataSnapshot.child("mail").getValue();
                name = (String) dataSnapshot.child("name").getValue();
                username = (String) dataSnapshot.child("username").getValue();
                password = (String) dataSnapshot.child("password").getValue();
                telefonnummer = (String) dataSnapshot.child("telefonnummer").getValue();

                SharedPreferences.Editor editor = getSharedPreferences("CHAPP_PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("email", email);
                editor.putString("name", name);
                editor.putString("username", username);
                editor.putString("telefonnummer", telefonnummer);
                editor.putString("password", password);
                editor.commit();
            };

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }


}
