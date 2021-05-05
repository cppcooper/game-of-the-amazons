package data.parallel;

import algorithms.analysis.Amazongs;
import algorithms.analysis.Mobility;
import algorithms.analysis.Territory;
import data.pod.Move;
import data.structures.GameState;
import org.apache.commons.math3.util.Precision;
import tools.Maths;
import tools.Tuner;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GameTreeNode represents a Move that was made by a player, and the value of the resulting state

 todo: every time winner is propagated we want to reverse the sign
 */

public class GameTreeNode extends GameTreeHeuristic {
    private final SynchronizedArrayList<GameTreeNode> super_nodes = new SynchronizedArrayList<>();
    private final SynchronizedArrayList<GameTreeNode> sub_nodes = new SynchronizedArrayList<>(); //note: that there is no way to remove nodes! this is by design!
    final public AtomicReference<Move> move = new AtomicReference<>();
    final public AtomicReference<GameState> state_after_move = new AtomicReference<>();

    public GameTreeNode(Move move, GameTreeNode parent, GameState state_after_move) {
        super(parent);
        this.move.set(move);
        this.state_after_move.set(state_after_move);
    }

    public GameTreeNode get(int i){
        return (GameTreeNode) super.get(i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameTreeNode that = (GameTreeNode) o;
        if(move.get().equals(that.move.get())){
            return state_after_move.get().equals(that.state_after_move.get());
        }
        return false;
    }

    @Override
    public boolean isReady() {
        return is_fully_evaluated.get() && maximum_sub.value.isEvaluated();
    }

    @Override
    public void propagate() {
        boolean x1 = winning_branches.needsPropagation();
        boolean x2 = aggregated.needsPropagation();
        boolean x3 = maximum_sub.needsPropagation(); // needs when node is a [new] child
        boolean x4 = minimum_sub.needsPropagation();
        if(x1 || x2 || x3 || x4) {
            for (int i = 0; i < super_nodes.size(); ++i) {
                if (!has_propagated.get()) {
                    has_propagated.set(true);
                }
                GameTreeNode parent = super_nodes.get(i);
                if (x1) {
                    winning_branches.unflag();
                    parent.winning_branches.value.aggregate(winning_branches.value.sum.get(), winning_branches.value.count.get(), (a, b) -> {
                        return a - b;
                    });
                    parent.winning_branches.flag();
                }
                if (x2) {
                    aggregated.unflag();
                    parent.aggregated.value.aggregate(aggregated.get(), aggregated.value.count(), (a, b) -> {
                        return a + b;
                    });
                    parent.aggregated.flag();
                }
                var h = combined.get();
                if (x3) {
                    maximum_sub.unflag();
                    if (parent.maximum_sub.get() < h) {
                        parent.maximum_sub.set(h);
                    }
                }
                if (x4) {
                    minimum_sub.unflag();
                    if (parent.minimum_sub.value.get() > h) {
                        parent.minimum_sub.set(h);
                    }
                }
                parent.propagate();
            }
        }
    }

    @Override
    public void propagateTo(GameTreeHeuristic other) {
        if (has_propagated.get()) {
            if (combined.isEvaluated()) {
                var h = combined.get();
                if (other.maximum_sub.get() < h) {
                    other.maximum_sub.set(h);
                }
                if (other.minimum_sub.value.get() > h) {
                    other.minimum_sub.set(h);
                }
            }
            if(winning_branches.value.is_aggregated.get()){
                other.winning_branches.value.aggregate(winning_branches.value.sum.get(), winning_branches.value.count.get(), (a, b) -> {
                    return a - b;
                });
                other.winning_branches.flag();
            }
            if(aggregated.value.is_aggregated.get()){
                other.aggregated.value.aggregate(aggregated.get(), aggregated.value.count(), (a, b) -> {
                    return a + b;
                });
                other.aggregated.flag();
            }
            other.propagate();
        }
    }

    @Override
    public synchronized void evaluate() {
        if(!is_fully_evaluated.get()) {
            evaluateAmazongs();
            evaluateTerritory();
            evaluateMobility();
            evaluateFreedom();
            evaluateReduction();
            combined.set(Maths.combine(amazongs,territory,mobility,freedom,reduction));
            aggregated.value.contribution.set(combined.get()); //todo: add value selector, integrate here
            aggregated.flag();
            maximum_sub.flag();
            minimum_sub.flag();
            is_fully_evaluated.set(true);
        }
    }

    @Override
    public void evaluateAmazongs() {
        if(!amazongs.isEvaluated()){
            amazongs.set(Amazongs.CalculateHeuristic(state_after_move.get()));
        }
    }

    @Override
    public void evaluateTerritory() {
        if(!territory.isEvaluated()){
            territory.set(Territory.CalculateHeuristic(state_after_move.get()));
        }
    }

    @Override
    public void evaluateMobility() {
        if(!mobility.isEvaluated()){
            mobility.set(Mobility.CalculateHeuristic(state_after_move.get()));
        }
    }

    @Override
    public void evaluateFreedom() {
        if(!freedom.isEvaluated()){
            freedom.set(Mobility.CalculateFreedomHeuristic(state_after_move.get()));
        }
    }

    @Override
    public void evaluateReduction() {
        if(!freedom.isEvaluated()){
            reduction.set(Mobility.CalculateFreedomHeuristic(state_after_move.get()));
        }
    }

    @Override
    public void identifyTerminalState() {
        is_winning.set(true);
        winning_branches.flag();
        maximum_sub.set(maximum_sub.get()); // set as evaluated
    }
}
