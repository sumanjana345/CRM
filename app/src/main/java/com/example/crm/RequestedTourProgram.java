package com.example.crm;

import android.os.Parcel;
import android.os.Parcelable;

public class RequestedTourProgram implements Parcelable {
    private String division;
    private int employee_id;
    private String shift_type;
    private String remarks;
    private String s_Date;
    private String doctorName;
    private String areaName;
    private String retailerName;
    private String visitWithName;
    private boolean requested = false;  // Default value
    private boolean approved = false;   // Default value
    private boolean rejected = false;   // Default value
    private int tourProgramId;

    // Default constructor
    public RequestedTourProgram() {
    }

    // Getters and Setters
    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public String getShift_type() {
        return shift_type;
    }

    public void setShift_type(String shift_type) {
        this.shift_type = shift_type;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getS_Date() {
        return s_Date;
    }

    public void setS_Date(String s_Date) {
        this.s_Date = s_Date;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getRetailerName() {
        return retailerName;
    }

    public void setRetailerName(String retailerName) {
        this.retailerName = retailerName;
    }

    public String getVisitWithName() {
        return visitWithName;
    }

    public void setVisitWithName(String visitWithName) {
        this.visitWithName = visitWithName;
    }

    public boolean isRequested() {
        return requested;
    }

    public void setRequested(boolean requested) {
        this.requested = requested;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isRejected() {
        return rejected;
    }

    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    public int getTourProgramId() {
        return tourProgramId;
    }

    public void setTourProgramId(int tourProgramId) {
        this.tourProgramId = tourProgramId;
    }

    // Check visibility based on the requested value
    public boolean isVisible() {
        return !requested;  // Return true if requested is false, meaning the item should be visible
    }

    // Parcelable implementation
    protected RequestedTourProgram(Parcel in) {
        division = in.readString();
        employee_id = in.readInt();
        shift_type = in.readString();
        remarks = in.readString();
        s_Date = in.readString();
        doctorName = in.readString();
        areaName = in.readString();
        retailerName = in.readString();
        visitWithName = in.readString();
        requested = in.readByte() != 0;
        approved = in.readByte() != 0;
        rejected = in.readByte() != 0;
        tourProgramId = in.readInt();
    }

    public static final Creator<RequestedTourProgram> CREATOR = new Creator<RequestedTourProgram>() {
        @Override
        public RequestedTourProgram createFromParcel(Parcel in) {
            return new RequestedTourProgram(in);
        }

        @Override
        public RequestedTourProgram[] newArray(int size) {
            return new RequestedTourProgram[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(division);
        dest.writeInt(employee_id);
        dest.writeString(shift_type);
        dest.writeString(remarks);
        dest.writeString(s_Date);
        dest.writeString(doctorName);
        dest.writeString(areaName);
        dest.writeString(retailerName);
        dest.writeString(visitWithName);
        dest.writeByte((byte) (requested ? 1 : 0));
        dest.writeByte((byte) (approved ? 1 : 0));
        dest.writeByte((byte) (rejected ? 1 : 0));
        dest.writeInt(tourProgramId);
    }
}
