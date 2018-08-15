package com.tunt.recyclerview.filter;

import java.util.List;

import io.reactivex.functions.Function;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public interface Filter<Item> extends Function<List<Item>, List<Item>> {
}