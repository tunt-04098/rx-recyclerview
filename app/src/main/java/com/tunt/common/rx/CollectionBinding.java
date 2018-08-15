package com.tunt.common.rx;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by TuNT on 8/15/18.
 * tunt.program.04098@gmail.com
 */
public class CollectionBinding<Item> {

    private final ItemAdapter<Item> adapter;

    private final ConnectableObservable<List<Item>> items;

    private Disposable sourceSubscription = Disposables.empty();

    CollectionBinding(Observable<List<Item>> source, ItemAdapter<Item> adapter) {
        this(source, adapter, AndroidSchedulers.mainThread());
    }

    CollectionBinding(Observable<List<Item>> source, ItemAdapter<Item> adapter, Scheduler scheduler) {
        this.items = source.observeOn(scheduler).unsubscribeOn(Schedulers.newThread()).replay();
        this.adapter = adapter;
    }

    public static <Item> Builder from(Observable<List<Item>> source) {
        return from(source, Functions.identity());
    }

    public static <S, Item, T extends Iterable<Item>> Builder<S, Item> from(Observable<S> source, Function<S, T> transformer) {
        return new Builder(source, transformer);
    }

    public ItemAdapter<Item> adapter() {
        return this.adapter;
    }

    public Disposable connect() {
        this.sourceSubscription = this.items.connect();
        return this.sourceSubscription;
    }

    public void disconnect() {
        this.sourceSubscription.dispose();
    }

    public Observable<List<Item>> items() {
        return this.items;
    }

    public static class Builder<S, Item> {
        private ItemAdapter<Item> adapter;
        private Pager.PagingFunction<S> pagingFunction;
        private final Observable<S> source;
        private final Function<S, List<Item>> transformer;

        public Builder(final Observable<S> source, final Function<S, List<Item>> transformer) {
            this.source = source;
            this.transformer = transformer;
        }

        public CollectionBinding<Item> build() {
            if (adapter == null) {
                throw new IllegalArgumentException("Adapter can't be null");
            }
            if (pagingFunction == null) {
                return new CollectionBinding(source.map(transformer), adapter);
            }
            if (!(this.adapter instanceof PagingAwareAdapter)) {
                throw new IllegalArgumentException("Adapter must implement " + PagingAwareAdapter.class + " when used in a paged binding");
            }
            final Pager<S, List<Item>> pager = Pager.create(pagingFunction, transformer);
            return (CollectionBinding<Item>) new PagedCollectionBinding(pager.page(source), (PagingAwareAdapter) adapter, pager);
        }

        public Builder<S, Item> withAdapter(final ItemAdapter<Item> adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder<S, Item> withPager(final Pager.PagingFunction<S> pagingFunction) {
            this.pagingFunction = pagingFunction;
            return this;
        }
    }
}