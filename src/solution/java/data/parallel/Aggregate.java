package data.parallel;

import interfaces.GetSet;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;

public class Aggregate implements GetSet, Comparable<Aggregate> {
    public final AtomicBoolean is_aggregated = new AtomicBoolean(false);
    public final AtomicDouble contribution = new AtomicDouble();
    public final AtomicDouble sum = new AtomicDouble();
    public final AtomicDouble mean = new AtomicDouble();
    public final AtomicInteger count = new AtomicInteger();
    public void set(double value){
        sum.set(value);
        mean.set(value / count.get());
    }
    public int addTo(double value){
        sum.add(value);
        return count.incrementAndGet();
    }
    public void aggregate(double value, int count, BiFunction<Double,Double,Double> agg_fn){
        is_aggregated.set(true);
        double t1 = agg_fn.apply(sum.get(),value);
        int t2 = this.count.get() + count;
        sum.set(t1);
        this.count.set(t2);
        mean.set(t1 / t2);
    }
    public double get(){
        return sum.get();
    }
    public int count(){
        return count.get();
    }

    @Override
    public int compareTo(Aggregate other) {
        return mean.compareTo(other.mean);
    }
}
