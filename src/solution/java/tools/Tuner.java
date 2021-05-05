package tools;

import data.parallel.GameTreeHeuristic;

public class Tuner {
    public static int ai_player_num = 1;
    public static int human_player_num = 2;
    public static boolean human_turn = false;
    public static final boolean use_static_pieces = false;
    public static final boolean use_heuristic_queue = false;
    public static final boolean use_decreasing_alpha_asymptote = true;

    public static final boolean disable_propagation_code = false;
    public static final boolean use_only_winning = true;
    public static final boolean alter_winner_heuristic = true;
    public static final boolean find_best_aggregate = true;
    public static final boolean use_winner_aggregate = true;
    public static final boolean use_winner_heuristic = false;
    public static final boolean use_amazongs_heuristic = true;
    public static final boolean use_mobility_heuristic = true;
    public static final boolean use_territory_heuristic = true;
    public static final int send_delay = 1000 * 15;
    public static final int max_wait_time = 1000 * 5;

    public static final int montecarlo_breadth_top = 10;
    public static final int montecarlo_breadth_bottom = 1;
    public static final int max_search_depth = 10;

    public static final int coord_min = 0;
    public static final int coord_max = 9;
    public static final int coord_upper = coord_max+1;
    public static final int coord_offset = 10-coord_max;
    public static final int state_size = coord_upper * coord_upper;

    public static final double tc = 7.3;

    public static final double t1c = 8.0;
    public static final double c1c = 3.5;
    public static final double c2c = 1.0;
    public static final double t2c = 1.0;

    public static final double t2p = 3.0;

    public static final double fwp = 1;
    public static final double fw = 2.0;
    public static final double alpha_curve = 0.5;
    public static final double alpha_cap = 30;
    public static final double w_cap = 65;
    public static final double falpha = 1.5;
    public static final double falphap = 1.5;
    public static final double falphab = 1;

    public static final double move_first_advantage = 1.0/5.0;

//    public static double get_aggregate_base(GameTreeHeuristic h){
//        if(Tuner.use_winner_aggregate && !Tuner.use_winner_heuristic) {
//            return h.winner.get();
//        } else {
//            return h.value.get();
//        }
//    }
}
