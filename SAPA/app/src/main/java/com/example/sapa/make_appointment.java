package com.example.sapa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityMakeAppointmentBinding;
import com.example.sapa.models.AppointmentRequest;
import com.example.sapa.models.defaultResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class make_appointment extends AppCompatActivity {

    private ActivityMakeAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMakeAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.makeAppointment, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        String slotName = getIntent().getStringExtra("slot_name");
        String startTime = getIntent().getStringExtra("start_time");
        String endTime = getIntent().getStringExtra("end_time");
        int maxCapacity = getIntent().getIntExtra("max_capacity", 0);
        String hospitalName = getIntent().getStringExtra("hospital_name");
        String sectionName = getIntent().getStringExtra("section_name");
        int sectionId = getIntent().getIntExtra("section_id", 0);
        String hospitalId = getIntent().getStringExtra("hospital_id");
        String schoolId = getIntent().getStringExtra("school_id");
        int slotId = getIntent().getIntExtra("slot_id", 0);
        double billing = getIntent().getDoubleExtra("billing", 0.0);
        String userId = getIntent().getStringExtra("user_id");
        ArrayList<String> selectedStudentIds = getIntent().getStringArrayListExtra("selected_student_ids");


        Log.d("MakeAppointment", "userId: " + userId);
        Log.d("MakeAppointment", "students: " + selectedStudentIds);

        binding.slotNameValue.setText(slotName != null ? slotName : "N/A");
        binding.startTimeValue.setText(startTime != null ? startTime : "N/A");
        binding.endTimeValue.setText(endTime != null ? endTime : "N/A");
        binding.studentsCount.setText(String.valueOf(selectedStudentIds.size()));
        binding.hospitalNameValue.setText(hospitalName != null ? hospitalName : "N/A");
        binding.sectionNameValue.setText(sectionName != null ? sectionName : "N/A");
        binding.paymentValue.setText(String.valueOf(billing));

        binding.actionButton.setOnClickListener(v -> {
            if (selectedStudentIds != null && selectedStudentIds.size() > maxCapacity) {
                Toast.makeText(this, "Selected students exceed maximum capacity", Toast.LENGTH_SHORT).show();
                return;
            }


            if (userId == null) {
                Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
                return;
            }


            Log.d("MakeAppointment", "Sending student IDs: " + selectedStudentIds);
            AppointmentRequest request = new AppointmentRequest(slotId, userId, selectedStudentIds);


            ApiInterface api = ApiClient.getClient(make_appointment.this).create(ApiInterface.class);

            api.addAppointment(request).enqueue(new Callback<defaultResponse>() {
                @Override
                public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        defaultResponse res = response.body();
//                        Toast.makeText(make_appointment.this, res.getMessage(), Toast.LENGTH_SHORT).show();

                        if ("success".equalsIgnoreCase(res.getStatus())) {


                            KAlertDialog successDialog = new KAlertDialog(make_appointment.this, true);
                            successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                            successDialog.setTitleText("Successful")
                                    .setContentText("Successfully Booked!")
                                    .setConfirmText("OK")
                                    .confirmButtonColor(R.color.mainColor)
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                        finish();
                                    })
                                    .show();

                        }
                    } else {


                        KAlertDialog successDialog = new KAlertDialog(make_appointment.this, true);
                        successDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                        successDialog.setTitleText("Error")
                                .setContentText("Failed to add appointment")
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                })
                                .show();


                        Log.e("make_appointment", "Unexpected response code: " + response.code());
//                        Toast.makeText(make_appointment.this, "Failed to add appointment", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<defaultResponse> call, Throwable t) {
                    Toast.makeText(make_appointment.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("MakeAppointment", "API Error", t);
                }
            });
        });

        binding.backButton.setOnClickListener(v -> finish());
    }
}
