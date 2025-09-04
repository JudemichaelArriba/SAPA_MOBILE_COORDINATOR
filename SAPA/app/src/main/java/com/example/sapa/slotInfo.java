package com.example.sapa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sapa.databinding.ActivitySlotInfoBinding;

public class slotInfo extends AppCompatActivity {

    private ActivitySlotInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);


        binding = ActivitySlotInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
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
        String userId = getIntent().getStringExtra("user_id");
        int sectionId = getIntent().getIntExtra("section_id", 0);
        String hospitalId = getIntent().getStringExtra("hospital_id");
        String schoolId = getIntent().getStringExtra("school_id");
        int slotId = getIntent().getIntExtra("slot_id", 0);
        double billing = getIntent().getDoubleExtra("billing",0.0);

        binding.slotNameValue.setText(slotName);
        binding.startTimeValue.setText(startTime);
        binding.endTimeValue.setText(endTime);
        binding.maxCapacityValue.setText(String.valueOf(maxCapacity));
        binding.hospitalNameValue.setText(hospitalName);
        binding.sectionNameValue.setText(sectionName);

        binding.backButton.setOnClickListener(v -> finish());

        binding.actionButton.setOnClickListener(v -> {
            Log.d("SlotInfo", "Slot Confirmed! " +
                    "\nSlot ID: " + slotId +
                    "\nSection ID: " + sectionId +
                    "\nHospital ID: " + hospitalId +
                    "\nSchool ID: " + schoolId);


            Intent intent = new Intent(slotInfo.this, select_students.class);
            intent.putExtra("slot_name", slotName);
            intent.putExtra("start_time", startTime);
            intent.putExtra("end_time", endTime);
            intent.putExtra("max_capacity", maxCapacity);
            intent.putExtra("hospital_name", hospitalName);
            intent.putExtra("section_name", sectionName);
            intent.putExtra("section_id", sectionId);
            intent.putExtra("hospital_id", hospitalId);
            intent.putExtra("school_id", schoolId);
            intent.putExtra("slot_id", slotId);
            intent.putExtra("billing", billing);
            intent.putExtra("user_id", userId);
            startActivity(intent);

        });
    }
}
