package com.example.testapp;

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

import com.example.testapp.app.MyApp;
import com.example.testapp.constants.Constants;
import com.example.testapp.data.DataProcessController;
import com.example.testapp.entity.LoginResponseUser;
import com.example.testapp.entity.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class login extends AppCompatActivity {
    EditText email,pass;
    Button login;
    User user=new User();
    TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=(EditText)findViewById(R.id.email);
        pass=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        register=(TextView)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(login.this,MainActivity.class);
                startActivity(i);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(email.getText().toString().trim())) {
                    email.setError("Input Email ID");
                    if (!email.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+[.]+[a-z]+")) {
                        email.setError("Invalid Email Address");
                    }
                    return;
                } else if (TextUtils.isEmpty(pass.getText().toString().trim())) {
                    pass.setError("Input Password");

                    return;
                } else {
                    try{
                        user.setUser_email_id(email.getText().toString());
                        user.setPassword(pass.getText().toString());
                        getservicedata(user);
                    }catch (Exception e){
                        Toast.makeText(login.this, "Can't login"+e, Toast.LENGTH_SHORT).show();
                    }


                }
            }

            private void getservicedata(User user) {
                final ProgressDialog ringProgressDialog=ProgressDialog.show(login.this,"Please Wait","loading",true);
                ringProgressDialog.setCancelable(true);
                final Call<LoginResponseUser> call = MyApp.getApiClient().login_inUser(user);
                call.enqueue(new Callback<LoginResponseUser>() {
                    @Override
                    public void onResponse(Call<LoginResponseUser> call, Response<LoginResponseUser> response) {
                        if (response.isSuccessful())
                        {
                            LoginResponseUser loginResponseUser = response.body();
                            ringProgressDialog.dismiss();
                            if(loginResponseUser==null)
                            {
                                Toast.makeText(login.this, "login failed", Toast.LENGTH_SHORT).show();
                            }
                            else if (Integer.parseInt(loginResponseUser.getStatus())==1)
                            {
                                Toast.makeText(login.this, "Success", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(login.this,Home.class));
                            }
                            else
                            {
                                Toast.makeText(login.this, "Check your email or password", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(login.this, Constants.REST_ERROR_API_ERROR, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponseUser> call, Throwable t) {
                        Toast.makeText(login.this, Constants.REST_ERROR_NETWORK, Toast.LENGTH_SHORT).show();
                        ringProgressDialog.dismiss();
                    }
                });
            }
        });

    }
}
