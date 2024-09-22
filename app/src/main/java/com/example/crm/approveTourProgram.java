package com.example.crm;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class approveTourProgram extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApproveTourProgramAdapter adapter;
    private Button btnApproveSelected;
    private List<RequestedTourProgram> requestedTourPrograms;
    private String employeeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_tour_program);

        initializeViews();
        retrieveDataFromIntent();
        setupRecyclerView(); // Moved this after data retrieval
        setupButtonListeners();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerapprovetprogram);
        btnApproveSelected = findViewById(R.id.approvetprogram);
        ImageView approvalBack = findViewById(R.id.approval_back);
    }

    private void retrieveDataFromIntent() {
        Intent intent = getIntent();
        requestedTourPrograms = intent.getParcelableArrayListExtra("tourPrograms");
        employeeId = intent.getStringExtra("employee_id");
        Log.d("EmployeeID", "Employee ID in approveTourProgram: " + employeeId);

        if (requestedTourPrograms == null) {
            requestedTourPrograms = new ArrayList<>();
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ApproveTourProgramAdapter(requestedTourPrograms);
        recyclerView.setAdapter(adapter);
    }

    private void setupButtonListeners() {
        btnApproveSelected.setOnClickListener(v -> {
            List<RequestedTourProgram> selectedPrograms = adapter.getSelectedTourPrograms();
            if (selectedPrograms.isEmpty()) {
                Toast.makeText(this, "No tour programs selected for approval.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    sendTourProgramForApproval(selectedPrograms);
                } catch (JSONException e) {
                    Log.e("JSONError", "Error creating JSON request", e);
                    Toast.makeText(this, "Error creating JSON request.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.approval_back).setOnClickListener(v -> onBackPressed());
    }

    private void sendTourProgramForApproval(List<RequestedTourProgram> selectedPrograms) throws JSONException {
        String url = "http://125.22.105.182:4777/api/ViewTourProgram";

        if (employeeId == null || employeeId.isEmpty()) {
            Toast.makeText(this, "Invalid employee ID. Cannot proceed with the approval.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();
        JSONArray requestsArray = new JSONArray();

        for (RequestedTourProgram program : selectedPrograms) {
            JSONObject requestObject = new JSONObject();
            requestObject.put("tourProgramId", program.getTourProgramId());
            requestObject.put("employee_id", employeeId); // Use String if necessary
            requestObject.put("s_Date", program.getS_Date());
            // Convert boolean to string "true" or "false"
            requestObject.put("requested", String.valueOf(program.isRequested()));
            requestsArray.put(requestObject);
        }

        jsonBody.put("requests", requestsArray);

        String requestBody = jsonBody.toString();

        Log.d("RequestBody", requestBody);

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                url,
                this::handleServerResponse,
                this::handleVolleyError
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    private void handleServerResponse(String response) {
        Log.d("ServerResponse", response);
        // Check for the exact response string
        if (response.trim().equals("Tour program requests inserted successfully.")) {
            Toast.makeText(this, "Tour program approval request submitted successfully.", Toast.LENGTH_SHORT).show();
            adapter.removeSelectedItems();
            // Navigate back to TourPlanner.java
            Intent intent = new Intent(approveTourProgram.this, TourPlanner.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Optional: To close the current activity
        } else {
            Log.d("ResponseMismatch", "Unexpected response: " + response);
            Toast.makeText(this, "unable to submit the responce .", Toast.LENGTH_SHORT).show();

        }

    }


    private void handleVolleyError(VolleyError error) {
        String errorMessage = "An error occurred";
        if (error.networkResponse != null && error.networkResponse.data != null) {
            errorMessage = new String(error.networkResponse.data, StandardCharsets.UTF_8);
        }
        Log.e("VolleyError", errorMessage);
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    public class ApproveTourProgramAdapter extends RecyclerView.Adapter<ApproveTourProgramAdapter.TourProgramViewHolder> {

        private final List<RequestedTourProgram> requestedTourPrograms;
        private final List<RequestedTourProgram> selectedTourPrograms = new ArrayList<>();

        public ApproveTourProgramAdapter(List<RequestedTourProgram> requestedTourPrograms) {
            // Filter the list to include only items where requested is false
            this.requestedTourPrograms = new ArrayList<>();
            for (RequestedTourProgram program : requestedTourPrograms) {
                if (!program.isRequested()) {
                    this.requestedTourPrograms.add(program);
                }
            }
        }

        @NonNull
        @Override
        public TourProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_approvetprogram, parent, false);
            return new TourProgramViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TourProgramViewHolder holder, int position) {
            RequestedTourProgram tourProgram = requestedTourPrograms.get(position);
            holder.bind(tourProgram);
        }

        private void updateSelection(RequestedTourProgram tourProgram, boolean isChecked) {
            tourProgram.setRequested(isChecked); // Set the requested field
            if (isChecked) {
                if (!selectedTourPrograms.contains(tourProgram)) {
                    selectedTourPrograms.add(tourProgram);
                }
            } else {
                selectedTourPrograms.remove(tourProgram);
            }
        }


        public List<RequestedTourProgram> getSelectedTourPrograms() {
            return new ArrayList<>(selectedTourPrograms);
        }

        public void removeSelectedItems() {
            requestedTourPrograms.removeAll(selectedTourPrograms);
            selectedTourPrograms.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return requestedTourPrograms.size();
        }

        public class TourProgramViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvShiftType, tvRemarks, tvDivision, tvEmployeeId, tvArea, tvDoctor, tvRetailer, tvVisitWith;
            CheckBox cbSelect;

            public TourProgramViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvShiftType = itemView.findViewById(R.id.tv_shift_type);
                tvRemarks = itemView.findViewById(R.id.tv_remarks);
                tvDivision = itemView.findViewById(R.id.tv_division);
                tvEmployeeId = itemView.findViewById(R.id.tv_employee_id);
                tvArea = itemView.findViewById(R.id.tv_area);
                tvDoctor = itemView.findViewById(R.id.tv_doctor);
                tvRetailer = itemView.findViewById(R.id.tv_retailer);
                tvVisitWith = itemView.findViewById(R.id.tv_visit_with);
                cbSelect = itemView.findViewById(R.id.cb_select);

                // Add click listener to the item view
                itemView.setOnClickListener(v -> {
                    // Toggle checkbox state
                    cbSelect.setChecked(!cbSelect.isChecked());
                    // Update selection based on new checkbox state
                    updateSelection(requestedTourPrograms.get(getAdapterPosition()), cbSelect.isChecked());
                });
            }

            public void bind(RequestedTourProgram tourProgram) {
                tvDate.setText("Date: " + tourProgram.getS_Date());
                tvShiftType.setText("Shift Type: " + tourProgram.getShift_type());
                tvRemarks.setText("Remarks: " + tourProgram.getRemarks());
                tvDivision.setText("Division: " + tourProgram.getDivision());
                tvEmployeeId.setText("Employee ID: " + tourProgram.getEmployee_id());
                tvArea.setText("Area: " + tourProgram.getAreaName());
                tvDoctor.setText("Doctor: " + tourProgram.getDoctorName());
                tvRetailer.setText("Retailer: " + tourProgram.getRetailerName());
                tvVisitWith.setText("Visit With: " + tourProgram.getVisitWithName());

                // Set the initial state of the checkbox
                cbSelect.setOnCheckedChangeListener(null); // Remove existing listener
                cbSelect.setChecked(selectedTourPrograms.contains(tourProgram));

                // Handle checkbox state change
                cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> updateSelection(tourProgram, isChecked));
            }
        }

    }

}
