package com.example.crm;

import static com.example.crm.AppData.employeeId;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashbordActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    SharedPreferences sp;
    private boolean clearHistory=true;
   // private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashbord);

       // progressBar = findViewById(R.id.progress_bar);
        // Force the app to use a light theme
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

       // checkConnection();

        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        initializeAppData();
      //  Toast.makeText(this, "Welcome " + AppData.employeeName, Toast.LENGTH_SHORT).show();

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.dcr_dashbord_toolbar);
        setSupportActionBar(toolbar);


        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle the click on the ImageView to open the drawer
        ImageView drawerToggleImage = findViewById(R.id.dcr_dashbord_back);
        drawerToggleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Find the NavigationView
        NavigationView navigationView = findViewById(R.id.navview);

        // Get the header view from the NavigationView
        View headerView = navigationView.getHeaderView(0);  // Ensure this index is correct

        // Initialize the TextViews from the header layout
        TextView headerName = headerView.findViewById(R.id.hedername);
        TextView headerDivision = headerView.findViewById(R.id.hederdivision);
        TextView headerEmail = headerView.findViewById(R.id.hederemail);
        TextView headerPhone = headerView.findViewById(R.id.hederphone);

        // Set text to the TextViews (Ensure you have data to populate)
        if (headerName != null) {
            headerName.setText("Name : " +AppData.employeeName);  // Set actual data here
        }
        if (headerDivision != null) {
            headerDivision.setText("Division: "+AppData.division);
        }
        if (headerEmail != null) {
            headerEmail.setText("Email : "+AppData.email);
        }
        if (headerPhone != null) {
            headerPhone.setText("Phone :"+AppData.phoneNumber);
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.dashbordbutton) {
                    // Navigate to the dashboard and show a toast
                    Toast.makeText(DashbordActivity.this, "DashBord View", Toast.LENGTH_SHORT).show();
                    // Close the drawer after selection
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (id == R.id.Logoutbutton) {
                    logout();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Find the CardView by its ID
        CardView tourplanCardView = findViewById(R.id.tourplan);
        // Set an OnClickListener to the CardView
        tourplanCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //   Create an Intent to start the TourPlanner activity
                Intent intent = new Intent(DashbordActivity.this, TourPlanner.class);
                startActivity(intent);
            }
        });



        CardView dcrview = findViewById(R.id.Dcr);
        dcrview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Call the TourProgramResponse method
                TourProgramResponse();

            }
        });



    }

    private String getCurrentDate() {
        // Define the date format as "yyyy-MM-dd"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Get the current date
        String currentDate = sdf.format(new Date());

        // Return the formatted current date
        return currentDate;
    }




    private void checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();

        if (network != null) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
            if (networkCapabilities != null) {
                boolean wifiConnected = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
                boolean mobileConnected = networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR);

                if (wifiConnected) {
                    Toast.makeText(this, "Connected to WiFi", Toast.LENGTH_SHORT).show();
                } else if (mobileConnected) {
                    Toast.makeText(this, "Connected to Mobile Data", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Not connected to the Internet", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Not connected to the Internet", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not connected to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeAppData() {
        // Initialize AppData with values from SharedPreferences
        AppData.employeeName = sp.getString("employeeName", "Default Name");
        employeeId = sp.getString("employeeId","");
        AppData.division = sp.getString("division", "Default Division");
        AppData.email = sp.getString("email", "Default Email");
        AppData.phoneNumber = sp.getString("phoneNumber", "Default Phone");
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Reset isLoggedIn flag but keep login details if rememberMe is checked
        editor.putBoolean("isLoggedIn", false);
        if (!sharedPreferences.getBoolean("rememberMe", false)) {
            editor.remove("businessId");
            editor.remove("email");
            editor.remove("password");
            editor.putBoolean("rememberMe", false);
        }

        editor.apply();

        Intent intent = new Intent(DashbordActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close SecondActivity so it is removed from the back stack

        Toast.makeText(DashbordActivity.this, "Successfully Logged Out", Toast.LENGTH_SHORT).show();
    }

    private void TourProgramResponse() {
        // Get the current date in the correct format
        String currentDate = getCurrentDate();

        // Create the URL for the API endpoint
        String url = "http://125.22.105.182:4777/api/TourProgramResponse";

        // Create the request body as a JSONObject
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("employeeId", employeeId);
            requestBody.put("s_Date", currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("employeeId")) {
                                // Handle the success case
                                int employeeId = response.getInt("employeeId");
                                String s_Date = response.getString("s_Date");

                                // Show a success message and navigate to DcrActivity
                              //  Toast.makeText(DashbordActivity.this, "Employee ID: " + employeeId + ", Date: " + s_Date, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(DashbordActivity.this, DcrActivity.class);
                                startActivity(intent);
                                finish();
                                DcrResponce();

                            } else if (response.has("message")) {
                                // Handle the error case
                                String message = response.getString("message");
                                String details = response.getString("details");

                                // Show specific message based on error details
                                if (message.contains("No TourProgram Found")) {
                                    Toast.makeText(DashbordActivity.this, "No TourProgram Found for the Date and EmployeeId", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(DashbordActivity.this, "Error: " + message + ", Details: " + details, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response
                        Toast.makeText(DashbordActivity.this, "TourProgram May Not Create or Approved for the Date and EmployeeId : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }


    private void DcrResponce() {
        // Get the current date in the correct format
        String currentDate = getCurrentDate();  // Ensure this returns "yyyy-MM-dd"

        // Create the URL for the API endpoint
        String url = "http://125.22.105.182:4777/api/DcrResponce";

        // Create the request body as a JSONObject
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("employeeId", employeeId);
            requestBody.put("dcrDate", currentDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a JsonObjectRequest
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("employeeId")) {
                                // Handle success response with status code 200
                                Intent intent = new Intent(DashbordActivity.this, Dcr_Dashbord.class);
                                startActivity(intent);
                                finish();
                            } else if (response.has("message")) {
                                // Handle error response with status code 500
                                String message = response.getString("message");
                                String details = response.getString("details");

                                if (message.contains("No DCR found")) {
                                    Toast.makeText(DashbordActivity.this, "No DCR Found for the Employee on the specified date.", Toast.LENGTH_SHORT).show();

                                    // Intent to DcrActivity
                                    Intent intent = new Intent(DashbordActivity.this, DcrActivity.class);
                                    startActivity(intent);
                                     finish();
                                } else {
                                    Toast.makeText(DashbordActivity.this, "Error: " + message + ", Details: " + details, Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error response
                        Toast.makeText(DashbordActivity.this, "Make the Dcr to Start Your day " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public  void finish()
    {
        if(clearHistory)
            finishAffinity();
    }
}



