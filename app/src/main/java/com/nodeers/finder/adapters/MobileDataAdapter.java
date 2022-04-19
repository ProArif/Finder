package com.nodeers.finder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.MobileDataModel;
import java.util.ArrayList;

public class MobileDataAdapter extends RecyclerView.Adapter<MobileDataAdapter.MobileViewHolder>{
    private ArrayList<MobileDataModel> dataSet;



    public static class MobileViewHolder extends RecyclerView.ViewHolder {

        TextView textViewModelName;
        TextView textViewIMEI;


        public MobileViewHolder(View itemView) {
            super(itemView);
            this.textViewModelName = (TextView) itemView.findViewById(R.id.modelName);
            this.textViewIMEI = (TextView) itemView.findViewById(R.id.mblIMEI);

        }
    }

    public MobileDataAdapter(ArrayList<MobileDataModel> data) {
        this.dataSet = data;
    }

    @Override
    public MobileViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mobile_data_view, parent, false);

        //view.setOnClickListener(MainActivity.myOnClickListener);

        MobileViewHolder myViewHolder = new MobileViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MobileViewHolder holder, final int listPosition) {

        TextView textViewModelName = holder.textViewModelName;
        TextView textViewIMEI = holder.textViewIMEI;

        textViewModelName.setText(dataSet.get(listPosition).getModelName());
        textViewIMEI.setText(dataSet.get(listPosition).getMobileImei());

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}

