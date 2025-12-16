package com.acharyaamrit.medicare.patient.map;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RouteDistanceHelper {
    private static final String TAG = "RouteDistanceHelper";
    private final ExecutorService executorService;
    private final Handler mainHandler;

    public RouteDistanceHelper() {
        executorService = Executors.newFixedThreadPool(3); // Allow multiple concurrent requests
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public interface RouteCallback {
        void onRouteCalculated(double distance, double duration);
        void onError(String error);
    }

    public void fetchActualDistance(double startLat, double startLon,
                                    double endLat, double endLon,
                                    RouteCallback callback) {
        executorService.execute(() -> {
            HttpURLConnection connection = null;
            try {
                // OSRM uses longitude,latitude order (lon,lat NOT lat,lon)
                String urlString = String.format(
                        "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=false&geometries=geojson",
                        startLon, startLat, endLon, endLat
                );

                Log.d(TAG, "Fetching route: " + urlString);

                connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(15000);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    Log.e(TAG, "HTTP Error: " + responseCode);
                    mainHandler.post(() -> callback.onError("Server error: " + responseCode));
                    return;
                }

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
                String code = jsonResponse.optString("code", "");

                if (!"Ok".equals(code)) {
                    Log.e(TAG, "OSRM Error: " + code);
                    mainHandler.post(() -> callback.onError("Route error: " + code));
                    return;
                }

                JSONArray routes = jsonResponse.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    double distance = route.getDouble("distance") / 1000.0; // Convert to km
                    double duration = route.getDouble("duration") / 60.0; // Convert to minutes

                    Log.d(TAG, String.format("Route calculated: %.2f km, %.0f min", distance, duration));

                    // Update UI on main thread
                    mainHandler.post(() -> callback.onRouteCalculated(distance, duration));
                } else {
                    Log.w(TAG, "No routes found in response");
                    mainHandler.post(() -> callback.onError("No route found"));
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching route: " + e.getMessage(), e);
                mainHandler.post(() -> callback.onError(e.getMessage()));
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}