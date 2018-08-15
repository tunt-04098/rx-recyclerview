package com.tunt.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;
import com.tunt.common.ViewModelUtils;
import com.tunt.common.rx.StickyHeaderAwareAdapter;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public abstract class StickyHeaderAdapter<Item> extends RecyclerAdapter<Item> implements StickyHeaderAwareAdapter<Item> {

    private StickyRecyclerHeadersDecoration itemDecorator;

    public StickyHeaderAdapter(boolean hasHeader, boolean hasFooter) {
        super(hasHeader, hasFooter);
    }

    public StickyHeaderAdapter() {
        super();
    }

    @Override
    public StickyRecyclerHeadersDecoration getHeaderItemDecoration() {
        if (itemDecorator == null) {
            itemDecorator = new StickyRecyclerHeadersDecoration(this);
            registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    itemDecorator.invalidateHeaders();
                    ViewModelUtils.resetStickyHeaderData(itemDecorator);
                }
            });
        }
        return itemDecorator;
    }

    @Override
    public long getHeaderId(int position) {
        if (hasHeader() && position == 0) return -1;
        if (hasFooter() && position == (getItemCount() - 1)) return -1;
        int offset = hasHeader() ? 1 : 0;
        int realPos = position - offset;
        return getHeaderId(realPos, getItem(realPos));
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new RecyclerView.ViewHolder(getStickyHeaderView(LayoutInflater.from(parent.getContext()), parent)){};
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (hasHeader() && position == 0) return;
        if (hasFooter() && position == (getItemCount() - 1)) return;
        int offset = hasHeader() ? 1 : 0;
        int realPos = position - offset;
        bindHeaderView(holder.itemView, realPos, getItem(realPos));
    }

    protected abstract long getHeaderId(int position, Item item);

    protected abstract View getStickyHeaderView(LayoutInflater inflater, ViewGroup parent);

    protected abstract void bindHeaderView(View headerView, int position, Item item);
}