package com.tunt.recyclerview;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.util.SparseArray;
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
public class BindingViewHolder<Item> extends SimpleViewHolder implements Selector.OnSelectorChangeListener {

    private Item item;
    private ViewDataBinding binding;

    private Selector selector;

    public BindingViewHolder(View itemView, Selector selector) {
        super(itemView);
        this.selector = selector;
        initSelector(selector);
        binding = DataBindingUtil.bind(itemView);
    }

    void bind(Item item, CollectionPosition position, SparseArray bindingMap) {
        this.item = item;
        if (binding != null) {
            binding.setVariable(BR.item, item);
            binding.setVariable(BR.itemPosition, position);
            int bindingSize = bindingMap.size();
            for (int i = 0; i < bindingSize; i++) {
                binding.setVariable(bindingMap.keyAt(i), bindingMap.valueAt(i));
            }

            binding.executePendingBindings();
        }
        if (selector != null) {
            setSelected(selector.isSelected(getAdapterPosition()));
        }
    }

    public Item getData() {
        return item;
    }

    @Override
    protected void onClick() {
        super.onClick();
        int position = getAdapterPosition();
        if (selector != null) {
            selector.toggle(position);
        }
        itemView.setSelected(selector.isSelected(position));
    }

    private void setSelected(boolean selected) {
        itemView.setSelected(selected);
    }

    private void initSelector(Selector selector) {
        this.selector = selector;
        selector.addOnSelectorChangeListener(this);
    }

    @Override
    public void onSelectorChanged() {
        setSelected(selector.isSelected(getAdapterPosition()));
    }
}