package com.acharyaamrit.medicare.doctor;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.R;
import com.acharyaamrit.medicare.common.LoginActivity;
import com.acharyaamrit.medicare.common.NotificationActivity;
import com.acharyaamrit.medicare.common.api.ApiClient;
import com.acharyaamrit.medicare.common.api.ApiService;
import com.acharyaamrit.medicare.common.database.DatabaseHelper;
import com.acharyaamrit.medicare.common.model.response.UserResponse;
import com.acharyaamrit.medicare.doctor.adapter.RecientPatientAdapter;
import com.acharyaamrit.medicare.doctor.model.Doctor;
import com.acharyaamrit.medicare.doctor.model.response.RecentPatient;
import com.acharyaamrit.medicare.doctor.model.response.RecentPatientsResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorHomeFragment extends Fragment {

    // Header views
    TextView name, did, specialist, todayPatientsCount, todayCases, totalPatients;
    ConstraintLayout notification_icon;

    // Bar chart views
    TextView sunCount, monCount, tueCount, wedCount, thuCount, friCount, satCount;
    TextView sunLabel, monLabel, tueLabel, wedLabel, thuLabel, friLabel, satLabel;
    View sunBar, monBar, tueBar, wedBar, thuBar, friBar, satBar;
    MaterialCardView addPatientCard, contactAdminCard;
    Button viewAll;




    // Bar arrays for easy access
    private View[] bars;
    private TextView[] countTexts;
    private TextView[] labelTexts;

    // Maximum bar height in dp
    private static final int MAX_BAR_HEIGHT_DP = 160;
    private static final int MIN_BAR_HEIGHT_DP = 10;

    public DoctorHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_doctor_home, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        ((DoctorHomePageActivity) requireActivity())
                .setSelectedBackground(R.id.home_button_background);
        setUpViews(view);
        setListener(view);
        setNavData(token);
        setRecentPatient(view);
        setTodayCases();
        return view;
    }

    public void setNavData(String token) {
        DatabaseHelper databaseHelper = new DatabaseHelper(requireContext());
        Doctor doctor = databaseHelper.getDoctorByToken(token);
        if (doctor != null) {
            name.setText(doctor.getName());
            did.setText("DID: " + doctor.getDoctor_id());
            specialist.setText((doctor.getSpeciality().isEmpty()) ? "" : doctor.getSpeciality());
        }
    }

    public void setUpViews(View view) {
        // Header views
        name = view.findViewById(R.id.name);
        did = view.findViewById(R.id.did);
        specialist = view.findViewById(R.id.specialist);
        notification_icon = view.findViewById(R.id.notification_icon);
        todayPatientsCount = view.findViewById(R.id.todayPatientsCount);
        todayCases = view.findViewById(R.id.todayCases);
        totalPatients = view.findViewById(R.id.totalPatients);
        addPatientCard = view.findViewById(R.id.addPatientCard);
        contactAdminCard = view.findViewById(R.id.contactAdminCard);
        viewAll = view.findViewById(R.id.viewAll);


        // Bar chart count TextViews
        sunCount = view.findViewById(R.id.sunCount);
        monCount = view.findViewById(R.id.monCount);
        tueCount = view.findViewById(R.id.tueCount);
        wedCount = view.findViewById(R.id.wedCount);
        thuCount = view.findViewById(R.id.thuCount);
        friCount = view.findViewById(R.id.friCount);
        satCount = view.findViewById(R.id.satCount);

        // Bar chart label TextViews
        sunLabel = view.findViewById(R.id.sunLabel);
        monLabel = view.findViewById(R.id.monLabel);
        tueLabel = view.findViewById(R.id.tueLabel);
        wedLabel = view.findViewById(R.id.wedLabel);
        thuLabel = view.findViewById(R.id.thuLabel);
        friLabel = view.findViewById(R.id.friLabel);
        satLabel = view.findViewById(R.id.satLabel);

        // Bar views
        sunBar = view.findViewById(R.id.sunBar);
        monBar = view.findViewById(R.id.monBar);
        tueBar = view.findViewById(R.id.tueBar);
        wedBar = view.findViewById(R.id.wedBar);
        thuBar = view.findViewById(R.id.thuBar);
        friBar = view.findViewById(R.id.friBar);
        satBar = view.findViewById(R.id.satBar);

        // Initialize arrays for easier manipulation
        // Order: Sunday = 0, Monday = 1, ... Saturday = 6
        bars = new View[]{sunBar, monBar, tueBar, wedBar, thuBar, friBar, satBar};
        countTexts = new TextView[]{sunCount, monCount, tueCount, wedCount, thuCount, friCount, satCount};
        labelTexts = new TextView[]{sunLabel, monLabel, tueLabel, wedLabel, thuLabel, friLabel, satLabel};
    }

    public void setListener(View view) {
        notification_icon.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), NotificationActivity.class));
        });
        addPatientCard.setOnClickListener(V ->{
            Fragment fragment = new DoctorPatientFragment();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        viewAll.setOnClickListener(v->{
            showAllRecientPatient(view);
        });
    }

    private void setTodayCases(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<UserResponse> call = apiService.fetchTodayCase("Bearer " + token);
        call.enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (!isAdded()) return;
                if(response.isSuccessful() && response.body() != null){
                    try {

                    todayCases.setText(String.valueOf(response.body().getMessage()));
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error Processing data", Toast.LENGTH_SHORT).show();
                    }
                }else{

                    try {
                        String errorJson = response.errorBody().string();
                        Gson gson = new Gson();
                        UserResponse errorResponse = gson.fromJson(errorJson, UserResponse.class);

                        String title = errorResponse.getTitle();

                    if ("Unauthenticated".equalsIgnoreCase(title)) {
                        navigateToLogin();
                    }

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

    private void setRecentPatient(View view) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RecentPatientsResponse> call = apiService.fetchPatientRecent("Bearer " + token);
        call.enqueue(new Callback<RecentPatientsResponse>() {

            @Override
            public void onResponse(Call<RecentPatientsResponse> call, Response<RecentPatientsResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<RecentPatient> recentPatientsResponse = response.body().getRecentPatientList();
                        int todayPatientCount = getTodayPatientCount(recentPatientsResponse);

                        if (recentPatientsResponse != null) {

                            todayPatientsCount.setText((todayPatientCount > 0) ? String.valueOf(todayPatientCount) : "0");
                            totalPatients.setText(String.valueOf(recentPatientsResponse.size()));


                            setRecentPatientAdapter(recentPatientsResponse, view);
                            // Update last week chart
                            updateLastWeekChart(recentPatientsResponse);
                        } else {
                            todayPatientsCount.setText("0");
                            updateLastWeekChart(null);
                        }

                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error processing data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    todayPatientsCount.setText("0");
                    updateLastWeekChart(null);
                }
            }

            @Override
            public void onFailure(Call<RecentPatientsResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "No Internet Connection. Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showAllRecientPatient(View v){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<RecentPatientsResponse> call = apiService.fetchPatientRecent("Bearer " + token);
        call.enqueue(new Callback<RecentPatientsResponse>() {

            @Override
            public void onResponse(Call<RecentPatientsResponse> call, Response<RecentPatientsResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<RecentPatient> recentPatientsResponse = response.body().getRecentPatientList();

                        if (recentPatientsResponse != null) {


                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
                            bottomSheetDialog.setContentView(R.layout.item_bottomsheet_recent_patient);

                            RecientPatientAdapter adapter = new RecientPatientAdapter(recentPatientsResponse, requireContext());

                            RecyclerView recyclerView = bottomSheetDialog.findViewById(R.id.recientPatientRecyclerBottomsheet);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            recyclerView.setAdapter(adapter);
                            bottomSheetDialog.show();



                        } else {

                        }

                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error processing data", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(requireContext(), "No Patients Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RecentPatientsResponse> call, Throwable t) {
            }
        });



    }
    private void setRecentPatientAdapter(List<RecentPatient> recentPatientsResponse , View view){
        List<RecentPatient> displayList = recentPatientsResponse.size() > 4
                ? recentPatientsResponse.subList(0, 4)
                : recentPatientsResponse;
        RecientPatientAdapter adapter = new RecientPatientAdapter(displayList, requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recientPatientRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

    }
    private void navigateToLogin() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }
    private int getTodayPatientCount(List<RecentPatient> patientList) {
        if (patientList == null || patientList.isEmpty()) {
            return 0;
        }

        int count = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String todayDate = sdf.format(new Date());

        for (RecentPatient patient : patientList) {
            String createdAt = patient.getCreated_at();
            if (createdAt != null && createdAt.equals(todayDate)) {
                count++;
            }
        }

        return count;
    }


    private void updateLastWeekChart(List<RecentPatient> patientList) {
        int[] dayCounts = getLastWeekPatientCounts(patientList);

        // Find maximum count to scale bars proportionally
        int maxCount = 1;
        for (int count : dayCounts) {
            if (count > maxCount) {
                maxCount = count;
            }
        }

        // Update each bar
        for (int i = 0; i < 7; i++) {
            final int count = dayCounts[i];

            // Update count text
            countTexts[i].setText(String.valueOf(count));

            // Calculate bar height proportionally
            int barHeightDp;
            if (count == 0) {
                barHeightDp = MIN_BAR_HEIGHT_DP;
            } else {
                barHeightDp = (int) (((float) count / maxCount) * MAX_BAR_HEIGHT_DP);
                barHeightDp = Math.max(barHeightDp, MIN_BAR_HEIGHT_DP);
            }

            // Convert dp to pixels
            float density = getResources().getDisplayMetrics().density;
            int barHeightPx = (int) (barHeightDp * density);

            ViewGroup.LayoutParams params = bars[i].getLayoutParams();
            params.height = barHeightPx;
            bars[i].setLayoutParams(params);


            setBarColor(bars[i], count, maxCount);
        }


        updateDayLabels();
    }


    private int[] getLastWeekPatientCounts(List<RecentPatient> patientList) {
        int[] counts = new int[7];

        if (patientList == null || patientList.isEmpty()) {
            return counts;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Create a map of date strings to their index in the array
        Map<String, Integer> dateIndexMap = new HashMap<>();

        for (int i = 6; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -(6 - i));
            String dateStr = sdf.format(cal.getTime());
            dateIndexMap.put(dateStr, i);
        }

        // Count patients for each day
        for (RecentPatient patient : patientList) {
            String createdAt = patient.getCreated_at();
            if (createdAt != null) {
                // Handle if createdAt contains time (e.g., "2024-01-15 10:30:00")
                if (createdAt.length() > 10) {
                    createdAt = createdAt.substring(0, 10);
                }

                Integer index = dateIndexMap.get(createdAt);
                if (index != null) {
                    counts[index]++;
                }
            }
        }

        return counts;
    }


    private void updateDayLabels() {
        String[] dayAbbreviations = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

        for (int i = 0; i < 7; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -(6 - i));
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

            String label = dayAbbreviations[dayOfWeek];


            if (i == 6) {
                labelTexts[i].setText("Today");
                labelTexts[i].setTextColor(getResources().getColor(R.color.primary_green, null));
            } else {
                labelTexts[i].setText(label);
                labelTexts[i].setTextColor(0xFF9CA3AF);
            }
        }
    }


    private void setBarColor(View bar, int count, int maxCount) {
        int[] colors = {
                0xFFE0F2FE,
                0xFFBAE6FD,
                0xFF7DD3FC,
                0xFF38BDF8,
                0xFF0EA5E9,
                0xFF0284C7,
                0xFF0369A1,
        };

        float ratio = maxCount > 0 ? (float) count / maxCount : 0;
        int colorIndex = (int) (ratio * (colors.length - 1));
        colorIndex = Math.min(colorIndex, colors.length - 1);

        bar.setBackgroundResource(R.drawable.bar_background);

        android.graphics.drawable.GradientDrawable drawable = new android.graphics.drawable.GradientDrawable();
        drawable.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(14 * getResources().getDisplayMetrics().density);
        drawable.setColor(colors[colorIndex]);
        bar.setBackground(drawable);
    }
}