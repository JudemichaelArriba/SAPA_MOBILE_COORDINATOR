package com.example.sapa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.UpcomingAppointmentsAdapter;
import com.example.sapa.databinding.FragmentAppointmentsPageBinding;
import com.example.sapa.models.UpcomingAppointment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Appointments_page extends Fragment {

    private FragmentAppointmentsPageBinding binding;
    private List<UpcomingAppointment> appointmentList;
    private List<UpcomingAppointment> allAppointments;
    private UpcomingAppointmentsAdapter adapter;

    public Appointments_page() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppointmentsPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appointmentList = new ArrayList<>();
        allAppointments = new ArrayList<>();
        adapter = new UpcomingAppointmentsAdapter(requireContext(), appointmentList, appointment -> {

            int totalStudents = 0;
            for (UpcomingAppointment appt : allAppointments) {
                if (appt.getSlotId() == appointment.getSlotId()) {
                    if (appt.getStudentCount() > 0) {
                        totalStudents += appt.getStudentCount();
                    } else {
                        totalStudents++;
                    }
                }
            }

            Intent intent = new Intent(requireContext(), selected_appointment.class);
            intent.putExtra("slotName", appointment.getSlotName());
            intent.putExtra("startTime", appointment.getStartTime());
            intent.putExtra("endTime", appointment.getEndTime());
            intent.putExtra("hospitalName", appointment.getHospitalName());
            intent.putExtra("sectionName", appointment.getSectionName());
            intent.putExtra("status", appointment.getAppointmentStatus());
            intent.putExtra("totalStudents", appointment.getStudentCount());

            intent.putExtra("slot_id", appointment.getSlotId());
            intent.putExtra("appointment_id", appointment.getAppointmentId());
            Log.d("appointment_page", "slotId: " + appointment.getSlotId());
            startActivity(intent);
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        fetchAppointments();

        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), select_school_for_appointment.class);
            startActivity(intent);
        });

        binding.statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedStatus = parent.getItemAtPosition(position).toString();
                filterAppointments(selectedStatus, binding.searchBar.getText().toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String status = binding.statusSpinner.getSelectedItem().toString();
                filterAppointments(status, s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void fetchAppointments() {
        SharedPreferences sharedPreferences = requireActivity()
                .getSharedPreferences("user_session", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("coordinator_id", "");
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            Log.e("Appointments_page", "User ID is null or empty");
            return;
        }

        ApiInterface api = ApiClient.getClient(requireContext()).create(ApiInterface.class);

        api.getUpcomingAppointments(userId).enqueue(new Callback<List<UpcomingAppointment>>() {
            @Override
            public void onResponse(Call<List<UpcomingAppointment>> call, Response<List<UpcomingAppointment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    appointmentList.clear();
                    allAppointments.clear();


                    appointmentList.addAll(response.body());
                    allAppointments.addAll(response.body());

                    adapter.notifyDataSetChanged();
                    Toast.makeText(requireContext(), "Fetched " + appointmentList.size() + " appointments", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "No appointments found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UpcomingAppointment>> call, Throwable t) {
                Toast.makeText(requireContext(), "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterAppointments(String status, String query) {
        appointmentList.clear();

        for (UpcomingAppointment appt : allAppointments) {
            boolean matchesStatus = status.equals("All") ||
                    (appt.getAppointmentStatus() != null && appt.getAppointmentStatus().equalsIgnoreCase(status));

            boolean matchesSearch = query.isEmpty() ||
                    (appt.getHospitalName() != null && appt.getHospitalName().toLowerCase().contains(query.toLowerCase())) ||
                    (appt.getSectionName() != null && appt.getSectionName().toLowerCase().contains(query.toLowerCase())) ||
                    (appt.getAppointmentStatus() != null && appt.getAppointmentStatus().toLowerCase().contains(query.toLowerCase()));

            if (matchesStatus && matchesSearch) {
                appointmentList.add(appt);
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
