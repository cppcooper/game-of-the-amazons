package ubc.cosc322;

import algorithms.analysis.Heuristics;
import algorithms.analysis.MonteCarlo;
import org.apache.commons.math3.util.Precision;
import structures.*;
import tools.RandomGen;
import ygraph.ai.smartfox.games.BaseGameGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class AICore {
    private static LocalState current_board_state = null;
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
        if (heuristics_thread != null && heuristics_thread.isAlive()) {
            heuristics_thread.interrupt();
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
        mc_sim_thread1 = new Thread(AICore::ExhaustiveMonteCarlo);
        mc_sim_thread2 = new Thread(AICore::NonExhaustiveMonteCarlo);
        if(heuristics_thread == null) {
            heuristics_thread = new Thread(Heuristics::ProcessQueue);
            heuristics_thread.start();
        }

        mc_sim_thread1.start();
        mc_sim_thread2.start();
    }

    private static void ExhaustiveMonteCarlo() {
        LocalState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && !copy.IsGameOver() && !terminate_threads.get()) {
            if(MonteCarlo.RunSimulation(copy, new MonteCarlo.SimPolicy(Integer.MAX_VALUE, Integer.MAX_VALUE, MonteCarlo.SimPolicy.policy_type.BREADTH_FIRST))){
                game_tree_is_explored.set(true);
                return;
            }
            copy = GetStateCopy();
        }
    }

    private static void NonExhaustiveMonteCarlo(){
        final int initial_branches = 3;
        final int initial_depth = 3;
        final float binc = 0.333f;
        final float dinc = 1.5f;
        float branches = initial_branches;
        float depth = initial_depth;
        LocalState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && !copy.IsGameOver() && !terminate_threads.get()) {
            if(MonteCarlo.RunSimulation(copy, new MonteCarlo.SimPolicy((int)branches,(int)depth, MonteCarlo.SimPolicy.policy_type.MONTE_CARLO))){
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

    public static void SendDelayedMessage() {
        move_sender_orphan = new Thread(()->{
            try {
                System.out.println("SendDelayedMessage: now waiting..");
                if(!game_tree_is_explored.get()) {
                    Thread.sleep(Tuner.wait_time);
                } else {
                    Thread.sleep(1000);
                }
                if(!Thread.interrupted()) {
                    Move move = GetBestMove();
                    current_board_state.MakeMove(move, true, true);
                    InterruptSimulations();
                    var msg = MakeMessage(move);
                    player.makeMove(msg);
                    player.getGameClient().sendMoveMessage(msg);
                    System.out.println("Move sent to server.");
                    PruneGameTree();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        move_sender_orphan.start();
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
                System.out.printf("QCurr: [%c, %d]: %d\n",64 + start.x, start.y, start.CalculateIndex());
                System.out.printf("QNew: [%c, %d]: %d\n",64 + next.x, next.y, next.CalculateIndex());
                System.out.printf("Arrow: [%c, %d]: %d\n",64 + arrow.x, arrow.y, arrow.CalculateIndex());
            });
            return msg;
        }
        return null;
    }

    private static Move GetBestMove() {
        Move move = null;
        double best;
        int index;
        int null_count = 0;
        do {
            GameTreeNode current_node = GameTree.get(GetState());
            best = Double.NEGATIVE_INFINITY;
            index = -1;
            if(current_node != null) {
                if(current_node.edges() == 0){
                    Debug.ZeroEdgesDetected.set(true);
                } else {
                    Debug.RunLevel1DebugCode(()->System.out.printf("GetBestMove: found a node with %d edges, now to find the best one\n", current_node.edges()));
                    for (int i = 0; i < current_node.edges(); ++i) {
                        GameTreeNode sub_node = current_node.get(i);
                        double heuristic = sub_node.heuristic.aggregate.get();

                        final int edge = i;
                        final double h = heuristic;
                        Debug.RunLevel1DebugCode(()->System.out.printf("GetBestMove: node %d with a heuristic of %.3f\n", edge, h));

                        if (heuristic > best) {
                            Debug.RunLevel1DebugCode(()->System.out.printf("GetBestMove: at least one good heuristic (%.2f) - Move: %s\n", h, sub_node.move.get()));
                            best = heuristic;
                            index = i;
                        }
                    }
                    if (index >= 0) {
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

    public static void PruneGameTree() {
        int prev_turn_num = GetState().GetMoveNumber() - 2;
        GameTree.remove(prev_turn_num);
    }

    public static synchronized int GetCurrentTurnNumber(){
        return current_board_state.GetMoveNumber();
    }

    public static synchronized void SetState(ArrayList<Integer> state) {
        current_board_state = new LocalState(state, true, false); // saves state reference instead of copying
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
            System.out.printf("QCurr: [%c, %d]: %d\n",64 + p1.x, p1.y, p1.CalculateIndex());
            System.out.printf("QNew: [%c, %d]: %d\n",64 + p2.x, p2.y, p2.CalculateIndex());
            System.out.printf("Arrow: [%c, %d]: %d\n",64 + p3.x, p3.y, p3.CalculateIndex());
        });
        if(!current_board_state.MakeMove(move, true, true)){
            current_board_state.DebugPrint();
            System.out.println("ILLEGAL MOVE");
            // Yuuup, this (y,x) is how Gao sends the variables.. verified with debugging inside HumanPlayerTest in the mouse event
            //System.out.printf("QCurr: [%c, %d]: %d\n",64 + p1.x, p1.y, p1.CalculateIndex());
            //System.out.printf("QNew: [%c, %d]: %d\n",64 + p2.x, p2.y, p2.CalculateIndex());
            //System.out.printf("Arrow: [%c, %d]: %d\n",64 + p3.x, p3.y, p3.CalculateIndex());
            TerminateThreads();
            player.kill();
            System.exit(1);
        }
        LocalState copy = GetStateCopy();
        GameTreeNode child = GameTree.get(copy);
        if(child == null){
            //we copy the state, because it's going to change.. and we don't want to invalidate the key we use in the hash map (game tree)
            System.out.println("New Move.. updating game tree now.");
            child = new GameTreeNode(move,parent);
            GameTree.put(copy,child);
        }
    }

    private static synchronized LocalState GetState() {
        return current_board_state;
    }

    private static synchronized LocalState GetStateCopy() {
        return new LocalState(current_board_state);
    }
}
