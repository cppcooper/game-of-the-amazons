package data;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicDouble {
    private AtomicLong value = new AtomicLong(Double.doubleToLongBits(0.0));
    private volatile double cached = 0.0;
    public double get(){
        return Double.longBitsToDouble(value.get());
    }
    public void set(double new_value){
        cached = new_value;
        value.set(Double.doubleToLongBits(new_value));
    }
    public void add(double value){
        set(cached + value);
    }
}
