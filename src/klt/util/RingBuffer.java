package klt.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Tobi on 10.11.2014.
 */
public class RingBuffer<E> implements Iterable<E> {
    private int maximumSize;
    private int pointer = 0;
    private List<E> container;
    private int actualSize = 0;

    public RingBuffer(int maximumSize){
        this.maximumSize = maximumSize;
        this.container = new ArrayList<>();

    }

    public E add(E element){
        container.add(pointer, element);
        pointer     ++;
        actualSize  ++;
        if (pointer >= maximumSize){
            pointer = 0;
            actualSize = maximumSize;
        }
        return element;
    }

    public E getElementAt(int index){
        int realIndex = index + pointer;
        if (realIndex >= maximumSize){
            realIndex -= maximumSize;
        }

        return container.get(realIndex);
    }

    public int size(){
        return actualSize;
    }

    public int getMaximumSize(){
        return maximumSize;
    }

    public void clear(){
        this.container = new ArrayList<>();
        this.pointer = 0;
        this.actualSize = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator(pointer, actualSize);
    }

    private class MyIterator implements Iterator<E>{
        private int startPointer;
        private int iteratorPointer;
        private int actualSize;

        private MyIterator(int pointer, int actualSize){
            this.iteratorPointer = pointer;
            this.actualSize = actualSize;
            this.startPointer = this.iteratorPointer;
        }

        @Override
        public boolean hasNext() {
            return iteratorPointer+1 != startPointer;
        }

        @Override
        public E next() {
            iteratorPointer++;
            if (iteratorPointer >= actualSize){
                iteratorPointer -= actualSize;
            }
            return container.get(iteratorPointer);
        }

        @Override
        public void remove() {

        }
    }
}
