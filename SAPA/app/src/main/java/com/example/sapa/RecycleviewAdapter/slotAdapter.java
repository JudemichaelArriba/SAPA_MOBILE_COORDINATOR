package com.example.sapa.RecycleviewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.R;
import com.example.sapa.models.hospitalSlots;

import java.util.List;

public class slotAdapter extends RecyclerView.Adapter<slotAdapter.SlotViewHolder> {

    private Context context;
    private List<hospitalSlots> slotList;
    private OnSlotClickListener listener;


    public slotAdapter(Context context, List<hospitalSlots> slotList, OnSlotClickListener listener) {
        this.context = context;
        this.slotList = slotList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_view_slots, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        hospitalSlots slot = slotList.get(position);

        holder.slotName.setText(slot.getSlot_name());
        holder.sectionValue.setText(slot.getSection_name());
        holder.startTimeValue.setText(slot.getStart_time());


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSlotClick(slot);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slotList != null ? slotList.size() : 0;
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        TextView slotName, sectionValue, startTimeValue;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            slotName = itemView.findViewById(R.id.slotName);
            sectionValue = itemView.findViewById(R.id.sectionValue);
            startTimeValue = itemView.findViewById(R.id.startTimeValue);
        }
    }


    public interface OnSlotClickListener {
        void onSlotClick(hospitalSlots slot);
    }
}
