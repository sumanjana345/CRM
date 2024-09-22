package com.example.crm;

import org.json.JSONException;
import org.json.JSONObject;

public class Gift {
    private String gift_Name;
    private int g_Quantity;

    public Gift(String gift_Name, int g_Quantity) {
        this.gift_Name = gift_Name;
        this.g_Quantity = g_Quantity;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gift_Name", gift_Name);
        jsonObject.put("g_Quantity", g_Quantity);
        return jsonObject;
    }
}


