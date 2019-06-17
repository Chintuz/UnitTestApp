package com.test.testapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;

import com.test.testapp.datamodel.DataManager;
import com.test.testapp.model.Data;
import com.test.testapp.model.DataMap;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private DataManager dataManager;

    public MutableLiveData<List<Data>> liveData = new MutableLiveData<>();

    public ObservableBoolean chhoseImage = new ObservableBoolean(false);

    public ObservableBoolean showImage = new ObservableBoolean(false);

    public ObservableBoolean notifyList = new ObservableBoolean(false);

    public MutableLiveData<Data> selectedModel = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        dataManager = new DataManager();
    }

    public void getData() {
        List<Data> list = dataManager.getAssetJsonData(getApplication(), "data.json");

        for (Data data : list) {
            DataMap dataMap = data.getDataMap();
            List<Boolean> booleanList = new ArrayList<>();
            if (dataMap.getOptions() != null){
                for (String s : dataMap.getOptions()) {
                    booleanList.add(false);
                }
                dataMap.setBoolOption(booleanList);
            }
        }

        liveData.setValue(list);
    }

    public void clearImage(Data data) {
        data.setBitmap(null);
        notifyList.set(true);
    }

    public void onImageClick(Data data) {
        selectedModel.setValue(data);
        if (data.getBitmap() == null) {
            chhoseImage.set(true);
        } else {
            showImage.set(true);
        }
    }

    public void onCloseClick() {
        showImage.set(false);
    }

    public void onToggleClick(Data data) {
        data.setCommentVisible(!data.isCommentVisible());
        notifyList.set(true);
    }
}
