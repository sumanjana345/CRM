package com.example.crm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

public class DoctorReport extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private TextView docLatitude, docLongitude, docTime,docDate;
    private Spinner dcrDoctorSpinner, dcrDoctorVisitWithSpinner, spinnerSample, spinnerGift;
    private EditText editSample, editGift ,dcr_doctor_remarks;
    private Handler timeHandler = new Handler();
    private Runnable timeRunnable;
    private Button addSample, addGift,endDcrButton;
    private LinearLayout sampleContainer, giftContainer;
    private ImageView backButton;
    List<String> visitWithOptions;
    List<String> selectedVisitWith;
    SharedPreferences sp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_report);

        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);


        initializeViews();
        setupAdapters();
        setupLocationServices();
        setupButtons();
        startLiveTime();
        setDocDate();


    }

    private void initializeViews() {
        docLatitude = findViewById(R.id.Doc_Latitude);
        docLongitude = findViewById(R.id.Doc_Longitude);
        docTime = findViewById(R.id.Doc_Time);
        docDate = findViewById(R.id.docDate);
        dcr_doctor_remarks =findViewById(R.id.dcr_doctor_remarks);
        dcrDoctorSpinner = findViewById(R.id.dcr_doctor);
        spinnerSample = findViewById(R.id.spinner_sample);
        spinnerGift = findViewById(R.id.spinner_gift);
        editSample = findViewById(R.id.edit_sample);
        editGift = findViewById(R.id.edit_gift);
        addSample = findViewById(R.id.add_sample);
        addGift = findViewById(R.id.add_gift);
        sampleContainer = findViewById(R.id.sample_container);
        giftContainer = findViewById(R.id.gift_container);
        endDcrButton = findViewById(R.id.doctor_end_dcr);
        backButton = findViewById(R.id.dcr_doctor_back);
        dcrDoctorVisitWithSpinner = findViewById(R.id.dcr_doctor_visitwith);


    }


