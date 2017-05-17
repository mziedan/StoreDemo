package com.beekoding.storedemo.Adapter;

/**
 * Created by moham on 2017-05-15.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beekoding.storedemo.Model.ProductItem;
import com.beekoding.storedemo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private final List<ProductItem> itemList;
    private final Context context;

    public RecyclerViewAdapter(Context context,
                               List<ProductItem> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_row, null);
        return new RecyclerViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.productDescription.setText(itemList.get(position).getProductDescription());
        holder.price.setText("$" + Double.toString(itemList.get(position).getPrice()));
        Picasso.with(context).load(itemList.get(position).getImage().getUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return (null != this.itemList ? this.itemList.size() : 0);
    }
}