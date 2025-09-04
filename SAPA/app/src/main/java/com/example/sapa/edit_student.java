package com.example.sapa;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityEditStudentBinding;
import com.example.sapa.models.defaultResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_student extends AppCompatActivity {

    private ActivityEditStudentBinding binding;
    private ApiInterface apiInterface;
    private String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);

        binding.backButton.setOnClickListener(v -> finish());


        studentId = getIntent().getStringExtra("id");
        String fullname = getIntent().getStringExtra("fullname");
        String email = getIntent().getStringExtra("email");
        String birthday = getIntent().getStringExtra("birthdate");
        String gender = getIntent().getStringExtra("gender");


        if (fullname != null && fullname.contains(" ")) {
            String[] parts = fullname.split(" ", 2);
            binding.firstname.setText(parts[0]);
            binding.lastname.setText(parts.length > 1 ? parts[1] : "");
        } else {
            binding.firstname.setText(fullname != null ? fullname : "");
        }

        binding.email.setText(email != null ? email : "");
        binding.birthdayInput.setText(birthday != null ? birthday : "");

        if ("Male".equalsIgnoreCase(gender)) {
            binding.radioMale.setChecked(true);
        } else if ("Female".equalsIgnoreCase(gender)) {
            binding.radioFemale.setChecked(true);
        }


        binding.addStudentBtn.setOnClickListener(v -> updateStudent());
    }

    private void updateStudent() {
        String firstname = binding.firstname.getText().toString().trim();
        String lastname = binding.lastname.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String contactNo = binding.mobileNumber.getText().toString().trim();
        String birthday = binding.birthdayInput.getText().toString().trim();

        String gender = "";
        int selectedId = binding.genderGroup.getCheckedRadioButtonId();
        if (selectedId == binding.radioMale.getId()) {
            gender = "Male";
        } else if (selectedId == binding.radioFemale.getId()) {
            gender = "Female";
        }

        if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
                contactNo.isEmpty() || birthday.isEmpty() || gender.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<defaultResponse> call = apiInterface.updateStudent(
                studentId,
                firstname,
                lastname,
                contactNo,
                email,
                birthday,
                gender
        );

        call.enqueue(new Callback<defaultResponse>() {
            @Override
            public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    defaultResponse res = response.body();
                    Toast.makeText(edit_student.this, res.getMessage(), Toast.LENGTH_SHORT).show();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        finish();
                    }
                } else {
                    Toast.makeText(edit_student.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<defaultResponse> call, Throwable t) {
                Toast.makeText(edit_student.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
