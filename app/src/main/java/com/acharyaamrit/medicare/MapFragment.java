package com.acharyaamrit.medicare;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.Patient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapFragment extends Fragment {
    private MapView mapView;
    private static final String TAG = "MapFragment";

    // UI Components
    private ImageView btnBack;
    private TextView tvDistance, tvDuration, tvPharmacyName;
    private FloatingActionButton fabLocationPoint;

    // Location Services
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // Map Markers and Route
    private Marker userLocationMarker, pharmacyMarker;
    private GeoPoint pharmacyLocation, currentUserLocation;
    private Polyline currentRouteLine;

    // Threading
    private ExecutorService executorService;
    private Handler mainHandler;

    // Constants
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int LOCATION_UPDATE_INTERVAL = 5000; // 5 seconds
    private static final int LOCATION_FASTEST_INTERVAL = 3000; // 3 seconds
    private static final double DEFAULT_ZOOM = 15.0;
    private static final double USER_FOCUS_ZOOM = 17.0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireActivity().getPackageName());
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        initializeViews(view);
        initializeServices();
        setupPharmacyLocation();
        setupLocationTracking();

        return view;
    }

    private void initializeViews(View view) {
        mapView = view.findViewById(R.id.map);
        btnBack = view.findViewById(R.id.btnBack);
        tvPharmacyName = view.findViewById(R.id.tvPharmacyName);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvDuration = view.findViewById(R.id.tvDuration);
        fabLocationPoint = view.findViewById(R.id.location_point);

        // Setup click listeners
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        fabLocationPoint.setOnClickListener(v -> centerMapOnUserLocation());

        // Configure map
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
    }

    private void initializeServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    private void setupPharmacyLocation() {
        Bundle args = getArguments();
        if (args != null) {
            double lat = Double.parseDouble(args.getString("latitude", "0"));
            double lon = Double.parseDouble(args.getString("longitude", "0"));
            String pharmacyName = args.getString("pharmacy_name", "Pharmacy");

            tvPharmacyName.setText(pharmacyName);
            pharmacyLocation = new GeoPoint(lat, lon);

            // Add pharmacy marker
            pharmacyMarker = new Marker(mapView);
            pharmacyMarker.setPosition(pharmacyLocation);
            pharmacyMarker.setTitle(pharmacyName);
            pharmacyMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            mapView.getOverlays().add(pharmacyMarker);
        }
    }

    private void setupLocationTracking() {
        setupLocationCallback();

        if (checkLocationPermission()) {
            initializeUserLocation();
        } else {
            requestLocationPermission();
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissions(
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeUserLocation();
            } else {
                showToast("Location permission denied. Cannot track your location.");
                loadStaticLocation();
            }
        }
    }

    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    currentUserLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    updateUserLocationMarker(currentUserLocation);
                    fetchAndDrawRoute(currentUserLocation, pharmacyLocation);
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void initializeUserLocation() {
        // Get last known location first
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        currentUserLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                        updateUserLocationMarker(currentUserLocation);
                        fetchAndDrawRoute(currentUserLocation, pharmacyLocation);
                        centerMapBetweenPoints(currentUserLocation, pharmacyLocation);
                    } else {
                        loadStaticLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get location: " + e.getMessage());
                    loadStaticLocation();
                });

        // Start continuous location updates
        startLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(LOCATION_FASTEST_INTERVAL)
                .setWaitForAccurateLocation(false)
                .build();

        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private void loadStaticLocation() {
        SharedPreferences prefs = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Patient patient = dbHelper.getPatientByToken(token);

        if (patient != null && patient.getLat() != null && patient.getLongt() != null) {
            double patientLat = Double.parseDouble(patient.getLat());
            double patientLon = Double.parseDouble(patient.getLongt());
            currentUserLocation = new GeoPoint(patientLat, patientLon);

            updateUserLocationMarker(currentUserLocation);
            fetchAndDrawRoute(currentUserLocation, pharmacyLocation);
            centerMapBetweenPoints(currentUserLocation, pharmacyLocation);
        }
    }

    private void updateUserLocationMarker(GeoPoint location) {
        if (userLocationMarker == null) {
            userLocationMarker = new Marker(mapView);
            userLocationMarker.setTitle("Your Location");
            userLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            userLocationMarker.setIcon(ContextCompat.getDrawable(requireContext(),
                    android.R.drawable.ic_menu_mylocation));
            mapView.getOverlays().add(userLocationMarker);
        }

        userLocationMarker.setPosition(location);
        mapView.invalidate();
    }

    private void centerMapOnUserLocation() {
        if (currentUserLocation != null) {
            // Animate to user location with higher zoom
            mapView.getController().animateTo(currentUserLocation, USER_FOCUS_ZOOM, 800L);
            showToast("Centered on your location");
        } else {
            showToast("Location not available");
        }
    }

    private void centerMapBetweenPoints(GeoPoint point1, GeoPoint point2) {
        if (point1 == null || point2 == null) return;

        double centerLat = (point1.getLatitude() + point2.getLatitude()) / 2;
        double centerLon = (point1.getLongitude() + point2.getLongitude()) / 2;
        GeoPoint centerPoint = new GeoPoint(centerLat, centerLon);

        mapView.getController().setCenter(centerPoint);
        mapView.getController().setZoom(DEFAULT_ZOOM);
    }

    private void fetchAndDrawRoute(GeoPoint start, GeoPoint end) {
        if (start == null || end == null) return;

        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                String urlString = String.format(
                        "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                        start.getLongitude(), start.getLatitude(),
                        end.getLongitude(), end.getLatitude()
                );

                connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray routes = jsonResponse.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONArray coordinates = route.getJSONObject("geometry")
                            .getJSONArray("coordinates");

                    ArrayList<GeoPoint> routePoints = new ArrayList<>();
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray coord = coordinates.getJSONArray(i);
                        routePoints.add(new GeoPoint(coord.getDouble(1), coord.getDouble(0)));
                    }

                    double distance = route.getDouble("distance") / 1000; // Convert to km
                    double duration = route.getDouble("duration") / 60; // Convert to minutes

                    // Update UI on main thread
                    mainHandler.post(() -> {
                        drawRoute(routePoints);
                        updateRouteInfo(distance, duration);
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching route: " + e.getMessage(), e);
                mainHandler.post(() -> showToast("Failed to load route"));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void drawRoute(ArrayList<GeoPoint> routePoints) {
        // Remove old route if exists
        if (currentRouteLine != null) {
            mapView.getOverlays().remove(currentRouteLine);
        }

        currentRouteLine = new Polyline(mapView);
        currentRouteLine.setPoints(routePoints);
        currentRouteLine.setColor(Color.BLUE);
        currentRouteLine.setWidth(10f);
        mapView.getOverlays().add(currentRouteLine);
        mapView.invalidate();
    }

    private void updateRouteInfo(double distance, double duration) {
        tvDistance.setText(String.format("%.2f km", distance));
        tvDuration.setText(String.format("%.0f min", duration));
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Stop location updates
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        // Shutdown executor service
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }

        // Remove all handler callbacks
        if (mainHandler != null) {
            mainHandler.removeCallbacksAndMessages(null);
        }
    }
}