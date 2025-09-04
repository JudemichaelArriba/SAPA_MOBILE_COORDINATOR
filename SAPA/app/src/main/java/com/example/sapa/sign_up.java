package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat.Type;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivitySingUpBinding;

public class sign_up extends AppCompatActivity {
    private ActivitySingUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.singUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstName = binding.firstname.getText().toString().trim();
                String lastName = binding.lastname.getText().toString().trim();
                String email = binding.emailTxt.getText().toString().trim();
                String contactNo = binding.mobileNumber.getText().toString().trim();
                String username = binding.username.getText().toString().trim();
                String password = binding.sPassword.getText().toString().trim();

                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                        contactNo.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(sign_up.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (contactNo.length() >11){
                    KAlertDialog warningDialog = new KAlertDialog(sign_up.this, true); // cancelable = true
                    warningDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                    warningDialog.setTitleText("Alert!")
                            .setContentText("Contact number is above 11!")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sDialog -> sDialog.dismissWithAnimation());
                    warningDialog.show();
                }


                else {
                    signUpUser(firstName, lastName, email, contactNo, username, password);
                }

            }
        });

        binding.loginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logInSwitch();
            }
        });


    }


    private void logInSwitch() {

        Intent intent = new Intent(sign_up.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    private void signUpUser(String firstName, String lastName, String email, String contactNo, String username, String password) {
        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);

        Call<SignUpResponse> call = apiInterface.signUp(firstName, lastName, email, contactNo, username, password);

        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {


                        KAlertDialog successDialog = new KAlertDialog(sign_up.this, true); // cancelable = true
                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("Success!")
                                .setContentText("Successfully Signup")
                                .setConfirmText("OK")
                                .setConfirmClickListener(sDialog -> {
                                    sDialog.dismissWithAnimation();
                                    logInSwitch();
                                });
                        successDialog.show();

//                        Toast.makeText(sign_up.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
//                        logInSwitch();
                    } else {

                        KAlertDialog warningDialog = new KAlertDialog(sign_up.this, true); // cancelable = true
                        warningDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                        warningDialog.setTitleText("Alert!")
                                .setContentText(response.body().getMessage())
                                .setConfirmText("OK")
                                .setConfirmClickListener(sDialog -> sDialog.dismissWithAnimation());
                        warningDialog.show();


//                        Toast.makeText(sign_up.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(sign_up.this, "Server returned error.", Toast.LENGTH_SHORT).show();
                    Log.e("Signup", "Connection failed: "+  response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Toast.makeText(sign_up.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("Signup", "Connection failed: "+ t.getMessage());
            }
        });

    }


}