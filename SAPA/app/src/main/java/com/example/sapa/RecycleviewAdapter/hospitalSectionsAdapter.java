package com.example.sapa.RecycleviewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.R;
import com.example.sapa.models.hospitalSections;

import java.util.List;

public class hospitalSectionsAdapter extends RecyclerView.Adapter<hospitalSectionsAdapter.SectionViewHolder> {

    private Context context;
    private List<hospitalSections> sectionList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(hospitalSections section);
    }

    public hospitalSectionsAdapter(Context context, List<hospitalSections> sectionList, OnItemClickListener listener) {
        this.context = context;
        this.sectionList = sectionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycleview_sections, parent, false);
        return new SectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
        hospitalSections section = sectionList.get(position);


        holder.hospitalName.setText(section.getHospital_name());


        holder.sectionName.setText(section.getSection_name());


        holder.sectionDescription.setText(section.getSection_description());


        holder.sectionBilling.setText("₱ " + section.getBilling());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(section);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sectionList != null ? sectionList.size() : 0;
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        TextView hospitalName, sectionName, sectionDescription, sectionBilling;

        public SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            sectionName = itemView.findViewById(R.id.sectionName);
            sectionDescription = itemView.findViewById(R.id.sectionValue);
            sectionBilling = itemView.findViewById(R.id.startTimeValue);
        }
    }
}
