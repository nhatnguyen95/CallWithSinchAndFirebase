package com.example.nhatnguyen.caller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class SignUpActivity extends BaseActivity {
    TextView tvEmail;
    EditText edPhoneNumber;
    Button btnDone;
    Firebase firebase;
    String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://nhatnguyen-caller.firebaseio.com/");
        tvEmail = (TextView)findViewById(R.id.tvEmailSignUp);
        edPhoneNumber = (EditText)findViewById(R.id.edPhoneNumberSignUp);
        btnDone = (Button)findViewById(R.id.btnDoneSignUp);
        Intent callerIntent = getIntent();
        Bundle bundleCaller = callerIntent.getBundleExtra("extra");
        email = bundleCaller.getString("email");
        password = bundleCaller.getString("password");
        tvEmail.setText(email);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp() {
        final User user = new User(tvEmail.getText().toString(),edPhoneNumber.getText().toString());
        firebase.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> stringObjectMap) {
                firebase.push().setValue(user);
                Toast.makeText(SignUpActivity.this,"Create Account Successful!",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(SignUpActivity.this,"Create Account Fail >.< !",Toast.LENGTH_LONG).show();
            }
        });
        finish();
    }
}
