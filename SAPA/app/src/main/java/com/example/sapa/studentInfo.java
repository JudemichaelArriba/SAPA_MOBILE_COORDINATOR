package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityStudentInfoBinding;
import com.example.sapa.models.defaultResponse;

import retrofit2.Call;

public class studentInfo extends AppCompatActivity {

    private ActivityStudentInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStudentInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        String id = getIntent().getStringExtra("id");
        String fullname = getIntent().getStringExtra("fullname");
        String email = getIntent().getStringExtra("email");
        String birthdate = getIntent().getStringExtra("birthdate");
        String gender = getIntent().getStringExtra("gender");
        String schoolName = getIntent().getStringExtra("schoolName");


        binding.fullnameTv.setText(fullname != null ? fullname : "N/A");
        binding.emailTv.setText(email != null ? email : "N/A");
        binding.birthdateTv.setText(birthdate != null ? birthdate : "N/A");
        binding.genderTv.setText(gender != null ? gender : "N/A");
        binding.schoolNameTv.setText(schoolName != null ? schoolName : "N/A");


        binding.backButton.setOnClickListener(v -> finish());


        binding.deleteBtn.setOnClickListener(v -> {
            String studentId = getIntent().getStringExtra("id");

            if (studentId == null || studentId.isEmpty()) {
                Toast.makeText(studentInfo.this, "Student ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            KAlertDialog confirmDialog = new KAlertDialog(studentInfo.this, true);
            confirmDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
            confirmDialog.setTitleText("Are you sure you want to delete this student?")
                    .setCancelText("NO")
                    .setConfirmText("YES")
                    .setConfirmClickListener(sweetAlertDialog -> {


                        ApiInterface api = ApiClient.getClient(studentInfo.this)
                                .create(ApiInterface.class);

                        Call<defaultResponse> call = api.deleteStudent(studentId);

                        call.enqueue(new retrofit2.Callback<defaultResponse>() {
                            @Override
                            public void onResponse(Call<defaultResponse> call, retrofit2.Response<defaultResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    if ("success".equalsIgnoreCase(response.body().getStatus())) {
                                        KAlertDialog successDialog = new KAlertDialog(studentInfo.this, true);
                                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                                        successDialog.setTitleText("Delete Successfully!")
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(kAlertDialog -> {
                                                    kAlertDialog.dismissWithAnimation();
                                                    finish();
                                                })
                                                .show();

                                    } else {


                                        KAlertDialog errorDialog = new KAlertDialog(studentInfo.this, true);
                                        errorDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                                        errorDialog.setTitleText("delete has been canceled.")
                                                .setContentText(response.body().getMessage())
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                                .show();


                                    }
                                } else {

                                    KAlertDialog errorDialog = new KAlertDialog(studentInfo.this, true);
                                    errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                                    errorDialog.setTitleText("Failed to delete student")
                                            .setConfirmText("OK")
                                            .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                            .show();


                                }
                            }

                            @Override
                            public void onFailure(Call<defaultResponse> call, Throwable t) {


                                KAlertDialog errorDialog = new KAlertDialog(studentInfo.this, true);
                                errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                                errorDialog.setTitleText("Error")
                                        .setContentText(t.getMessage())
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                        .show();

                            }
                        });

                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .setCancelClickListener(KAlertDialog::dismissWithAnimation)
                    .show();
        });


        binding.editStudent.setOnClickListener(v -> {
            Intent intent = new Intent(studentInfo.this, edit_student.class);

            intent.putExtra("id", id);
            intent.putExtra("fullname", fullname);
            intent.putExtra("email", email);
            intent.putExtra("birthdate", birthdate);
            intent.putExtra("gender", gender);
            intent.putExtra("schoolName", schoolName);

            startActivity(intent);
        });
    }
}
