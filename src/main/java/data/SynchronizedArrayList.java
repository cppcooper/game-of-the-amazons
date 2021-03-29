package data;

import java.util.ArrayList;

public class SynchronizedArrayList<E> {
    private final ArrayList<E> list = new ArrayList<>();

    public synchronized void clear(){
        list.clear();
    }

    public synchronized void add(E e){
        list.add(e);
    }

    public synchronized E get(int i){
        return list.get(i);
    }

    public synchronized int size(){
        return list.size();
    }

    public synchronized boolean contains(E e){
        return list.contains(e);
    }
}
