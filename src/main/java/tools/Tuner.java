package tools;

public class Tuner {
    public static final boolean use_static_pieces = false;
    public static final boolean use_heuristic_queue = true;
    public static final boolean use_lowest_heuristic = false;
    public static final boolean use_highest_heuristic = true;
    public static final boolean use_dynamic_heuristic = false;
    public static final boolean use_decreasing_alpha_asymptote = true;

    public static final boolean find_best_aggregate = false;
    public static final boolean use_winner_heuristic = false;
    public static final boolean use_amazongs_heuristic = true;
    public static final boolean use_mobility_heuristic = true;
    public static final boolean use_territory_heuristic = false;
    public static final int send_delay = 1000 * 5;
    public static final int max_wait_time = 1000 * 15;

    public static final int coord_min = 0;
    public static final int coord_max = 9;
    public static final int coord_upper = coord_max+1;
    public static final int coord_offset = 10-coord_max;
    public static final int state_size = coord_upper * coord_upper;

    public static final double c1c = 2.0;
    public static final double c2c = 1.0;
    public static final double t2c = 1.0;

    public static final double t2p = 3.0;

    public static final double fwp = 1;
    public static final double fw = 20.0;
    public static final double alpha_curve = 0.25;
    public static final double alpha_cap = 50;
    public static final double falpha = 1.5;
    public static final double falphap = 1.5;
    public static final double falphab = 20;

    public static final double move_first_advantage = 1.0/5.0;
}
