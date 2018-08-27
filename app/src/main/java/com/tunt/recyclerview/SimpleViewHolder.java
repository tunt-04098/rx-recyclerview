package com.tunt.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.tunt.recyclerview.listener.OnItemClickListener;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class SimpleViewHolder<Item> extends RecyclerView.ViewHolder {

    private Item item;

    private OnItemClickListener<Item> onItemClickListener;

    public SimpleViewHolder(View itemView) {
        super(itemView);
        RxView.clicks(itemView).throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(aVoid -> onClick());
    }

    public <V extends View> V getItemView() {
        V itemView = (V) this.itemView;
        if (itemView == null)
            throw new IllegalArgumentException("Cannot cast itemView to your wanted View");
        return itemView;
    }

    void bind(Item item) {
        this.item = item;
    }

    public Item getData() {
        return item;
    }

    public void setOnItemClickListener(OnItemClickListener<Item> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    protected void onClick() {
        int position = getAdapterPosition();
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(itemView, getData(), position);
        }
    }
}