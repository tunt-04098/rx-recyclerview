package com.tunt.common;

import android.util.SparseArray;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import java.lang.reflect.Field;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class ViewModelUtils {

    /**
     * Trick to reset the header rects in Sticky Header
     */
    public static void resetStickyHeaderData(StickyRecyclerHeadersDecoration itemDecoration) {
        Field privateSparseArrayField = null;
        try {
            privateSparseArrayField = StickyRecyclerHeadersDecoration.class.getDeclaredField("mHeaderRects");
            privateSparseArrayField.setAccessible(true);
            SparseArray rects = (SparseArray) privateSparseArrayField.get(itemDecoration);
            rects.clear();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}