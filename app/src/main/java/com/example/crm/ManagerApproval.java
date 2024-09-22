package com.example.crm;

import static com.example.crm.AppData.manager_id;
import static java.lang.Boolean.parseBoolean;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ManagerApproval extends AppCompatActivity {

    private Spinner employeeApproveSpinner;
    private ArrayList<Employee> employeeList = new ArrayList<>();
    private RequestQueue requestQueue;
    SharedPreferences sp;
    private RecyclerView recyclerView;
    private ApproveTprogramAdapter adapter;
    Button approveButton, rejectButton;
    private List<ApproveTprogramItem> selectedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_approval);

        sp = getSharedPreferences("user_prefs", MODE_PRIVATE);
        initializeAppData();

        Toast.makeText(this, "Welcome " + manager_id, Toast.LENGTH_SHORT).show();

        ImageView managerdashbord_back = findViewById(R.id.managerdashbord_back);
        managerdashbord_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rejectButton = findViewById(R.id.rejectEmpTprogram);
        approveButton = findViewById(R.id.approveEmpTprogram);

        // Initialize the Spinner
        employeeApproveSpinner = findViewById(R.id.EmployeeApprove);

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.managerrecycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and set it to the RecyclerView
        adapter = new ApproveTprogramAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialize Volley RequestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Fetch data from API
        fetchEmployeeData();

        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleApproval(true);
            }
        });

        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleApproval(false);
            }
        });
    }

    private void initializeAppData() {
        manager_id = sp.getString("manager_id", "Default manager_id");
        AppData.manager_Name = sp.getString("manager_Name", "Default manager_Name");
    }

    private void fetchEmployeeData() {
        String url = "http://125.22.105.182:4777/api/Employee/" + manager_id;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        employeeList.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject employeeObject = response.getJSONObject(i);
                                int employeeId = employeeObject.getInt("employee_id");
                                String employeeName = employeeObject.getString("e_name");
                                employeeList.add(new Employee(employeeId, employeeName));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        populateSpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("FetchEmployeeData", "Error: " + error.getMessage());
                        Toast.makeText(ManagerApproval.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void populateSpinner() {
        ArrayAdapter<Employee> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, employeeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeApproveSpinner.setAdapter(adapter);

        employeeApproveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Employee selectedEmployee = (Employee) parentView.getItemAtPosition(position);
                Toast.makeText(ManagerApproval.this, "Selected: " + selectedEmployee.getEName(), Toast.LENGTH_SHORT).show();
                fetchData(selectedEmployee.getEmployeeId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void fetchData(int employeeId) {
        String url = "http://125.22.105.182:4777/api/TourProgramapprove/by-employee?employeeId=" + employeeId;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<ApproveTprogramItem> itemList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject itemObject = response.getJSONObject(i);
                                String division = itemObject.optString("division", "");
                                String shiftType = itemObject.optString("shift_type", "");
                                String date = itemObject.optString("s_Date", "");
                                String doctorName = itemObject.optString("doctorName", "");
                                String areaName = itemObject.optString("areaName", "");
                                String retailerName = itemObject.optString("retailerName", "");
                                String visitWithName = itemObject.optString("visitWithName", "");
                                String remarks = itemObject.optString("remarks", "");

                                boolean requested = parseBoolean(itemObject.optString("requested", "false"));
                                boolean approved = parseBoolean(itemObject.optString("approved", "false"));
                                boolean rejected = parseBoolean(itemObject.optString("rejected", "false"));

                                int tourProgramId = itemObject.optInt("tourProgramId", -1);

                                if (!approved && !rejected) {
                                    ApproveTprogramItem item = new ApproveTprogramItem(
                                            division, shiftType, date, doctorName, areaName, retailerName,
                                            visitWithName, remarks, requested, approved, rejected, tourProgramId
                                    );
                                    itemList.add(item);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.updateData(itemList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ManagerApproval.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private void handleApproval(boolean isApproved) {
        JSONArray approveRejectRequests = new JSONArray();
        for (ApproveTprogramItem item : adapter.getItemList()) {
            if (item.isChecked()) { // Assuming you have an isChecked() method
                JSONObject request = new JSONObject();
                try {
                    request.put("tourProgramId", item.getTourProgramId());
                    request.put("employeeId", getSelectedEmployeeId()); // Implement this to get the selected employee ID
                    request.put("s_Date", item.getDate()); // Set the date accordingly
                    request.put("approved", String.valueOf(isApproved)); // Convert boolean to String
                    request.put("rejected", String.valueOf(!isApproved)); // Convert boolean to String
                    approveRejectRequests.put(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("approveRejectRequests", approveRejectRequests);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log the JSON body request
        Log.d("APIRequest", jsonBody.toString());
        sendApiRequest(jsonBody);
    }

    private void sendApiRequest(JSONObject jsonBody) {
        String url = "http://125.22.105.182:4777/api/ApproveReject/approve-reject";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String message = response.getString("message");
                            Toast.makeText(ManagerApproval.this, message, Toast.LENGTH_SHORT).show();
                            // Optionally refresh data or UI here
                            // Navigate to ManagerDashbord activity
                            Intent intent = new Intent(ManagerApproval.this, ManagerDashbord.class);
                            startActivity(intent);
                            finish(); // Optional: Close the current activity

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ManagerApproval.this, "Failed to process request", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private int getSelectedEmployeeId() {
        Employee selectedEmployee = (Employee) employeeApproveSpinner.getSelectedItem();
        return selectedEmployee.getEmployeeId();
    }
}
