package com.example.theukuleleband.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.theukuleleband.R;
import com.example.theukuleleband.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {
    private EditText etFullName, etUsername, etPhone, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private static final String REGISTER_URL = "http://yourserver.com/signup.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        etFullName = findViewById(R.id.etFullName);
        etUsername = findViewById(R.id.etUsername);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        final String fullName = etFullName.getText().toString().trim();
        final String username = etUsername.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (fullName.isEmpty() || username.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        new RegisterUserTask().execute(fullName, username, phone, email, password);
    }

    private class RegisterUserTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage("Registering user...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            HashMap<String, String> data = new HashMap<>();
            data.put("FullName", params[0]);
            data.put("Username", params[1]);
            data.put("Phone", params[2]);
            data.put("Email", params[3]);
            data.put("Password", params[4]);

            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendPostRequest(REGISTER_URL, data);
        }

        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
            progressDialog.dismiss();

            try {
                JSONObject jsonResponse = new JSONObject(response);
                boolean error = jsonResponse.getBoolean("error");
                String message = jsonResponse.getString("message");
                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_LONG).show();

                if (!error) {
                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                    finish();
                }
            } catch (JSONException e) {
                Toast.makeText(RegistrationActivity.this, "Registration failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
