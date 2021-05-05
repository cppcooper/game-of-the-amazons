package data.parallel;

import interfaces.GetSet;

import java.util.concurrent.atomic.AtomicBoolean;

public class Evaluation implements GetSet, Comparable<Evaluation> {
    private final AtomicDouble evaluation = new AtomicDouble();
    private final AtomicBoolean is_evaluated = new AtomicBoolean(false);

    public Evaluation(){}
    public Evaluation(double value) {
        evaluation.set(value);
    }

    @Override
    public synchronized void set(double new_value) {
        evaluation.set(new_value);
        is_evaluated.set(true);
    }

    @Override
    public double get() {
        return evaluation.get();
    }

    public double add(double new_value) {
        is_evaluated.set(true);
        return evaluation.add(new_value);
    }

    public boolean isEvaluated() {
        return is_evaluated.get();
    }

    @Override
    public int compareTo(Evaluation other) {
        return evaluation.compareTo(other.evaluation);
    }
}
