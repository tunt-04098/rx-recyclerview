package com.tunt.recyclerview.selector;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class MultiSelector extends Selector {

    @Override
    public void setSelectedInternal(int position, boolean isSelected) {
        mSelected.put(position, isSelected);
    }
}