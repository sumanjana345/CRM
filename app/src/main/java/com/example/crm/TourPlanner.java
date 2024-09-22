package com.example.crm;

import static com.example.crm.AppData.employeeId;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class TourPlanner extends AppCompatActivity {

    private TextView currentMonthTextView;
    private TextView pastMonthTextView;
    private TextView futureMonthTextView;
    SharedPreferences sp;
    int employeeIdInt;
    private ImageView backButton;
    private ProgressBar progressBar;
    private FrameLayout mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_planner);

        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String employeeId = sp.getString("employeeId", "");

        // Initialize the class-level employeeIdInt variable
        employeeIdInt = Integer.parseInt(employeeId);
        int employeeIdInt = Integer.parseInt(employeeId);
      //  Toast.makeText(this, "Hello " + employeeId, Toast.LENGTH_SHORT).show();

        // Initialize TextViews
        currentMonthTextView = findViewById(R.id.currentmonth);
        pastMonthTextView = findViewById(R.id.pastmonth);
        futureMonthTextView = findViewById(R.id.futuremonth);

        // Initialize ImageViews and set click listeners
        ImageView cmCreate = findViewById(R.id.cmcreate);
        ImageView fmCreate = findViewById(R.id.fmcreate);
        ImageView cmView = findViewById(R.id.cmview);
        ImageView pmView = findViewById(R.id.pmview);
        ImageView fmView = findViewById(R.id.fmview);
        ImageView cmApproval = findViewById(R.id.cmapproval);
        ImageView pmApproval = findViewById(R.id.pmapproval);
        ImageView fmApproval = findViewById(R.id.fmapproval);
        ImageView backButton = findViewById(R.id.tprogeam_dashbord_back);


        progressBar = findViewById(R.id.progressBar);
        mainContent = findViewById(R.id.main_content);

        // Show ProgressBar and hide main content for 2 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hide ProgressBar
                progressBar.setVisibility(View.GONE);
                // Show main content
                mainContent.setVisibility(View.VISIBLE);
            }
        }, 1000); // 1000 milliseconds = 1.0 seconds



        cmCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentMonthDates(employeeIdInt); // Fetch and pass current month dates
            }
        });

        fmCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchFutureMonthDates(employeeIdInt); // Fetch and pass future month dates
            }
        });

        cmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCurrentMonthTourProgram();
            }
        });

        pmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchPastMonthTourProgram();
            }
        });

        fmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchFutureMonthTourProgram();
            }
        });

        cmApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCurrentTourProgram();
                }
        });

        pmApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPastTourProgram();
            }
        });


        fmApproval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestFutureTourProgram();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Fetch month names and update TextViews
        fetchMonthNames();
    }

    private void fetchMonthNames() {
        String url = "http://125.22.105.182:4777/FetchDateMonthYear?includePastMonth=true&includeCurrentMonth=true&includeFutureMonth=true";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String currentMonthName = null;
                            String pastMonthName = null;
                            String futureMonthName = null;

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject monthData = response.getJSONObject(i);
                                String monthName = monthData.getString("monthName");

                                if (monthData.has("dateOfCurrentMonth") && monthData.get("dateOfCurrentMonth") != JSONObject.NULL) {
                                    currentMonthName = monthName;
                                }
                                if (monthData.has("dateOfPreviousMonth") && monthData.get("dateOfPreviousMonth") != JSONObject.NULL) {
                                    pastMonthName = monthName;
                                }
                                if (monthData.has("dateOfNextMonth") && monthData.get("dateOfNextMonth") != JSONObject.NULL) {
                                    futureMonthName = monthName;
                                }
                            }

                            currentMonthTextView.setText(currentMonthName != null ? currentMonthName : "No Current Month Data");
                            pastMonthTextView.setText(pastMonthName != null ? pastMonthName : "No Past Month Data");
                            futureMonthTextView.setText(futureMonthName != null ? futureMonthName : "No Future Month Data");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TourPlanner.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TourPlanner.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchDates(final String dateType, boolean includePastMonth, boolean includeCurrentMonth, boolean includeFutureMonth, int employeeId) {
        // Update the URL to include employeeId
        String url = "http://125.22.105.182:4777/FetchDateMonthYear?includePastMonth=" + includePastMonth
                + "&includeCurrentMonth=" + includeCurrentMonth
                + "&includeFutureMonth=" + includeFutureMonth
                + "&employeeId=" + employeeId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<String> dates = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject dateObject = response.getJSONObject(i);

                                String date = null;

                                // Fetch the correct date based on the dateType
                                if ("current".equals(dateType) && dateObject.has("dateOfCurrentMonth")) {
                                    date = dateObject.getString("dateOfCurrentMonth");
                                } else if ("future".equals(dateType) && dateObject.has("dateOfNextMonth")) {
                                    date = dateObject.getString("dateOfNextMonth");
                                }

                                // Remove the "T00:00:00" part if present
                                if (date != null && date.contains("T")) {
                                    date = date.split("T")[0]; // Get the date part only
                                }

                                if (date != null) {
                                    dates.add(date);
                                }
                            }

                            // Check if any dates were retrieved
                            if (dates.isEmpty()) {
                                Toast.makeText(TourPlanner.this, "No dates found for " + dateType, Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Pass dates to the next activity
                            Intent intent = new Intent(TourPlanner.this, createTprogramActivity.class);
                            intent.putStringArrayListExtra("dates", dates);
                            intent.putExtra("dateType", dateType);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TourPlanner.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TourPlanner.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    // Example methods to call fetchDates with the employeeId
    private void fetchCurrentMonthDates(int employeeId) {
        fetchDates("current", false, true, false, employeeId);
    }

    private void fetchFutureMonthDates(int employeeId) {
        fetchDates("future", false, false, true, employeeId);
    }


    private void fetchTourProgram(int employeeIdInt, String monthType) {
        String url = "http://125.22.105.182:4777/api/ViewTourProgram/" + employeeIdInt + "?monthType=" + monthType;
        Log.d("API URL", "URL: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d("API Response", "Response: " + response);
                    try {
                        ArrayList<TourProgram> tourPrograms = new ArrayList<>();

                        if (response.startsWith("[")) {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tourProgramJson = jsonArray.getJSONObject(i);

                                TourProgram tourProgram = new TourProgram();
                                tourProgram.setDate(tourProgramJson.getString("s_Date").split("T")[0]);
                                tourProgram.setShiftType(tourProgramJson.getString("shift_type"));
                                tourProgram.setRemarks(tourProgramJson.getString("remarks"));
                                tourProgram.setDivision(tourProgramJson.getString("division"));
                                tourProgram.setEmployeeId(tourProgramJson.getString("employee_id"));
                                tourProgram.sethQid(tourProgramJson.getString("hQid"));

                                // Handle array fields
                                tourProgram.setDoctorNames(tourProgramJson.getString("doctorName").split(","));
                                tourProgram.setAreaNames(tourProgramJson.getString("areaName").split(","));
                                tourProgram.setRetailerNames(tourProgramJson.getString("retailerName").split(","));
                                tourProgram.setVisitWithNames(tourProgramJson.getString("visitWithName").split(","));


                                // Add the TourProgram object to the list
                                tourPrograms.add(tourProgram);
                            }

                            // Create an intent for the appropriate activity
                            Intent intent;
                            intent = new Intent(TourPlanner.this, ViewTourProgram.class);
                            intent.putParcelableArrayListExtra("tourPrograms", tourPrograms); // Pass the tourPrograms list
                            startActivity(intent);

                        } else {
                            Log.d("API Response", "Message: " + response);
                            Toast.makeText(TourPlanner.this, response, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(TourPlanner.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        Log.e("API Error", "Error 404: Resource not found");
                        Toast.makeText(TourPlanner.this, "No tour programs found for this employee and month type", Toast.LENGTH_SHORT).show();
                    } else if (error.networkResponse != null) {
                        Log.e("API Error", "Error: " + error.networkResponse.statusCode);
                        Log.e("API Error", "Error body: " + new String(error.networkResponse.data));
                        Toast.makeText(TourPlanner.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("API Error", "Error: " + error.toString());
                        Toast.makeText(TourPlanner.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(stringRequest);
    }

//    private void logArray(String label, String[] array) {
//        if (array != null && array.length > 0) {
//            Log.d("Array Log", label + ": " + String.join(", ", array));
//        } else {
//            Log.d("Array Log", label + ": No data");
//        }
//    }




    private void fetchCurrentMonthTourProgram() {
        fetchTourProgram(employeeIdInt, "Current");
    }

    private void fetchPastMonthTourProgram() {
        fetchTourProgram(employeeIdInt, "Past");
    }

    private void fetchFutureMonthTourProgram() {
        fetchTourProgram(employeeIdInt, "Future");
    }


    private void requestTourProgram(int employeeIdInt, String monthType) {
        String url = "http://125.22.105.182:4777/api/ViewTourProgram/" + employeeIdInt + "?monthType=" + monthType;
        Log.d("API URL", "URL: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    ArrayList<RequestedTourProgram> tourPrograms = new ArrayList<>();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            RequestedTourProgram tourProgram = new RequestedTourProgram();

                            tourProgram.setDivision(obj.getString("division"));
                            tourProgram.setEmployee_id(obj.getInt("employee_id"));
                            // tourProgram.setHqid(obj.getInt("hQid")); // Uncomment if required
                            tourProgram.setShift_type(obj.getString("shift_type"));
                            tourProgram.setRemarks(obj.getString("remarks"));
                            tourProgram.setS_Date(obj.getString("s_Date"));
                            tourProgram.setDoctorName(obj.getString("doctorName"));
                            tourProgram.setAreaName(obj.getString("areaName"));
                            tourProgram.setRetailerName(obj.getString("retailerName"));
                            tourProgram.setVisitWithName(obj.getString("visitWithName"));

                            // Convert varchar values to boolean
                            tourProgram.setRequested("true".equalsIgnoreCase(obj.getString("requested")));
                            tourProgram.setApproved("true".equalsIgnoreCase(obj.getString("approved")));
                            tourProgram.setRejected("true".equalsIgnoreCase(obj.getString("rejected")));

                            tourProgram.setTourProgramId(obj.getInt("tourProgramId"));

                            tourPrograms.add(tourProgram);
                        }

                        // Save employee_id in SharedPreferences
//                        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putInt("employee_id", employeeIdInt);
//                        editor.apply();

                        Intent intent = new Intent(TourPlanner.this, approveTourProgram.class);
                        intent.putParcelableArrayListExtra("tourPrograms", new ArrayList<>(tourPrograms));
                        intent.putExtra("employee_id", employeeId); // Make sure employeeId is correctly initialized
                        startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Volley", "Json parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                        Log.e("API Error", "Error 404: Resource not found");
                        Toast.makeText(TourPlanner.this, "No tour programs found to send For Approval for this employee and month type", Toast.LENGTH_SHORT).show();
                    } else if (error.networkResponse != null) {
                        Log.e("API Error", "Error: " + error.networkResponse.statusCode);
                        Log.e("API Error", "Error body: " + new String(error.networkResponse.data));
                        Toast.makeText(TourPlanner.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("API Error", "Error: " + error.toString());
                        Toast.makeText(TourPlanner.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }



    private void requestCurrentTourProgram() {
        requestTourProgram(employeeIdInt, "Current");
    }

    private void requestPastTourProgram() {
        requestTourProgram(employeeIdInt, "Past");
    }

    private void requestFutureTourProgram() {
        requestTourProgram(employeeIdInt, "Future");
    }


}
