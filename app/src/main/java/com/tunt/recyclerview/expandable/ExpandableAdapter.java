package com.tunt.recyclerview.expandable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tunt.recyclerview.SimpleViewHolder;
import com.tunt.recyclerview.StickyHeaderAdapter;
import com.tunt.recyclerview.selector.GroupSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TuNT on 8/20/2018.
 * tunt.program.04098@gmail.com
 */
public abstract class ExpandableAdapter<Item> extends StickyHeaderAdapter<Item> {

    private List<Group> groups;

    private final static int FILTER_EXPANDABLE_INDEX = 0xf117e12;

    private GroupSelector selector;

    public ExpandableAdapter(List<Item>[] sections) {
        super(false, false);
        if (sections == null || sections.length == 0) return;
        boolean[] isMultiSelectors = new boolean[sections.length];
        calculateGroup(sections, isMultiSelectors);
    }

    public ExpandableAdapter(List<Item>[] sections, boolean[] isMultiSelectors) {
        super(false, false);
        calculateGroup(sections, isMultiSelectors);
    }

    public ExpandableAdapter(boolean hasHeader, boolean hasFooter, List<Item>[] sections, boolean[] isMultiSelectors) {
        super(hasHeader, hasFooter);
        calculateGroup(sections, isMultiSelectors);
    }

    private void calculateGroup(List<Item>[] sections, boolean[] isMultiSelectors) {
        if (sections == null || sections.length == 0) return;
        // Calculate groups
        groups = new ArrayList<>();
        for (int i = 0; i < sections.length; i++) {
            Group group = new Group(true, sections[i].size(), isMultiSelectors[i], false);
            groups.add(group);
        }
        selector = new GroupSelector(groups);
        setSelector(selector);
        addFilter(FILTER_EXPANDABLE_INDEX, new ExpandableHeaderFilter(groups));
    }

    protected abstract void bindGroupView(View groupView, int position, Item item, int groupIndex, boolean expanded);

    protected abstract View getGroupView(LayoutInflater inflater, ViewGroup parent);

    public void setGroupSelected(int... groupSelected) {
        if (selector != null) {
            selector.setGroupSelected(groupSelected);
//            selector.recalculateSelected();
        }
    }

    public void setGroupSelected(List<Integer>... groupSelected) {
        if (groupSelected == null) {
            return;
        }
        if (selector != null) {
            List<Integer>[] result = new List[groupSelected.length];
            result[0] = groupSelected[0];

            if (groupSelected.length > 1) {
                for (int i = 1; i < groupSelected.length; i++) {
                    int numberItem = 0;
                    for (int j = 0; j < i; j++) {
                        numberItem += groups.get(j).getGroupItemCount();
                    }
                    List<Integer> indexList = new ArrayList<>();
                    for (int index : groupSelected[i]) {
                        if (index < 0) {
                            continue;
                        }
                        indexList.add(index + numberItem);
                    }
                    result[i] = indexList;
                }
            }
            selector.setGroupSelected(result);
//            selector.recalculateSelected();
        }

    }

    public void setItemSelected(int groupIndex, int itemRealPosition, boolean selected) {
        selector.setItemSelected(groupIndex, itemRealPosition, selected);
    }

    public void setItemInGroupSelected(int groupIndex, int itemInGroupPosition, boolean selected) {
        selector.setItemInGroupSelected(groupIndex, itemInGroupPosition, selected);
    }

    public void clearGroupSelected(int groupIndex) {
        selector.clearGroupSelected(groupIndex);
    }

    @Override
    protected final long getHeaderId(int position, Item item) {
        return findGroupIndexOfItem(position);
    }

    @Override
    protected final View getStickyHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return getGroupView(inflater, parent);
    }

    @Override
    protected final void bindHeaderView(View headerView, int position, Item item) {
        int groupIndex = findGroupIndexOfItem(position);
        boolean expanded = groups.get(groupIndex).isExpand();
        bindGroupView(headerView, position, item, groupIndex, expanded);
    }

    @Override
    public final void onAfterBindViewHolder(SimpleViewHolder holder, int position) {
        super.onAfterBindViewHolder(holder, position);
        int groupIndex = findGroupIndexOfItem(position);
        boolean expanded = groups.get(groupIndex).isExpand();
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        }
        if (expanded) {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            lp.height = 0;
        }
        holder.itemView.setLayoutParams(lp);
    }

    public void onHeaderClick(View header, int position, long headerId) {
        int groupIndex = findGroupIndexOfItem(position);
        Group group = groups.get(groupIndex);
        group.setExpand(!group.isExpand());
        applyFilter();
    }

    private int findGroupIndexOfItem(int position) {
        return ExpandableUtils.findGroupIndexOfItem(groups, position);
    }

    public List<Integer> getGroupSelected(int groupIndex) {
        return selector.getGroupSelected(groupIndex);
    }
}