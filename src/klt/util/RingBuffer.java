package klt.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Tobi on 10.11.2014.
 */
public class RingBuffer<E> implements Iterable<E> {
    private final int maximumSize;
    private LinkedList<E> container;


    public RingBuffer(int maximumSize) {
        this.maximumSize = maximumSize;
        container = new LinkedList<>();
    }

    @Override
    public Iterator<E> iterator() {
        return container.descendingIterator();
    }

    public boolean add(E elem){
        if (container.contains(elem)){
            container.remove(elem);
        }
        if(container.size()+1 >= maximumSize){
            container.poll();
        }
        return container.add(elem);
    }


    public void clear() {
        container = new LinkedList<>();
    }

    public boolean contains(String observation, Integer action) {
        return container.contains(new SarsaLambdaQueueElement(observation, action, 0));
    }


    public E getElement(String observation, Integer action) {
        return container.get(container.indexOf(new SarsaLambdaQueueElement(observation, action, 0)));
    }
}
