package org.techtown.newandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Retrofit retrofit = ClientApi.getClientApi();
    private RetrofitInterface retrofitInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrofitInterface = retrofit.create(RetrofitInterface.class);
        Button capture_btn = (Button)findViewById(R.id.capture_btn);

        findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                handleLoginDialog();
            }
        });

        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                handleRegisterDialog();
            }
        });

        capture_btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
                startActivity(intent);
            }
        });
    }

    private void handleLoginDialog() {
        View view = getLayoutInflater().inflate(R.layout.activity_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view).show();

        Button btnLogin = view.findViewById(R.id.btn_login);
        final EditText user_id = view.findViewById(R.id.user_id);
        final EditText user_pw = view.findViewById(R.id.user_pw);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                HashMap<String, String> map = new HashMap<>();

                map.put("user_id", user_id.getText().toString());
                map.put("user_pw", user_pw.getText().toString());

                Call<LoginActivity> call = retrofitInterface.executeLogin(map);

                call.enqueue(new Callback<LoginActivity>() {
                    @Override
                    public void onResponse(Call<LoginActivity> call, Response<LoginActivity> response) {
                        if(response.code() == 200) {

                            LoginActivity login = response.body();
                            //Log.d(TAG ,"onResponse: ??????, ??????\n" + login.toString());

                            AlertDialog.Builder login_builder = new AlertDialog.Builder(MainActivity.this);
                            login_builder.setTitle("?????? ?????? ?????? ??????");
                            login_builder.setMessage("????????? ???????????????????");
                            login_builder.setPositiveButton("???",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(getApplicationContext(), "????????????????????? ???????????????.", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
                                            startActivity(intent);
                                        }
                            });
                            login_builder.setNegativeButton("?????????",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                                        }
                            });
                            login_builder.show();
                        } else if (response.code() == 204) {
                            Toast.makeText(getApplicationContext(), "????????? ?????? ??????????????? ?????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 404) {
                            Toast.makeText(MainActivity.this, "????????? ???????????? ???????????????.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginActivity> call, Throwable t){
                        //?????? ?????? (????????? ?????? ??????, ?????? ?????? ??? ??????????????? ??????)
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    private void handleRegisterDialog() {
        View view = getLayoutInflater().inflate(R.layout.activity_register, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view).show();

        Button btn_register = view.findViewById(R.id.btn_register);
        final EditText name = view.findViewById(R.id.name);
        final EditText email = view.findViewById(R.id.email);
        final EditText register_id = view.findViewById(R.id.register_id);
        final EditText register_pw = view.findViewById(R.id.register_pw);

        btn_register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();

                map.put("name", name.getText().toString());
                map.put("email", email.getText().toString());
                map.put("user_id", register_id.getText().toString());
                map.put("user_pw", register_pw.getText().toString());

                Call<Void> call = retrofitInterface.executeRegister(map);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(response.code() == 200) {
                            Toast.makeText(MainActivity.this,
                                    "??????????????? ??????????????? ???????????????.", Toast.LENGTH_LONG).show();
                        } else if(response.code() == 400) {
                            Toast.makeText(MainActivity.this,
                                    "?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(MainActivity.this, t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}