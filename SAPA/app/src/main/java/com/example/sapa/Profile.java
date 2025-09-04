package com.example.sapa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.sapa.ApiAndInterface.ApiClient;
import com.example.sapa.ApiAndInterface.ApiInterface;
import com.example.sapa.databinding.FragmentProfileBinding;
import com.example.sapa.models.UserData;
import com.example.sapa.models.UserProfileResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Profile extends Fragment {

    private FragmentProfileBinding binding;

    public Profile() {
    }

    public static Profile newInstance() {
        return new Profile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_session", requireActivity().MODE_PRIVATE);
        String userId = sharedPreferences.getString("coordinator_id", "");

        if (!userId.isEmpty()) {
            fetchUserProfile(userId);
        } else {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        binding.editProfileBtn.setOnClickListener(v -> {





                String username = binding.usernameTv.getText().toString();
                String email = binding.emailTv.getText().toString();
                String fullName = binding.fullNameTv.getText().toString();
                String mobile = binding.mobileTv2.getText().toString();
                String role = binding.roleTv.getText().toString();
                String status = binding.statusTv.getText().toString();

                String profileImageUrl = (String) binding.profileImage.getTag();
                if (profileImageUrl == null) {
                    profileImageUrl = "";
                }


                Intent intent = new Intent(requireActivity(), edit_profile.class);
                intent.putExtra("id", userId);
                intent.putExtra("username", username);
                intent.putExtra("email", email);
                intent.putExtra("fullName", fullName);
                intent.putExtra("mobile", mobile);
                intent.putExtra("role", role);
                intent.putExtra("status", status);
                intent.putExtra("profileImage", profileImageUrl);

                requireActivity().startActivity(intent);




        });

        return view;
    }

    private void fetchUserProfile(String userId) {
        ApiInterface apiInterface = ApiClient.getClient(requireContext()).create(ApiInterface.class);
        Call<UserProfileResponse> call = apiInterface.getUserProfile(userId);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();
                    if (profile.getStatus().equals("success")) {
                        updateUI(profile);
                    } else {
                        Toast.makeText(getActivity(), profile.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Failed to fetch profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(UserProfileResponse profile) {
        UserData data = profile.getData();

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                binding.usernameTv.setText(data.getUsername());
                binding.emailTv.setText(data.getEmail());
                binding.fullNameTv.setText(data.getFullName());
                binding.mobileTv2.setText(data.getMobile());
                binding.roleTv.setText(data.getRole());
                binding.statusTv2.setText(data.getStatus());

                if (data.getProfileImage() != null && !data.getProfileImage().isEmpty()) {

                    Glide.with(requireActivity())
                            .load(data.getProfileImage())
                            .circleCrop()
                            .placeholder(R.drawable.circle_background)
                            .error(R.drawable.circle_background)
                            .into(binding.profileImage);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
