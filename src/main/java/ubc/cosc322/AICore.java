package ubc.cosc322;

import algorithms.search.BreadFirstSearch;
import data.Heuristic;
import algorithms.analysis.HeuristicsQueue;
import algorithms.search.MonteCarlo;
import data.*;
import data.structures.GameState;
import data.structures.GameTree;
import data.structures.GameTreeNode;
import data.structures.MovePool;
import tools.Benchmarker;
import tools.Debug;
import tools.RandomGen;
import tools.Tuner;
import ygraph.ai.smartfox.games.BaseGameGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class AICore {
    private static GameState current_board_state = null;
    private static AIPlayer player = null;
    private static Thread mc_sim_thread1 = null;
    private static Thread mc_sim_thread2 = null;
    private static Thread heuristics_thread = null;
    private static Thread move_sender_orphan = null;
    private static final AtomicBoolean terminate_threads = new AtomicBoolean(false);
    private static final AtomicBoolean game_tree_is_explored = new AtomicBoolean(false);

    public static void main(String[] args) {
        try {
            MovePool.generate_pool();
            RandomGen rng = new RandomGen();
            player = new AIPlayer("coopstar" + rng.nextInt(4488), "secure_password");
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    player.Go();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void TerminateThreads() {
        terminate_threads.set(true);
        if (mc_sim_thread1 != null && mc_sim_thread1.isAlive()) {
            mc_sim_thread1.interrupt();
        }
        if (mc_sim_thread2 != null && mc_sim_thread2.isAlive()) {
            mc_sim_thread2.interrupt();
        }
        if(Tuner.use_heuristic_queue) {
            if (heuristics_thread != null && heuristics_thread.isAlive()) {
                heuristics_thread.interrupt();
            }
        }
        if (move_sender_orphan != null && move_sender_orphan.isAlive()) {
            move_sender_orphan.interrupt();
        }
        try {
            while (
                    (mc_sim_thread1 != null && mc_sim_thread1.isAlive())
                    || (mc_sim_thread2 != null && mc_sim_thread2.isAlive())
                    || (heuristics_thread != null && heuristics_thread.isAlive())
            ) {
                Thread.sleep(100);
            }
            mc_sim_thread1 = null;
            mc_sim_thread2 = null;
            heuristics_thread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        terminate_threads.set(false);
    }

    public static void InterruptSimulations(){
        mc_sim_thread1.interrupt();
        mc_sim_thread2.interrupt();
    }

    public static void LaunchThreads(){
        if(mc_sim_thread1 != null && mc_sim_thread1.isAlive() && !mc_sim_thread1.isInterrupted()){
            mc_sim_thread1.interrupt();
        }
        if(mc_sim_thread2 != null && mc_sim_thread2.isAlive() && !mc_sim_thread2.isInterrupted()){
            mc_sim_thread2.interrupt();
        }
        mc_sim_thread1 = new Thread(AICore::ExhaustiveSearch);
        mc_sim_thread2 = new Thread(AICore::MonteCarloTreeSearch);
        if(Tuner.use_heuristic_queue) {
            if (heuristics_thread == null) {
                heuristics_thread = new Thread(HeuristicsQueue::ProcessQueue);
                heuristics_thread.start();
            }
        }

        mc_sim_thread1.start();
        mc_sim_thread2.start();
    }

    private static void ExhaustiveSearch() {
        Debug.PrintThreadID("ExhaustiveSearch");
        GameState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && copy.CanGameContinue() && !terminate_threads.get()) {
            if(BreadFirstSearch.Search(copy)){
                game_tree_is_explored.set(true);
                System.out.println("\nGAME TREE IS NOW FULLY EXPLORED.\n");
                return;
            }
            copy = GetStateCopy();
        }
    }

    private static void MonteCarloTreeSearch(){
        Debug.PrintThreadID("MonteCarloSearch");
        final int initial_branches = 3;
        final int initial_depth = 6;
        final float binc = 2.f;
        final float dinc = 1.5f;
        float branches = initial_branches;
        float depth = initial_depth;
        GameState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && copy.CanGameContinue() && !terminate_threads.get()) {
            if(MonteCarlo.RunSimulation(copy, new MonteCarlo.SimPolicy((int)branches,(int)depth))){
                branches += binc;
                depth += dinc;
            } else {
                branches = initial_branches;
                depth = initial_depth;
            }
            if(copy.GetMoveNumber() != GetState().GetMoveNumber()) {
                copy = GetStateCopy();
            }
        }
    }

    public static void SendMessage() {
        move_sender_orphan = new Thread(()->{
            try {
                Move move = GetBestMove();
                current_board_state.MakeMove(move, true, true);
                InterruptSimulations();
                var msg = MakeMessage(move);
                player.makeMove(msg);
                player.getGameClient().sendMoveMessage(msg);
                System.out.println("Move sent to server.");
                PruneGameTree();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        move_sender_orphan.start();
    }

    private static Move GetBestMove() throws InterruptedException {
        Move move = null;
        double best_low;
        double best_high;
        int index;
        int index_low;
        int index_high;
        int null_count = 0;
        Benchmarker B = new Benchmarker();
        B.Start();
        do {
            GameTreeNode current_node = GameTree.get(GetState());
            best_low = Double.POSITIVE_INFINITY;//Double.NEGATIVE_INFINITY;
            best_high = Double.NEGATIVE_INFINITY;
            index_low = -1;
            index_high = -1;
            if(current_node != null) {
                if(current_node.edges() == 0){
                    Debug.ZeroEdgesDetected.set(true);
                } else {
                    Debug.RunLevel2DebugCode(()->System.out.printf("GetBestMove: found a node with %d edges, now to find the best one\n", current_node.edges()));
                    for (int i = 0; i < current_node.edges(); ++i) {
                        GameTreeNode sub_node = current_node.get(i);;
                        if(!sub_node.heuristic.is_ready.get() && B.Elapsed() <= Tuner.max_wait_time){
                            Debug.RunLevel2DebugCode(()->System.out.printf("GetBestMove: node not ready. [Node: %s]\n", sub_node));
                            HeuristicsQueue.CalculateHeuristicsAll(sub_node.state_after_move.get(), sub_node, true);
                            if(sub_node.heuristic.has_aggregated.get()){

                            }
                        }
                        double heuristic = 0.0;
                        if(Tuner.use_aggregate_heuristic){
                            heuristic = sub_node.heuristic.aggregate.get();
                        } else {
                            heuristic = sub_node.heuristic.value.get();
                        }
                        final int edge = i;
                        final double h = heuristic;
                        Debug.RunLevel1DebugCode(()->System.out.printf("GetBestMove: node %d with a heuristic of %.3f\n", edge, h));

                        if (Tuner.use_lowest_heuristic && h < best_low) {
                            Debug.RunLevel2DebugCode(()->System.out.printf("GetBestMove: at least one good heuristic (%.2f) - Move: %s\n", h, sub_node.move.get()));
                            best_low = h;
                            index_low = i;
                        }
                        if (Tuner.use_highest_heuristic && h > best_high) {
                            Debug.RunLevel2DebugCode(()->System.out.printf("GetBestMove: at least one good heuristic (%.2f) - Move: %s\n", h, sub_node.move.get()));
                            best_high = h;
                            index_high = i;
                        }
                    }
                    Heuristic h_low = null;
                    if(index_low >= 0 && Tuner.use_lowest_heuristic) {
                        h_low = current_node.get(index_low).heuristic;
                    }
                    Heuristic h_high = null;
                    if(index_high >= 0 && Tuner.use_highest_heuristic){
                        h_high = current_node.get(index_high).heuristic;
                    }
                    if (h_low != null && h_high != null) {
                        if(h_low.territory.get() > h_high.territory.get()){
                            index = index_low;
                        } else {
                            index = index_high;
                        }
                    } else {
                        index = index_high >= 0 ? index_high : index_low;
                    }
                    if(index >= 0){
                        move = current_node.get(index).move.get();
                        System.out.println("GetBestMove: found a move");
                    } else {
                        Debug.NoIndexFound.set(true);
                    }
                }
            } else {
                //null_count = 0;
                Debug.NoParentNodeFound.set(true);
                System.out.println("GetBestMove: GameTree can't find the state");
            }
        } while (move == null);
        return move;
    }

    private static Map<String, Object> MakeMessage(Move move) {
        if (move != null) {
            Position start = new Position(move.start);
            Position next = new Position(move.next);
            Position arrow = new Position(move.arrow);
            // Yuuup, this (y,x; row,col) is how Gao sends the variables.. verified with debugging inside HumanPlayerTest in the mouse event
            ArrayList<Integer> msg_start = new ArrayList<>(Arrays.asList(start.row(), start.col()));
            ArrayList<Integer> msg_next = new ArrayList<>(Arrays.asList(next.row(), next.col()));
            ArrayList<Integer> msg_arrow = new ArrayList<>(Arrays.asList(arrow.row(), arrow.col()));
            Map<String, Object> msg = new HashMap<>();
            msg.put("queen-position-current", msg_start);
            msg.put("queen-position-next", msg_next);
            msg.put("arrow-position", msg_arrow);
            Debug.RunLevel3DebugCode(()->{
                current_board_state.DebugPrint();
                System.out.println(move);
            });
            return msg;
        }
        return null;
    }

    public static void PruneGameTree() {
        int prev_turn_num = GetState().GetMoveNumber() - 2;
        GameTree.remove(prev_turn_num);
    }

    public static synchronized int GetCurrentMoveNumber(){
        return current_board_state.GetMoveNumber();
    }

    public static synchronized void SetState(ArrayList<Integer> state) {
        current_board_state = new GameState(state, true, false); // saves state reference instead of copying
        game_tree_is_explored.set(false);
        current_board_state.DebugPrint();
    }

    public static synchronized void UpdateState(final Map<String, Object> msgDetails) {
        ArrayList<Integer> qcurr = (ArrayList) msgDetails.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList) msgDetails.get("queen-position-next");
        ArrayList<Integer> arrow = (ArrayList) msgDetails.get("arrow-position");
        Position p1 = new Position(qcurr);
        Position p2 = new Position(qnew);
        Position p3 = new Position(arrow);
        Move move = new Move(
                p1.CalculateIndex(),
                p2.CalculateIndex(),
                p3.CalculateIndex());
        GameTreeNode parent = GameTree.get(current_board_state);
        Debug.RunLevel3DebugCode(()->{
            current_board_state.DebugPrint();
            System.out.println(move);
        });
        if(!current_board_state.MakeMove(move, true, true)){
            current_board_state.DebugPrint();
            System.out.println("ILLEGAL MOVE");
            System.out.println(move);
            TerminateThreads();
            player.kill();
            System.exit(1);
        }
        GameState copy = GetStateCopy();
        GameTreeNode child = GameTree.get(copy);
        if(child == null){
            //we copy the state, because it's going to change.. and we don't want to invalidate the key we use in the hash map (game tree)
            System.out.println("New Move.. updating game tree now.");
            child = new GameTreeNode(move,parent,copy);
            GameTree.put(child);
        }
    }

    private static synchronized GameState GetState() {
        return current_board_state;
    }

    private static synchronized GameState GetStateCopy() {
        return new GameState(current_board_state);
    }
}
