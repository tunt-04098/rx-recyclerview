package com.tunt.common.rx;

import android.view.View;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public interface PagingAwareAdapter<Item> extends ItemAdapter<Item> {

    boolean isIdle();

    void setPagingState(PagingState pagingState);

    void setOnErrorRetryListener(final View.OnClickListener onClickListener);

    void setOnNexPageListener(OnNextPageListener onNexPageListener);

    enum PagingState {
        IDLE,
        LOADING,
        ERROR
    }

    interface OnNextPageListener {
        void onNextPage();
    }
}