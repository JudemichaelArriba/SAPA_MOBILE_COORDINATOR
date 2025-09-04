package com.example.sapa;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityAddStudentPageBinding;
import com.example.sapa.models.defaultResponse;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class add_student_page extends AppCompatActivity {

    private ActivityAddStudentPageBinding binding;
    private String selectedGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAddStudentPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.backButton.setOnClickListener(v -> finish());

        binding.genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMale) {
                selectedGender = "Male";
            } else if (checkedId == R.id.radioFemale) {
                selectedGender = "Female";
            }
        });

        binding.birthdayInput.setOnClickListener(v -> showDatePickerDialog());

        binding.addStudentBtn.setOnClickListener(v -> addStudent());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                    binding.birthdayInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void addStudent() {

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", "");

        String firstName = binding.firstname.getText().toString().trim();
        String lastName = binding.lastname.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String contactNo = binding.mobileNumber.getText().toString().trim();
        String birthdate = binding.birthdayInput.getText().toString().trim();
        String schoolId = getIntent().getStringExtra("school_id");

        if(firstName.isBlank() || lastName.isBlank() || email.isBlank() || contactNo.isBlank() || birthdate.isBlank()){
            KAlertDialog nullFieldsDialog = new KAlertDialog(add_student_page.this, true);
            nullFieldsDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
            nullFieldsDialog.setTitleText("Missing Fields")
                    .setContentText("Please fill all the fields.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                    .show();
            return;
        }

        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<defaultResponse> call = apiInterface.addStudent(firstName, lastName, contactNo, email, birthdate, selectedGender, coordinatorId, schoolId);

        call.enqueue(new Callback<defaultResponse>() {
            @Override
            public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();

                    int alertType = KAlertDialog.SUCCESS_TYPE;
                    if(message.toLowerCase().contains("must be at least 18") || message.toLowerCase().contains("exists")){
                        alertType = KAlertDialog.WARNING_TYPE;
                    }

                    KAlertDialog dialog = new KAlertDialog(add_student_page.this, true);
                    dialog.changeAlertType(alertType);

                    if(alertType == KAlertDialog.SUCCESS_TYPE){
                        dialog.setTitleText("Success")
                                .setContentText(message);
                    } else {
                        dialog.setTitleText("Warning")
                                .setContentText(message);
                    }

                    final int finalAlertType = alertType;
                    dialog.setConfirmText("OK")
                            .confirmButtonColor(R.color.mainColor)
                            .setConfirmClickListener(sweetAlertDialog -> {
                                sweetAlertDialog.dismissWithAnimation();
                                if(finalAlertType == KAlertDialog.SUCCESS_TYPE) finish();
                            })
                            .show();

                } else {
                    KAlertDialog errorDialog = new KAlertDialog(add_student_page.this, true);
                    errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                    errorDialog.setTitleText("Failed")
                            .setContentText("Failed to add student. Server returned an error.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                            .show();
                }
            }

            @Override
            public void onFailure(Call<defaultResponse> call, Throwable t) {
                KAlertDialog errorDialog = new KAlertDialog(add_student_page.this, true);
                errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                errorDialog.setTitleText("Network Error")
                        .setContentText(t.getMessage())
                        .setConfirmText("OK")
                        .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                        .show();
            }
        });
    }
}
