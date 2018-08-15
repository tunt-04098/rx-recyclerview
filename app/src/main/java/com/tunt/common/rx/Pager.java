package com.tunt.common.rx;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by TuNT on 8/15/18.
 * tunt.program.04098@gmail.com
 * <p>
 * using the pager of soundcloud
 * https://gist.github.com/sockeqwe/f057de9d2cb3384a6db6
 */
public class Pager<I, O> {
    private static final Observable FINISH_SEQUENCE = Observable.never();

    private PublishSubject<Observable<I>> pages;
    private Observable<I> nextPage = finish();
    private Disposable subscription = Disposables.empty();

    private final PagingFunction<I> pagingFunction;
    private final Function<I, O> pageTransformer;

    public static <T> Pager<T, T> create(PagingFunction<T> pagingFunction) {
        return new Pager<>(pagingFunction, Functions.identity());
    }

    public static <I, O> Pager<I, O> create(PagingFunction<I> pagingFunction, Function<I, O> pageTransformer) {
        return new Pager<>(pagingFunction, pageTransformer);
    }

    Pager(PagingFunction<I> pagingFunction, Function<I, O> pageTransformer) {
        this.pagingFunction = pagingFunction;
        this.pageTransformer = pageTransformer;
    }

    /**
     * Used in the paging function to signal the caller that no more pages are available, i.e.
     * to finish paging by completing the paged sequence.
     *
     * @return the finish token
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable<T> finish() {
        return FINISH_SEQUENCE;
    }

    public void reset() {
        nextPage = finish();
    }

    /**
     * Transforms the given sequence to have its subsequent pages pushed into the observer subscribed
     * to the new sequence returned by this method. You can advance to the next page by calling {@link #next()}
     *
     * @param source the source sequence, which would be the first page of the sequence to be paged
     * @return a new sequence based on {@code source}, where subscribers keep receiving pages through subsequent calls
     * to {@link #next()}
     */
    public Observable<O> page(final Observable<I> source) {
        return Observable.create(emitter -> {
            pages = PublishSubject.create();
            subscription = Observable.merge(pages).subscribe(
                    new PageConsumerOnNext(emitter),
                    new PageConsumerOnError(emitter),
                    new Action() {
                        @Override
                        public void run() throws Exception {
                            emitter.onComplete();
                        }
                    });
            pages.onNext(source);
        });
    }

    /**
     * Returns the last page received from the pager. You may use this to
     * retry that observable in case it failed the first time around.
     */
    public Observable<O> currentPage() {
        return page(nextPage);
    }

    /**
     * @return true, if there are more pages to be emitted.
     */
    public boolean hasNext() {
        return nextPage != FINISH_SEQUENCE;
    }

    /**
     * Advances the pager by pushing the next page of items into the current observer, is there is one. If the pager
     * has been unsubscribed from or there are no more pages, this method does nothing.
     */
    public void next() {
        if (!subscription.isDisposed() && hasNext()) {
            pages.onNext(nextPage);
        }
    }

    public interface PagingFunction<T> extends Function<T, Observable<T>> {
    }

    private class PageConsumerOnNext implements Consumer<I> {

        private final Emitter<? super O> inner;

        public PageConsumerOnNext(Emitter<? super O> inner) {
            this.inner = inner;
        }

        @Override
        public void accept(I result) throws Exception {
            nextPage = pagingFunction.apply(result);
            inner.onNext(pageTransformer.apply(result));
            if (nextPage == FINISH_SEQUENCE) {
                pages.onComplete();
            }
        }
    }

    private class PageConsumerOnError implements Consumer<Throwable> {

        private final Emitter<? super O> inner;

        public PageConsumerOnError(Emitter<? super O> inner) {
            this.inner = inner;
        }

        @Override
        public void accept(Throwable error) throws Exception {
            inner.onError(error);
        }
    }
}