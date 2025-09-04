package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.StudentAdapter;
import com.example.sapa.databinding.ActivityStudentPageBinding;
import com.example.sapa.models.School;
import com.example.sapa.models.Students;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class studentPage extends AppCompatActivity {

    private ActivityStudentPageBinding binding;
    private ApiInterface apiInterface;
    private StudentAdapter adapter;
    private List<Students> studentsList = new ArrayList<>();
    private List<Students> allStudentsList = new ArrayList<>();
    private boolean isFetching = false;

    private List<School> schoolList = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(studentsList, this, false);
        binding.recyclerView.setAdapter(adapter);


        adapter.setOnStudentClickListener(student -> {
            Intent intent = new Intent(studentPage.this, studentInfo.class);
            intent.putExtra("id", student.getId());
            intent.putExtra("fullname", student.getStudentFullname());
            intent.putExtra("email", student.getEmail());
            intent.putExtra("birthdate", student.getBirthdate());
            intent.putExtra("gender", student.getGender());
            intent.putExtra("schoolName", student.getSchoolName());
            startActivity(intent);
        });

        apiInterface = ApiClient.getClient(this).create(ApiInterface.class);


        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.schoolSpinner.setAdapter(spinnerAdapter);


        binding.schoolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterStudents(binding.search.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        binding.search.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
            }
        });

        binding.backButton.setOnClickListener(v -> finish());
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(studentPage.this, select_school.class);
            startActivity(intent);
            finish();
        });
    }

    private void filterStudents(String query) {
        String selectedSchoolName = binding.schoolSpinner.getSelectedItem().toString();
        List<Students> filteredList = new ArrayList<>();

        for (Students s : allStudentsList) {
            boolean matchesQuery = s.getStudentFullname().toLowerCase().contains(query.toLowerCase()) ||
                    s.getId().toLowerCase().contains(query.toLowerCase());

            boolean matchesSchool = selectedSchoolName.equals("All") ||
                    (getSchoolNameById(s.getSchoolId()).equalsIgnoreCase(selectedSchoolName));

            if (matchesQuery && matchesSchool) {
                filteredList.add(s);
            }
        }

        studentsList.clear();
        studentsList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    private String getSchoolNameById(String schoolId) {
        for (School school : schoolList) {
            if (school.getSchoolId().equals(schoolId)) return school.getSchoolName();
        }
        return "";
    }

    private void fetchSchools() {
        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<School>> call = apiInterface.getSchools(coordinatorId);
        call.enqueue(new Callback<List<School>>() {
            @Override
            public void onResponse(Call<List<School>> call, Response<List<School>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<School> responseList = response.body();
                    schoolList.clear();
                    for (School s : responseList) {
                        if (!"pending".equalsIgnoreCase(s.getSchoolStatus())) {
                            schoolList.add(s);
                        }
                    }

                    List<String> schoolNames = new ArrayList<>();
                    schoolNames.add("All");
                    for (School s : schoolList) schoolNames.add(s.getSchoolName());

                    spinnerAdapter.clear();
                    spinnerAdapter.addAll(schoolNames);
                    spinnerAdapter.notifyDataSetChanged();
                    binding.schoolSpinner.setSelection(0);
                } else {
                    Toast.makeText(studentPage.this, "No schools found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<School>> call, Throwable t) {
                Toast.makeText(studentPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchStudents() {
        if (isFetching) return;
        isFetching = true;

        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", null);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(this, "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
            isFetching = false;
            return;
        }

        Call<List<Students>> call = apiInterface.getStudentsByCoordinator(coordinatorId);
        call.enqueue(new Callback<List<Students>>() {
            @Override
            public void onResponse(Call<List<Students>> call, Response<List<Students>> response) {
                isFetching = false;
                if (response.isSuccessful() && response.body() != null) {
                    allStudentsList.clear();
                    allStudentsList.addAll(response.body());
                    studentsList.clear();
                    studentsList.addAll(allStudentsList);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(studentPage.this, "No students found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Students>> call, Throwable t) {
                isFetching = false;
                Toast.makeText(studentPage.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSchools();
        fetchStudents();
    }
}
