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
        try {
            int available_cores = Runtime.getRuntime().availableProcessors();
            int available_threads = available_cores - 2;
            ExecutorService sim_pool = Executors.newFixedThreadPool(available_threads > 2 ? available_threads : 2);
            Thread heuristics_processor = new Thread(() -> ProcessHeuristicsQueue());
            heuristics_processor.run();
            //todo (3): figure out how to wait for new game to start
            //todo (2): integrate heuristics into a processing queue, I think discord has a pin about this
            //todo (1): while there are more Moves to explore, we do so. If there are not, we wait for a new game to start

            //todo (3): need an exit condition, perhaps we should relaunch when there is a new game?
            heuristics_processor.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void SetState(ArrayList<Integer> state){
        //todo (3): if this happens in the middle of a game the turn number will be incorrect, so we need to prevent that potential problem
        current_board_state = new LocalState(state,true,false); // saves state reference instead of copying
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

    //todo: should this make and return a copy? I think so as it makes sense.. but let's wait til we have a use of the function
    private static synchronized LocalState GetState(){
        return current_board_state;
    }

    public static Move GetBestMove(){
        return null;
    }

    private static void PruneGameTree(){
        //todo: check if we should prune the game tree
    }

    private static void ProcessHeuristicsQueue(){

    }
}
