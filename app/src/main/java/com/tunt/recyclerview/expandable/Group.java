package com.tunt.recyclerview.expandable;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class Group {
    private boolean isExpand;
    private int groupItemCount;
    private boolean multiSelector;
    private boolean allowUnCheck;

    public Group(boolean isExpand, int groupItemCount, boolean multiSelector, boolean allowUnCheck) {
        this.isExpand = isExpand;
        this.groupItemCount = groupItemCount;
        this.multiSelector = multiSelector;
        this.allowUnCheck = allowUnCheck;
    }

    public Group(boolean isExpand, int groupItemCount) {
        this(isExpand, groupItemCount, false, false);
    }

    public boolean isExpand() {
        return isExpand;
    }

    public int getGroupItemCount() {
        return groupItemCount;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public void setGroupItemCount(int groupItemCount) {
        this.groupItemCount = groupItemCount;
    }

    public boolean isMultiSelector() {
        return multiSelector;
    }

    public void setMultiSelector(boolean multiSelector) {
        this.multiSelector = multiSelector;
    }
}