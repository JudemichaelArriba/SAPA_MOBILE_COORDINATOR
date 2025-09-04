package com.example.sapa.ApiAndInterface;


import com.example.sapa.SignUpResponse;
import com.example.sapa.models.AppointmentRequest;
import com.example.sapa.models.Hospitals;
import com.example.sapa.models.LoginResponse;
import com.example.sapa.models.School;
import com.example.sapa.models.Students;
import com.example.sapa.models.UpcomingAppointment;
import com.example.sapa.models.UserBillsResponse;
import com.example.sapa.models.UserProfileResponse;
import com.example.sapa.models.defaultResponse;
import com.example.sapa.models.hospitalSections;
import com.example.sapa.models.hospitalSlots;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {


    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("signup.php")
    Call<SignUpResponse> signUp(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("email") String email,
            @Field("contact_no") String contactNo,
            @Field("username") String username,
            @Field("password") String password
    );





    @FormUrlEncoded
    @POST("update_school.php")
    Call<defaultResponse> updateSchool(
            @Field("school_id") String schoolId,
            @Field("school_name") String schoolName,
            @Field("school_address") String schoolAddress,
            @Field("school_email") String schoolEmail,
            @Field("contact_no") String contactNo,
            @Field("profile_image") String profileImageBase64
    );




    @FormUrlEncoded
    @POST("cancel_appointments.php")
    Call<defaultResponse> cancelAppointmentsByAppointmentId(@Field("appointment_id") int appointmentId);


    @FormUrlEncoded
    @POST("update_student.php")
    Call<defaultResponse> updateStudent(
            @Field("student_id") String studentId,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("contact_no") String contactNo,
            @Field("email") String email,
            @Field("birthdate") String birthdate,
            @Field("gender") String gender
    );



    @GET("get_bills.php")
    Call<UserBillsResponse> getUserBills(
            @Query("user_id") String userId
    );


    @GET("getStudentWithoutAppointments.php")
    Call<List<Students>> getVacantStudents(
            @Query("coordinator_id") String coordinatorId,
            @Query("school_id") String schoolId
    );


    @FormUrlEncoded
    @POST("pay_total_bills.php")
    Call<defaultResponse> payTotalBills(
            @Field("user_id") String userId,
            @Field("amount") double amount
    );
    @Multipart
    @POST("add_school.php")
    Call<defaultResponse> addSchool(
            @Part("school_name") RequestBody schoolName,
            @Part("school_address") RequestBody schoolAddress,
            @Part("school_email") RequestBody schoolEmail,
            @Part("contact_no") RequestBody contactNo,
            @Part("coordinator_id") RequestBody coordinatorId,
            @Part MultipartBody.Part profileImage
    );

    @GET("school_delete.php")
    Call<defaultResponse> deleteSchool(
            @Query("school_id") String schoolId
    );
    @GET("student_delete.php")
    Call<defaultResponse> deleteStudent(
            @Query("student_id") String studentId
    );


    @FormUrlEncoded
    @POST("pay_specific_bill.php")
    Call<defaultResponse> paySpecificBill(
            @Field("user_id") String userId,
            @Field("bill_code") String billCode
    );

    @GET("getSchool.php")
    Call<List<School>> getSchools(@Query("coordinator_id") String coordinatorId);

    @GET("get_user2.php")
    Call<UserProfileResponse> getUserProfile(@Query("user_id") String userId);

    @GET("get_sections.php")
    Call<List<hospitalSections>> getSectionsByHospital(@Query("hospital_id") String hospitalId);

    @GET("get_hospital.php")
    Call<List<Hospitals>> getHospitals();

    @GET("get_slots.php")
    Call<List<hospitalSlots>> getSlotsBySectionId(
            @Query("section_id") int sectionId
    );

    @GET("getStudentWithCoordinatorId.php")
    Call<List<Students>> getStudentsByCoordinator(@Query("coordinator_id") String coordinatorId);


    @FormUrlEncoded
    @POST("add_student.php")
    Call<defaultResponse> addStudent(
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("contact_no") String contactNo,
            @Field("email") String email,
            @Field("birthdate") String birthdate,
            @Field("gender") String gender,
            @Field("coordinator_id") String coordinatorId,
            @Field("school_id") String schoolId
    );

    @POST("add_appointment.php")
    Call<defaultResponse> addAppointment(@Body AppointmentRequest appointmentRequest);


    @GET("get_upcomingAppointments.php")
    Call<List<UpcomingAppointment>> getUpcomingAppointments(
            @Query("user_id") String userId
    );


    @FormUrlEncoded
    @POST("update_profile.php")
    Call<defaultResponse> updateUser(
            @Field("user_id") String userId,
            @Field("first_name") String firstName,
            @Field("last_name") String lastName,
            @Field("email") String email,
            @Field("contact_no") String contactNo,
            @Field("username") String username,
            @Field("password") String password,
            @Field("profile_image") String profileImageBase64
    );




    @GET("get_studentBaseAppointment.php")
    Call<List<Students>> getStudentsByAppointment(
            @Query("appointment_id") int appointment_id
    );



}
