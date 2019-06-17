
package com.test.testapp.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataMap {

    @SerializedName("options")
    @Expose
    private List<String> options = null;

    private List<Boolean> boolOption = null;

    public List<Boolean> getBoolOption() {
        return boolOption;
    }

    public void setBoolOption(List<Boolean> boolOption) {
        this.boolOption = boolOption;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public DataMap withOptions(List<String> options) {
        this.options = options;
        return this;
    }

}
