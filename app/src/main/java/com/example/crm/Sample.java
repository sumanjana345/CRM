package com.example.crm;

import org.json.JSONException;
import org.json.JSONObject;

public class Sample {
    private String sample_Name;
    private int s_Quantity;

    public Sample(String sample_Name, int s_Quantity) {
        this.sample_Name = sample_Name;
        this.s_Quantity = s_Quantity;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sample_Name", sample_Name);
        jsonObject.put("s_Quantity", s_Quantity);
        return jsonObject;
    }
}


