package com.tunt.recyclerview.selector;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class SingleSelector extends Selector {

    private boolean isAllowDeselectItem;

    public SingleSelector() {
        this(false);
    }

    public SingleSelector(boolean isAllowDeselectItem) {
        this.isAllowDeselectItem = isAllowDeselectItem;
    }

    @Override
    public void setSelectedInternal(int position, boolean isSelected) {
        if (isSelected) {
            mSelected.clear();
            mSelected.put(position, isSelected);
        } else {
            if (isAllowDeselectItem) {
                mSelected.put(position, isSelected);
            }
        }
    }
}