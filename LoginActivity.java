package com.example.theukuleleband.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theukuleleband.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;

    private static final String LOGIN_URL = "http://yourserver.com/login.php"; // Change to your actual server URL
    private static final String PREFS_NAME = "UserSession"; // Shared Preferences name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailInput = findViewById(R.id.login_email);
        passwordInput = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> attemptLogin());
    }

    private void attemptLogin() {s
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoginTask().execute(email, password);
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];

            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Create JSON request body
                JSONObject requestBody = new JSONObject();
                requestBody.put("Email", email);
                requestBody.put("Password", password);

                OutputStream os = conn.getOutputStream();
                os.write(requestBody.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    return "{\"success\": false, \"message\": \"Server error: " + responseCode + "\"}";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "{\"success\": false, \"message\": \"Network error\"}";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject response = new JSONObject(result);
                boolean success = response.getBoolean("success");

                if (success) {
                    JSONObject user = response.getJSONObject("user");
                    String userId = user.getString("id");
                    String name = user.getString("name");
                    String email = user.getString("email");
                    String role = user.getString("role");

                    saveSession(userId, name, email, role);
                    navigateToDashboard(role);
                } else {
                    String message = response.getString("message");
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void saveSession(String userId, String name, String email, String role) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", userId);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.apply();
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "Customer":
                intent = new Intent(this, CustomerDashboard.class);
                break;
            case "DJ":
                intent = new Intent(this, DJDashboard.class);
                break;
            case "Mcee":
                intent = new Intent(this, MceeDashboard.class);
                break;
            case "Band":
                intent = new Intent(this, BandDashboard.class);
                break;
            case "Accountant":
                intent = new Intent(this, AccountantDashboard.class);
                break;
            case "Storeman":
                intent = new Intent(this, StoremanDashboard.class);
                break;
            case "Inspector":
                intent = new Intent(this, InspectorDashboard.class);
                break;
            case "Dispatchman":
                intent = new Intent(this, DispatchmanDashboard.class);
                break;
            case "Supplier":
                intent = new Intent(this, SupplierDashboard.class);
                break;
            default:
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_LONG).show();
                return;
        }
        startActivity(intent);
        finish();
    }
}
