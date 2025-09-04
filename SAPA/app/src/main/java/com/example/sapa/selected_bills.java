package com.example.sapa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivitySelectedBillsBinding;
import com.example.sapa.models.defaultResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class selected_bills extends AppCompatActivity {

    private ActivitySelectedBillsBinding binding;
    private String billCode;
    private double billAmount;
    private String hospitalName;
    private String sectionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySelectedBillsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        billCode = getIntent().getStringExtra("BILL_CODE");
        billAmount = getIntent().getDoubleExtra("BILL_AMOUNT", 0);
        String billStatus = getIntent().getStringExtra("BILL_STATUS");
        String issuedAt = getIntent().getStringExtra("BILL_ISSUED_AT");
        hospitalName = getIntent().getStringExtra("HOSPITAL_NAME");
        sectionName = getIntent().getStringExtra("SECTION_NAME");
        Log.e("selectedBills", "hospitalName: " + hospitalName);
        Log.e("selectedBills", "sectionName: " + sectionName);
        Log.e("selectedBills", "Bill code: " + billCode);


        binding.amountInput.setText(String.valueOf(billAmount));
        binding.totalAmount.setText("₱ " + billAmount);
        binding.hospitalNameValue.setText(hospitalName != null ? hospitalName : "N/A");
        binding.sectionNameValue.setText(sectionName != null ? sectionName : "N/A");
        binding.backButton.setOnClickListener(v->{
            finish();
        });
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = sharedPreferences.getString("coordinator_id", "");

        binding.payButton.setOnClickListener(v -> {
            if (billCode == null || userId.isEmpty()) {
                Toast.makeText(this, "Invalid bill or user.", Toast.LENGTH_SHORT).show();
                return;
            }

            String valueStr = binding.amountInput.getText().toString().trim();
            if (valueStr.isEmpty()) {
                Toast.makeText(this, "Please enter a value!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double enteredAmount = Double.parseDouble(valueStr);

                if (enteredAmount <= 0) {
                    showErrorDialog("Warning", "Amount must be greater than 0");
                    return;
                } else if (enteredAmount < billAmount) {
                    showErrorDialog("Warning", "Payment is not enough. Please pay the full amount.");
                    return;
                } else if (enteredAmount > billAmount) {
                    showErrorDialog("Warning", "Payment is too much. Enter the exact amount.");
                    return;
                }

                ApiInterface api = ApiClient.getClient(this).create(ApiInterface.class);
                Call<defaultResponse> call = api.paySpecificBill(userId, billCode);
                call.enqueue(new Callback<defaultResponse>() {
                    @Override
                    public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(selected_bills.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(selected_bills.this, "Payment failed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<defaultResponse> call, Throwable t) {
                        Toast.makeText(selected_bills.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showErrorDialog(String title, String message) {
        KAlertDialog errorDialog = new KAlertDialog(selected_bills.this, true);
        errorDialog.changeAlertType(KAlertDialog.WARNING_TYPE);
        errorDialog.setTitleText(title);
        errorDialog.setContentText(message)
                .setConfirmText("OK")
                .confirmButtonColor(R.color.mainColor)
                .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                .show();
    }
}
