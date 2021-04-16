package data.pod;

import algorithms.analysis.Amazongs;
import algorithms.analysis.Mobility;
import algorithms.analysis.Territory;
import algorithms.analysis.Winner;
import data.parallel.AtomicDouble;
import data.structures.GameState;
import data.structures.GameTreeNode;
import tools.Maths;
import tools.Tuner;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Heuristic {
    public final AtomicDouble aggregate = new AtomicDouble();
    public final AtomicDouble aggregate_avg = new AtomicDouble();
    public final AtomicInteger aggregate_count = new AtomicInteger();

    public final AtomicDouble value = new AtomicDouble();
    public final AtomicDouble maximum_sub = new AtomicDouble(Double.NEGATIVE_INFINITY);
    public final AtomicDouble minimum_sub = new AtomicDouble(Double.POSITIVE_INFINITY);

    public final AtomicDouble winner = new AtomicDouble();
    public final AtomicDouble amazongs = new AtomicDouble();
    public final AtomicDouble mobility = new AtomicDouble();
    public final AtomicDouble territory = new AtomicDouble();

    public final AtomicBoolean is_ready = new AtomicBoolean(false);
    public final AtomicBoolean has_propagated = new AtomicBoolean(false);
    public final AtomicBoolean has_aggregated = new AtomicBoolean(false);
    public final AtomicBoolean has_max = new AtomicBoolean(false);
    public final AtomicBoolean has_min = new AtomicBoolean(false);
    public final AtomicBoolean has_winner = new AtomicBoolean(false);
    public final AtomicBoolean has_amazongs = new AtomicBoolean(false);
    public final AtomicBoolean has_mobility = new AtomicBoolean(false);
    public final AtomicBoolean has_territory = new AtomicBoolean(false);

    public void FillAmazongs(GameState board) {
        if (!has_amazongs.get()) {
            has_amazongs.set(true);
            amazongs.set(Amazongs.CalculateHeuristic(board));
        }
    }

    public void FillTerritory(GameState board) {
        if (!has_territory.get()) {
            has_territory.set(true);
            territory.set(Territory.CalculateHeuristic(board));
        }
    }

    public void FillWinner(GameState board) {
        if (!has_winner.get()) {
            has_winner.set(true);
            winner.set(Winner.CalculateHeuristic(board));
        }
    }

    public void FillMobility(GameState board) {
        if(!has_mobility.get()) {
            has_mobility.set(true);
            mobility.set(Mobility.CalculateHeuristic(board));
        }
    }

    public void FillFreedom(GameState board) {
        if(!has_mobility.get()) {
            mobility.set(Mobility.CalculateFreedomHeuristic(board));
        }
    }

    public void FillReduction(GameState board) {
        if(!has_mobility.get()) {
            mobility.set(Mobility.CalculateReductionHeuristic(board));
        }
    }


    @Override
    public String toString() {
        return String.format("value: %.4f\naggregate avg: %.4f\n" +
                "aggregate count: %d\naggregate: %.4f\n" +
                "amazongs: %.4f\nwinning: %.4f\n" +
                "mobility: %.4f\nterritory: %.4f\n",
                value.get(), aggregate_avg.get(), aggregate_count.get(), aggregate.get(), amazongs.get(), winner.get(), mobility.get(), territory.get());
    }
}
