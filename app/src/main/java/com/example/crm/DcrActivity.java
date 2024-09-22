package com.example.crm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DcrActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvLatitude, tvLongitude, tvDate, tvTime;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Handler timeHandler;
    private Runnable timeRunnable;
    EditText dcr_remarks;
    Button dcrButton;
    private Spinner dcrShiftSpinner, dcrVisitWithSpinner, dcrAreaSpinner;
    SharedPreferences sp;
    List<String> dcrareaOptions;
    List<String> visitWithOptions;
    List<String> selectedAreas;
    List<String> selectedVisitWith;
    private ImageView dcrbackButton;
    private ProgressBar progressBar;
    private FrameLayout mainContent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dcr);

        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        dcrShiftSpinner = findViewById(R.id.dcr_shift);
        dcrVisitWithSpinner = findViewById(R.id.dcr_visit_with);
        dcrAreaSpinner = findViewById(R.id.dcr_area);
        dcrButton = findViewById(R.id.dcr_button);
        dcr_remarks = findViewById(R.id.dcr_remarks);
        ImageView dcrbackButton =  findViewById(R.id.dcr_back);


        progressBar = findViewById(R.id.progressBar);
        mainContent = findViewById(R.id.main_content);


        // Show the ProgressBar for 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide the ProgressBar
                progressBar.setVisibility(View.GONE);
                // Show the main content
                mainContent.setVisibility(View.VISIBLE);
            }
        }, 1000); // Delay in milliseconds (1000 ms = 1 sec)



        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String hQid = sp.getString("hQid", "");
        String employeeId = sp.getString("employeeId","");

        dcrareaOptions = new ArrayList<>();
        visitWithOptions = new ArrayList<>();

        selectedAreas = new ArrayList<>();
        selectedVisitWith = new ArrayList<>();



        // Set up Shift Spinner
        String[] shiftOptions = {"Work", "Others"};
        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shiftOptions);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dcrShiftSpinner.setAdapter(shiftAdapter);

        // Set up Visit With Spinner
        fetchVisitWithData(employeeId);
        ArrayAdapter<String> visitWithAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select VisitWith"});
        dcrVisitWithSpinner.setAdapter(visitWithAdapter);
        dcrVisitWithSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(dcrVisitWithSpinner, visitWithOptions, selectedVisitWith, visitWithAdapter);
            }
            return true;
        });

        // Set up Area Spinner
        fetchAreaData(hQid);
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Area"});
        dcrAreaSpinner.setAdapter(areaAdapter);
        dcrAreaSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(dcrAreaSpinner, dcrareaOptions, selectedAreas, areaAdapter);
            }
            return true;
        });

        // Set listener for shiftSpinner
        dcrShiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedShift = (String) parent.getItemAtPosition(position);
                if ("Work".equals(selectedShift)) {
                    setSpinnersVisibility(View.VISIBLE);
                } else {
                    setSpinnersVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case when nothing is selected
            }
        });

        final String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        tvDate.setText(currentDate);  // Display the current date in the TextView

        // Check if the user already submitted for today
        String lastSubmittedDate = sp.getString("last_submitted_date", "");

        if (lastSubmittedDate.equals(currentDate)) {
            // If already submitted, redirect to Dcr_Dashbord immediately
            Intent intent = new Intent(DcrActivity.this, Dcr_Dashbord.class);
            startActivity(intent);
            finish();  // Close DcrActivity
        }


        // Initialize the time handler and runnable
        timeHandler = new Handler();
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                // Get the current time
                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                tvTime.setText("Time: " + currentTime);
                // Post the runnable again with a 1-second delay
                timeHandler.postDelayed(this, 1000);
            }
        };
        // Start the runnable
        timeHandler.post(timeRunnable);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Check location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            setupLocationRequest();
            getLocation();
        }


        // Handle back button click
        dcrbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                Intent intent = new Intent(DcrActivity.this, DashbordActivity.class);
