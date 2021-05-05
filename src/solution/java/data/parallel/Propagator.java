package data.parallel;

import interfaces.GetSet;

import java.util.concurrent.atomic.AtomicBoolean;

public class Propagator<E extends GetSet & Comparable<E>> implements Comparable<Propagator> {
    public final E value;
    private final AtomicBoolean needs_to_propagate = new AtomicBoolean(false);
    public Propagator(E value){
        this.value = value;
    }
    public boolean needsPropagation(){
        return needs_to_propagate.get();
    }
    public void flag(){
        needs_to_propagate.set(true);
    }
    public void unflag(){
        needs_to_propagate.set(false);
    }
    public double get(){
        return value.get();
    }
    public void set(double new_value){
        value.set(new_value);
    }

    @Override
    public int compareTo(Propagator other) {
        if(value.getClass() == other.value.getClass()) {
            return value.compareTo((E)other.value);
        }
        return 0;
    }
}
