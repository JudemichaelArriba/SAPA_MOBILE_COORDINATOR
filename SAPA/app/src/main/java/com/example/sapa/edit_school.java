package com.example.sapa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityEditSchoolBinding;
import com.example.sapa.models.defaultResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_school extends AppCompatActivity {

    private ActivityEditSchoolBinding binding;
    private String schoolId = "";
    private String schoolImageBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityEditSchoolBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            schoolId = extras.getString("school_id", "");
            String schoolName = extras.getString("school_name", "");
            String schoolMobile = extras.getString("school_mobile", "");
            String schoolEmail = extras.getString("school_email", "");
            String schoolAddress = extras.getString("school_address", "");
            schoolImageBase64 = extras.getString("school_image_base64", "");

            binding.schoolName.setText(schoolName);
            binding.mobileNumber.setText(schoolMobile);
            binding.emailTxt.setText(schoolEmail);
            binding.addressTxt.setText(schoolAddress);

            if (schoolImageBase64 != null && !schoolImageBase64.isEmpty()) {
                try {
                    if (schoolImageBase64.startsWith("data:image")) {
                        schoolImageBase64 = schoolImageBase64.substring(schoolImageBase64.indexOf(",") + 1);
                    }
                    byte[] decodedBytes = Base64.decode(schoolImageBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        binding.profileImage.setImageBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        binding.backButton.setOnClickListener(v -> finish());

        binding.updateBtn.setOnClickListener(v -> {
            String updatedName = binding.schoolName.getText().toString().trim();
            String updatedMobile = binding.mobileNumber.getText().toString().trim();
            String updatedEmail = binding.emailTxt.getText().toString().trim();
            String updatedAddress = binding.addressTxt.getText().toString().trim();

            if (schoolId.isEmpty()) {
                showErrorDialog("Error: School ID not found.");
                return;
            }


            KAlertDialog confirmDialog = new KAlertDialog(edit_school.this, true);
            confirmDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
            confirmDialog.setTitleText("Are you sure you want to update?")
                    .setCancelText("NO")
                    .setConfirmText("YES")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        updateSchool(updatedName, updatedAddress, updatedEmail, updatedMobile);
                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                    .show();
        });
    }

    private void updateSchool(String name, String address, String email, String mobile) {
        ApiInterface apiInterface = ApiClient.getClient(edit_school.this).create(ApiInterface.class);
        Call<defaultResponse> call = apiInterface.updateSchool(
                schoolId,
                name,
                address,
                email,
                mobile,
                schoolImageBase64
        );

        call.enqueue(new Callback<defaultResponse>() {
            @Override
            public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String msg = response.body().getMessage();

                    if (msg.equalsIgnoreCase("no changes detected; update canceled.")) {


                        KAlertDialog errorDialog = new KAlertDialog(edit_school.this, true);
                        errorDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                        errorDialog.setTitleText("No Changes Detected!")
                                .setContentText("Update has been canceled.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                .show();
//                        showWarningDialog("No Changes Detected!", "Update has been canceled.");
                    } else if (msg.toLowerCase().contains("exists") || msg.toLowerCase().contains("error")) {

                        KAlertDialog errorDialog = new KAlertDialog(edit_school.this, true);
                        errorDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                        errorDialog.setTitleText("Update has been canceled.")
                                .setContentText(msg)
                                .setConfirmText("OK")
                                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                .show();



//                        showErrorDialog(msg);
                    } else {


                        KAlertDialog successDialog = new KAlertDialog(edit_school.this, true);
                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("Updated Successfully!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(kAlertDialog -> {
                                    kAlertDialog.dismissWithAnimation();
                                    finish();
                                })
                                .show();





//                        showSuccessDialog("Updated Successfully!", msg);
//                        finish();
                    }
                } else {

                    KAlertDialog errorDialog = new KAlertDialog(edit_school.this, true);
                    errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                    errorDialog.setTitleText("Update failed. Server returned an error.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                            .show();




//                    showErrorDialog("Update failed. Server returned an error.");
                }
            }

            @Override
            public void onFailure(Call<defaultResponse> call, Throwable t) {
                showErrorDialog("Network Error: " + t.getMessage());
            }
        });
    }

    private void showErrorDialog(String message) {
        KAlertDialog errorDialog = new KAlertDialog(edit_school.this, true);
        errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText("Error")
                .setContentText(message)
                .setConfirmText("OK")
                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                .show();
    }

    private void showWarningDialog(String title, String message) {
        KAlertDialog warningDialog = new KAlertDialog(edit_school.this, true);
        warningDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
        warningDialog.setTitleText(title)
                .setContentText(message)
                .setConfirmText("OK")
                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                .show();
    }

    private void showSuccessDialog(String title, String message) {
        KAlertDialog successDialog = new KAlertDialog(edit_school.this, true);
        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
        successDialog.setTitleText(title)
                .setContentText(message)
                .setConfirmText("OK")
                .confirmButtonColor(R.color.mainColor)
                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                .show();
    }
}
