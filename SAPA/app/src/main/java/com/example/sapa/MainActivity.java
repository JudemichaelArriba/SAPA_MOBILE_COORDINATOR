package com.example.sapa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityMainBinding;
import com.example.sapa.models.LoginResponse;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;

public class MainActivity extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context context = new ContextThemeWrapper(MainActivity.this, R.style.WhiteDialogTheme);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Sprite FadingCircle = new FadingCircle();
        binding.spinKit.setIndeterminateDrawable(FadingCircle);

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.loginBtn.setEnabled(false);
        binding.loadingOverlay.setVisibility(View.VISIBLE);

        new Thread(() -> {

            String serverUrl = ApiClient.scanServerBlocking(MainActivity.this);
            Log.d("MainForLogin", "Server ready at: " + serverUrl);

            runOnUiThread(() -> {

                binding.loginBtn.setEnabled(true);
                binding.loadingOverlay.setVisibility(View.GONE);
            });
        }).start();


        binding.signUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpSwitch();
            }
        });


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = binding.usernameTv.getText().toString().trim();
                String password = binding.passwordEditText.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {


                    KAlertDialog nullFieldsDialog = new KAlertDialog(MainActivity.this, true); // true = cancelable
                    nullFieldsDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                    nullFieldsDialog.setTitleText("Missing Fields")
                            .setContentText("Please fill in both username and password.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                            })
                            .show();


                } else {
                    binding.loadingOverlay.setVisibility(View.VISIBLE);
                    binding.loginBtn.setEnabled(false);


                    binding.main.post(() -> performLogin(username, password));

                }


            }
        });


    }


    private void showConnectionErrorDialog() {
        KAlertDialog errorDialog = new KAlertDialog(this, true);
        errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText("Connection Error")
                .setContentText("Failed to connect to the server. Please try again.")
                .setConfirmText("OK")
                .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                .show();
    }


    private void performLogin(String username, String password) {

        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.login(username, password);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                binding.loadingOverlay.setVisibility(View.GONE);
                binding.loginBtn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    if (status.equals("success")) {
                        String coordinatorId = response.body().getUser_id();
                        String requestStatus = response.body().getStatus();
                        Log.d("MainForLogin", "requestStatus: " + requestStatus);
                        Log.d("MainForLogin", "coordinatorId: " + coordinatorId);
                        getSharedPreferences("user_session", MODE_PRIVATE)
                                .edit()
                                .putString("coordinator_id", coordinatorId)
                                .putString("request_status", requestStatus)
                                .apply();

                        KAlertDialog successDialog = new KAlertDialog(MainActivity.this, true);
                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("Successfully Logged In!")
                                .setContentText("Welcome back!")
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    dashboardSwitch();
                                })
                                .show();


                    } else {
                        KAlertDialog successDialog = new KAlertDialog(MainActivity.this, true); // true = cancelable
                        successDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                        successDialog.setTitleText("Wrong Credentials!")
                                .setContentText("Please Enter the right username/password")
                                .setConfirmText("OK")
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();

                                })
                                .show();

                    }
                } else {
                    Log.e("MainForLogin", "Unexpected response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                binding.loadingOverlay.setVisibility(View.GONE);
                binding.loginBtn.setEnabled(true);

                Log.e("MainForLogin", "Connection failed: " + t.getMessage(), t);
                KAlertDialog errorDialog = new KAlertDialog(MainActivity.this, true);
                errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                errorDialog.setTitleText("Connection Failed")
                        .setContentText("Unable to connect to the server. Please check your internet or XAMPP.")
                        .setCancelText("Cancel")
                        .setConfirmText("Retry")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();

                            binding.loadingOverlay.setVisibility(View.VISIBLE);
                            binding.loginBtn.setEnabled(false);
                            binding.main.post(() -> performLogin(username, password));
                        })
                        .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                        .show();

            }

        });


    }


    private void signUpSwitch() {

        Intent intent = new Intent(MainActivity.this, sign_up.class);
        startActivity(intent);
        finish();

    }


    private void dashboardSwitch() {

        Intent intent = new Intent(MainActivity.this, dashboard.class);
        startActivity(intent);
        finish();

    }


}

