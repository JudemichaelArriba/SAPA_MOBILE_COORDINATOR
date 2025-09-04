package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.SchoolAdapter;
import com.example.sapa.databinding.ActivitySchoolsBinding;
import com.example.sapa.models.School;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class schools extends AppCompatActivity {
    private ActivitySchoolsBinding binding;
    private List<School> schoolList = new ArrayList<>();
    private List<School> fullSchoolList = new ArrayList<>();
    private SchoolAdapter adapter;
    private ApiInterface apiInterface;
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySchoolsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SchoolAdapter(schoolList, this, school -> {
            Intent intent = new Intent(schools.this, schoolInfo.class);
            intent.putExtra("school_id", school.getSchoolId());
            intent.putExtra("school_name", school.getSchoolName());
            intent.putExtra("school_address", school.getSchoolAddress());
            intent.putExtra("school_status", school.getSchoolStatus());
            intent.putExtra("school_image_base64", school.getImageBase64());
            intent.putExtra("school_mobile", school.getContactNo());
            intent.putExtra("school_email", school.getSchoolEmail());
            intent.putExtra("StudentCount", school.getStudentCount());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);

        fetchSchools();

        binding.backButton.setOnClickListener(v -> finish());

        binding.fab.setOnClickListener(view -> {
            String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                    .getString("coordinator_id", null);
            String requestStatus = getSharedPreferences("user_session", MODE_PRIVATE)
                    .getString("request_status", null);

            Log.d("SchoolsActivity", "Coordinator ID: " + coordinatorId);
            Log.d("SchoolsActivity", "request_status: " + requestStatus);

            if (coordinatorId == null || coordinatorId.isEmpty()) {
                Toast.makeText(schools.this, "Coordinator ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("Pending".equalsIgnoreCase(requestStatus)) {
                KAlertDialog successDialog = new KAlertDialog(schools.this, true);
                successDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                successDialog.setTitleText("Status is Pending!")
                        .setContentText("Your status is still Pending!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                        .show();
                return;
            }

            Intent intent = new Intent(schools.this, add_schools.class);
            startActivity(intent);
        });


        binding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterSchools(binding.search.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        binding.search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSchools(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterSchools(String query) {
        String selectedStatus = binding.statusSpinner.getSelectedItem().toString();
        List<School> filteredList = new ArrayList<>();

        for (School school : fullSchoolList) {
            boolean matchesQuery = school.getSchoolName().toLowerCase().contains(query.toLowerCase()) ||
                    school.getSchoolAddress().toLowerCase().contains(query.toLowerCase()) ||
                    school.getSchoolStatus().toLowerCase().contains(query.toLowerCase());

            boolean matchesStatus = selectedStatus.equals("All") ||
                    school.getSchoolStatus().equalsIgnoreCase(selectedStatus);

            if (matchesQuery && matchesStatus) {
                filteredList.add(school);
            }
        }

        adapter.updateData(filteredList);
    }

    private void fetchSchools() {
        if (isFetching) return;
        isFetching = true;

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        Log.d("SchoolsActivity", "Coordinator ID: " + coordinatorId);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(schools.this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
            isFetching = false;
            return;
        }

        Call<List<School>> call = apiInterface.getSchools(coordinatorId);
        call.enqueue(new Callback<List<School>>() {
            @Override
            public void onResponse(Call<List<School>> call, Response<List<School>> response) {
                isFetching = false;
                if (response.isSuccessful() && response.body() != null) {
                    fullSchoolList.clear();
                    fullSchoolList.addAll(response.body());

                    schoolList.clear();
                    schoolList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(schools.this, "No schools found for this coordinator", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<School>> call, Throwable t) {
                isFetching = false;
                Toast.makeText(schools.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSchools();
    }
}
