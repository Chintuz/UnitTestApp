package com.test.testapp.datamodel;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.testapp.model.Data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DataManager {

    public List<Data> getAssetJsonData(Context context, String fileName) {
        String json = null;
        List<Data> list = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            list = new Gson().fromJson(json, new TypeToken<List<Data>>() {
            }.getType());
            return list;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
