package com.tranxuanloc.emobits.main;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Trần Xuân Lộc on 1/15/2016.
 */
public class SimpleResponse {
    @SerializedName("status")
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
