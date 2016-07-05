package com.tranxuanloc.emobits.dj;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tranxuanloc on 4/6/2016.
 */
public class DJInfo {
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private ArrayList<ListDJInfo> data;

    public ArrayList<ListDJInfo> getData() {
        ArrayList<ListDJInfo> data = new ArrayList<>();
        data.add(new DJInfo.ListDJInfo("1", "Beyond", "Breaks", 1430));
        data.add(new DJInfo.ListDJInfo("2", "Snake", "Hard Dance", 1232));
        data.add(new DJInfo.ListDJInfo("3", "Fedde Le Grand", "Hip-Hop", 1189));
        data.add(new DJInfo.ListDJInfo("4", "Ummet Ozcan", "Funk / R&B", 1530));
        data.add(new DJInfo.ListDJInfo("5", "Showtek", "Minimal", 1210));
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    class ListDJInfo {
        @SerializedName("iddj")
        private String id;
        @SerializedName("namedj")
        private String name;
        @SerializedName("styledj")
        private String style;
        @SerializedName("total")
        private int total;

        public ListDJInfo(String id, String name, String style, int total) {
            this.id = id;
            this.name = name;
            this.style = style;
            this.total = total;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getStyle() {
            return style;
        }

        public int getTotal() {
            return total;
        }
    }
}
