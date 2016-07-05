package com.tranxuanloc.emobits.main;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tranxuanloc on 4/16/2016.
 */
public class UserInfo {
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private ArrayList<ListUserInfo> data;

    public ArrayList<ListUserInfo> getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    class ListUserInfo {
        @SerializedName("device")
        private String device;
        @SerializedName("name")
        private String name;
        @SerializedName("param")
        private String param;

        public String getDevice() {
            return device;
        }

        public String getName() {
            return name;
        }

        public String getParam() {
            return param;
        }
    }
}
