package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.RecycleviewAdapter.SchoolAdapter;
import com.example.sapa.databinding.FragmentSchoolsAndStudentsBinding;
import com.example.sapa.models.School;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class schoolsAndStudents extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private List<School> schoolList = new ArrayList<>();
    private SchoolAdapter adapter;
    private boolean isFetching = false;

    private FragmentSchoolsAndStudentsBinding binding;

    public schoolsAndStudents() {
    }

    public static schoolsAndStudents newInstance(String param1, String param2) {
        schoolsAndStudents fragment = new schoolsAndStudents();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentSchoolsAndStudentsBinding.inflate(inflater, container, false);


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SchoolAdapter(schoolList, getContext(), null);
        binding.recyclerView.setAdapter(adapter);


        binding.studentCard.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), studentPage.class))
        );

        binding.schoolCard.setOnClickListener(v -> {
            String coordinatorId = requireActivity().getSharedPreferences("user_session", getActivity().MODE_PRIVATE)
                    .getString("coordinator_id", null);

            String requestStatus = requireActivity()
                    .getSharedPreferences("user_session", getActivity().MODE_PRIVATE)
                    .getString("request_status", null);

            if (coordinatorId == null || coordinatorId.isEmpty()) {
                Toast.makeText(getActivity(), "Coordinator ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestStatus == null) {
                Toast.makeText(getActivity(), "Request status not found.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (requestStatus.equalsIgnoreCase("Pending")) {
                KAlertDialog successDialog = new KAlertDialog(getActivity(), true);
                successDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                successDialog.setTitleText("Status is Pending!")
                        .setContentText("Your status is still Pending!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
                        })
                        .show();
                return;
            }

            startActivity(new Intent(getActivity(), schools.class));
        });


        fetchSchools();


        binding.seeAllSchool.setOnClickListener(v -> {
            String coordinatorId = requireActivity()
                    .getSharedPreferences("user_session", requireActivity().MODE_PRIVATE)
                    .getString("coordinator_id", null);

            String requestStatus = requireActivity()
                    .getSharedPreferences("user_session", requireActivity().MODE_PRIVATE)
                    .getString("request_status", null);
            Log.d("schoolandstudent", "request_status: " + requestStatus);
            if (coordinatorId == null || coordinatorId.isEmpty()) {
                Toast.makeText(requireActivity(), "Coordinator ID not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestStatus == null) {
                Toast.makeText(requireActivity(), "Request status not found.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (requestStatus.equalsIgnoreCase("Pending")) {
                KAlertDialog successDialog = new KAlertDialog(requireActivity(), true);
                successDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                successDialog.setTitleText("Status is Pending!")
                        .setContentText("Your status is still Pending!")
                        .setConfirmText("OK")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
                        })
                        .show();
                return;
            }

            startActivity(new Intent(requireActivity(), schools.class));
        });


        return binding.getRoot();


    }

    private void fetchSchools() {
        if (isFetching) return;
        isFetching = true;

        String coordinatorId = requireActivity().getSharedPreferences("user_session", getActivity().MODE_PRIVATE)
                .getString("coordinator_id", null);

        Log.d("SchoolsAndStudentFragment", "Coordinator id for testing: " + coordinatorId);

        if (coordinatorId == null || coordinatorId.isEmpty()) {
            Toast.makeText(getActivity(), "Coordinator ID is missing!", Toast.LENGTH_SHORT).show();
            isFetching = false;
            return;
        }

        ApiInterface apiInterface = ApiClient.getClient(requireContext()).create(ApiInterface.class);
        Call<List<School>> call = apiInterface.getSchools(coordinatorId);
        call.enqueue(new Callback<List<School>>() {
            @Override
            public void onResponse(Call<List<School>> call, Response<List<School>> response) {
                isFetching = false;
                if (response.isSuccessful()) {
                    List<School> responseList = response.body();
                    if (responseList != null && !responseList.isEmpty()) {
                        schoolList.clear();
                        schoolList.addAll(responseList);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("SchoolsAndStudentFragment", "Empty list");
                        Toast.makeText(getActivity(), "No schools", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("SchoolsAndStudentFragment", "Error: " + errorBody);
                    } catch (Exception e) {
                        Log.e("SchoolsAndStudentFragment", "Error", e);
                    }
                    Toast.makeText(getActivity(), "Server returned error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<School>> call, Throwable t) {
                isFetching = false;
                Log.e("SchoolsAndStudentFragment", "API: " + t.getMessage(), t);
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchSchools();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // prevent memory leaks
    }
}
