package com.mojo.com;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    LinearLayout ll_login;
    EditText et_mail, et_password;
    Intent intent_showRooms, intent_showAccountActivity;
    TextView tv_createAccount;
    String email, pwd;
    public FirebaseAuth mAuth;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;    // Request code für READ_CONTACTS, beliebige zahl > 0.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        et_mail = (EditText) findViewById(R.id.et_mail);
        et_password = (EditText) findViewById(R.id.et_password);
        tv_createAccount = (TextView) findViewById(R.id.tv_createAccount);
        mAuth = FirebaseAuth.getInstance();

        ll_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            contactPermisson();
            while (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            };
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
//  // https://firebase.google.com/docs/auth/android/password-auth
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
}
