package com.charlik.instagram;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    TextView etSignUp;
    EditText etNama;
    EditText etPassword;
    Boolean loginMode=true;
    String UserName="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        etSignUp = (TextView) findViewById(R.id.etSignUp);
        etNama = (EditText) findViewById(R.id.etNama);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                   FetchUser();
                }

                return false;
            }
        });

        InitializeParse();
        //CreateObject();
        //ReadObject();

        if(ParseUser.getCurrentUser() != null){
            HomeScreen();
        }
    }

    public void ChangeMode(View view){
        if (loginMode) {
            loginMode = false;
            btnLogin.setText("SignUp");
            etSignUp.setText("Or, Login");
        } else {
            loginMode = true;
            btnLogin.setText("Login");
            etSignUp.setText("Or, SignUp");
        }
    }

    public void btnClicked(View view){
        FetchUser();
    }

    private void ReadObject() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Score");

        query.whereGreaterThan("score",60);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null){
                   if (objects.size() > 0){
                       for (ParseObject object : objects){
                           String username = object.getString("username");
                           Integer score = object.getInt("score");
                           Toast.makeText(MainActivity.this, "Username : " + username + ", Score : " + Integer.toString(score), Toast.LENGTH_SHORT).show();
                       }
                   }
                }
            }
        });

    }

    private void CreateObject() {
        ParseObject obj = new ParseObject("Score");
        obj.put("username","charlik");
        obj.put("score",60);
        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(MainActivity.this, "Save Berhasil", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Save Gagal : " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void InitializeParse() {
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("YOUR_APP_ID")
                .server("YOUR_SERVER_URL")
                .build()
        );
    }

    private void HomeScreen(){
        Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
        intent.putExtra("UserName", UserName);

        startActivity(intent);
    }

    public void HideInput(View view){
        InputMethodManager ipm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        ipm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }

    public void FetchUser(){
        if(loginMode){
            ParseUser.logInInBackground(etNama.getText().toString(),
                    etPassword.getText().toString(),
                    new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(e==null){
                                Toast.makeText(MainActivity.this, "Login Berhasil dengan email" + user.getEmail(), Toast.LENGTH_SHORT).show();
                                UserName = etNama.getText().toString();
                                HomeScreen();
                            } else {
                                Toast.makeText(MainActivity.this, "Login Gagal : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            ParseUser user = new ParseUser();
            user.setUsername(etNama.getText().toString());
            user.setPassword(etPassword.getText().toString());
            UserName = etNama.getText().toString();

            //user.setEmail("");
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e==null){
                        Toast.makeText(MainActivity.this, "SignUp Berhasil", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "SignUp gagal : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
