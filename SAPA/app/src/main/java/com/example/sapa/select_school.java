package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.SchoolAdapter;
import com.example.sapa.databinding.ActivitySelectSchoolBinding;
import com.example.sapa.models.School;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class select_school extends AppCompatActivity {

    private ActivitySelectSchoolBinding binding;
    private List<School> schoolList = new ArrayList<>();
    private SchoolAdapter adapter;
    private ApiInterface apiInterface;
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectSchoolBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);


        adapter = new SchoolAdapter(schoolList, this, school -> {

            String schoolId = school.getSchoolId();
            Intent intent = new Intent(select_school.this, add_student_page.class);
            intent.putExtra("school_id", schoolId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);


        fetchSchools();

        binding.backButton.setOnClickListener(v -> {
            Intent intent = new Intent(select_school.this, studentPage.class);
            startActivity(intent);
            finish();
        });
    }


    private void fetchSchools() {
        if (isFetching) return;
        isFetching = true;

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        Log.d("SelectSchool", "Coordinator ID: " + coordinatorId);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(select_school.this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
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

                        List<School> approvedSchools = new ArrayList<>();
                        for (School s : responseList) {
                            if ("Approved".equalsIgnoreCase(s.getSchoolStatus())) {
                                approvedSchools.add(s);
                            }
                        }

                        if (!approvedSchools.isEmpty()) {
                            adapter.updateData(approvedSchools);
                        } else {
                            Toast.makeText(select_school.this, "No approved schools found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("SelectSchool", "Empty list or null body");
                        Toast.makeText(select_school.this, "No schools found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(select_school.this, "Server returned error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<List<School>> call, Throwable t) {
                isFetching = false;
                Log.e("SelectSchool", "API Failure: " + t.getMessage(), t);
                Toast.makeText(select_school.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSchools();
    }
}
