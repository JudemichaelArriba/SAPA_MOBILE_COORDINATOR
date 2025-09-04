package com.example.sapa;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.developer.kalert.KAlertDialog;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.ActivityTotalPaymentBinding;
import com.example.sapa.models.defaultResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class total_payment extends AppCompatActivity {

    private ActivityTotalPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTotalPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String totalAmountStr = getIntent().getStringExtra("TOTAL_AMOUNT");
        String userId = getIntent().getStringExtra("USER_ID");

        if (totalAmountStr != null) {
            binding.totalAmount.setText(totalAmountStr);
        }

        binding.backButton.setOnClickListener(v -> finish());

        binding.payButton.setOnClickListener(v -> {
            String valueStr = binding.amountInput.getText().toString().trim();

            if (userId == null || totalAmountStr == null || valueStr.isEmpty()) {
                Toast.makeText(this, "Please enter a value!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double enteredAmount = Double.parseDouble(valueStr);
                double totalAmount = Double.parseDouble(totalAmountStr.replace("₱", "").trim());

                if (enteredAmount <= 0) {
                    showErrorDialog("Invalid Amount", "Amount must be greater than 0");
                } else if (enteredAmount < totalAmount) {
                    showErrorDialog("Insufficient Payment", "Entered amount is less than the total bill.");
                } else if (enteredAmount > totalAmount) {
                    showErrorDialog("Overpayment", "Entered amount is more than the total bill.");
                } else {

                    payTotalBills(userId, enteredAmount);
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void payTotalBills(String userId, double totalAmount) {
        ApiInterface api = ApiClient.getClient(this).create(ApiInterface.class);
        api.payTotalBills(userId, totalAmount).enqueue(new Callback<defaultResponse>() {
            @Override
            public void onResponse(Call<defaultResponse> call, Response<defaultResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    defaultResponse res = response.body();
                    if ("success".equalsIgnoreCase(res.getStatus())) {
                        KAlertDialog successDialog = new KAlertDialog(total_payment.this, true);
                        successDialog.changeAlertType(KAlertDialog.SUCCESS_TYPE);
                        successDialog.setTitleText("Successful")
                                .setContentText("Payment successful!")
                                .setConfirmText("OK")
                                .confirmButtonColor(R.color.mainColor)
                                .setConfirmClickListener(sweetAlertDialog -> {
                                    sweetAlertDialog.dismissWithAnimation();
                                    finish();
                                })
                                .show();
                    } else {
                        showErrorDialog("Unsuccessful", "Payment unsuccessful!");
                    }
                } else {
                    Toast.makeText(total_payment.this, "API response failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<defaultResponse> call, Throwable t) {
                Toast.makeText(total_payment.this, "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showErrorDialog(String title, String message) {
        KAlertDialog errorDialog = new KAlertDialog(total_payment.this, true);
        errorDialog.changeAlertType(KAlertDialog.ERROR_TYPE);
        errorDialog.setTitleText(title)
                .setContentText(message)
                .setConfirmText("OK")
                .confirmButtonColor(R.color.mainColor)
                .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation())
                .show();
    }
}
