package com.tunt.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class SimpleViewHolder extends RecyclerView.ViewHolder {
    public SimpleViewHolder(View itemView) {
        super(itemView);
    }

    public <V extends View> V getItemView() {
        V itemView = (V) this.itemView;
        if (itemView == null)
            throw new IllegalArgumentException("Cannot cast itemView to your wanted View");
        return itemView;
    }
}