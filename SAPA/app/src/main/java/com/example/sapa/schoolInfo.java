package com.example.sapa;

import android.content.Intent;
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
import com.example.sapa.databinding.ActivitySchoolInfoBinding;
import com.example.sapa.models.defaultResponse;

import retrofit2.Call;

public class schoolInfo extends AppCompatActivity {

    private ActivitySchoolInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        binding = ActivitySchoolInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String school_id = extras.getString("school_id","N/A");
            String schoolName = extras.getString("school_name", "N/A");
            String schoolAddress = extras.getString("school_address", "N/A");
            String schoolStatus = extras.getString("school_status", "N/A");
            String schoolEmail = extras.getString("school_email", "N/A"); // if you passed email; else remove
            String schoolImageBase64 = extras.getString("school_image_base64", "");
            String schoolMobile = extras.getString("school_mobile", "N/A");
           int studentCount = extras.getInt("StudentCount",0);

            binding.schoolName1.setText(schoolName);
            binding.schoolAddressTv.setText(schoolAddress);
            binding.statusTv.setText(schoolStatus);
            binding.schoolEmailTv.setText(schoolEmail);
            binding.mobileTv2.setText(schoolMobile);
            binding.studentsCountTv.setText(String.valueOf(studentCount));

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


        binding.backButton.setOnClickListener(v -> {

            finish();

        });

        binding.editSchool.setOnClickListener(v->{

            Intent intent = new Intent(schoolInfo.this, edit_school.class);


            Bundle extras1 = getIntent().getExtras();
            if (extras1 != null) {
                String schoolId = extras1.getString("school_id", "");
                String schoolImageBase64 = extras1.getString("school_image_base64", "");

                intent.putExtra("school_id", schoolId);
                intent.putExtra("school_image_base64", schoolImageBase64);
            }


            intent.putExtra("school_name", binding.schoolName1.getText().toString());
            intent.putExtra("school_address", binding.schoolAddressTv.getText().toString());
            intent.putExtra("school_email", binding.schoolEmailTv.getText().toString());
            intent.putExtra("school_mobile", binding.mobileTv2.getText().toString());

            startActivity(intent);


        });
        binding.deleteBtn.setOnClickListener(v -> {
            String schoolId = getIntent().getStringExtra("school_id");

            if (schoolId == null || schoolId.isEmpty()) {

                KAlertDialog errorDialog = new KAlertDialog(schoolInfo.this, true);
                errorDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                errorDialog.setTitleText("School ID not found")

                        .setConfirmText("OK")
                        .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                        .show();

                return;
            }

            KAlertDialog confirmDialog = new KAlertDialog(schoolInfo.this, true);
            confirmDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
            confirmDialog.setTitleText("Are you sure you want to delete this school?")
                    .setCancelText("NO")
                    .setConfirmText("YES")
                    .setConfirmClickListener(sweetAlertDialog -> {

                        ApiInterface api = ApiClient.getClient(schoolInfo.this)
                                .create(ApiInterface.class);

                        Call<defaultResponse> call = api.deleteSchool(schoolId);

                        call.enqueue(new retrofit2.Callback<defaultResponse>() {
                            @Override
                            public void onResponse(Call<defaultResponse> call, retrofit2.Response<defaultResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    if ("success".equalsIgnoreCase(response.body().getStatus()) ||
                                            "true".equalsIgnoreCase(response.body().getStatus())) {
                                        KAlertDialog successDialog = new KAlertDialog(schoolInfo.this, true);
                                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                                        successDialog.setTitleText("Delete Successfully!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(kAlertDialog -> {
                                                    kAlertDialog.dismissWithAnimation();
                                                    finish();
                                                })
                                                .show();
                                    } else {
                                        KAlertDialog successDialog = new KAlertDialog(schoolInfo.this, true);
                                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                                        successDialog.setTitleText("Delete Successfully!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(kAlertDialog -> {
                                                    kAlertDialog.dismissWithAnimation();
                                                    finish();
                                                })
                                                .show();


                                    }
                                } else {
                                    KAlertDialog errorDialog = new KAlertDialog(schoolInfo.this, true);
                                    errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                                    errorDialog.setTitleText("Failed to delete schools")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                            .show();

                                }
                            }

                            @Override
                            public void onFailure(Call<defaultResponse> call, Throwable t) {
                                KAlertDialog errorDialog = new KAlertDialog(schoolInfo.this, true);
                                errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                                errorDialog.setTitleText("Error")
                                        .setContentText(t.getMessage())
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                        .show();
                            }
                        });


                    })
                    .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                    .show();
        });

    }
}
