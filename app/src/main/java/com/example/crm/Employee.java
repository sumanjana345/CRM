package com.example.crm;

public class Employee {
    private int employee_id;
    private String e_name;

    // Constructor
    public Employee(int employee_id, String e_name) {
        this.employee_id = employee_id;
        this.e_name = e_name;
    }

    // Getters
    public int getEmployeeId() { return employee_id; }
    public String getEName() { return e_name; }

    @Override
    public String toString() {
        return e_name + " (" + employee_id + ")";
    }
}
