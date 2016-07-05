package com.tranxuanloc.emobits.search;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by tranxuanloc on 4/8/2016.
 */
public class SessionInfo {
    @SerializedName("status")
    private int status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private ArrayList<ListSessionInfo> data;

    public ArrayList<ListSessionInfo> getData() {
        ArrayList<ListSessionInfo> data = new ArrayList<>();
        data.add(new ListSessionInfo("Beyond", "Rude Awakening", "http://epoint.edu.vn/assets/js/m.mp3", "Breaks", "http://static.djbooth.net/pics-albums/thumbs/tgod-mafia-rude-awakening.jpg"));
        data.add(new ListSessionInfo("Snake", "Views", "http://epoint.edu.vn/assets/js/m.mp3", "Hard Dance", "http://static.djbooth.net/pics-albums/thumbs/drake-views-from-the-6.jpg"));
        data.add(new ListSessionInfo("Fedde Le Grand", "Momentum EP", "http://epoint.edu.vn/assets/js/m.mp3", "Hip-Hop", "http://static.djbooth.net/pics-albums/thumbs/dylan-st-john-momentum.jpg"));
        data.add(new ListSessionInfo("Ummet Ozca", "Over You EP", "http://epoint.edu.vn/assets/js/m.mp3", "Funk / R&B", "http://static.djbooth.net/pics-albums/thumbs/jack-marzilla-over-you.jpg"));
        data.add(new ListSessionInfo("Showtek", "Mic Swordz EP", "http://epoint.edu.vn/assets/js/m.mp3", "Minimal", "http://static.djbooth.net/pics-albums/thumbs/noveliss-mic-swordz-ep.jpg"));
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

    public class ListSessionInfo {
        @SerializedName("iddj")
        private String idDJ;
        @SerializedName("idmusic")
        private String idSong;
        @SerializedName("mp3thumbnail")
        private String thumbnailURL;
        @SerializedName("mp3url")
        private String songURL;
        @SerializedName("mp3title")
        private String nameSong;
        @SerializedName("namedj")
        private String nameDJ;
        @SerializedName("styledj")
        private String styleDJ;
        @SerializedName("mp3param")
        private String songParam;
        @SerializedName("paramdj")
        private String djParam;

        public ListSessionInfo(String nameDJ, String nameSong, String songURL, String styleDJ, String thumbnailURL) {
            this.nameDJ = nameDJ;
            this.nameSong = nameSong;
            this.songURL = songURL;
            this.styleDJ = styleDJ;
            this.thumbnailURL = thumbnailURL;
        }

        public String getDjParam() {
            return djParam;
        }

        public String getIdDJ() {
            return idDJ;
        }

        public String getIdSong() {
            return idSong;
        }

        public String getDJName() {
            return nameDJ;
        }

        public String getSongName() {
            return nameSong;
        }

        public String getSongParam() {
            return songParam;
        }

        public String getSongURL() {
            return songURL;
        }

        public String getDJStyle() {
            return styleDJ;
        }

        public String getThumbnailURL() {
            return thumbnailURL;
        }
    }
}
