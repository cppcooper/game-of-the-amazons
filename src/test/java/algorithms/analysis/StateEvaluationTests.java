package algorithms.analysis;

import algorithms.search.MoveCompiler;
import org.junit.jupiter.api.Test;
import data.structures.GameState;
import tools.Debug;
import tools.Maths;
import tools.RandomGen;
import static org.junit.jupiter.api.Assertions.*;

public class StateEvaluationTests {
    @Test
    void evaluate_states(){
        int[][] states = new int[][]{
                Debug.test_state_alpha_debug,
                Debug.test_state_alpha_debug,
                Debug.test_state_white_good_opening,
                Debug.test_state_white_good_opening,
                Debug.test_state_black_good_opening,
                Debug.test_state_black_good_opening,

                Debug.test_state_black_winning,
                Debug.test_state_black_winning,
                Debug.test_state_black_clear_advantage,
                Debug.test_state_black_clear_advantage,
                Debug.test_state_black_advantage,
                Debug.test_state_black_advantage,
                Debug.test_state_black_disadvantage,
                Debug.test_state_black_disadvantage,
                Debug.test_state_black_clear_disadvantage,
                Debug.test_state_black_clear_disadvantage,

                Debug.test_state_white_winning,
                Debug.test_state_white_winning,
                Debug.test_state_white_clear_advantage,
                Debug.test_state_white_clear_advantage,
                Debug.test_state_white_advantage,
                Debug.test_state_white_advantage,
                Debug.test_state_white_disadvantage,
                Debug.test_state_white_disadvantage,
                Debug.test_state_white_clear_disadvantage,
                Debug.test_state_white_clear_disadvantage,
                Debug.late_state,
        };
        String[] state_info = new String[]{
                "black piece alpha debug",
                "black piece alpha debug",
                "white opening",
                "white opening",
                "black opening",
                "black opening",

                "black winning",
                "black winning",
                "black clear advantage",
                "black clear advantage",
                "black advantage",
                "black advantage",
                "black disadvantage",
                "black disadvantage",
                "black clear disadvantage",
                "black clear disadvantage",

                "white winning",
                "white winning",
                "white clear advantage",
                "white clear advantage",
                "white advantage",
                "white advantage",
                "white disadvantage",
                "white disadvantage",
                "white clear disadvantage",
                "white clear disadvantage",
                "late state"
        };
        System.out.println("Remember that evaluations calculate the value the state has, usually with respect to the player that made the last move.");
        GameState board = new GameState();
        board.SetMoveNumber(1);
        board.FindPieces();
        board.DebugPrint();
        double w = Winner.CalculateHeuristic(board);
        double m = Mobility.CalculateHeuristic(board);
        double t = Territory.CalculateHeuristic(board);
        double a = Amazongs.CalculateHeuristic(board);
        System.out.printf("w: %.4f\nm: %.4f\nt: %.4f\na: %.4f\ncurrent turn: %d\n", w, m, t, a, board.GetPlayerTurn());
        int i = 0;
        for(int[] state : states){
            board = new GameState(state);
            board.SetMoveNumber(i+1);
            board.FindPieces();
            System.out.printf("(%s::MN%d)\n  %s evaluation\n", state_info[i++], board.GetMoveNumber(), board.GetNextPlayerTurn() == 1 ? "black" : "white");
            board.DebugPrint();
            w = Winner.CalculateHeuristic(board);
            m = Mobility.CalculateHeuristic(board);
            t = Territory.CalculateHeuristic(board);
            a = Amazongs.CalculateHeuristic(board);
            double value = Maths.h(a, m+t, w);
            System.out.printf("winner: %.4f\nmobility: %.4f\nterritory: %.4f\namazongs: %.4f\ncombined: %.4f\n\n", w, m, t, a, value);
        }
    }

    @Test
    void find_all_possible_moves() {
        GameState board = new GameState();
        int[] positions = MoveCompiler.GetAllValidPositions();
        var moves = MoveCompiler.GetMoveList(board,positions,true);
        System.out.printf("possible moves (without pieces): %d\n",moves.size());
    }

    @Test
    void find_first_degree_positions() {
        GameState board = new GameState();
        int[] positions = MoveCompiler.GetAllValidPositions();
        int[][] first_degree_territory = MoveCompiler.GetOpenPositions(board,positions,false);
        int max = 0;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < first_degree_territory.length; ++i) {
            int first_degree = 0;
            for (int x : first_degree_territory[i]) {
                if (x < 0) {
                    break;
                }
                first_degree++;
            }
            if(first_degree > max){
                max = first_degree;
            }
            if(first_degree < min){
                min = first_degree;
            }
        }
        System.out.printf("min: %d\nmax: %d\n",min,max);
    }

    @Test
    void find_minmax_mobility(){
        final RandomGen rng = new RandomGen();
        final int trials = 1000000;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for(int i = 0; i < trials; ++i){
            GameState board = rng.GetRandomBoard();
            double heuristic = Amazongs.CalculateHeuristic(board);
            if(heuristic > max){
                max = heuristic;
            }
            if(heuristic < min){
                min = heuristic;
            }
        }
        System.out.printf("min: %.3f\nmax: %.3f\n",min,max);
    }

    @Test
    void find_max_territory_count(){
        final RandomGen rng = new RandomGen();
        final int trials = 100000;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for(int i = 0; i < trials; ++i){
            GameState board = new GameState(rng.GetRandomState(),true,true);
            double heuristic = Territory.CalculateHeuristic(board);
            boolean new_value = false;
            if(heuristic > max){
                new_value = true;
                max = heuristic;
            }
            if(heuristic < min){
                new_value = true;
                min = heuristic;
            }
            if(new_value){
                System.out.printf("--------\nnew min: %.3f\nnew max: %.3f\n",min,max);
            }
        }
        System.out.printf("min: %.3f\nmax: %.3f\n",min,max);
    }

    @Test
    void testing_infinite(){
        assertEquals(Double.NEGATIVE_INFINITY < 0, true);
        System.out.println(Maths.clamp(Double.POSITIVE_INFINITY,0,10));
        System.out.println(Math.pow(2, -Double.POSITIVE_INFINITY));
        System.out.println(Double.isNaN(Double.POSITIVE_INFINITY));
    }

}
