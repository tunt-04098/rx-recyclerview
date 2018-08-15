package com.tunt.recyclerview.listener;

import android.view.View;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public interface OnItemChildViewClickListener<Item> {
    void onItemChildViewClicked(View view, Item data, int position);
}