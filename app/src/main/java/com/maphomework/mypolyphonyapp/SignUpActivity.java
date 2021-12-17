package com.maphomework.mypolyphonyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText username, fullName, email, password;
    Button signUp;
    TextView txtLogin;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.edtTextUsernameSignup);
        fullName = findViewById(R.id.edtTextFullnameSignup);
        email = findViewById(R.id.edtTextEmailSignup);
        password = findViewById(R.id.edtTextPasswordSignup);

        signUp = findViewById(R.id.btnSignupSignup);
        txtLogin = findViewById(R.id.txtLoginSignup);

        auth = FirebaseAuth.getInstance();

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(SignUpActivity.this);
                pd.setMessage("Please Wait");
                pd.show();

                String strUsername = username.getText().toString();
                String strFullName = fullName.getText().toString();
                String strEmail = email.getText().toString();
                String strPassword = password.getText().toString();

                if (TextUtils.isEmpty(strUsername) || TextUtils.isEmpty(strFullName) || TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)){
                    Toast.makeText(SignUpActivity.this, "All Fields Are Required", Toast.LENGTH_SHORT).show();
                }
                else if (strPassword.length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password must have at least 6 characters.", Toast.LENGTH_SHORT).show();
                }
                else{
                    signUp(strUsername, strFullName, strEmail, strPassword);
                }
            }
        });
    }

    public void signUp(String username, String fullName, String email, String password){
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    String userid = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference().child("Users");

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("username", username);
                    hashMap.put("fullname", fullName);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/polyphonyproject-1d40f.appspot.com/o/placeholder.jpg?alt=media&token=eed61d5c-07cd-43f8-b1f3-94ce8afc780f");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                pd.dismiss();
                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else{
                    pd.dismiss();
                    Toast.makeText(SignUpActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}