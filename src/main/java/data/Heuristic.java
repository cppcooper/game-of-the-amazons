package data;

import data.parallel.AtomicDouble;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Heuristic {
    public final AtomicDouble aggregate = new AtomicDouble();
    public final AtomicDouble aggregate_avg = new AtomicDouble();
    public final AtomicInteger aggregate_count = new AtomicInteger();

    public final AtomicDouble value = new AtomicDouble();
    public final AtomicDouble maximum_sub = new AtomicDouble();
    public final AtomicDouble minimum_sub = new AtomicDouble();

    public final AtomicDouble winner = new AtomicDouble();
    public final AtomicDouble amazongs = new AtomicDouble();
    public final AtomicDouble mobility = new AtomicDouble();
    public final AtomicDouble territory = new AtomicDouble();

    public final AtomicBoolean is_ready = new AtomicBoolean(false);
    public final AtomicBoolean has_propagated = new AtomicBoolean(false);
    public final AtomicBoolean has_aggregated = new AtomicBoolean(false);
    public final AtomicBoolean has_winner = new AtomicBoolean(false);
    public final AtomicBoolean has_amazongs = new AtomicBoolean(false);
    public final AtomicBoolean has_mobility = new AtomicBoolean(false);
    public final AtomicBoolean has_territory = new AtomicBoolean(false);


    @Override
    public String toString() {
        return String.format("value: %.4f\naggregate: %.4f\n" +
                "amazongs: %.4f\nwinning: %.4f\n" +
                "mobility: %.4f\nterritory: %.4f\n",
                value.get(), aggregate_avg.get(), amazongs.get(), winner.get(), mobility.get(), territory.get());
    }
}
