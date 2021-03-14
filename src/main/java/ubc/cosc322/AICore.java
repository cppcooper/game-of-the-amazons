package ubc.cosc322;

import structures.LocalState;
import structures.Move;
import structures.Position;
import ygraph.ai.smartfox.games.BaseGameGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AICore {
    private static LocalState current_board_state = null;
    private static Player player = null;

    public static void main(String[] args){
        if(args.length >= 2) {
            try {
                player = new Player(args[0], args[1]);
                BaseGameGUI.sys_setup();
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        player.Go();
                    }
                });
                Thread ai_thread = new Thread(() -> AICore.run());
                ai_thread.run();

                while(true){
                    // todo (4): figure out how to wait/start new game after game over
                    // todo (4): verify game over condition/event/etc.
                    while(PlayersHaveMoves()){
                        if(player.our_turn.get()) {
                            Thread.sleep(749*40); // 749ms x 40 = 29.96 seconds
                            player.getGameClient().sendMoveMessage(MakeMessage(AICore.GetBestMove()));
                        }
                        Thread.sleep(750); // 750ms x 40 = 30 seconds
                    }
                    // todo (4): figure out how to detect a termination condition
                    if(false){
                        break;
                    }
                }
                ai_thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Command line arguments missing.");
        }
    }

    private static void run(){
        try {
            int available_cores = Runtime.getRuntime().availableProcessors();
            int available_threads = available_cores - 2;
            ExecutorService sim_pool = Executors.newFixedThreadPool(available_threads > 2 ? available_threads : 2);
            Thread heuristics_processor = new Thread(() -> ProcessHeuristicsQueue());
            heuristics_processor.run();
            // todo (3): figure out how to wait for new game to start
            // todo (3): find a faster way to: while there are more Moves to explore, we do so. If there are not, we wait for a new game to start
            while(PlayersHaveMoves()){

            }

            // todo (3): need an exit condition, perhaps we should relaunch when there is a new game?
            heuristics_processor.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void ProcessHeuristicsQueue(){
        // todo (2): integrate heuristics into a processing queue, I think discord has a pin about this
    }

    public static synchronized void SetState(ArrayList<Integer> state){
        // todo (3): if this happens in the middle of a game the turn number will be incorrect, so we need to prevent that potential problem
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

    // todo (6): verify this needs to return a copy of the state
    private static synchronized LocalState GetState(){
        return new LocalState(current_board_state);
    }

    private static synchronized boolean PlayersHaveMoves(){
        return !current_board_state.IsGameOver();
    }

    private static Move GetBestMove(){
        return null;
    }

    private static Map<String,Object> MakeMessage(Move move){
        Position start = new Position(move.start);
        Position next = new Position(move.piece);
        Position arrow = new Position(move.arrow);
        ArrayList<Integer> msg_start = new ArrayList(Arrays.asList(new int[]{start.x,start.y}));//)msgDetails.get("queen-position-current");
        ArrayList<Integer> msg_next = new ArrayList(Arrays.asList(new int[]{next.x,next.y}));;//("queen-position-next");
        ArrayList<Integer> msg_arrow = new ArrayList(Arrays.asList(new int[]{arrow.x,arrow.y}));//("arrow-position");
        Map<String, Object> msg = new HashMap<>();
        msg.put("queen-position-current",msg_start);
        msg.put("queen-position-next",msg_next);
        msg.put("arrow-position",msg_arrow);
        return msg;
    }

    private static void PruneGameTree(){
        // todo (4): implement/ check if we should prune the game tree
    }
}
