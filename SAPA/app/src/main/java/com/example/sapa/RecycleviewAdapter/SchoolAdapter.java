package com.example.sapa.RecycleviewAdapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sapa.R;
import com.example.sapa.models.School;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {
    private List<School> schoolList;
    private Context context;


    private OnSchoolClickListener listener;

    public interface OnSchoolClickListener {
        void onSchoolClick(School school);
    }


    public SchoolAdapter(List<School> schoolList, Context context, OnSchoolClickListener listener) {
        this.schoolList = schoolList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SchoolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_row, parent, false);
        return new SchoolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolViewHolder holder, int position) {
        School school = schoolList.get(position);
        holder.schoolName.setText(school.getSchoolName());
        holder.schoolAddress.setText(school.getSchoolAddress());
        holder.schoolStatus.setText(school.getSchoolStatus());

        loadBase64Image(school.getImageBase64(), holder.cardImage);


        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSchoolClick(school);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schoolList.size();
    }

    public static class SchoolViewHolder extends RecyclerView.ViewHolder {
        TextView schoolName, schoolAddress, schoolStatus;
        ImageView cardImage;

        public SchoolViewHolder(@NonNull View itemView) {
            super(itemView);
            schoolName = itemView.findViewById(R.id.schoolName);
            schoolAddress = itemView.findViewById(R.id.schoolAddress);
            schoolStatus = itemView.findViewById(R.id.schoolStatus);
            cardImage = itemView.findViewById(R.id.profile_image);
        }
    }

    private void loadBase64Image(String base64Image, ImageView imageView) {
        if (base64Image == null || base64Image.isEmpty()) {
            Log.w("SchoolAdapter", "Base64 string is empty or null.");
            return;
        }

        try {
            if (base64Image.startsWith("data:image")) {
                base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
            }
            base64Image = base64Image.replaceAll("[^A-Za-z0-9+/=]", "");

            int padding = base64Image.length() % 4;
            if (padding > 0) {
                base64Image += "====".substring(padding);
            }

            byte[] decodedBytes = Base64.decode(base64Image, Base64.NO_WRAP);
            if (decodedBytes.length == 0) return;

            File debugFile = new File(context.getExternalCacheDir(), "debug_image.jpg");
            try (FileOutputStream fos = new FileOutputStream(debugFile)) {
                fos.write(decodedBytes);
            }

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);

            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            Log.e("SchoolAdapter", "Exception decoding Base64: " + e.getMessage(), e);
        }
    }

    public void updateData(List<School> newSchools) {
        schoolList.clear();
        schoolList.addAll(newSchools);
        notifyDataSetChanged();
    }
}
