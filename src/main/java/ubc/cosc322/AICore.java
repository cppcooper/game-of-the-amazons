package ubc.cosc322;

import structures.LocalState;
import structures.Move;
import structures.Position;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AICore {
    private static LocalState current_board_state;

    public static void run(){
        int available_cores = Runtime.getRuntime().availableProcessors();
        int available_threads = available_cores - 2;
        ExecutorService sim_pool = Executors.newFixedThreadPool(available_threads > 2 ? available_threads : 2);
        //todo: integrate heuristics into a processing queue, I think discord has a pin about this
        Thread heuristics_processor = new Thread();
        //todo: while there are more Moves to explore, we do so. If there are not, we wait for a new game to start
        //todo: figure out how to wait for new game to start
        try {
            heuristics_processor.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void SetState(ArrayList<Integer> state){
        try {
            //todo: if this happens in the middle of a game the turn number will be incorrect, so we need to prevent that potential problem
            current_board_state = new LocalState(state,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void UpdateState(final Map<String, Object> msgDetails) {
        ArrayList<Integer> qcurr = (ArrayList)msgDetails.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList)msgDetails.get("queen-position-next");
        ArrayList<Integer> arrow = (ArrayList)msgDetails.get("arrow-position");
        Move move = new Move(
                Position.CalculateIndex(qcurr.get(0),qcurr.get(1)),
                Position.CalculateIndex(qnew.get(0),qnew.get(1)),
                Position.CalculateIndex(arrow.get(0),arrow.get(1)));
        current_board_state.MakeMove(move,true);
        PruneGameTree();
    }

    private static synchronized LocalState GetState(){
        return current_board_state;
    }

    public static Move GetBestMove(){
        return null;
    }

    private static void PruneGameTree(){
        //todo: check if we should prune the game tree
    }
}
