package com.example.sapa;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.StudentAdapter;
import com.example.sapa.databinding.ActivitySelectedAppointmentBinding;
import com.example.sapa.models.Students;
import com.example.sapa.models.defaultResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class selected_appointment extends AppCompatActivity {

    private ActivitySelectedAppointmentBinding binding;
    private StudentAdapter studentAdapter;
    private List<Students> studentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectedAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        String slotName = getIntent().getStringExtra("slotName");
        String startTime = getIntent().getStringExtra("startTime");
        String endTime = getIntent().getStringExtra("endTime");
        String hospitalName = getIntent().getStringExtra("hospitalName");
        String sectionName = getIntent().getStringExtra("sectionName");
        String status = getIntent().getStringExtra("status");
        int totalStudents = getIntent().getIntExtra("totalStudents", 0);
        int appointment_id = getIntent().getIntExtra("appointment_id", -1);

        Log.d("selected_appointments", "appointment_id: " + appointment_id);

        binding.slotNameValue.setText(slotName != null ? slotName : "N/A");
        binding.startTimeValue.setText(startTime != null ? startTime : "N/A");
        binding.endTimeValue.setText(endTime != null ? endTime : "N/A");
        binding.hospitalNameValue.setText(hospitalName != null ? hospitalName : "N/A");
        binding.sectionNameValue.setText(sectionName != null ? sectionName : "N/A");
        binding.statusValue.setText(status != null ? status : "N/A");
        binding.maxCapacityValue.setText(String.valueOf(totalStudents));


        if (status != null && status.equalsIgnoreCase("Cancelled")) {
            binding.cancelAppointmentBtn.setEnabled(false);
            binding.cancelAppointmentBtn.setAlpha(0.5f);
        } else {
            binding.cancelAppointmentBtn.setEnabled(true);
            binding.cancelAppointmentBtn.setAlpha(1f);
        }


        binding.studentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(studentList, this, false);
        binding.studentsRecyclerView.setAdapter(studentAdapter);


        if (appointment_id != -1) {
            fetchStudents(appointment_id);
        }

        binding.backButton.setOnClickListener(v -> finish());

        binding.cancelAppointmentBtn.setOnClickListener(v -> {
            if (appointment_id == -1) {
                Toast.makeText(this, "Invalid Appointment ID", Toast.LENGTH_SHORT).show();
                return;
            }

            ApiInterface api = ApiClient.getClient(this).create(ApiInterface.class);
            Call<defaultResponse> call = api.cancelAppointmentsByAppointmentId(appointment_id);

            call.enqueue(new Callback<defaultResponse>() {
                @Override
                public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        KAlertDialog successDialog = new KAlertDialog(selected_appointment.this, true);
                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("Successful")
                                .setContentText(response.body().getMessage())
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                })
                                .show();
                    } else {
                        KAlertDialog errorDialog = new KAlertDialog(selected_appointment.this, true);
                        errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                        errorDialog.setTitleText("Error")
                                .setContentText("Failed to cancel appointment")
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                })
                                .show();
                        Log.d("selected_appointments", "response: " + response.body());
                    }
                }

                @Override
                public void onFailure(Call<defaultResponse> call, Throwable t) {
                    Log.d("selected_appointments", "error: " + t.getMessage());
                    Toast.makeText(selected_appointment.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void fetchStudents(int appointment_id) {
        ApiInterface api = ApiClient.getClient(this).create(ApiInterface.class);
        Call<List<Students>> call = api.getStudentsByAppointment(appointment_id);

        call.enqueue(new Callback<List<Students>>() {
            @Override
            public void onResponse(Call<List<Students>> call, Response<List<Students>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    studentList.clear();
                    studentList.addAll(response.body());
                    studentAdapter.notifyDataSetChanged();
                    for (Students s : studentList) {
                        Log.d("selected_appointments", "Student: " + s.getStudentFullname()
                                + ", Email: " + s.getEmail()
                                + ", School: " + s.getSchoolName());
                    }
                } else {
                    Log.d("selected_appointments", "Empty response or null body");
                    Toast.makeText(selected_appointment.this, "No students found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Students>> call, Throwable t) {
                Log.e("selected_appointments", "Error fetching students: " + t.getMessage(), t);
                Toast.makeText(selected_appointment.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
