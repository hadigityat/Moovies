package com.can.moovies;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrailerItem {

    @SerializedName("source")
    @Expose
    private String source;

    @SerializedName("type")
    @Expose
    private String type;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
