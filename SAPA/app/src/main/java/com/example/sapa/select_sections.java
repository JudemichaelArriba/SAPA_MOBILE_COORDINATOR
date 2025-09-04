package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.hospitalSectionsAdapter;
import com.example.sapa.databinding.ActivitySelectSectionsBinding;
import com.example.sapa.models.hospitalSections;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class select_sections extends AppCompatActivity {

    private ActivitySelectSectionsBinding binding;
    private hospitalSectionsAdapter sectionsAdapter;
    private List<hospitalSections> sectionList = new ArrayList<>();

    private String schoolId;
    private String hospitalId;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySelectSectionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        schoolId = getIntent().getStringExtra("school_id");
        hospitalId = getIntent().getStringExtra("hospital_id");
        userId = getIntent().getStringExtra("user_id");
        Log.d("SelectSections", "Received school_id: " + schoolId);
        Log.d("SelectSections", "Received hospital_id: " + hospitalId);


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sectionsAdapter = new hospitalSectionsAdapter(this, sectionList, section -> {
            Intent intent = new Intent(select_sections.this, select_slot.class);
            intent.putExtra("section_id", section.getSection_id());
            intent.putExtra("hospital_id", hospitalId);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("billing", section.getBilling());
            intent.putExtra("user_id", userId);
            Log.d("SelectSections", "Received section_id: " + section.getSection_id());
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(sectionsAdapter);

        if (hospitalId != null && !hospitalId.isEmpty()) {
            fetchHospitalSections(hospitalId);
        } else {
            Log.d("SelectSections", "Hospital ID missing ");
        }

        binding.backButton.setOnClickListener(v -> finish());
    }

    private void fetchHospitalSections(String hospitalId) {
        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<List<hospitalSections>> call = apiInterface.getSectionsByHospital(hospitalId);

        call.enqueue(new Callback<List<hospitalSections>>() {
            @Override
            public void onResponse(Call<List<hospitalSections>> call, Response<List<hospitalSections>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sectionList.clear();
                    sectionList.addAll(response.body());
                    sectionsAdapter.notifyDataSetChanged();

                    Log.d("SelectSections", "Fetched " + sectionList.size() + " sections");
                } else {
                    Toast.makeText(select_sections.this, "No sections found", Toast.LENGTH_SHORT).show();
                    Log.e("SelectSections", "Empty response body or not successful");
                }
            }

            @Override
            public void onFailure(Call<List<hospitalSections>> call, Throwable t) {
                Toast.makeText(select_sections.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SelectSections", "Error fetching sections", t);
            }
        });
    }
}
