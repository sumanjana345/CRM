package com.example.crm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class createTprogramActivity extends AppCompatActivity {

    private Spinner dateSpinner;
    private Spinner shiftSpinner;
    private Spinner areaSpinner;
    private Spinner doctorSpinner;
    private Spinner visitWithSpinner;
    private Spinner retailerSpinner;
    private TextView selectedDatesTextView;
    private EditText editdivision;
    private EditText edit_E_id;
    private EditText editHeadquater;
    EditText tprogramRemerks_edit;

    private boolean[] selectedItems;
    private List<String> dateList;
    private List<String> selectedDates;
    private List<String> filteredDates;


    private ArrayAdapter<String> listAdapter;

    // For other spinners
    private List<String> areaOptions;
    private List<String> doctorOptions;
    private List<String> visitWithOptions;
    private List<String> retailerOptions;

    private List<String> selectedAreas;
    private List<String> selectedDoctors;
    private List<String> selectedVisitWith;
    private List<String> selectedRetailers;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tprogram);

        dateSpinner = findViewById(R.id.dateSpinner);
        shiftSpinner = findViewById(R.id.shift_spinner);
        selectedDatesTextView = findViewById(R.id.selectedDatesTextView);

        areaSpinner = findViewById(R.id.area_spinner);
        doctorSpinner = findViewById(R.id.doctor_spinner);
        visitWithSpinner = findViewById(R.id.visitwith_spinner);
        retailerSpinner = findViewById(R.id.retailer_spinner);
        Button saveButton = findViewById(R.id.create_Sbutton);
        tprogramRemerks_edit = findViewById(R.id.remerks_edit);
        editdivision = findViewById(R.id.editdivision);
        edit_E_id = findViewById(R.id.edit_E_id);
        editHeadquater = findViewById(R.id.editHeadquater);

        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String hQid = sp.getString("hQid", "");
        String employeeId = sp.getString("employeeId","");
        Toast.makeText(this, "Hello " + sp.getString("employeeId", ""), Toast.LENGTH_SHORT).show();
        editdivision.setText( sp.getString("division", ""));
        edit_E_id.setText( sp.getString("employeeId", ""));
        editHeadquater.setText( sp.getString("hQid", ""));

       // fetchPreviouslySelectedDates(employeeId);

        dateList = new ArrayList<>();
        // Retrieve the dates from the Intent
        ArrayList<String> receivedDates = getIntent().getStringArrayListExtra("dates");
        String dateType = getIntent().getStringExtra("dateType");
        // Check if the receivedDates is not null and add them to dateList
        if (receivedDates != null) {
            dateList.addAll(receivedDates);
        }

        selectedItems = new boolean[dateList.size()];
        selectedDates = new ArrayList<>();
        filteredDates = new ArrayList<>(dateList);

        // Initialize lists for other spinners
        areaOptions = new ArrayList<>();
        doctorOptions = new ArrayList<>();
        visitWithOptions = new ArrayList<>();
        retailerOptions = new ArrayList<>();

        selectedAreas = new ArrayList<>();
        selectedDoctors = new ArrayList<>();
        selectedVisitWith = new ArrayList<>();
        selectedRetailers = new ArrayList<>();

        // Set up Shift Spinner
        String[] shiftOptions = {"Work", "Others"};
        ArrayAdapter<String> shiftAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shiftOptions);
        shiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shiftSpinner.setAdapter(shiftAdapter);

        // Set up Date Spinner
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Dates"});
        dateSpinner.setAdapter(dateAdapter);
        dateSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(dateSpinner, dateList, selectedDates, dateAdapter);
            }
            return true;
        });

        fetchAreaData(hQid);
        // Set up Area Spinner
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Area"});
        areaSpinner.setAdapter(areaAdapter);
        areaSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(areaSpinner, areaOptions, selectedAreas, areaAdapter);
            }
            return true;
        });

        // Set up Doctor Spinner
        fetchDoctorData(employeeId);
        ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Doctor"});
        doctorSpinner.setAdapter(doctorAdapter);
        doctorSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(doctorSpinner, doctorOptions, selectedDoctors, doctorAdapter);
            }
            return true;
        });

        fetchVisitWithData(employeeId);
        // Set up Visit With Spinner
        ArrayAdapter<String> visitWithAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select VisitWith"});
        visitWithSpinner.setAdapter(visitWithAdapter);
        visitWithSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(visitWithSpinner, visitWithOptions, selectedVisitWith, visitWithAdapter);
            }
            return true;
        });

        // Set up Retailer Spinner
        fetchRetailerData(employeeId);
        ArrayAdapter<String> retailerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Select Retailer"});
        retailerSpinner.setAdapter(retailerAdapter);
        retailerSpinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showMultiSelectDialog(retailerSpinner, retailerOptions, selectedRetailers, retailerAdapter);
            }
            return true;
        });

        // Set listener for shiftSpinner
        shiftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        saveButton.setOnClickListener(v -> {

            // Parse employeeId and hQid as integers
            int employeeid = Integer.parseInt(edit_E_id.getText().toString());
            int hqid = Integer.parseInt(editHeadquater.getText().toString());
            String division = editdivision.getText().toString();
            String shiftType = shiftSpinner.getSelectedItem().toString();
            String remarks = tprogramRemerks_edit.getText().toString();

            // Check if the dateSpinner is selected
            if (selectedDates.isEmpty()) {
                Toast.makeText(createTprogramActivity.this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the shiftSpinner value is "Work"
            if (shiftType.equals("Work")) {
                if (selectedAreas.isEmpty()) {
                    // If "Work" is selected, ensure the areaSpinner has a selected value
                    Toast.makeText(createTprogramActivity.this, "Please select an area", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else if (shiftType.equals("Others")) {
                // If "Others" is selected, ensure remarks are filled
                if (remarks.isEmpty()) {
                    Toast.makeText(createTprogramActivity.this, "Please provide remarks for 'Others'", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            try {
                // Create the JSON object to send in the POST request
                JSONObject tourProgramData = new JSONObject();
                tourProgramData.put("employee_id", employeeid); // As integer
                tourProgramData.put("division", division);
                tourProgramData.put("hQid", hqid); // As integer
                tourProgramData.put("shift_type", shiftType);
                tourProgramData.put("remarks", remarks);

                // Create the date array from selectedDates
                JSONArray s_DateArray = new JSONArray();
                for (String date : selectedDates) {
                    JSONObject dateObject = new JSONObject();
                    dateObject.put("date", date);
                    s_DateArray.put(dateObject);
                }
                tourProgramData.put("s_Date", s_DateArray);

                // Create the areas array
                JSONArray areasArray = new JSONArray();
                for (String areaName : selectedAreas) {
                    JSONObject areaObject = new JSONObject();
                    areaObject.put("areaName", areaName);
                    areasArray.put(areaObject);
                }
                tourProgramData.put("areas", areasArray);

                // Create the doctors array
                JSONArray doctorsArray = new JSONArray();
                for (String doctorName : selectedDoctors) {
                    JSONObject doctorObject = new JSONObject();
                    doctorObject.put("doctorName", doctorName);
                    doctorsArray.put(doctorObject);
                }
                tourProgramData.put("doctors", doctorsArray);

                // Create the retailers array
                JSONArray retailersArray = new JSONArray();
                for (String retailerName : selectedRetailers) {
                    JSONObject retailerObject = new JSONObject();
                    retailerObject.put("retailerName", retailerName);
                    retailersArray.put(retailerObject);
                }
                tourProgramData.put("retailers", retailersArray);

                // Create the visitWiths array
                JSONArray visitWithsArray = new JSONArray();
                for (String visitWithName : selectedVisitWith) {
                    JSONObject visitWithObject = new JSONObject();
                    visitWithObject.put("visitWithName", visitWithName);
                    visitWithsArray.put(visitWithObject);
                }
                tourProgramData.put("visitWiths", visitWithsArray);

                // Log JSON payload for debugging
                Log.d("JSON Payload", tourProgramData.toString());

                // Create the Volley request
                String url = "http://125.22.105.182:4777/api/TourPrograms";
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        url,
                        response -> {
                            // Handle the response as a String
                            Toast.makeText(createTprogramActivity.this, "Response: " + response, Toast.LENGTH_SHORT).show();
                            // Redirect to TourPlanner
                            Intent intent1 = new Intent(createTprogramActivity.this, TourPlanner.class);
                            startActivity(intent1);
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
                            Toast.makeText(createTprogramActivity.this, "Error saving data: " + errorMessage, Toast.LENGTH_LONG).show();
                            error.printStackTrace(); // Print stack trace for detailed debugging
                        }
                ) {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        // Convert JSON object to bytes
                        return tourProgramData.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }
                };

                // Add the request to the Volley request queue
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);

            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(createTprogramActivity.this, "Invalid number format", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(createTprogramActivity.this, "Error creating JSON data", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView create_back = findViewById(R.id.create_back);
        create_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
//                Intent intent = new Intent(createTprogramActivity.this,TourPlanner.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

//    private void fetchPreviouslySelectedDates(String employeeId) {
//        String url = "http://125.22.105.182:4777/api/TourProgram/dates/" + employeeId;
//
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//
//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
//                Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        List<String> selectedDates = new ArrayList<>();
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject dateObject = response.getJSONObject(i);
//                                String date = dateObject.getString("s_Date");
//                                if (date.contains("T")) {
//                                    date = date.split("T")[0]; // Extract date part only
//                                }
//                                selectedDates.add(date);
//                            }
//                            // Update spinner with available dates
//                            filterOutPreviouslySelectedDates(selectedDates);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(createTprogramActivity.this, "Error parsing dates", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(createTprogramActivity.this, "Error fetching dates: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );
//
//        requestQueue.add(jsonArrayRequest);
//    }
//
//    // Remove previously selected dates from the spinner options
//    private void filterOutPreviouslySelectedDates(List<String> selectedDates) {
//        filteredDates.removeAll(selectedDates);
//        // Update the spinner with filtered dates
//        setupSpinner(filteredDates);
//    }
//
//    private void setupSpinner(List<String> dates) {
//        dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dates);
//        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        dateSpinner.setAdapter(dateAdapter);
//    }



    private void fetchAreaData(String hQid) {
        String url = "http://125.22.105.182:4777/Area/" + hQid; // Concatenate hQid with the API URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            areaOptions.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String areaName = jsonObject.getString("areaName"); // Assuming the API response has a key "areaName"
                                areaOptions.add(areaName);
                            }
                            ArrayAdapter<String> areaAdapter = (ArrayAdapter<String>) areaSpinner.getAdapter();
                            areaAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(createTprogramActivity.this, "Error parsing area data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(createTprogramActivity.this, "Error fetching area data", Toast.LENGTH_SHORT).show();
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
                            ArrayAdapter<String>visitWithAdapter = (ArrayAdapter<String>) visitWithSpinner.getAdapter();
                            visitWithAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(createTprogramActivity.this, "Error parsing visit with data", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(createTprogramActivity.this, "Resource not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Volley Error", "Error fetching visit with data");
                                    Toast.makeText(createTprogramActivity.this, "Error fetching visit with data", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Volley Error", "Error fetching visit with data (error object is null)");
                                Toast.makeText(createTprogramActivity.this, "Error fetching visit with data (error object is null)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Volley Error", "Error fetching visit with data (error object is null)");
                            Toast.makeText(createTprogramActivity.this, "Error fetching visit with data (error object is null)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        requestQueue.add(jsonArrayRequest);
    }


    private void fetchDoctorData(String employeeId) {
        String url = "http://125.22.105.182:4777/api/Doctor/" + employeeId; // Concatenate employeeId with the API URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            doctorOptions.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String doctorName = jsonObject.getString("doctorName"); // Fetch doctorName from API response
                                doctorOptions.add(doctorName);
                            }
                            ArrayAdapter<String>doctorAdapter = (ArrayAdapter<String>) doctorSpinner.getAdapter();
                            doctorAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(createTprogramActivity.this, "Error parsing Doctor data", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(createTprogramActivity.this, "Resource not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Volley Error", "Error fetching Doctor data");
                                    Toast.makeText(createTprogramActivity.this, "Error fetching Doctor data", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Volley Error", "Error fetching Doctor data (error object is null)");
                                Toast.makeText(createTprogramActivity.this, "Error fetching Doctor data (error object is null)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Volley Error", "Error fetching Doctor data (error object is null)");
                            Toast.makeText(createTprogramActivity.this, "Error fetching Doctor data (error object is null)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void fetchRetailerData(String employeeId) {
        String url = "http://125.22.105.182:4777/api/Retail/" + employeeId; // Concatenate employeeId with the API URL

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            retailerOptions.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String retailerName = jsonObject.getString("retailerName"); // Fetch doctorName from API response
                                retailerOptions.add(retailerName);
                            }
                            ArrayAdapter<String>retailerAdapter = (ArrayAdapter<String>) retailerSpinner.getAdapter();
                            retailerAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(createTprogramActivity.this, "Error parsing Retail data", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(createTprogramActivity.this, "Resource not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("Volley Error", "Error fetching Retail data");
                                    Toast.makeText(createTprogramActivity.this, "Error fetching Retail data", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.e("Volley Error", "Error fetching Retail data (error object is null)");
                                Toast.makeText(createTprogramActivity.this, "Error fetching Retail data (error object is null)", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("Volley Error", "Error fetching Retail data (error object is null)");
                            Toast.makeText(createTprogramActivity.this, "Error fetching Retail data (error object is null)", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void showMultiSelectDialog(final Spinner spinner, final List<String> options, final List<String> selectedOptions, final ArrayAdapter<String> spinnerAdapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Options : ");

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
                Toast.makeText(createTprogramActivity.this, selectedItem + " selected", Toast.LENGTH_SHORT).show();
            } else {
                selectedOptions.remove(selectedItem);
                Toast.makeText(createTprogramActivity.this, selectedItem + " deselected", Toast.LENGTH_SHORT).show();
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
            String selectedItemsText = selectedOptions.isEmpty() ? "No items selected" : "Selected: " + String.join(", ", selectedOptions);

            spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{selectedItemsText}));
            spinner.setSelection(0); // Optionally select the first item

            // Update TextView for selected dates if it's the date spinner
            if (spinner == dateSpinner) {
                selectedDatesTextView.setText(selectedItemsText);
            }
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

            // Update TextView for selected dates if it's the date spinner
            if (spinner == dateSpinner) {
                selectedDatesTextView.setText("");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setSpinnersVisibility(int visibility) {
        areaSpinner.setVisibility(visibility);
        doctorSpinner.setVisibility(visibility);
        visitWithSpinner.setVisibility(visibility);
        retailerSpinner.setVisibility(visibility);
    }


}
