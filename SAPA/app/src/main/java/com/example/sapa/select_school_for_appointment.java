package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.SchoolAdapter;
import com.example.sapa.databinding.ActivitySelectSchoolForAppointmentBinding;
import com.example.sapa.models.School;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class select_school_for_appointment extends AppCompatActivity {

    private ActivitySelectSchoolForAppointmentBinding binding;
    private List<School> schoolList = new ArrayList<>();
    private SchoolAdapter adapter;
    private ApiInterface apiInterface;
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        binding = ActivitySelectSchoolForAppointmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);

        adapter = new SchoolAdapter(schoolList, this, school -> {
            String schoolId = school.getSchoolId();


            Intent intent = new Intent(select_school_for_appointment.this, select_hospital.class);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("user_id", coordinatorId);
            startActivity(intent);
        });

        binding.recyclerView.setAdapter(adapter);

        fetchSchools();

        binding.backButton.setOnClickListener(v -> finish());
    }

    private void fetchSchools() {
        if (isFetching) return;
        isFetching = true;

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        Log.d("SelectSchoolForAppt", "Coordinator ID: " + coordinatorId);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
            isFetching = false;
            return;
        }

        Call<List<School>> call = apiInterface.getSchools(coordinatorId);
        call.enqueue(new Callback<List<School>>() {
            @Override
            public void onResponse(Call<List<School>> call, Response<List<School>> response) {
                isFetching = false;
                if (response.isSuccessful()) {
                    List<School> responseList = response.body();
                    if (responseList != null && !responseList.isEmpty()) {

                        List<School> nonPendingSchools = new ArrayList<>();
                        for (School s : responseList) {
                            if (s.getSchoolStatus() != null && !s.getSchoolStatus().equalsIgnoreCase("pending")) {
                                nonPendingSchools.add(s);
                            }
                        }

                        if (!nonPendingSchools.isEmpty()) {
                            adapter.updateData(nonPendingSchools);
                        } else {
                            Toast.makeText(select_school_for_appointment.this, "No approved schools found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(select_school_for_appointment.this, "No schools found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(select_school_for_appointment.this, "Server returned error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<School>> call, Throwable t) {
                isFetching = false;
                Log.e("SelectSchoolForAppt", "API Failure: " + t.getMessage(), t);
                Toast.makeText(select_school_for_appointment.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSchools();
    }
}
