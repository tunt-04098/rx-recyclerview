package com.tunt.recyclerview;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class SimpleRecyclerAdapter<Item> extends RecyclerAdapter<Item> {

    private @LayoutRes int itemLayout;

    public SimpleRecyclerAdapter(@LayoutRes int itemLayout){
        this.itemLayout = itemLayout;
    }

    public SimpleRecyclerAdapter(@LayoutRes int itemLayout, boolean hasHeader, boolean hasFooter){
        super(hasHeader, hasFooter);
        this.itemLayout = itemLayout;
    }

    public SimpleRecyclerAdapter(@LayoutRes int itemLayout, boolean allowDeselectItem){
        super(false, false, allowDeselectItem);
        this.itemLayout = itemLayout;
    }

    @Override
    public View getBaseItemView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return inflater.inflate(itemLayout, parent, false);
    }
}