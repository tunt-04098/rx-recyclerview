package com.tunt.recyclerview.expandable;

import com.tunt.recyclerview.filter.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TuNT on 8/20/2018.
 * tunt.program.04098@gmail.com
 */
public class ExpandableHeaderFilter<Item> implements Filter<Item> {

    private List<Group> groups;
    private Item emptyItem = null;

    public ExpandableHeaderFilter(List<Group> groups) {
        this.groups = groups;
        emptyItem = generateEmptyItemData();
    }

    private Item generateEmptyItemData() {
        return null;
    }

    @Override
    public List<Item> apply(List<Item> items) {
        List<Item> result = new ArrayList<>();
        if(items == null || items.isEmpty()) return result;
        int groupCount = groups.size();
        int index = 0;
        for (int i = 0; i < groupCount; i++) {
            Group group = groups.get(i);
            if (group.getGroupItemCount() == 0) {
                result.add(emptyItem);
                continue;
            }
            if (group.isExpand()) {
                for (int j = 0; j < group.getGroupItemCount(); j++) {
                    result.add(items.get(index + j));
                }
            } else {
                result.add(items.get(index));
            }
            index += group.getGroupItemCount();
        }
        return result;
    }
}