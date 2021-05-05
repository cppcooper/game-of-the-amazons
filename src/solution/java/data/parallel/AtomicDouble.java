package data.parallel;

import data.interfaces.GetSet;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicDouble implements Comparable<AtomicDouble>, GetSet {
    private AtomicLong value = new AtomicLong(Double.doubleToLongBits(0.0));
    private volatile double cached = 0.0;

    public AtomicDouble(){}
    public AtomicDouble(double d){
        set(d);
    }
    public double get(){
        return Double.longBitsToDouble(value.get());
    }
    public synchronized void set(double new_value){
        cached = new_value;
        value.set(Double.doubleToLongBits(new_value));
    }
    public double add(double value){
        set(cached + value);
        return cached;
    }

    @Override
    public String toString() {
        return "AtomicDouble{" +
                "value=" + value +
                ", cached=" + cached +
                '}';
    }

    @Override
    public int compareTo(AtomicDouble other) {
        return Double.compare(this.cached, other.cached);
    }
}
