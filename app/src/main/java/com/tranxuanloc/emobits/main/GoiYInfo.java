package com.tranxuanloc.emobits.main;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Trần Xuân Lộc on 1/16/2016.
 */
public class GoiYInfo {
    @SerializedName("status")
    String status;
    @SerializedName("data")
    ArrayList<ListInfo> data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<ListInfo> getData() {
        return data;
    }

    public void setData(ArrayList<ListInfo> data) {
        this.data = data;
    }

    class ListInfo {
        @SerializedName("topicid")
        String topicID;
        @SerializedName("topictitle")
        String topicTitle;

        public String getTopicID() {
            return topicID;
        }

        public void setTopicID(String topicID) {
            this.topicID = topicID;
        }

        public String getTopicTitle() {
            return topicTitle;
        }

        public void setTopicTitle(String topicTitle) {
            this.topicTitle = topicTitle;
        }
    }

}
