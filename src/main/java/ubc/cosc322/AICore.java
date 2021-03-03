package ubc.cosc322;

import structures.LocalState;
import structures.Move;
import structures.Position;

import java.util.ArrayList;
import java.util.Map;

public class AICore {
    private static LocalState current_board_state;

    public static void run(){

    }

    public static synchronized void SetState(ArrayList<Integer> state){
        try {
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

    }
}
