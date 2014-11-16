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

    public boolean contains(SarsaLambdaQueueElement elem){
        return container.contains(elem);
    }

    public boolean contains(String observation, int action){
        return contains(new SarsaLambdaQueueElement(observation, action, 0));
    }

    public E add(E element){
        if (container.contains(element)){
            remove(element);
        }
        container.add(pointer, element);
        pointer     ++;
        actualSize  ++;
        if (pointer >= maximumSize){
            pointer = 0;
            actualSize = maximumSize;
        }
        return element;
    }

    public boolean remove(E elem){
        int deleteIndex = Integer.MAX_VALUE;
        for (int i = 0; i < container.size(); i++) {
            if(container.get(i).equals(elem)){
                deleteIndex = i;
                break;
            }
        }
        if (deleteIndex != Integer.MAX_VALUE) {
            container.remove(deleteIndex);
            int toMove = (actualSize - Math.abs(pointer - 1 - deleteIndex));
            int currentIndexToMove = deleteIndex;
            int nextIndexToMove = deleteIndex - 1;

            for (int i = 0; i < toMove; i++) {
                if (nextIndexToMove < 0){
                    nextIndexToMove = maximumSize - 1;
                }
                if (currentIndexToMove < 0){
                    currentIndexToMove = maximumSize - 1;
                }
                container.set(currentIndexToMove, container.get(nextIndexToMove));
                nextIndexToMove++;
                currentIndexToMove++;
            }
            return true;
        }
        return false;
    }

    public E getElement(String observation, int action){
        SarsaLambdaQueueElement elem = new SarsaLambdaQueueElement(observation, action, 0);
        int index = container.indexOf(elem);
        return container.get(index);
    }

    public E getElementAt(int index){
        int realIndex = pointer - index - 1;
        if (realIndex < 0){
            realIndex += (actualSize - 1);
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
        private int stepCounter = 0;
        private int actualSize;

        private MyIterator(int pointer, int actualSize){
            this.iteratorPointer = pointer-1;
            this.actualSize = actualSize;
            this.startPointer = this.iteratorPointer;
        }

        @Override
        public boolean hasNext() {
            return stepCounter != actualSize;
        }

        @Override
        public E next() {
            stepCounter++;
            iteratorPointer--;
            if (iteratorPointer < 0){

                iteratorPointer = maximumSize - 1;
            }
            return container.get(iteratorPointer);
        }

        @Override
        public void remove() {

        }
    }
}
