package ubc.cosc322;

import algorithms.analysis.Heuristics;
import algorithms.analysis.MonteCarlo;
import structures.*;
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
    private static final AtomicBoolean terminate_threads = new AtomicBoolean(false);
    private static final AtomicBoolean game_tree_is_explored = new AtomicBoolean(false);

    public static void main(String[] args) {
        if (args.length >= 2) {
            try {
                player = new AIPlayer(args[0], args[1]);
                BaseGameGUI.sys_setup();
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        player.Go();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Command line arguments missing.");
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
        try {
            Thread.sleep(749 * 40);
            Move move = GetBestMove();
            current_board_state.MakeMove(move,true);
            InterruptSimulations();
            player.getGameClient().sendMoveMessage(MakeMessage(move));
            PruneGameTree();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> MakeMessage(Move move) {
        if (move != null) {
            Position start = new Position(move.start);
            Position next = new Position(move.next);
            Position arrow = new Position(move.arrow);
            ArrayList<Integer> msg_start = new ArrayList(Arrays.asList(new int[]{start.x, start.y}));
            ArrayList<Integer> msg_next = new ArrayList(Arrays.asList(new int[]{next.x, next.y}));
            ArrayList<Integer> msg_arrow = new ArrayList(Arrays.asList(new int[]{arrow.x, arrow.y}));
            Map<String, Object> msg = new HashMap<>();
            msg.put("queen-position-current", msg_start);
            msg.put("queen-position-next", msg_next);
            msg.put("arrow-position", msg_arrow);
            return msg;
        }
        return null;
    }

    private static Move GetBestMove() {
        // todo (1): verify GetBestMove implementation
        Move move = null;
        double best;
        int index;
        do {
            GameTreeNode current_node = GameTree.get(GetStateCopy());
            best = -1;
            index = -1;
            if(current_node != null) {
                for (int i = 0; i < current_node.edges(); ++i) {
                    GameTreeNode sub_node = current_node.get(i);
                    double aggregate = sub_node.aggregate_heuristic.get();
                    if (aggregate > best) {
                        best = aggregate;
                        index = i;
                    }
                }
                if (index > 0 && current_node.edges() > 0) {
                    move = current_node.get(index).move.get();
                }
            }
        } while (move == null);
        return move;
    }

    public static void PruneGameTree() {
        int prev_turn_num = current_board_state.GetPlayerTurn() - 1;
        GameTree.remove(prev_turn_num);
    }

    public static synchronized int GetCurrentTurnNumber(){
        return current_board_state.GetMoveNumber();
    }

    public static synchronized void SetState(ArrayList<Integer> state) {
        current_board_state = new LocalState(state, true, false); // saves state reference instead of copying
        game_tree_is_explored.set(false);
    }

    public static synchronized void UpdateState(final Map<String, Object> msgDetails) {
        ArrayList<Integer> qcurr = (ArrayList) msgDetails.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList) msgDetails.get("queen-position-next");
        ArrayList<Integer> arrow = (ArrayList) msgDetails.get("arrow-position");
        Move move = new Move(
                Position.CalculateIndex(qcurr.get(0), qcurr.get(1)),
                Position.CalculateIndex(qnew.get(0), qnew.get(1)),
                Position.CalculateIndex(arrow.get(0), arrow.get(1)));
        GameTreeNode parent = GameTree.get(current_board_state);
        current_board_state.MakeMove(move, true);
        GameTreeNode child = GameTree.get(current_board_state);
        if(child == null){
            //we copy the state, because it's going to change.. and we don't want to invalidate the key we use in the hash map (game tree)
            LocalState copy = new LocalState(current_board_state);
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