private void setDocDate(){
    // Get the current date
    String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    docDate.setText("Date: " + currentDate);

}


    private void startLiveTime() {
        timeRunnable = new Runnable() {
            @Override
            public void run() {
                updateLiveTime();
                timeHandler.postDelayed(this, 1000); // Update every second
            }
        };
        timeHandler.post(timeRunnable);
    }

    private void updateLiveTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        docTime.setText("Time: " + currentTime);
    }

    private void setupAdapters() {
        ArrayList<String> doctorList = new ArrayList<>();


        String employeeId = sp.getString("employeeId","");
        visitWithOptions = new ArrayList<>();
        selectedVisitWith = new ArrayList<>();
        ArrayList<String> sampleListOptions = new ArrayList<>();
        ArrayList<String> giftListOptions = new ArrayList<>();

        fetchDoctorData(employeeId);
        ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, doctorList);
        doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dcrDoctorSpinner.setAdapter(doctorAdapter);

        // Set up Visit With Spinner
        fetchVisitWithData(employeeId);
        ArrayAdapter<String> visitWithAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select VisitWith"});
        dcrDoctorVisitWithSpinner.setAdapter(visitWithAdapter);
        dcrDoctorVisitWithSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(dcrDoctorVisitWithSpinner, visitWithOptions, selectedVisitWith, visitWithAdapter);
            }
            return true;
        });
        fetchSampleAndGiftData(employeeId);
        ArrayAdapter<String> sampleSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sampleListOptions);

        sampleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSample.setAdapter(sampleSpinnerAdapter);

        fetchSampleAndGiftData(employeeId);
        ArrayAdapter<String> giftSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, giftListOptions);
        giftSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGift.setAdapter(giftSpinnerAdapter);
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
                            ArrayAdapter<String>visitWithAdapter = (ArrayAdapter<String>) dcrDoctorVisitWithSpinner.getAdapter();
                            visitWithAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DoctorReport.this, "Error parsing visit with data", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(DoctorReport.this, "Resource not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Volley Error", "Error fetching visit with data");
                                    Toast.makeText(DoctorReport.this, "Error fetching visit with data", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Volley Error", "Error fetching visit with data (error object is null)");
                                Toast.makeText(DoctorReport.this, "Error fetching visit with data (error object is null)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Volley Error", "Error fetching visit with data (error object is null)");
                            Toast.makeText(DoctorReport.this, "Error fetching visit with data (error object is null)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        requestQueue.add(jsonArrayRequest);
    }

    private void fetchSampleAndGiftData(String employeeId) {
        String url = "http://125.22.105.182:4777/api/SampleAndGift/" + employeeId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> sampleList = new ArrayList<>();
                            ArrayList<String> giftList = new ArrayList<>();

                            // Create a separate ArrayList for the default options
                            ArrayList<String> defaultSampleList = new ArrayList<>();
//                            defaultSampleList.add("Select Sample");
                            ArrayList<String> defaultGiftList = new ArrayList<>();
//                            defaultGiftList.add("Select Gift");

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String sampleName = jsonObject.getString("sample_Name");
                                String giftName = jsonObject.getString("gift_Name");

                                sampleList.add(sampleName);
                                giftList.add(giftName);
                            }

                            // Merge the default options with the API data
                            sampleList.addAll(0, defaultSampleList);
                            giftList.addAll(0, defaultGiftList);

                            ArrayAdapter<String> sampleSpinnerAdapter = new ArrayAdapter<>(DoctorReport.this, android.R.layout.simple_spinner_item, sampleList);
                            sampleSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerSample.setAdapter(sampleSpinnerAdapter);

                            ArrayAdapter<String> giftSpinnerAdapter = new ArrayAdapter<>(DoctorReport.this, android.R.layout.simple_spinner_item, giftList);
                            giftSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerGift.setAdapter(giftSpinnerAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DoctorReport.this, "Error parsing sample and gift data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }


    private void fetchDoctorData(String employeeId) {
        String url = "http://125.22.105.182:4777/api/Doctor/" + employeeId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> doctorList = new ArrayList<>();
                            // Create a separate ArrayList for the default options
                            ArrayList<String> defaultDoctorList = new ArrayList<>();
//

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String doctorName = jsonObject.getString("doctorName");
                                doctorList.add(doctorName);
                            }
                            // Merge the default options with the API data
                            doctorList.addAll(0, defaultDoctorList);

                            ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(DoctorReport.this, android.R.layout.simple_spinner_item, doctorList);
                            doctorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            dcrDoctorSpinner.setAdapter(doctorAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(DoctorReport.this, "Error parsing Doctor data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
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
                Toast.makeText(DoctorReport.this, selectedItem + " selected", Toast.LENGTH_SHORT).show();
            } else {
                selectedOptions.remove(selectedItem);
                Toast.makeText(DoctorReport.this, selectedItem + " deselected", Toast.LENGTH_SHORT).show();
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
            String selectedItemsText ="Selected : "+ String.join(", ", selectedOptions);
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

    private void setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        setupLocationRequest();
        getLocation();
    }

    private void setupLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    docLatitude.setText("Latitude: " + location.getLatitude());
                    docLongitude.setText("Longitude: " + location.getLongitude());
                }
            }
        };
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    docLatitude.setText("Latitude: " + location.getLatitude());
                    docLongitude.setText("Longitude: " + location.getLongitude());
                }
            }
        });
    }

    private void setupButtons() {
        findViewById(R.id.doctor_sample).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSampleView();
            }
        });

        findViewById(R.id.doctor_gift).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGiftView();
            }
        });

        addSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddSample();
            }
        });

        addGift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddGift();
            }
        });

        endDcrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employeeId = sp.getString("employeeId","");
                String latitudeString = docLatitude.getText().toString().replace("Latitude: ", "");
                double latitude = Double.parseDouble(latitudeString);
                // Remove "Longitude: " from the string before parsing
                String longitudeString = docLongitude.getText().toString().replace("Longitude: ", "");
                double longitude = Double.parseDouble(longitudeString);
                String docDateString = docDate.getText().toString().replace("Date: ", "");
                // Parse the date string in "dd-MM-yyyy" format to a Date object
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Date docDate = null;
                try {
                    docDate = inputDateFormat.parse(docDateString);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                // Format the Date object to the desired "yyyy-MM-dd" format
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDocDate = outputDateFormat.format(docDate);
                String DocTime = docTime.getText().toString().replace("Time: ","");
                String remarks = dcr_doctor_remarks.getText().toString();
                String doctorName = dcrDoctorSpinner.getSelectedItem().toString(); // Replace with actual value
               // String sampleName = sampleContainer;

                try {
                    // Create the JSON object to send in the POST request
                    JSONObject doctorDcrData = new JSONObject();
                    // Prepare JSON data
                    doctorDcrData.put("employeeId", Integer.parseInt(employeeId));
                    doctorDcrData.put("latitude", latitude);
                    doctorDcrData.put("longitude", longitude);

                    doctorDcrData.put("doctorDcrDate", formattedDocDate);
                    doctorDcrData.put("doctorDcrTime", DocTime);

                    doctorDcrData.put("remarks", remarks); // Replace with your actual remarks
                    doctorDcrData.put("doctorName", doctorName); // Replace with your actual doctor name

                    // Create JSON array for samples
                    JSONArray sampleArray = new JSONArray();
                    for (Sample sample : sampleList) {
                        sampleArray.put(sample.toJson());
                    }
                    doctorDcrData.put("doctorDcrSamples", sampleArray);

                    // Create JSON array for gifts
                    JSONArray giftArray = new JSONArray();
                    for (Gift gift : giftList) {
                        giftArray.put(gift.toJson());
                    }
                    doctorDcrData.put("doctorDcrGifts", giftArray);

                    // Create the visitWiths array
                    JSONArray visitWithsArray = new JSONArray();
                    for (String visitWithName : selectedVisitWith) {
                        JSONObject visitWithObject = new JSONObject();
                        visitWithObject.put("visitWithNames", visitWithName);
                        visitWithsArray.put(visitWithObject);
                    }

                    doctorDcrData.put("doctorDcrVisitWiths", visitWithsArray);

                    // Log JSON payload for debugging
                    Log.d("JSON Payload", doctorDcrData.toString());

                    // Create the Volley request
                    String url = "http://125.22.105.182:4777/api/DoctorDcr";
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            url,
                            response -> {
                                // Handle the response as a String
                                Toast.makeText(DoctorReport.this, "Doctor DCR data inserted successfully", Toast.LENGTH_SHORT).show();
                                // Redirect to Dcr_Dashbord
                                Intent intent = new Intent(DoctorReport.this, Dcr_Dashbord.class);
                                startActivity(intent);
                                finish(); // Close the current activity
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
                                Toast.makeText(DoctorReport.this, "Error saving data: " + errorMessage, Toast.LENGTH_LONG).show();
                                error.printStackTrace(); // Print stack trace for detailed debugging
                            }
                    ) {
                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            // Convert JSON object to bytes
                            return doctorDcrData.toString().getBytes();
                        }

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }
                    };

                    // Add the request to the Volley request queue
                    RequestQueue requestQueue = Volley.newRequestQueue(DoctorReport.this);
                    requestQueue.add(stringRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DoctorReport.this, "Error creating JSON data", Toast.LENGTH_SHORT).show();
                }
            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent = new Intent(DoctorReport.this, Dcr_Dashbord.class);
                startActivity(intent);
                //finish();  Optionally call finish() if you want to close the current activity
            }
        });
    }

    private void showSampleView() {
        spinnerSample.setVisibility(View.VISIBLE);
        editSample.setVisibility(View.VISIBLE);
        addSample.setVisibility(View.VISIBLE);
        sampleContainer.setVisibility(View.VISIBLE);

        spinnerGift.setVisibility(View.GONE);
        editGift.setVisibility(View.GONE);
        addGift.setVisibility(View.GONE);
        giftContainer.setVisibility(View.GONE);
    }

    private void showGiftView() {
        spinnerSample.setVisibility(View.GONE);
        editSample.setVisibility(View.GONE);
        addSample.setVisibility(View.GONE);
        sampleContainer.setVisibility(View.GONE);

        spinnerGift.setVisibility(View.VISIBLE);
        editGift.setVisibility(View.VISIBLE);
        addGift.setVisibility(View.VISIBLE);
        giftContainer.setVisibility(View.VISIBLE);
    }
    private ArrayList<Sample> sampleList = new ArrayList<>();
    private ArrayList<Gift> giftList = new ArrayList<>();

    private void handleAddSample() {
        String selectedSample = spinnerSample.getSelectedItem().toString();
        String sampleText = editSample.getText().toString();

        if (TextUtils.isEmpty(sampleText)) {
            Toast.makeText(DoctorReport.this, "Please enter the sample quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(sampleText);
        Sample sample = new Sample(selectedSample, quantity);
        sampleList.add(sample);

        // Create and add item view
        View sampleView = createItemView(selectedSample + ", Quantity = " + sampleText, sampleList.size() - 1, "sample");
        sampleContainer.addView(sampleView);

        editSample.setText(""); // Clear the input field
    }



    private void handleAddGift() {
        String selectedGift = spinnerGift.getSelectedItem().toString();
        String giftText = editGift.getText().toString();

        if (TextUtils.isEmpty(giftText)) {
            Toast.makeText(DoctorReport.this, "Please enter the gift quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(giftText);
        Gift gift = new Gift(selectedGift, quantity);
        giftList.add(gift);

        // Create and add item view
        View giftView = createItemView(selectedGift + ", Quantity = " + giftText, giftList.size() - 1, "gift");
        giftContainer.addView(giftView);

        editGift.setText(""); // Clear the input field
    }



    private View createItemView(final String itemText, final int position, final String type) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(0, 10, 0, 10);

        TextView itemTextView = new TextView(this);
        itemTextView.setText(itemText);
        itemTextView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button removeButton = new Button(this);
        removeButton.setText("Remove");
        removeButton.setTextColor(Color.RED);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item from the UI
                ((LinearLayout) itemLayout.getParent()).removeView(itemLayout);

                // Remove the item from the ArrayList
                if (type.equals("sample")) {
                    if (position >= 0 && position < sampleList.size()) {
                        sampleList.remove(position);
                    }
                } else if (type.equals("gift")) {
                    if (position >= 0 && position < giftList.size()) {
                        giftList.remove(position);
                    }
                }
            }
        });

        itemLayout.addView(itemTextView);
        itemLayout.addView(removeButton);

        return itemLayout;
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Dcr_Dashbord activity when the back button is pressed
        super.onBackPressed();
        Intent intent = new Intent(DoctorReport.this, Dcr_Dashbord.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove time handler callbacks to prevent memory leaks
        timeHandler.removeCallbacks(timeRunnable);

        // Optionally navigate back to the Dcr_Dashbord activity on destruction
        // Note: Be cautious about using onDestroy for navigation. Back navigation should be in onBackPressed()
        Intent intent = new Intent(DoctorReport.this, Dcr_Dashbord.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocationServices();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
