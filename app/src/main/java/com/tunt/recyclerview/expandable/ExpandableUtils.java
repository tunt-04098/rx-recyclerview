package com.tunt.recyclerview.expandable;

import java.util.List;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class ExpandableUtils {

    private final static int NUMBER_ITEM_WHEN_COLLAPSING = 1;

    public static int findGroupIndexOfItem(List<Group> groups, int position) {
        int startItem = 0;
        int endItem = 0;
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            int groupItemCount = group.isExpand() ? group.getGroupItemCount() : NUMBER_ITEM_WHEN_COLLAPSING;
            endItem = startItem + groupItemCount;
            if (position >= startItem && position < endItem) {
                return i;
            }
            startItem = endItem;
        }
        return groups.size() - 1;
    }

    public static int findRealPositionOfItem(List<Group> groups, int position) {
        int groupIndex = findGroupIndexOfItem(groups, position);
        int realPosition = 0;
        int currentPosition = 0;
        for (int i = 0; i < groupIndex; i++) {
            Group group = groups.get(i);
            int groupItemCount = group.isExpand() ? group.getGroupItemCount() : NUMBER_ITEM_WHEN_COLLAPSING;
            currentPosition += groupItemCount;
            realPosition += group.getGroupItemCount();
        }
        return realPosition + position - currentPosition;
    }

    public static int findCurrentPositionFromRealPosition(List<Group> groups, int realPosition) {
        int start = 0;
        int current = 0;
        for (int i = 0; i < groups.size(); i++) {
            Group group = groups.get(i);
            int end = start + group.getGroupItemCount();
            if (end > realPosition) {
                if (group.isExpand()) {
                    return current + realPosition - start;
                }
                return -1;
            }
            start = end;
            current += (group.isExpand() ? group.getGroupItemCount() : 1);
        }
        return 0;
    }

    public static int[] indexOfGroup(List<Group> groups, int groupIndex) {
        int[] result = new int[2];
        for (int i = 0; i < groupIndex; i++) {
            Group group = groups.get(i);
            result[0] += group.isExpand() ? group.getGroupItemCount() : 1;
        }
        Group group = groups.get(groupIndex);
        result[1] = (group.isExpand() ? group.getGroupItemCount() : 1) + result[0];
        return result;
    }
}