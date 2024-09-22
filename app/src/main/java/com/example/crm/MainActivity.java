package com.example.crm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText etBusinessId;
    private EditText etEmail;
    private EditText etPassword;
    private CheckBox cbRememberMe;
    private Button btnLogin;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etBusinessId = findViewById(R.id.etBusinessId);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        cbRememberMe = findViewById(R.id.cbRememberMe);
        btnLogin = findViewById(R.id.btnLogin);

        // Initialize the RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            String savedDivision = sharedPreferences.getString("division", ""); // Get the saved division
            navigateToAppropriateActivity(savedDivision);
            return;
        }

        // Load saved login details if "Remember Me" was checked
        boolean rememberMe = sharedPreferences.getBoolean("rememberMe", false);

        if (rememberMe) {
            String savedBusinessId = sharedPreferences.getString("businessId", "");
            String savedEmail = sharedPreferences.getString("email", "");
            String savedPassword = sharedPreferences.getString("password", "");

            etBusinessId.setText(savedBusinessId);
            etEmail.setText(savedEmail);
            etPassword.setText(savedPassword);
            cbRememberMe.setChecked(true);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


    private void login() {
        String businessId = etBusinessId.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(businessId)) {
            etBusinessId.setError("BusinessId is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            return;
        }

        // API URL
        String url = "http://125.22.105.182:4777/api/Login/login";

        // Create a JSON object with the login details
        JSONObject loginRequest = new JSONObject();
        try {
            loginRequest.put("business_id", businessId);
            loginRequest.put("email", email);
            loginRequest.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, loginRequest,
                response -> {
                    try {
                        // Extract the message from the response
                        String message = response.getString("message");

                        // Check if login is successful
                        if (message.equals("Login Successful")) {
                            // Extract user details
                            JSONObject userDetails = response.getJSONObject("userDetails");

                            AppData.employeeId = userDetails.getString("employee_id");
                            AppData.employeeName = userDetails.getString("e_name");
                            AppData.division = userDetails.getString("division");
                            AppData.phoneNumber = userDetails.getString("ph_number");
                            AppData.hQid = userDetails.getString("hQid");
                            AppData.email = userDetails.getString("email");
                            AppData.manager_id = userDetails.getString("manager_id");
                            AppData.manager_Name = userDetails.getString("manager_Name");

                            // Save login state and details
                            SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.putBoolean("rememberMe", cbRememberMe.isChecked());
                            editor.putString("division", AppData.division); // Save division
                            editor.putString("employeeId", AppData.employeeId);
                            editor.putString("employeeName", AppData.employeeName);
                            editor.putString("hQid", AppData.hQid);
                            editor.putString("email", AppData.email);
                            editor.putString("phoneNumber", AppData.phoneNumber);
                            editor.putString("manager_id",AppData.manager_id);
                            editor.putString("manager_Name",AppData.manager_Name);

                            // Save or clear login details based on "Remember Me" checkbox
                            if (cbRememberMe.isChecked()) {
                                editor.putString("businessId", businessId);
                                editor.putString("email", email);
                                editor.putString("password", password);
                            } else {
                                editor.remove("businessId");
                                editor.remove("email");
                                editor.remove("password");
                            }

                            // Apply changes
                            editor.apply();

                            // Navigate to the appropriate activity
                            navigateToAppropriateActivity(AppData.division);
                        } else {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Handle error
                    String errorMsg = "Error: " + error.getMessage();
                    if (error.networkResponse != null) {
                        errorMsg += "\nStatus Code: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
    }


    private void navigateToAppropriateActivity(String division) {
        Intent intent;
        if ("Manager".equalsIgnoreCase(division)) {
            intent = new Intent(MainActivity.this, ManagerDashbord.class);
        } else {
            intent = new Intent(MainActivity.this, DashbordActivity.class);
        }
        startActivity(intent);
        finish(); // Close MainActivity so it doesn't appear in the back stack
    }

//    private void navigateToSecondActivity() {
//        Intent intent = new Intent(MainActivity.this, DashbordActivity.class);
//        startActivity(intent);
//        finish(); // Close MainActivity so it doesn't appear in the back stack
//    }

//    private void navigateToManagerDashboard() {
//        Intent intent = new Intent(MainActivity.this, ManagerDashbord.class);
//        startActivity(intent);
//        finish(); // Close MainActivity so it doesn't appear in the back stack
//    }

}
