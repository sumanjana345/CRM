package com.example.crm;
public class ApproveTprogramItem {
    private String division;
    private String shiftType;
    private String date;
    private String doctorName;
    private String areaName;
    private String retailerName;
    private String visitWithName;
    private String remarks;
    private boolean requested;
    private boolean approved;
    private boolean rejected;
    private int tourProgramId;
    private boolean checked;

    public ApproveTprogramItem(String division, String shiftType, String date, String doctorName,
                               String areaName, String retailerName, String visitWithName,
                               String remarks, boolean requested, boolean approved, boolean rejected, int tourProgramId) {
        this.division = division;
        this.shiftType = shiftType;
        this.date = date;
        this.doctorName = doctorName;
        this.areaName = areaName;
        this.retailerName = retailerName;
        this.visitWithName = visitWithName;
        this.remarks = remarks;
        this.requested = requested;
        this.approved = approved;
        this.rejected = rejected;
        this.tourProgramId = tourProgramId;
    }

    // Getters
    public String getDivision() { return division; }
    public String getShiftType() { return shiftType; }
    public String getDate() { return date; }
    public String getDoctorName() { return doctorName; }
    public String getAreaName() { return areaName; }
    public String getRetailerName() { return retailerName; }
    public String getVisitWithName() { return visitWithName; }
    public String getRemarks() { return remarks; }
    public boolean isRequested() { return requested; }
    public boolean isApproved() { return approved; }
    public boolean isRejected() { return rejected; }
    public int getTourProgramId() { return tourProgramId; }



    public ApproveTprogramItem(/* constructor parameters */) {
        // Initialize fields
        this.checked = false; // Default value
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
