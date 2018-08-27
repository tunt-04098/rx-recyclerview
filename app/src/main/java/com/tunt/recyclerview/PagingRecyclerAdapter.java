package com.tunt.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tunt.common.rx.PagingAwareAdapter;

import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public abstract class PagingRecyclerAdapter<Item> extends RecyclerAdapter<Item> implements PagingAwareAdapter<Item> {

    private static final int TYPE_LOADING = 2;
    private static final int TYPE_ERROR = 3;
    public static final int TYPE_ITEM = 4;
    public static final int TYPE_MAX = TYPE_ITEM;

    private PagingAwareAdapter.PagingState pagingState = PagingState.LOADING;
    private View.OnClickListener onRetryClickListener;
    private OnNextPageListener onNextPageListener;

    public PagingRecyclerAdapter(boolean hasHeader, boolean hasFooter) {
        super(hasHeader, hasFooter);
    }

    public PagingRecyclerAdapter(boolean hasHeader, boolean hasFooter, boolean allowDeselectItem) {
        super(hasHeader, hasFooter, allowDeselectItem);
    }

    public PagingRecyclerAdapter() {
        super();
    }

    @Override
    public void setPagingState(PagingState state) {
        this.pagingState = state;
        notifyDataSetChanged();
    }

    public PagingState getPagingState() {
        return pagingState;
    }

    @Override
    public boolean isIdle() {
        return pagingState == PagingState.IDLE;
    }

    @Override
    public void setOnErrorRetryListener(View.OnClickListener onClickListener) {
        this.onRetryClickListener = onClickListener;
    }

    @Override
    public void setOnNexPageListener(OnNextPageListener onNextPageListener) {
        this.onNextPageListener = onNextPageListener;
    }

    @Override
    public boolean enableBinding(int viewType) {
        if (viewType == TYPE_LOADING || viewType == TYPE_ERROR) return false;
        return super.enableBinding(viewType);
    }

    @Override
    public View getBaseItemView(LayoutInflater inflater, ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOADING) {
            return createPagingLoadingView(inflater, parent);
        }
        if (viewType == TYPE_ERROR) {
            return createPagingErrorView(inflater, parent);
        }
        return getPagingItemView(inflater, parent, viewType);
    }

    @Override
    public int getBaseItemViewType(int position) {
        if (position < getCollectionItemCount()) {
            return getPagingItemViewType(position);
        }
        return pagingState == PagingState.LOADING ? TYPE_LOADING : TYPE_ERROR;
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();
        if (pagingState != PagingState.IDLE) {
            count++;
        }
        return count;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if (position == getItemCount() - 1) {
            if (onNextPageListener != null) {
                if (isIdle()) {
                    // trick do not update when recycler view in-layout
                    holder.getItemView().post(() -> onNextPageListener.onNextPage());
                }
            }
        }
    }

    @Override
    public void onAfterBindViewHolder(SimpleViewHolder holder, int position) {
        if (getBaseItemViewType(position) == TYPE_LOADING) {
            onBindPagingLoadingView(holder, position);
            return;
        }
        if (getBaseItemViewType(position) == TYPE_ERROR) {
            onBindPagingErrorView(holder, position);
            return;
        }
    }

    @Override
    public void onError(Throwable e) {
        setPagingState(PagingState.ERROR);
        super.onError(e);
    }

    @Override
    public void onComplete() {
        super.onComplete();
        setPagingState(PagingState.IDLE);
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    protected View createPagingLoadingView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.layout_paging_progress, parent, false);
    }

    protected View createPagingErrorView(LayoutInflater inflater, ViewGroup parent) {
        View errorView = inflater.inflate(R.layout.layout_paging_error, parent, false);
        errorView.findViewById(R.id.btPagingRetry).setOnClickListener(onRetryClickListener);
        return errorView;
    }

    @Override
    public void onNext(List<Item> items) {
        this.pagingState = PagingState.IDLE;
        super.onNext(items);
    }

    public abstract View getPagingItemView(LayoutInflater inflater, ViewGroup parent, int viewType);

    public void onBindPagingLoadingView(SimpleViewHolder holder, int position) {}

    public void onBindPagingErrorView(SimpleViewHolder holder, int position) {}

    public int getPagingItemViewType(int position) {
        return TYPE_ITEM;
    }
}