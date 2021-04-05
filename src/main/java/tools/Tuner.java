package tools;

import data.Heuristic;

public class Tuner {
    public static int our_player_num = 1;
    public static int other_player_num = 2;
    public static final boolean use_static_pieces = false;
    public static final boolean use_heuristic_queue = true;
    public static final boolean use_decreasing_alpha_asymptote = true;

    public static final boolean disable_propagation_code = false;
    public static final boolean alter_winner_heuristic = true;
    public static final boolean find_best_value_first = false;
    public static final boolean find_best_aggregate = true;
    public static final boolean use_winner_aggregate = true;
    public static final boolean use_winner_heuristic = false;
    public static final boolean use_amazongs_heuristic = true;
    public static final boolean use_mobility_heuristic = true;
    public static final boolean use_territory_heuristic = true;
    public static final int send_delay = 1000 * 5;
    public static final int max_wait_time = 1000 * 5;

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
    public static final double fw = 5.0;
    public static final double alpha_curve = 0.75;
    public static final double alpha_cap = 30;
    public static final double w_cap = 70;
    public static final double falpha = 1.5;
    public static final double falphap = 1.5;
    public static final double falphab = 1;

    public static final double move_first_advantage = 1.0/5.0;

    public static double get_aggregate_base(Heuristic h){
        if(Tuner.use_winner_aggregate) {
            return h.winner.get();
        } else {
            return h.value.get();
        }
    }
}
