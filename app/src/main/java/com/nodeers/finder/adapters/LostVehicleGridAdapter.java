package com.nodeers.finder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.nodeers.finder.R;
import com.nodeers.finder.datamodels.VehicleDataModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LostVehicleGridAdapter extends ArrayAdapter<VehicleDataModel> {
    public LostVehicleGridAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // below line is use to inflate the
        // layout for our item of list view.
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_items, parent, false);
        }

        // after inflating an item of listview item
        // we are getting data from array list inside
        // our modal class.
        VehicleDataModel dataModal = getItem(position);

        // initializing our UI components of list view item.
        TextView nameTV = listitemView.findViewById(R.id.tv_name);
        ImageView person_img = listitemView.findViewById(R.id.person_img);

        // after initializing our items we are
        // setting data to our view.
        // below line is use to set data to our text view.
        nameTV.setText(dataModal.getName());

        // in below line we are using Picasso to load image
        // from URL in our Image VIew.
        Picasso.get().load(dataModal.getImgUrl()).into(person_img);

        // below line is use to add item
        // click listener for our item of list view.
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on the item click on our list view.
                // we are displaying a toast message.
                Toast.makeText(getContext(), "Item clicked is : " + dataModal.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        return listitemView;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    public LostVehicleGridAdapter(@NonNull Context context, ArrayList<VehicleDataModel> dataModalArrayList) {
        super(context, 0,dataModalArrayList);
    }
}