//                startActivity(intent);
//                finish(); // Optionally call finish() if you want to close the current activity
            }
        });

        dcrButton.setOnClickListener(v -> {
            // Prepare the data to be sent
            String shiftType = dcrShiftSpinner.getSelectedItem().toString(); // Replace with actual value
            String remarks = dcr_remarks.getText().toString();
            // Remove "Latitude: " from the string before parsing
            String latitudeString = tvLatitude.getText().toString().replace("Latitude: ", "");
            double latitude = Double.parseDouble(latitudeString);
            // Remove "Longitude: " from the string before parsing
            String longitudeString = tvLongitude.getText().toString().replace("Longitude: ", "");
            double longitude = Double.parseDouble(longitudeString);
            // Get the date string from TextView and remove the "Date: " prefix
            String dcrDateString = tvDate.getText().toString().replace("Date: ", "");
            // Parse the date string in "dd-MM-yyyy" format to a Date object
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date dcrDate = null;
            try {
                dcrDate = inputDateFormat.parse(dcrDateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            // Format the Date object to the desired "yyyy-MM-dd" format
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDcrDate = outputDateFormat.format(dcrDate);

            String dcrTime = tvTime.getText().toString().replace("Time: ","");


//            // Check if the dateSpinner is selected
//            if (shiftType.equals("Work")) {
//                Toast.makeText(DcrActivity.this, "Please select an area", Toast.LENGTH_SHORT).show();
//                return;
//            }else if (shiftType.equals("Others")) {
//                // If "Others" is selected, ensure remarks are filled
//                if (remarks.isEmpty()) {
//                    Toast.makeText(DcrActivity.this, "Please provide remarks for 'Others'", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }

            try {
                // Create the JSON object to send in the POST request
                JSONObject dcrData = new JSONObject();
                dcrData.put("shiftType", shiftType);
                dcrData.put("remarks", remarks);
                dcrData.put("latitude", latitude);
                dcrData.put("longitude", longitude);
                dcrData.put("dcrDate", formattedDcrDate);
                dcrData.put("dcrTime", dcrTime);
                dcrData.put("employeeId", Integer.parseInt(employeeId)); // Convert employeeId to integer

                // Create the areaName array
                JSONArray areaNameArray = new JSONArray();
                for (String areaName : selectedAreas) {
                    JSONObject areaObject = new JSONObject();
                    areaObject.put("areaName", areaName);
                    areaNameArray.put(areaObject);
                }
                dcrData.put("areaName", areaNameArray);

                // Create the visitWithName array
                JSONArray visitWithNameArray = new JSONArray();
                for (String visitWithName : selectedVisitWith) {
                    JSONObject visitWithObject = new JSONObject();
                    visitWithObject.put("visitWithName", visitWithName);
                    visitWithNameArray.put(visitWithObject);
                }
                dcrData.put("visitWithName", visitWithNameArray);

                // Log JSON payload for debugging
                Log.d("JSON Payload", dcrData.toString());

                // Create the Volley request
                String url = "http://125.22.105.182:4777/api/Dcr";
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        url,
                        response -> {
                            // Handle the response as a String
                            Toast.makeText(DcrActivity.this, "Response: " + response, Toast.LENGTH_SHORT).show();
//                            // Save the current date as the last submitted date in SharedPreferences
//                            SharedPreferences.Editor editor = sp.edit();
//                            editor.putString("last_submitted_date", currentDate);
//                            editor.apply();

                            // Redirect to Dcr_Dashbord after submission
                            Intent intent = new Intent(DcrActivity.this, Dcr_Dashbord.class);
                            startActivity(intent);
                            finish();  // Close DcrActivity

                        },
                        error -> {
                            String errorMessage;
                            if (error.networkResponse != null) {
                                // Log HTTP response code
                                errorMessage = "HTTP Error Code: " + error.networkResponse.statusCode;
                            } else {
                                // Log general network error message
                                errorMessage = "Network Error: " + error.getMessage();
                            }
                            Toast.makeText(DcrActivity.this, "Error saving data: " + errorMessage, Toast.LENGTH_LONG).show();
                            error.printStackTrace(); // Print stack trace for detailed debugging
                        }
                ) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        // Convert JSON object to bytes
                        return dcrData.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };

                // Add the request to the Volley request queue
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(DcrActivity.this, "Error creating JSON data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void fetchAreaData(String hQid) {
        String url = "http://125.22.105.182:4777/Area/" + hQid; // Concatenate hQid with the API URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            dcrareaOptions.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String areaName = jsonObject.getString("areaName"); // Assuming the API response has a key "areaName"
                                dcrareaOptions.add(areaName);
                            }
                            ArrayAdapter<String> areaAdapter = (ArrayAdapter<String>) dcrAreaSpinner.getAdapter();
                            areaAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DcrActivity.this, "Error parsing area data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(DcrActivity.this, "Error fetching area data", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchVisitWithData(String employeeId) {
        String url = "http://125.22.105.182:4777/api/VisitWith/" + employeeId; // Concatenate employeeId with the API URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            visitWithOptions.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String managerName = jsonObject.getString("manager_Name"); // Fetch manager_Name from API response
                                visitWithOptions.add(managerName);
                            }
                            visitWithOptions.add("Self");
                            ArrayAdapter<String>visitWithAdapter = (ArrayAdapter<String>) dcrVisitWithSpinner.getAdapter();
                            visitWithAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DcrActivity.this, "Error parsing visit with data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            error.printStackTrace();
                            NetworkResponse response = error.networkResponse;
                            if (response != null) {
                                int statusCode = response.statusCode;
                                if (statusCode == 404) {
                                    Log.e("Volley Error", "Resource not found");
                                    Toast.makeText(DcrActivity.this, "Resource not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Volley Error", "Error fetching visit with data");
                                    Toast.makeText(DcrActivity.this, "Error fetching visit with data", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Volley Error", "Error fetching visit with data (error object is null)");
                                Toast.makeText(DcrActivity.this, "Error fetching visit with data (error object is null)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Volley Error", "Error fetching visit with data (error object is null)");
                            Toast.makeText(DcrActivity.this, "Error fetching visit with data (error object is null)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        requestQueue.add(jsonArrayRequest);
    }

    private void showMultiSelectDialog(final Spinner spinner, final List<String> options, final List<String> selectedOptions, final ArrayAdapter<String> spinnerAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Options");

        // Inflate the custom layout with SearchView and ListView
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_multiselect, null);
        builder.setView(dialogView);

        SearchView searchView = dialogView.findViewById(R.id.searchView);
        ListView listView = dialogView.findViewById(R.id.listView);

        // Prepare filtered list and adapter
        final List<String> filteredOptions = new ArrayList<>(options);
        final ArrayAdapter<String> listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, filteredOptions);
        listView.setAdapter(listAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // Pre-select items that were already selected
        for (int i = 0; i < filteredOptions.size(); i++) {
            if (selectedOptions.contains(filteredOptions.get(i))) {
                listView.setItemChecked(i, true);
            }
        }

        // Handle item selection
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = filteredOptions.get(position);
            boolean isChecked = listView.isItemChecked(position);

            if (isChecked) {
                selectedOptions.add(selectedItem);
                Toast.makeText(DcrActivity.this, selectedItem + " selected", Toast.LENGTH_SHORT).show();
            } else {
                selectedOptions.remove(selectedItem);
                Toast.makeText(DcrActivity.this, selectedItem + " deselected", Toast.LENGTH_SHORT).show();
            }
        });

        // Implement search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filteredOptions.clear();
                for (String option : options) {
                    if (option.toLowerCase().contains(newText.toLowerCase())) {
                        filteredOptions.add(option);
                    }
                }
                listAdapter.notifyDataSetChanged();
                return true;
            }
        });

        builder.setPositiveButton("OK", (dialog, id) -> {
            // Update spinner display
            String selectedItemsText = "Selected : "+ String.join(", ", selectedOptions);
            spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{selectedItemsText}));
            spinner.setSelection(0); // Optionally select the first item


        });

        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        builder.setNeutralButton("Clear All", (dialog, id) -> {
            // Clear all selections
            selectedOptions.clear();
            for (int i = 0; i < listView.getCount(); i++) {
                listView.setItemChecked(i, false);
            }
            // Update spinner display
            spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Option"}));
            spinner.setSelection(0);


        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setSpinnersVisibility(int visibility) {
        dcrAreaSpinner.setVisibility(visibility);
        dcrVisitWithSpinner.setVisibility(visibility);
    }

    private void setupLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(30000); // 30 seconds
        locationRequest.setFastestInterval(15000); // 15 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        tvLatitude.setText("Latitude: " + latitude);
                        tvLongitude.setText("Longitude: " + longitude);
                    }
                }
            }
        };
    }

    private void getLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

                        tvLatitude.setText("Latitude: " + latitude);
                        tvLongitude.setText("Longitude: " + longitude);
                    } else {
                        Toast.makeText(DcrActivity.this, "Unable to get location. Make sure location is enabled on the device.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationRequest();
                getLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        // Remove callbacks to stop updating the time
        timeHandler.removeCallbacks(timeRunnable);
    }

    @Override
    public void onBackPressed() {
       /* if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        super.onBackPressed();
        Intent intent = new Intent(DcrActivity.this, DashbordActivity.class);
        startActivity(intent);
        finish();

    }
}
