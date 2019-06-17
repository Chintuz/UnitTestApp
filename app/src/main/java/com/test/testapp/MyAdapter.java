package com.test.testapp;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.test.testapp.databinding.CommentLayoutBinding;
import com.test.testapp.databinding.ImageLayoutBinding;
import com.test.testapp.databinding.RadioGroupLayoutBinding;
import com.test.testapp.model.Data;
import com.test.testapp.model.RadioModel;
import com.test.testapp.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private List<Data> list;
    public Context context;
    public MainViewModel viewModel;

    private HashMap<Integer, List<RadioModel>> modelListMap = new HashMap<>();


    public MyAdapter(List<Data> list, Context context, MainViewModel viewModel) {
        this.list = list;
        this.context = context;
        this.viewModel = viewModel;
        inflater = LayoutInflater.from(context);

        int a = 0;
        for (Data data : list) {
            List<RadioModel> modelList = new ArrayList<>();
            if (data.getDataMap() != null && data.getDataMap().getOptions() != null) {
                for (String s : data.getDataMap().getOptions()) {
                    RadioModel model = new RadioModel();
                    model.setType(s);
                    model.setChecked(false);
                    modelList.add(model);
                }
            }
            modelListMap.put(a, modelList);
            a++;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {
            case 0:
                ImageLayoutBinding imageBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.image_layout, viewGroup, false);
                return new ImageViewHolder(imageBinding);
            case 1:
                RadioGroupLayoutBinding radioBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.radio_group_layout, viewGroup, false);
                return new RadioViewHolder(radioBinding);
            case 2:
                CommentLayoutBinding commentBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()),
                        R.layout.comment_layout, viewGroup, false);
                return new CommentViewHolder(commentBinding);
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {

        switch (list.get(position).getType()) {
            case "PHOTO":
                return 0;
            case "SINGLE_CHOICE":
                return 1;
            case "COMMENT":
                return 2;
            default:
                return -1;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder myViewHolder, int position) {
        Data data = list.get(position);

        int id = (position + 1) * 100;

        switch (getItemViewType(position)) {
            case 0:
                ((ImageViewHolder) myViewHolder).binding.setModel(data);
                ((ImageViewHolder) myViewHolder).binding.setHandler(viewModel);
                break;
            case 1:
                ((RadioViewHolder) myViewHolder).binding.setModel(data);
                ((RadioViewHolder) myViewHolder).binding.feedbackGrp.removeAllViews();

                if (modelListMap.get(position) != null && ((RadioViewHolder) myViewHolder).binding.feedbackGrp.getChildCount() == 0) {

                    List<RadioModel> modelList = modelListMap.get(position);

                    RadioGroup group = new RadioGroup(context);
                    group.setOrientation(RadioGroup.VERTICAL);
                    for (int a = 0; a < modelList.size(); a++) {
                        RadioModel radioModel = modelList.get(a);

                        RadioButton btn = new RadioButton(context);
                        btn.setText(radioModel.getType());
                        Logger.v("radio button type - " + radioModel.getType());
                        btn.setTag(a);
                        btn.setId(id++);

                        btn.setOnCheckedChangeListener(null);

                        btn.setOnCheckedChangeListener((buttonView, isChecked) -> {

                            if (isChecked) {
                                for (RadioModel model : modelList) {
                                    model.setChecked(false);
                                }
                                int pos = (Integer) buttonView.getTag();
                                modelList.get(pos).setChecked(true);
                            }
                        });

                        btn.setChecked(radioModel.isChecked());
                        ((RadioViewHolder) myViewHolder).binding.feedbackGrp.addView(btn);
                    }
                }

                break;
            case 2:
                ((CommentViewHolder) myViewHolder).binding.setModel(data);
                ((CommentViewHolder) myViewHolder).binding.setHandler(viewModel);

                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageLayoutBinding binding;

        public ImageViewHolder(@NonNull ImageLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private CommentLayoutBinding binding;

        public CommentViewHolder(@NonNull CommentLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public class RadioViewHolder extends RecyclerView.ViewHolder {
        private RadioGroupLayoutBinding binding;

        public RadioViewHolder(@NonNull RadioGroupLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
