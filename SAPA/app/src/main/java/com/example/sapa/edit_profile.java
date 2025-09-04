package com.example.sapa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityEditProfileBinding;
import com.example.sapa.models.defaultResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class edit_profile extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    private byte[] imageBytes;
    private String userId;
    private boolean imageChanged = false;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        binding.profileImage.setImageBitmap(bitmap);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
                        imageBytes = baos.toByteArray();
                        imageChanged = true;

                    } catch (Exception e) {
                        e.printStackTrace();
                        imageChanged = false;
                    }
                } else {
                    imageChanged = false;
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.editProfile, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeFields();
        setupListeners();
    }

    private void initializeFields() {
        Intent intent = getIntent();
        String fullName = intent.getStringExtra("fullName");
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String mobile = intent.getStringExtra("mobile");
        String profileImageUrl = intent.getStringExtra("profileImage");
        userId = intent.getStringExtra("id");

        if (fullName != null && fullName.contains(" ")) {
            String[] parts = fullName.trim().split(" ");
            String lastName = parts[parts.length - 1];
            String firstName = fullName.substring(0, fullName.lastIndexOf(" "));
            binding.firstname.setText(firstName);
            binding.lastname.setText(lastName);
        } else {
            binding.firstname.setText(fullName != null ? fullName : "");
            binding.lastname.setText("");
        }

        binding.username.setText(username != null ? username : "");
        binding.emailTxt.setText(email != null ? email : "");
        binding.mobileNumber.setText(mobile != null ? mobile : "");

        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(binding.profileImage);
        }
    }

    private void setupListeners() {
        binding.addImageBtn.setOnClickListener(v -> openGallery());

        binding.updateBtn.setOnClickListener(v -> {
            KAlertDialog warningDialog = new KAlertDialog(edit_profile.this, true);
            warningDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
            warningDialog.setTitleText("Are you sure you want to update?")
                    .setCancelText("NO")
                    .setConfirmText("YES")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        updateProfile();
                        sweetAlertDialog.dismissWithAnimation();
                    })
                    .setCancelClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                    .show();

        });

        binding.backButton.setOnClickListener(v -> finish());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void updateProfile() {
        String firstName = binding.firstname.getText().toString().trim();
        String lastName = binding.lastname.getText().toString().trim();
        String username = binding.username.getText().toString().trim();
        String mobile = binding.mobileNumber.getText().toString().trim();
        String email = binding.emailTxt.getText().toString().trim();
        String password = binding.sPassword.getText().toString().trim();
        String passwordToSend = password.isEmpty() ? null : password;
        String base64Image = imageChanged && imageBytes != null ? Base64.encodeToString(imageBytes, Base64.NO_WRAP) : null;

        ApiInterface apiInterface = ApiClient.getClient(this).create(ApiInterface.class);
        Call<defaultResponse> call = apiInterface.updateUser(
                userId,
                firstName,
                lastName,
                email,
                mobile,
                username,
                passwordToSend,
                base64Image
        );

        call.enqueue(new Callback<defaultResponse>() {
            @Override
            public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String msg = response.body().getMessage();

                    if (msg.equalsIgnoreCase("no changes detected; update canceled.")) {
                        KAlertDialog errorDialog = new KAlertDialog(edit_profile.this, true);
                        errorDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
                        errorDialog.setTitleText("No Changes Detected!")
                                .setContentText("Update has been canceled.")
                                .setConfirmText("OK")
                                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                .show();
                    } else if (msg.toLowerCase().contains("exists") || msg.toLowerCase().contains("error")) {
                        KAlertDialog errorDialog1 = new KAlertDialog(edit_profile.this, true);
                        errorDialog1.changeAlertType(KAlertDialog.ERROR_TYPE);
                        errorDialog1 .setTitleText("Update Failed")
                                .setContentText(msg)
                                .setConfirmText("OK")
                                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                .show();
                    } else {
                        KAlertDialog errorDialog2 = new KAlertDialog(edit_profile.this, true);
                        errorDialog2.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                        errorDialog2.setTitleText("Updated Successfully!")
                                .setContentText(msg)
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                                .show();
                    }

                } else {
                    KAlertDialog errorDialog3 = new KAlertDialog(edit_profile.this, true);
                    errorDialog3 .changeAlertType(KAlertDialog.ERROR_TYPE);
                    errorDialog3 .setTitleText("Error")
                            .setContentText("Update failed. Server returned an error.")
                            .setConfirmText("OK")
                            .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<defaultResponse> call, Throwable t) {
                KAlertDialog errorDialog = new KAlertDialog(edit_profile.this, true);
                errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
                errorDialog.setTitleText("Network Error")
                        .setContentText(t.getMessage())
                        .setConfirmText("OK")
                        .setConfirmClickListener(KAlertDialog::dismissWithAnimation)
                        .show();
            }
        });

    }

    private void showErrorDialog(String message) {
        KAlertDialog errorDialog = new KAlertDialog(edit_profile.this, true);
        errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText("Error")
                .setContentText(message)
                .setConfirmText("OK")
                .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
