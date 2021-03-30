package tools;

public class Tuner {
    public static final boolean use_static_pieces = true;
    public static final boolean use_heuristic_queue = true;
    public static final boolean use_lowest_heuristic = true;
    public static final boolean use_highest_heuristic = false;

    public static final boolean use_aggregate_heuristic = true;
    public static final boolean use_winner_heuristic = false;
    public static final boolean use_mobility_heuristic = true;
    public static final boolean use_territory_heuristic = false;
    public static final int max_wait_time = 1000 * 20;

    public static final int coord_min = 0;
    public static final int coord_max = 9;
    public static final int coord_upper = coord_max+1;
    public static final int coord_offset = 10-coord_max;
    public static final int state_size = coord_upper * coord_upper;

    public static final double t1c = 1.0;
    public static final double c1c = 1.0;
    public static final double c2c = 1.0;
    public static final double t2c = 1.0;

    public static final double t1p = 1.0;
    public static final double t2p = 3.0;

    public static final double fw = 1.0;
    public static final double falpha = 2.0;
}
