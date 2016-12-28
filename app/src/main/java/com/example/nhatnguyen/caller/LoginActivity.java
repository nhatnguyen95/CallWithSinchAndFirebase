package com.example.nhatnguyen.caller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.sinch.android.rtc.SinchError;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {

    private Button mLoginButton, mSignUpButton;
    private EditText mLoginName, mLoginPassword;
    private ProgressDialog mSpinner;
    private Firebase firebase;
    private String phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Firebase.setAndroidContext(this);
        firebase = new Firebase("https://nhatnguyen-caller.firebaseio.com/");
        mLoginName = (EditText) findViewById(R.id.loginName);
        mLoginPassword = (EditText)findViewById(R.id.loginPassword);
        mLoginButton = (Button) findViewById(R.id.loginButton);
        mSignUpButton = (Button) findViewById(R.id.signupButton);
        mLoginButton.setEnabled(false);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoginName.getText().toString().matches("") || mLoginPassword.getText().toString().matches("")) {
                    Toast.makeText(LoginActivity.this, "Please fill in your Email or Password first!", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("email", mLoginName.getText().toString());
                    bundle.putString("password", mLoginPassword.getText().toString());
                    intent.putExtra("extra", bundle);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onServiceConnected() {
        mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked() {
        final String userName = mLoginName.getText().toString();
        String password = mLoginPassword.getText().toString();

        if (userName.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in your Email and Password !", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            firebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User post = postSnapshot.getValue(User.class);
                        if (userName.equals(post.getEmail()))
                            getSinchServiceInterface().startClient(post.getPhoneNumber());
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            showSpinner();
        } else {
            openPlaceCallActivity();
        }
    }

    private void openPlaceCallActivity() {

        firebase.authWithPassword(mLoginName.getText().toString(), mLoginPassword.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Intent mainActivity = new Intent(LoginActivity.this, PlaceCallActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("email", mLoginName.getText().toString());
                mainActivity.putExtra("extra", bundle);
                startActivity(mainActivity);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(LoginActivity.this, "Wrong Email or Password :v", Toast.LENGTH_LONG).show();
                mSpinner.dismiss();
            }
        });

    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
