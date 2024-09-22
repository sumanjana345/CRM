package com.example.crm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewDcrReport extends AppCompatActivity {

    private Spinner viewDcrSpinner;
    private ImageView backButton;
    SharedPreferences sp;
    private String employeeId;
    private RecyclerView recyclerView;
    private DcrAdapter DcrAdapter;
    private List<DoctorDcrData> doctorDcrDataList = new ArrayList<>();
    private List<RetailerDcrData> retailerDcrDataList = new ArrayList<>();
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dcr_report);

        // Initialize the Spinner and Back Button
        viewDcrSpinner = findViewById(R.id.view_dcr_spinner);
        backButton = findViewById(R.id.view_dcr_back);

        // Retrieve employeeId from SharedPreferences
//        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
//        employeeId = sp.getString("employeeId", ""); // Fetch the employeeId

        // Handle back button click
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent intent = new Intent(ViewDcrReport.this, Dcr_Dashbord.class);
                startActivity(intent);
                finish(); // Optionally call finish() if you want to close the current activity
            }
        });

        // Create an ArrayList and add items to it
        ArrayList<String> viewDcrOptions = new ArrayList<>();
        viewDcrOptions.add("Select View DCR"); // Prompt text
        viewDcrOptions.add("Doctor");
        viewDcrOptions.add("Retailer");

        // Create an instance of the custom adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, viewDcrOptions) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item from Spinner (hint text)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Set the hint text color gray
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the Spinner
        viewDcrSpinner.setAdapter(adapter);

        // Set a listener on the Spinner
        viewDcrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { // Ensure the hint is not selected
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    Toast.makeText(ViewDcrReport.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();

                    // Get employeeId from SharedPreferences
                    SharedPreferences sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    String employeeId = sp.getString("employeeId", "");

                    // Determine API URL based on selection
                    String apiUrl;
                    if (selectedItem.equals("Doctor")) {
                        apiUrl = "http://125.22.105.182:4777/ViewDoctorDcr/ByEmployeeId/" + employeeId;
                    } else if (selectedItem.equals("Retailer")) {
                        apiUrl = "http://125.22.105.182:4777/ViewRetailerDcr/ByEmployeeId/" + employeeId;
                    } else {
                        return; // No action if the selection is not valid
                    }

                    // Create a RequestQueue
                    RequestQueue requestQueue = Volley.newRequestQueue(ViewDcrReport.this);


                    // Create a JsonArrayRequest for the API call
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                            Request.Method.GET,
                            apiUrl,
                            null,
                            response -> {
                                List<Object> dcrDataList = new ArrayList<>();
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject jsonObject = response.getJSONObject(i);

                                        if (selectedItem.equals("Doctor")) {
                                            DoctorDcrData doctorData = new DoctorDcrData();
                                            doctorData.setEmployeeId(jsonObject.getInt("employeeId"));
                                            doctorData.setLatitude(jsonObject.getDouble("latitude"));
                                            doctorData.setLongitude(jsonObject.getDouble("longitude"));
                                            doctorData.setDoctor_DcrDate(jsonObject.getString("doctor_DcrDate"));
                                            doctorData.setDoctor_DcrTime(jsonObject.getString("doctor_DcrTime"));
                                            doctorData.setRemarks(jsonObject.getString("remarks"));
                                            doctorData.setDoctor_Name(jsonObject.getString("doctor_Name"));
                                            doctorData.setVisitWithNames(jsonObject.getString("visitWithNames"));
                                            doctorData.setGift_Name(jsonObject.getString("gift_Name"));
                                            doctorData.setG_Quantity(jsonObject.getString("g_Quantity"));
                                            doctorData.setSample_Name(jsonObject.getString("sample_Name"));
                                            doctorData.setS_Quantity(jsonObject.getString("s_Quantity"));

                                            dcrDataList.add(doctorData);
                                        } else if (selectedItem.equals("Retailer")) {
                                            RetailerDcrData retailerData = new RetailerDcrData();
                                            retailerData.setEmployeeId(jsonObject.getInt("employeeId"));
                                            retailerData.setLatitude(jsonObject.getDouble("latitude"));
                                            retailerData.setLongitude(jsonObject.getDouble("longitude"));
                                            retailerData.setRetailer_DcrDate(jsonObject.getString("retailer_DcrDate"));
                                            retailerData.setRetailer_DcrTime(jsonObject.getString("retailer_DcrTime"));
                                            retailerData.setRemarks(jsonObject.getString("remarks"));
                                            retailerData.setRetailer_Name(jsonObject.getString("retailer_Name"));
                                            retailerData.setVisitWithNames(jsonObject.getString("visitWithNames"));
                                            retailerData.setGift_Name(jsonObject.getString("gift_Name"));
                                            retailerData.setG_Quantity(jsonObject.getString("g_Quantity"));
                                            retailerData.setSample_Name(jsonObject.getString("sample_Name"));
                                            retailerData.setS_Quantity(jsonObject.getString("s_Quantity"));

                                            dcrDataList.add(retailerData);
                                        }
                                    }

                                    // Update RecyclerView with the fetched data
                                    RecyclerView recyclerView = findViewById(R.id.recyclerViewArea);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ViewDcrReport.this));
                                    DcrAdapter adapter = new DcrAdapter(dcrDataList);
                                    recyclerView.setAdapter(adapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ViewDcrReport.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                                }
                            },
                            error -> {
                                Toast.makeText(ViewDcrReport.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_LONG).show();
                            }
                    );

                    // Add request to the RequestQueue
                    requestQueue.add(jsonArrayRequest);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Dcr_Dashbord activity when the back button is pressed
        super.onBackPressed();
        Intent intent = new Intent(ViewDcrReport.this, Dcr_Dashbord.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Optionally navigate back to the Dcr_Dashbord activity on destruction
        // Note: Be cautious about using onDestroy for navigation. Back navigation should be in onBackPressed()
        Intent intent = new Intent(ViewDcrReport.this, Dcr_Dashbord.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    // Method to fetch DCR data based on the API URL
//    private void fetchDcrData(String apiUrl) {
//        // Use this method to call your API using Retrofit, Volley, or any other network library
//        Toast.makeText(this, "Fetching DCR data from: " + apiUrl, Toast.LENGTH_SHORT).show();
//        // Make your API call here
//    }


}
