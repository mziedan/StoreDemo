package com.beekoding.storedemo.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beekoding.storedemo.R;

/**
 * Created by moham on 2017-05-15.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {
    public final TextView productDescription;
    public final TextView price;
    public final ImageView image;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        productDescription = (TextView) itemView.findViewById(R.id.grid_item_description);
        price = (TextView) itemView.findViewById(R.id.grid_item_price);
        image = (ImageView) itemView.findViewById(R.id.grid_item_image);
    }

    @Override
    public void onClick(View view) {

    }
}
