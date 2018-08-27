package com.tunt.recyclerview;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

import com.tunt.common.rx.ItemAdapter;
import com.tunt.recyclerview.filter.Filter;
import com.tunt.recyclerview.listener.OnItemChildViewClickListener;
import com.tunt.recyclerview.listener.OnItemClickListener;
import com.tunt.recyclerview.selector.GroupSelector;
import com.tunt.recyclerview.selector.MultiSelector;
import com.tunt.recyclerview.selector.Selector;
import com.tunt.recyclerview.selector.SingleSelector;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public abstract class RecyclerAdapter<Item> extends RecyclerView.Adapter<SimpleViewHolder> implements ItemAdapter<Item>, IDestroy {

    static final int TYPE_HEADER = 0;
    static final int TYPE_FOOTER = 1;
    static final int TYPE_ITEM = 2;
    public static final int TYPE_MAX = TYPE_ITEM;

    /**
     * items list to display
     */
    private List<Item> items;

    /**
     * when filter is enabling -> this list is backup for original list
     */
    private List<Item> originItems;

    /**
     * List selector of origin Item
     */
    private List<Integer> originSelectedIndex;

    /**
     * Filter function
     */

    private SparseArray<Filter<Item>> filters = new SparseArray<>();

    /**
     * enable Header view
     */
    private boolean hasHeader;

    /**
     * enable Footer view
     */
    private boolean hasFooter;

    /**
     * allow/disallow deselect item in list
     */
    private boolean allowDeselectItem;

    /**
     * Item click listener
     */
    private OnItemClickListener<Item> onItemClickListener;

    /**
     * Array of view resId and corresponding OnClickListener
     */
    private SparseArray<OnItemChildViewClickListener> mChildViewClickListeners;

    /**
     * The enum that defines two types of selector.
     */
    public enum SelectorType {
        SINGLE, MULTI
    }

    private Selector selector;

    private SparseArray<Object> mBindingData = new SparseArray<>();

    public RecyclerAdapter(boolean hasHeader, boolean hasFooter) {
        this(hasHeader, hasFooter, false);
    }

    public RecyclerAdapter(boolean hasHeader, boolean hasFooter, boolean allowDeselectItem) {
        this.items = new ArrayList<>();
        this.hasHeader = hasHeader;
        this.hasFooter = hasFooter;
        this.allowDeselectItem = allowDeselectItem;
        selector = createSelector();
        mChildViewClickListeners = new SparseArray<>();
    }

    public RecyclerAdapter() {
        this(false, false);
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public final boolean isHeaderView(int position) {
        return hasHeader && position == 0;
    }

    public final boolean isFooterView(int position) {
        int count = getItemCount();
        return hasFooter && position == count - 1;
    }

    @Override
    public final SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem;
        if (viewType == TYPE_HEADER) {
            viewItem = getHeaderView(LayoutInflater.from(parent.getContext()), parent);
        } else if (viewType == TYPE_FOOTER) {
            viewItem = getFooterView(LayoutInflater.from(parent.getContext()), parent);
        } else {
            viewItem = getBaseItemView(LayoutInflater.from(parent.getContext()), parent, viewType);
        }

        SimpleViewHolder holder;
        if (enableBinding(viewType)) {
            holder = new BindingViewHolder<>(viewItem, selector);
            ((BindingViewHolder<Item>) holder).setOnItemClickListener(onItemClickListener);

            /**
             * Do setting child views with corresponding listener
             */
            for (int i = 0; i < mChildViewClickListeners.size(); i++) {
                int resId = mChildViewClickListeners.keyAt(i);
                OnItemChildViewClickListener listener = mChildViewClickListeners.valueAt(i);
                View childView = ((BindingViewHolder<Item>) holder).itemView.findViewById(resId);
                if (childView != null) {
                    RxView.clicks(childView)
                            .throttleFirst(RecyclerViewConstants.DEFAULT_THROTTLE_FIRST_CLICK_TIME, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                            .subscribe(aVoid -> {
                                listener.onItemChildViewClicked(childView, ((BindingViewHolder<Item>) holder).getData(), holder.getAdapterPosition());
                            });
                }
            }
        } else {
            holder = new SimpleViewHolder(viewItem);
        }
        holder.setOnItemClickListener(onItemClickListener);

        onAfterCreateViewHolder(holder, viewType);

        return holder;
    }

    @Override
    public final void onBindViewHolder(SimpleViewHolder holder, int position) {
        if (isHeaderView(position)) return;
        if (isFooterView(position)) return;
        int offsetPosition = hasHeader ? 1 : 0;
        if (holder instanceof BindingViewHolder) {
            int positionInCollection = position - offsetPosition;
            ((BindingViewHolder<Item>) holder).bind(getItem(positionInCollection),
                    new CollectionPosition(getCollectionItemCount(), position), mBindingData);
        }

        onAfterBindViewHolder(holder, position);
    }

    public void onAfterCreateViewHolder(SimpleViewHolder holder, int viewType) {
        // Do something like add listener to view...
    }

    public void onAfterBindViewHolder(SimpleViewHolder holder, int position) {

    }

    @Override
    public final int getItemViewType(int position) {
        if (isHeaderView(position)) return TYPE_HEADER;
        if (isFooterView(position)) return TYPE_FOOTER;
        return getBaseItemViewType(position - (hasHeader ? 1 : 0));
    }

    /**
     * Get type of list item view bind with {@link Item item}. Always start from {@value TYPE_MAX}
     *
     * @param position: position in {@code List<Item>}
     * @return
     */
    public int getBaseItemViewType(int position) {
        return TYPE_ITEM;
    }

    public boolean enableBinding(int viewType) {
        if (viewType == TYPE_HEADER || viewType == TYPE_FOOTER) return false;
        return true;
    }

    @Override
    public int getCollectionItemCount() {
        return items.size();
    }

    @Override
    public int getItemCount() {
        int count = items.size();
        return count + (hasHeader ? 1 : 0) + (hasFooter ? 1 : 0);
    }

    @Override
    public void addItem(Item item) {
        if (!items.contains(item)) {
            items.add(item);
        }
    }

    @Override
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public Item getItem(int position) {
        return items.get(position);
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public void setItems(List<Item> items) {
        recalculateSelector(getSelected(), this.items, items);
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean isFiltering() {
        return filters.size() > 0;
    }

    @Override
    public void prependItem(Item item) {
        items.add(0, item);
    }

    @Override
    public void removeItem(int position) {
        items.remove(position);
    }

    public abstract View getBaseItemView(LayoutInflater inflater, ViewGroup parent, int viewType);

    protected Selector createSelector() {
        return new SingleSelector(allowDeselectItem);
    }

    public Selector getSelector() {
        return selector;
    }

    public View getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    public View getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    public void setOnItemClickListener(OnItemClickListener<Item> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemChildViewClickListener(@IdRes int resId, OnItemChildViewClickListener listener) {
        mChildViewClickListeners.put(resId, listener);
    }

    @Override
    public void destroy() {
        setOnItemClickListener(null);
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onComplete() {
        Log.i(RecyclerViewConstants.TAG, "onCompleted on RecyclerAdapter from source()");
    }

    @Override
    public void onError(Throwable e) {
        notifyDataSetChanged();
        e.printStackTrace();
    }

    @Override
    public void onNext(List<Item> items) {
        Log.i(RecyclerViewConstants.TAG, "onNext on RecyclerAdapter from source(): " + items);
        if (this.items.isEmpty()) {
            if (items != null) {
                if (filters.size() > 0) {
                    originItems = new ArrayList<>(items);
                    applyFilter();
                } else {
                    setItems(items);
                }
            }
            return;
        }
        if (items == null) return;

        // paging here

        // if has filter
        if (filters.size() > 0) {
            originItems.addAll(items);
            applyFilter();
        } else {
            this.items.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void addBinding(int variableId, Object value) {
        mBindingData.put(variableId, value);
    }

    public void removeBinding(int variableId) {
        mBindingData.remove(variableId);
    }

    public void applyFilter() {
        if (filters != null && filters.size() > 0) {
            // if original Items is null, backup here
            if (originItems == null || originItems.size() == 0) {
                originItems = new ArrayList<>(items);
                originSelectedIndex = getSelected();
            }

            List<Item> filterItems = originItems;
            for (int i = 0; i < filters.size(); i++) {
                int key = filters.keyAt(i);
                Filter<Item> filter = filters.get(key);
                try {
                    filterItems = filter.apply(filterItems);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            setItems(filterItems);
            notifyDataSetChanged();
        } else {
            clearFilter();
        }
    }

    public boolean hasFooter() {
        return hasFooter;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public boolean isAllowDeselectItem() {
        return allowDeselectItem;
    }

    public void setAllowDeselectItem(boolean allowDeselectItem) {
        this.allowDeselectItem = allowDeselectItem;
    }

    public void setSelected(int position) {
        setSelected(position, true);
    }

    public void setSelected(int position, boolean selected) {
        if (selector != null) {
            selector.setSelected(position, selected);
        }
    }

    public void setSelectorType(SelectorType type) {
        switch (type) {
            case SINGLE:
                selector = new SingleSelector();
                break;
            case MULTI:
                selector = new MultiSelector();
                break;
        }
    }

    public void addFilter(Filter<Item> filter) {
        if (filters == null) {
            filters = new SparseArray<>();
        }
        addFilter(filters.size(), filter);
    }

    public void addFilter(int filterIndex, Filter<Item> filter) {
        if (filters == null) {
            filters = new SparseArray<>();
        }
        filters.put(filterIndex, filter);

        applyFilter();
    }

    public void clearFilter() {
        this.filters.clear();
        if (originItems != null) {
            List<Integer> selected = getSelected();
            selector.clearSelected();

            if (selected.isEmpty() || selector instanceof MultiSelector) {
                if (!originSelectedIndex.isEmpty()) {
                    for (int selectedIndex : originSelectedIndex) {
                        selector.setSelected(selectedIndex, true);
                    }
                }
            } else {
                recalculateSelector(selected, items, originItems);
            }
            this.items = new ArrayList<>(this.originItems);
            this.originItems = null;
        }
        notifyDataSetChanged();
    }

    public void removeFilter(int filterIndex) {
        if (filters != null) {
            filters.remove(filterIndex);
        }

        applyFilter();
    }

    public List<Integer> getSelected() {
        return selector.getSelected();
    }

    public List<Item> getSelectedObject() {
        List<Item> selectedObject = new ArrayList<>();
        List<Integer> selected = getSelected();
        if (selected == null || selected.isEmpty()) return selectedObject;

        for (int index : selected) {
            if (selector instanceof GroupSelector) {
                selectedObject.add(this.originItems.get(index));
            } else {
                selectedObject.add(this.items.get(index));
            }
        }
        return selectedObject;
    }

    private void recalculateSelector(List<Integer> selected, List<Item> oldList, List<Item> newList) {

        if (selector instanceof GroupSelector && isFiltering()) {
//            ((GroupSelector) selector).recalculateSelected();
            return;
        }

        boolean valid = true;
        for (int index : selected) {
            if (oldList.size() <= index) {
                valid = false;
                break;
            }
        }
        if (!valid) return;

        if (selector instanceof MultiSelector && isFiltering()) {
            // combine with original to the final result
            selector.clearSelected();
            // 1st update to original selected items
            for (int i = 0; i < oldList.size(); i++) {
                Item item = oldList.get(i);
                int originIndex = originItems.indexOf(item);
                if (originIndex < 0) continue;
                Integer originIntegerValue = Integer.valueOf(originIndex);
                boolean itemSelected = selected.contains(Integer.valueOf(i));
                if (itemSelected) {
                    if (!originSelectedIndex.contains(originIntegerValue)) {
                        originSelectedIndex.add(originIntegerValue);
                    }
                } else {
                    originSelectedIndex.remove(originIntegerValue);
                }
            }
            for (int i = 0; i < originSelectedIndex.size(); i++) {
                Item item = originItems.get(originSelectedIndex.get(i));
                int newIndex = newList.indexOf(item);
                if (newIndex < 0) continue;
                selector.setSelected(newIndex, true);
            }
            return;
        }

        if (selected.isEmpty()) return;
        if (oldList.isEmpty()) return;

        // 2. recalculate
        selector.clearSelected();
        for (int index : selected) {
            Item item = oldList.get(index);
            int newIndex = newList.indexOf(item);
            if (newIndex >= 0) {
                selector.setSelected(newIndex, true);
            }
        }
    }
}