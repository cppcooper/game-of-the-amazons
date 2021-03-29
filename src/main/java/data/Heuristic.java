package data;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Heuristic {
    public final AtomicDouble value = new AtomicDouble();
    public final AtomicDouble maximum_sub = new AtomicDouble();
    public final AtomicDouble minimum_sub = new AtomicDouble();

    public final AtomicInteger winner = new AtomicInteger();
    public final AtomicDouble mobility = new AtomicDouble();
    public final AtomicDouble territory = new AtomicDouble();

    public final AtomicBoolean has_propagated = new AtomicBoolean(false);
    public final AtomicBoolean has_winner = new AtomicBoolean(false);
    public final AtomicBoolean has_mobility = new AtomicBoolean(false);
    public final AtomicBoolean has_territory = new AtomicBoolean(false);

}
