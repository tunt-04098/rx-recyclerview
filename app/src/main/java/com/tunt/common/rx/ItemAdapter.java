package com.tunt.common.rx;

import java.util.List;

import io.reactivex.Observer;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public interface ItemAdapter<Item> extends Observer<List<Item>> {

    void addItem(Item item);

    void clear();

    Item getItem(int position);

    int getCollectionItemCount();

    List<Item> getItems();

    void setItems(List<Item> items);

    boolean isEmpty();

    void notifyDataSetChanged();

    void prependItem(Item item);

    void removeItem(int position);
}