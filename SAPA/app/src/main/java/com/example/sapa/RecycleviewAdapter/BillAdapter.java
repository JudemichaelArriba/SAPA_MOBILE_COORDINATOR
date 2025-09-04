package com.example.sapa.RecycleviewAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.R;
import com.example.sapa.models.Bill;

import java.util.ArrayList;
import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private List<Bill> billList = new ArrayList<>();
    private OnBillClickListener listener;

    public interface OnBillClickListener {
        void onBillClick(Bill bill);
    }

    public BillAdapter(List<Bill> billList, OnBillClickListener listener) {
        if (billList != null) this.billList.addAll(billList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycleview_bills, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = billList.get(position);
        holder.billCode.setText(bill.getBillCode());
        holder.amount.setText(String.format("₱ %.2f", bill.getAmount()));
        holder.issuedAtValue.setText(bill.getIssuedAt());
        holder.statusValue.setText(bill.getStatus());


        if (bill.getStatus().equalsIgnoreCase("Paid") || bill.getStatus().equalsIgnoreCase("Cancelled")) {
            holder.itemView.setAlpha(0.5f);
            holder.itemView.setClickable(false);
        } else {
            holder.itemView.setAlpha(1f);
            holder.itemView.setClickable(true);

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBillClick(bill);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView billCode, amount, issuedAtValue, statusValue;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            billCode = itemView.findViewById(R.id.billCode);
            amount = itemView.findViewById(R.id.amount);
            issuedAtValue = itemView.findViewById(R.id.issuedAtValue);
            statusValue = itemView.findViewById(R.id.statusValue);
        }
    }

    public void updateList(List<Bill> newList) {
        if (newList == null) return;
        billList.clear();
        billList.addAll(newList);
        notifyDataSetChanged();
    }
}

