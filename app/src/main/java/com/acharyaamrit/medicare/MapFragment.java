package com.acharyaamrit.medicare;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.acharyaamrit.medicare.database.DatabaseHelper;
import com.acharyaamrit.medicare.model.Patient;

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

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(getActivity().getPackageName());

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.map);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("user_preference", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        Double lat = Double.parseDouble(getArguments().getString("latitude"));
        Double lon = Double.parseDouble(getArguments().getString("longitude"));
        String pharmacy_name = getArguments().getString("pharmacy_name");

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        Patient patient = dbHelper.getPatientByToken(token);

        if (patient.getLat() != null && patient.getLongt() != null) {
            Double patient_lat = Double.parseDouble(patient.getLat());
            Double patient_longt = Double.parseDouble(patient.getLongt());

            GeoPoint pharmacy = new GeoPoint(lat, lon);
            GeoPoint patientLoc = new GeoPoint(patient_lat, patient_longt);

            // Add pharmacy marker
            Marker pharmacyMarker = new Marker(mapView);
            pharmacyMarker.setPosition(pharmacy);
            pharmacyMarker.setTitle(pharmacy_name);
            mapView.getOverlays().add(pharmacyMarker);

            // Add patient marker
            Marker patientMarker = new Marker(mapView);
            patientMarker.setPosition(patientLoc);
            patientMarker.setTitle("Your Location");
            mapView.getOverlays().add(patientMarker);

            // Calculate center point
            double centerLat = (patient_lat + lat) / 2;
            double centerLon = (patient_longt + lon) / 2;
            GeoPoint centerPoint = new GeoPoint(centerLat, centerLon);

            mapView.getController().setCenter(centerPoint);
            mapView.getController().setZoom(14.0);

            // Fetch and draw route
            fetchRoute(patientLoc, pharmacy);
        }
        return view;
    }

    private void fetchRoute(GeoPoint start, GeoPoint end) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // OSRM API URL
                String urlString = String.format(
                        "https://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?overview=full&geometries=geojson",
                        start.getLongitude(), start.getLatitude(),
                        end.getLongitude(), end.getLatitude()
                );

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
                    JSONObject geometry = route.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");

                    ArrayList<GeoPoint> routePoints = new ArrayList<>();
                    for (int i = 0; i < coordinates.length(); i++) {
                        JSONArray coord = coordinates.getJSONArray(i);
                        double lon = coord.getDouble(0);
                        double lat = coord.getDouble(1);
                        routePoints.add(new GeoPoint(lat, lon));
                    }

                    // Get distance and duration
                    double distance = route.getDouble("distance") / 1000; // Convert to km
                    double duration = route.getDouble("duration") / 60; // Convert to minutes

                    // Update UI on main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            drawRoute(routePoints);
                            String info = String.format("Distance: %.2f km, Duration: %.0f min", distance, duration);
                            Toast.makeText(getContext(), info, Toast.LENGTH_LONG).show();
                        });
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching route: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to load route", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void drawRoute(ArrayList<GeoPoint> routePoints) {
        Polyline routeLine = new Polyline(mapView);
        routeLine.setPoints(routePoints);
        routeLine.setColor(Color.BLUE);
        routeLine.setWidth(10f);
        mapView.getOverlays().add(routeLine);
        mapView.invalidate();
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
}