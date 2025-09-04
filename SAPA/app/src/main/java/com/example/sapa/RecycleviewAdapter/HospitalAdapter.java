package com.example.sapa.RecycleviewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.R;
import com.example.sapa.models.Hospitals;

import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private Context context;
    private List<Hospitals> hospitalList;
    private OnHospitalClickListener listener;

    public HospitalAdapter(Context context, List<Hospitals> hospitalList, OnHospitalClickListener listener) {
        this.context = context;
        this.hospitalList = hospitalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_hospital, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospitals hospital = hospitalList.get(position);
        holder.hospitalName.setText(hospital.getHospital_name());
        holder.hospitalEmail.setText(hospital.getHospital_email());
        holder.hospitalMobile.setText(hospital.getHospital_contact());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHospitalClick(hospital);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView hospitalName, hospitalEmail, hospitalMobile;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            hospitalEmail = itemView.findViewById(R.id.hospitalEmail);
            hospitalMobile = itemView.findViewById(R.id.hospitalMobile);
        }
    }


    public interface OnHospitalClickListener {
        void onHospitalClick(Hospitals hospital);
    }
}
