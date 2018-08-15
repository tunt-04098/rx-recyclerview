package com.tunt.recyclerview;

/**
 * Created by TuNT on 8/15/2018.
 * tunt.program.04098@gmail.com
 */
public class CollectionPosition {

    private int size;
    private int position;

    public CollectionPosition(int size, int position) {
        this.size = size;
        this.position = position;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isFirst() {
        return position == 0;
    }

    public boolean isLast() {
        return position == size - 1;
    }

    public boolean isMiddle(){
        return !isFirst() && !isLast();
    }
}