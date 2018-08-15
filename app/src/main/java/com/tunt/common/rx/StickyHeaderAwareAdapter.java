package com.tunt.common.rx;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public interface StickyHeaderAwareAdapter<Item> extends ItemAdapter<Item>, StickyRecyclerHeadersAdapter {
    StickyRecyclerHeadersDecoration getHeaderItemDecoration();
}