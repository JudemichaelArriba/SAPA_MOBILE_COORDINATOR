package com.example.sapa.RecycleviewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.R;
import com.example.sapa.models.UpcomingAppointment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UpcomingAppointmentsAdapter extends RecyclerView.Adapter<UpcomingAppointmentsAdapter.AppointmentViewHolder> {

    private final Context context;
    private final List<UpcomingAppointment> appointments;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UpcomingAppointment appointment);
    }

    public UpcomingAppointmentsAdapter(Context context, List<UpcomingAppointment> appointments, OnItemClickListener listener) {
        this.context = context;
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyleview_appointments, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        UpcomingAppointment a = appointments.get(position);

        holder.hospitalName.setText(a.getHospitalName() != null ? a.getHospitalName() : "N/A");
        holder.sectionName.setText(a.getSectionName() != null ? a.getSectionName() : "N/A");
        holder.appointmentStatus.setText(a.getAppointmentStatus() != null ? a.getAppointmentStatus() : "N/A");



        String startTimeStr = a.getStartTime() != null ? a.getStartTime() : "";
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        try {
            if (!startTimeStr.isEmpty()) {
                Date date = input.parse(startTimeStr);
                holder.appointmentDate.setText(output.format(date));
            } else {
                holder.appointmentDate.setText("N/A");
            }
        } catch (ParseException e) {
            holder.appointmentDate.setText(startTimeStr);
        }


        if (startTimeStr.length() >= 16) {
            holder.startTime.setText(startTimeStr.substring(11, 16));
        } else {
            holder.startTime.setText("N/A");
        }

        String endTimeStr = a.getEndTime() != null ? a.getEndTime() : "";
        if (endTimeStr.length() >= 16) {
            holder.endTime.setText(endTimeStr.substring(11, 16));
        } else {
            holder.endTime.setText("N/A");
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(a));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView hospitalName, sectionName, appointmentStatus, appointmentDate, startTime, endTime;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            hospitalName = itemView.findViewById(R.id.hospitalName);
            sectionName = itemView.findViewById(R.id.section_name);
            appointmentStatus = itemView.findViewById(R.id.appointment_status);
            appointmentDate = itemView.findViewById(R.id.appointment_date);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
        }
    }
}
