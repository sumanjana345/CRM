package com.example.crm;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ApproveTprogramAdapter extends RecyclerView.Adapter<ApproveTprogramAdapter.ApproveTprogramViewHolder> {

    private List<ApproveTprogramItem> itemList;
    private final SparseBooleanArray checkBoxStates;

    public ApproveTprogramAdapter(List<ApproveTprogramItem> itemList) {
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        checkBoxStates = new SparseBooleanArray();
    }

    public List<ApproveTprogramItem> getItemList() {
        return itemList;  // Return the current item list
    }

    // Method to update data in the adapter
    public void updateData(List<ApproveTprogramItem> newItemList) {
        this.itemList = newItemList != null ? newItemList : new ArrayList<>();
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public ApproveTprogramViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approvetprogramitem, parent, false);
        return new ApproveTprogramViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApproveTprogramViewHolder holder, int position) {
        ApproveTprogramItem item = itemList.get(position);
        holder.textDivision.setText("Division : " + item.getDivision());
        holder.textShiftType.setText("Shift : " + item.getShiftType());
        holder.textDate.setText("Date : " + item.getDate());
        holder.textDoctors.setText("Doctors : " + item.getDoctorName());
        holder.textAreas.setText("Area : " + item.getAreaName());
        holder.textRetailers.setText("Retailer : " + item.getRetailerName());
        holder.textVisitWith.setText("VisitWith : " + item.getVisitWithName());
        holder.textRemarks.setText("Remarks : " + item.getRemarks());
        holder.texttourProgramId.setText("TourProgramId : " + String.valueOf(item.getTourProgramId()));

        // Set checkbox state
        holder.checkBox.setChecked(checkBoxStates.get(position, false));

        // Toggle the checkbox state on item click
        holder.itemView.setOnClickListener(v -> {
            boolean isChecked = !holder.checkBox.isChecked();
            holder.checkBox.setChecked(isChecked);
            checkBoxStates.put(position, isChecked);
            item.setChecked(isChecked);  // Update the item's checked state
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ApproveTprogramViewHolder extends RecyclerView.ViewHolder {
        public TextView textDivision;
        public TextView textShiftType;
        public TextView textDate;
        public TextView textDoctors;
        public TextView textAreas;
        public TextView textRetailers;
        public TextView textVisitWith;
        public TextView textRemarks;
        public TextView texttourProgramId;
        public CheckBox checkBox;

        public ApproveTprogramViewHolder(View itemView) {
            super(itemView);
            textDivision = itemView.findViewById(R.id.textDivision);
            textShiftType = itemView.findViewById(R.id.textShiftType);
            textDate = itemView.findViewById(R.id.textDate);
            textDoctors = itemView.findViewById(R.id.textDoctors);
            textAreas = itemView.findViewById(R.id.textAreas);
            textRetailers = itemView.findViewById(R.id.textRetailers);
            textVisitWith = itemView.findViewById(R.id.textVisitWith);
            textRemarks = itemView.findViewById(R.id.textRemarks);
            texttourProgramId = itemView.findViewById(R.id.texttourProgramId);
            checkBox = itemView.findViewById(R.id.cbmanager_app_rej); // CheckBox
        }
    }
}


