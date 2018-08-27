package com.tunt.recyclerview;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.tunt.recyclerview.listener.OnItemClickListener;
import com.tunt.recyclerview.selector.Selector;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
//public abstract class ClickableViewHolder<Item> extends SimpleViewHolder implements Selector.OnSelectorChangeListener {
//
//    private Item item;
//    private OnItemClickListener<Item> onItemClickListener;
//    private Selector selector;
//
//    public ClickableViewHolder(View itemView, Selector selector) {
//        super(itemView);
//        RxView.clicks(itemView).throttleFirst(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(aVoid -> onClick());
//        this.selector = selector;
//        initSelector(selector);
//    }
//
//    void bind(Item item, int position) {
//        this.item = item;
//        bindData(item, position);
//        if (selector != null) {
//            setSelected(selector.isSelected(getAdapterPosition()));
//        }
//    }
//
//    public abstract void bindData(Item item, int position);
//
//    public Item getData() {
//        return item;
//    }
//
//    void setOnItemClickListener(OnItemClickListener<Item> onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    private void onClick() {
//        int position = getAdapterPosition();
//        if (selector != null) {
//            selector.toggle(position);
//        }
//        if (onItemClickListener != null) {
//            onItemClickListener.onItemClick(itemView, getData(), position);
//        }
//        itemView.setSelected(selector.isSelected(position));
//    }
//
//    private void setSelected(boolean selected) {
//        itemView.setSelected(selected);
//    }
//
//    private void initSelector(Selector selector) {
//        this.selector = selector;
//        selector.addOnSelectorChangeListener(this);
//    }
//
//    @Override
//    public void onSelectorChanged() {
//        setSelected(selector.isSelected(getAdapterPosition()));
//    }
//}