package com.example.crm;

import android.os.Parcel;
import android.os.Parcelable;

public class TourProgram implements Parcelable {
    private String date;
    private String shiftType;
    private String remarks;
    private String division;
    private String employeeId;
    private String hQid;
    private String[] doctorNames;
    private String[] areaNames;
    private String[] retailerNames;
    private String[] visitWithNames;
    private String requested;
    // Default constructor (needed for creating new instances)
    public TourProgram() {
    }

    // Constructor for Parcel
    protected TourProgram(Parcel in) {
        date = in.readString();
        shiftType = in.readString();
        remarks = in.readString();
        division = in.readString();
        employeeId = in.readString();
        hQid = in.readString();
        doctorNames = in.createStringArray();
        areaNames = in.createStringArray();
        retailerNames = in.createStringArray();
        visitWithNames = in.createStringArray();
    }

    public static final Creator<TourProgram> CREATOR = new Creator<TourProgram>() {
        @Override
        public TourProgram createFromParcel(Parcel in) {
            return new TourProgram(in);
        }

        @Override
        public TourProgram[] newArray(int size) {
            return new TourProgram[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(shiftType);
        parcel.writeString(remarks);
        parcel.writeString(division);
        parcel.writeString(employeeId);
        parcel.writeString(hQid);
        parcel.writeStringArray(doctorNames);
        parcel.writeStringArray(areaNames);
        parcel.writeStringArray(retailerNames);
        parcel.writeStringArray(visitWithNames);
    }

    // Getters and Setters...

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String gethQid() {
        return hQid;
    }

    public void sethQid(String hQid) {
        this.hQid = hQid;
    }

    public String[] getDoctorNames() {
        return doctorNames;
    }

    public void setDoctorNames(String[] doctorNames) {
        this.doctorNames = doctorNames;
    }

    public String[] getAreaNames() {
        return areaNames;
    }

    public void setAreaNames(String[] areaNames) {
        this.areaNames = areaNames;
    }

    public String[] getRetailerNames() {
        return retailerNames;
    }

    public void setRetailerNames(String[] retailerNames) {
        this.retailerNames = retailerNames;
    }

    public String[] getVisitWithNames() {
        return visitWithNames;
    }

    public void setVisitWithNames(String[] visitWithNames) {
        this.visitWithNames = visitWithNames;
    }

//    public void setrequested(String requested) {
//        this.requested = requested;
//    }
//    public String getrequested() {
//        return requested;
//    }


}