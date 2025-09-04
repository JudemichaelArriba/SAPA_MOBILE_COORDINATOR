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
import com.example.sapa.RecycleviewAdapter.slotAdapter;
import com.example.sapa.databinding.ActivitySelectSlotBinding;
import com.example.sapa.models.hospitalSlots;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class select_slot extends AppCompatActivity {

    private ActivitySelectSlotBinding binding;
    private int sectionId;
    private String hospitalId, schoolId;

    private slotAdapter adapter;
    private List<hospitalSlots> slotList = new ArrayList<>();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivitySelectSlotBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        double payments = getIntent().getDoubleExtra("billing",0);
        sectionId = getIntent().getIntExtra("section_id", 0);
        hospitalId = getIntent().getStringExtra("hospital_id");
        schoolId = getIntent().getStringExtra("school_id");
        userId = getIntent().getStringExtra("user_id");
        Log.d("SelectSlot", "Received section_id: " + sectionId);
        Log.d("SelectSlot", "Received hospital_id: " + hospitalId);
        Log.d("SelectSlot", "Received school_id: " + schoolId);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new slotAdapter(this, slotList, slot -> {
            Intent intent = new Intent(select_slot.this, slotInfo.class);
            intent.putExtra("billing", payments);
            intent.putExtra("section_id", sectionId);
            intent.putExtra("hospital_id", hospitalId);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("slot_id", slot.getSlot_id());
            intent.putExtra("slot_name", slot.getSlot_name());
            intent.putExtra("start_time", slot.getStart_time());
            intent.putExtra("end_time", slot.getEnd_time());
            intent.putExtra("max_capacity", slot.getMax_capacity());
            intent.putExtra("hospital_name", slot.getHospital_name());
            intent.putExtra("section_name", slot.getSection_name());
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);

        fetchSlots(sectionId);

        binding.backButton.setOnClickListener(v -> finish());
    }

    private void fetchSlots(int sectionId) {
        ApiInterface api = ApiClient.getClient(this).create(ApiInterface.class);
        Call<List<hospitalSlots>> call = api.getSlotsBySectionId(sectionId);

        call.enqueue(new Callback<List<hospitalSlots>>() {
            @Override
            public void onResponse(Call<List<hospitalSlots>> call, Response<List<hospitalSlots>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    slotList.clear();
                    for (hospitalSlots slot : response.body()) {
                        if (slot.getMax_capacity() > 0) {
                            slotList.add(slot);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("SelectSlot", "Available slots fetched: " + slotList.size());
                } else {
                    Log.e("SelectSlot", "API Error: " + response.message());
                    Toast.makeText(select_slot.this, "No slots found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<hospitalSlots>> call, Throwable t) {
                Log.e("SelectSlot", "API Error: " + t.getMessage());
                Toast.makeText(select_slot.this, "Failed to fetch slots", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
