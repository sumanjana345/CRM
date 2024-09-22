package com.example.crm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DcrAdapter extends RecyclerView.Adapter<DcrAdapter.DcrViewHolder> {
    private List<Object> dcrDataList;

    public DcrAdapter(List<Object> dcrDataList) {
        this.dcrDataList = dcrDataList;
    }

    @NonNull
    @Override
    public DcrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dcr, parent, false);
        return new DcrViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DcrViewHolder holder, int position) {
        Object dcrData = dcrDataList.get(position);

        if (dcrData instanceof DoctorDcrData) {
            DoctorDcrData doctorData = (DoctorDcrData) dcrData;
            holder.dcrDateTextView.setText("Date: " + doctorData.getDoctor_DcrDate());
            holder.dcrTimeTextView.setText("Time: " + doctorData.getDoctor_DcrTime());
            holder.latitudeTextView.setText("Latitude: " + String.valueOf(doctorData.getLatitude()));
            holder.longitudeTextView.setText("Latitude: " + String.valueOf(doctorData.getLongitude()));
            holder.remarksTextView.setText("Remarks: " + doctorData.getRemarks());
            holder.retailerNameTextView.setText("Doctor Name: " + doctorData.getDoctor_Name()); // Assuming retailerName for Doctor
            holder.visitWithNamesTextView.setText("VisitWith Name: " + doctorData.getVisitWithNames());
            holder.giftNameTextView.setText("Gift Name: " + doctorData.getGift_Name());
            holder.gQuantityTextView.setText("Quantity: " + doctorData.getG_Quantity());
            holder.sampleNameTextView.setText("Sample Name : " + doctorData.getSample_Name());
            holder.sQuantityTextView.setText("Quantity: " + doctorData.getS_Quantity());
        } else if (dcrData instanceof RetailerDcrData) {
            RetailerDcrData retailerData = (RetailerDcrData) dcrData;
            holder.dcrDateTextView.setText("Date: " + retailerData.getRetailer_DcrDate());
            holder.dcrTimeTextView.setText("Time: " + retailerData.getRetailer_DcrTime());
            holder.latitudeTextView.setText("Latitude: " + String.valueOf(retailerData.getLatitude()));
            holder.longitudeTextView.setText("Latitude: " + String.valueOf(retailerData.getLongitude()));
            holder.remarksTextView.setText("Remarks: " + retailerData.getRemarks());
            holder.retailerNameTextView.setText("Retailer Name: " + retailerData.getRetailer_Name());
            holder.visitWithNamesTextView.setText("VisitWith Name: " + retailerData.getVisitWithNames());
            holder.giftNameTextView.setText("Gift Name: " + retailerData.getGift_Name());
            holder.gQuantityTextView.setText("Quantity: " + retailerData.getG_Quantity());
            holder.sampleNameTextView.setText("Sample Name : " + retailerData.getSample_Name());
            holder.sQuantityTextView.setText("Quantity: " + retailerData.getS_Quantity());
        }
    }

    @Override
    public int getItemCount() {
        return dcrDataList.size();
    }

    public static class DcrViewHolder extends RecyclerView.ViewHolder {
        TextView dcrDateTextView, dcrTimeTextView, latitudeTextView, longitudeTextView;
        TextView remarksTextView, retailerNameTextView, visitWithNamesTextView;
        TextView giftNameTextView, gQuantityTextView, sampleNameTextView, sQuantityTextView;

        public DcrViewHolder(@NonNull View itemView) {
            super(itemView);
            dcrDateTextView = itemView.findViewById(R.id.tv_dcrDate);
            dcrTimeTextView = itemView.findViewById(R.id.tv_dcrtime);
            latitudeTextView = itemView.findViewById(R.id.tv_latitude);
            longitudeTextView = itemView.findViewById(R.id.tv_longitude);
            remarksTextView = itemView.findViewById(R.id.tv_dcrremarks);
            retailerNameTextView = itemView.findViewById(R.id.retailer_Name);
            visitWithNamesTextView = itemView.findViewById(R.id.visitWithNames);
            giftNameTextView = itemView.findViewById(R.id.gift_Name);
            gQuantityTextView = itemView.findViewById(R.id.g_Quantity);
            sampleNameTextView = itemView.findViewById(R.id.sample_Name);
            sQuantityTextView = itemView.findViewById(R.id.s_Quantity);
        }
    }
}
