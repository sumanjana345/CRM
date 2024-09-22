package com.example.crm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewTourProgram extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<TourProgram> tourPrograms;
    private TourProgramAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tour_program);

        recyclerView = findViewById(R.id.recyclerViewArea);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the tourPrograms list from the intent
        Intent intent = getIntent();
        tourPrograms = intent.getParcelableArrayListExtra("tourPrograms");

        // If tourPrograms list is empty or null, initialize an empty list
        if (tourPrograms == null) {
            tourPrograms = new ArrayList<>();
        }

        // Initialize and set the adapter
        adapter = new TourProgramAdapter(tourPrograms);
        recyclerView.setAdapter(adapter);

        // Back button click listener
        ImageView viewBack = findViewById(R.id.view_back);
        viewBack.setOnClickListener(v -> onBackPressed());
    }

    // RecyclerView Adapter class
    private class TourProgramAdapter extends RecyclerView.Adapter<TourProgramAdapter.TourProgramViewHolder> {

        private List<TourProgram> tourPrograms;

        public TourProgramAdapter(List<TourProgram> tourPrograms) {
            this.tourPrograms = tourPrograms != null ? tourPrograms : new ArrayList<>();
        }

        @NonNull
        @Override
        public TourProgramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tourprogram, parent, false);
            return new TourProgramViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TourProgramViewHolder holder, int position) {
            TourProgram tourProgram = tourPrograms.get(position);

            holder.tvDate.setText("Date: " + tourProgram.getDate());
            holder.tvShiftType.setText("Shift Type: " + tourProgram.getShiftType());
            holder.tvRemarks.setText("Remarks: " + tourProgram.getRemarks());
            holder.tvDivision.setText("Division: " + tourProgram.getDivision());
            holder.tvEmployeeId.setText("Employee ID: " + tourProgram.getEmployeeId());
            holder.tvArea.setText("Area: " + String.join(", ", tourProgram.getAreaNames()));
            holder.tvDoctor.setText("Doctor: " + String.join(", ", tourProgram.getDoctorNames()));
            holder.tvRetailer.setText("Retailer: " + String.join(", ", tourProgram.getRetailerNames()));
            holder.tvVisitWith.setText("Visit With: " + String.join(", ", tourProgram.getVisitWithNames()));
        }

        @Override
        public int getItemCount() {
            return tourPrograms.size();
        }

        public class TourProgramViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvShiftType, tvRemarks, tvDivision, tvEmployeeId, tvArea, tvDoctor, tvRetailer, tvVisitWith;

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
            }
        }
    }
}
