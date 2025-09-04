package com.example.sapa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityAddSchoolsBinding;
import com.example.sapa.models.defaultResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class add_schools extends AppCompatActivity {

    private ActivityAddSchoolsBinding binding;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAddSchoolsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.button3.setOnClickListener(v -> openGallery());


        binding.addStudentBtn.setOnClickListener(v -> {
            if (imageUri != null && imageBytes != null) {


                uploadSchoolData();
            } else {
                Toast.makeText(this, "Please select an image first.", Toast.LENGTH_SHORT).show();
            }
        });


        binding.backButton.setOnClickListener(v -> {
            Intent intent = new Intent(add_schools.this, schools.class);
            startActivity(intent);
            finish();
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            try {
                Bitmap bitmap;
                if (Build.VERSION.SDK_INT >= 29) {
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageUri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                } else {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                }

                binding.profileImage.setImageBitmap(bitmap);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                imageBytes = stream.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadSchoolData() {
        String name = binding.inputSchoolName.getText().toString();
        String address = binding.inputSchoolAddress.getText().toString();
        String email = binding.inputSchoolEmail.getText().toString();
        String contact = binding.inputSchoolContact.getText().toString();


        if (name.isBlank() || address.isBlank() || email.isBlank() || contact.isBlank()) {
            KAlertDialog nullFieldsDialog = new KAlertDialog(add_schools.this, true);
            nullFieldsDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
            nullFieldsDialog.setTitleText("Missing Fields")
                    .setContentText("Please fill All the Fields.")
                    .setConfirmText("OK")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .show();
            return;
        }


        String coordinatorId = getSharedPreferences("user_session", MODE_PRIVATE)
                .getString("coordinator_id", "");


        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody emailBody = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody contactBody = RequestBody.create(MediaType.parse("text/plain"), contact);
        RequestBody coordinatorIdBody = RequestBody.create(MediaType.parse("text/plain"), coordinatorId);


        RequestBody imageBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData(
                "profile_image",
                "school_" + System.currentTimeMillis() + ".jpg",
                imageBody
        );

        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<defaultResponse> call = apiInterface.addSchool(
                nameBody,
                addressBody,
                emailBody,
                contactBody,
                coordinatorIdBody,
                imagePart
        );

        call.enqueue(new Callback<defaultResponse>() {
            @Override
            public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        if ("success".equals(response.body().getStatus())) {

                            KAlertDialog successDialog = new KAlertDialog(add_schools.this, true);
                            successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                            successDialog.setTitleText("Successfully Added!")
                                    .setContentText(response.body().getMessage())
                                    .setConfirmText("OK")
                                    .confirmButtonColor(R.color.mainColor)
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                        finish();
                                    })
                                    .show();

                        } else {

                            KAlertDialog warningDialog = new KAlertDialog(add_schools.this, true);
                            warningDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                            warningDialog.setTitleText(response.body().getMessage())
                                    .setConfirmText("OK")
                                    .confirmButtonColor(R.color.mainColor)
                                    .setConfirmClickListener(sweetAlertDialog -> {
                                        sweetAlertDialog.dismissWithAnimation();
                                    })
                                    .show();
                        }
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        KAlertDialog errorDialog = new KAlertDialog(add_schools.this, true);
                        errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                        errorDialog.setTitleText("Server Error")
                                .setContentText(errorBody)
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                })
                                .show();
                    } catch (IOException e) {
                        KAlertDialog errorDialog = new KAlertDialog(add_schools.this, true);
                        errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                        errorDialog.setTitleText("Server error!")
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                })
                                .show();
                    }
                }
            }


            @Override
            public void onFailure(Call<defaultResponse> call, Throwable t) {
                Toast.makeText(add_schools.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
