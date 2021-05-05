package data.parallel;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class GameTreeHeuristic extends NodeBase implements Comparable<GameTreeHeuristic> {
    protected AtomicBoolean is_winning = new AtomicBoolean(false);
    protected AtomicBoolean has_propagated = new AtomicBoolean(false);
    protected AtomicBoolean is_fully_evaluated = new AtomicBoolean(false);
    public final Propagator<Aggregate> winning_branches = new Propagator<>(new Aggregate()); // needed when a terminal state is found
    public final Propagator<Aggregate> aggregated = new Propagator<>(new Aggregate()); // needed when evaluation is performed
    public final Propagator<Evaluation> maximum_sub = new Propagator<>(new Evaluation(Double.NEGATIVE_INFINITY)); // needed when a child is evaluated
    public final Propagator<Evaluation> minimum_sub = new Propagator<>(new Evaluation(Double.POSITIVE_INFINITY));

    public final Evaluation combined = new Evaluation();
    public final Evaluation amazongs = new Evaluation();
    public final Evaluation territory = new Evaluation();
    public final Evaluation mobility = new Evaluation();
    public final Evaluation freedom = new Evaluation();
    public final Evaluation reduction = new Evaluation();

    protected GameTreeHeuristic(NodeBase parent) {
        super(parent);
    }

    @Override
    public int compareTo(GameTreeHeuristic other) {
        if(is_winning.get() && !other.is_winning.get()){
            return 10000;
        }
        if(!is_winning.get() && other.is_winning.get()){
            return -10000;
        }
        //todo: aggregate comparisons, do them all. Side todo: have evaluation comparisons check for if the evaluation took place
        if(isReady() && other.isReady()){
            double h1 = combined.get() - maximum_sub.get();
            double h2 = other.combined.get() - other.maximum_sub.get();
            return Double.compare(h1, h2);
        }
        if((winning_branches.value.count() | other.winning_branches.value.count()) > 1000){
            return winning_branches.compareTo(other.winning_branches);
        }
        if(combined.isEvaluated() && other.combined.isEvaluated()){
            return combined.compareTo(other.combined);
        }
        if(amazongs.isEvaluated() && other.amazongs.isEvaluated()){
            return amazongs.compareTo(other.amazongs);
        }
        if(territory.isEvaluated() && other.territory.isEvaluated()){
            return territory.compareTo(other.territory);
        }
        if(reduction.isEvaluated() && other.reduction.isEvaluated()){
            return reduction.compareTo(other.reduction);
        }
        if(mobility.isEvaluated() && other.mobility.isEvaluated()){
            return mobility.compareTo(other.mobility);
        }
        if(freedom.isEvaluated() && other.freedom.isEvaluated()){
            return freedom.compareTo(other.freedom);
        }
        return 0;
    }

    public abstract boolean isReady();
    public abstract void propagate();
    public abstract void propagateTo(GameTreeHeuristic other);
    public abstract void evaluate();
    public abstract void evaluateAmazongs();
    public abstract void evaluateTerritory();
    public abstract void evaluateMobility();
    public abstract void evaluateFreedom();
    public abstract void evaluateReduction();
    public abstract void identifyTerminalState();

//    @Override
//    public String toString() {
//        return String.format("value: %.4f\naggregate avg: %.4f\n" +
//                "aggregate count: %d\naggregate: %.4f\n" +
//                "amazongs: %.4f\nwinning: %.4f\n" +
//                "mobility: %.4f\nterritory: %.4f\n",
//                value.get(), aggregate_avg.get(), aggregate_count.get(), aggregate.get(), amazongs.get(), winner.get(), mobility.get(), territory.get());
//    }
}
