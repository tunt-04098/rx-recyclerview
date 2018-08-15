package com.tunt.recyclerview.selector;

import android.util.SparseArray;

import com.tunt.recyclerview.expandable.ExpandableUtils;
import com.tunt.recyclerview.expandable.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class GroupSelector extends Selector {
    private List<Group> groups;
    private SparseArray<Selector> groupSelected = new SparseArray<>();

    public GroupSelector(List<Group> groups) {
        setGroups(groups);
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
        // initialize selector for each group
        if (groups == null ? true : groups.isEmpty()) return;
        for (int i = groups.size() - 1; i >= 0; i--) {
            Group group = groups.get(i);
            Selector selector;
            if (group.isMultiSelector()) {
                selector = new MultiSelector();
            } else {
                selector = new SingleSelector();
            }
            groupSelected.put(i, selector);
        }
    }

    public void setItemSelected(int groupIndex, int itemRealPosition, boolean selected) {
        groupSelected.get(groupIndex).setSelected(itemRealPosition, selected);
    }

    public void setItemInGroupSelected(int groupIndex, int itemInGroupPosition, boolean selected) {
        int realPosition = itemInGroupPosition;
        for (int i = 0; i < groupIndex; i++) {
            realPosition += groups.get(i).getGroupItemCount();
        }
        setItemSelected(groupIndex, realPosition, selected);
        notifySelectorChange();
    }

    public void clearGroupSelected(int groupIndex) {
        groupSelected.get(groupIndex).clearSelected();
        notifySelectorChange();
    }

    public void setGroupSelected(int... groupSelected) {
        if (groups == null) {
            throw new IllegalStateException("Must setup groups first");
        }
        for (int i = 0; i < groupSelected.length; i++) {
            setItemSelected(i, groupSelected[i], true);
        }
        notifySelectorChange();
    }

    public void setGroupSelected(List<Integer>... groupSelected) {
        for (int i = 0; i < groupSelected.length; i++) {
            List<Integer> selected = groupSelected[i];
            if (selected == null ? true : selected.isEmpty()) continue;
            for (Integer index : selected) {
                setItemSelected(i, index, true);
            }
        }
        notifySelectorChange();
    }

    @Override
    protected void setSelectedInternal(int position, boolean isSelected) {
        int groupIndex = findGroupIndexOfItem(position);
        int realPosition = findRealPositionOfItem(position);
        setItemSelected(groupIndex, realPosition, isSelected);
    }

    /**
     * Override it to ensure selected item is definitely original index
     *
     * @return
     */
    @Override
    public List<Integer> getSelected() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < groupSelected.size(); i++) {
            result.addAll(groupSelected.valueAt(i).getSelected());
        }
        return result;
    }

    @Override
    public boolean isSelected(int position) {
        int groupIndex = findGroupIndexOfItem(position);
        int realPosition = findRealPositionOfItem(position);
        return groupSelected.get(groupIndex).isSelected(realPosition);
    }

    private int findGroupIndexOfItem(int position) {
        return ExpandableUtils.findGroupIndexOfItem(groups, position);
    }

    private int findRealPositionOfItem(int position) {
        return ExpandableUtils.findRealPositionOfItem(groups, position);
    }

    public List<Integer> getGroupSelected(int groupIndex){
        return groupSelected.get(groupIndex).getSelected();
    }
}