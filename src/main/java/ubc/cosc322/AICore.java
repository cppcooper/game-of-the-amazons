package ubc.cosc322;

import algorithms.analysis.MonteCarlo;
import structures.LocalState;
import structures.Move;
import structures.Position;
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
    private static AtomicBoolean terminate_threads = new AtomicBoolean(false);

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
            heuristics_thread = new Thread(AICore::ProcessHeuristicsQueue);
        }

        mc_sim_thread1.start();
        mc_sim_thread2.start();
        heuristics_thread.start();
    }

    private static void ExhaustiveMonteCarlo() {
        LocalState copy = GetStateCopy();
        while (!copy.IsGameOver() && !terminate_threads.get()) {
            MonteCarlo.RunSimulation(copy, copy.GetPlayerTurn(), new MonteCarlo.SimPolicy(Integer.MAX_VALUE, Integer.MAX_VALUE));
            copy = GetStateCopy();
        }
    }

    private static void NonExhaustiveMonteCarlo(){
        LocalState copy = GetStateCopy();
        while (!copy.IsGameOver() && !terminate_threads.get()) {
            MonteCarlo.RunSimulation(copy, copy.GetPlayerTurn(), new MonteCarlo.SimPolicy(3,10));
            copy = GetStateCopy();
        }
    }

    private static void ProcessHeuristicsQueue() {
        // todo (1): implement queue and processor's control structures (including thread interrupt handling)
        // todo (1): integrate heuristic calculations with.. GameTree? with something (this involves the GetBestMove task)
        // todo (1): integrate queue with suppliers (ie. monte carlo simulations)
    }

    public static void SendMessage() {
        try {
            // todo (2): refactor GetBestMove/SendMessage..? perhaps instead of waiting for 29.96 seconds we should constantly run GetBestMove (timing it) and then send the best move we can find moments before our time runs out.. this might be good if GetBestMove takes a fair amount of time to execute
            Thread.sleep(749 * 40);
            player.getGameClient().sendMoveMessage(MakeMessage(GetBestMove()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> MakeMessage(Move move) {
        if (move != null) {
            Position start = new Position(move.start);
            Position next = new Position(move.piece);
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
        return null;
    }

    private static void PruneGameTree() {
        // todo (4): implement/ check if we should prune the game tree
    }

    public static synchronized void SetState(ArrayList<Integer> state) {
        current_board_state = new LocalState(state, true, false); // saves state reference instead of copying
    }

    public static synchronized void UpdateState(final Map<String, Object> msgDetails) {
        ArrayList<Integer> qcurr = (ArrayList) msgDetails.get("queen-position-current");
        ArrayList<Integer> qnew = (ArrayList) msgDetails.get("queen-position-next");
        ArrayList<Integer> arrow = (ArrayList) msgDetails.get("arrow-position");
        Move move = new Move(
                Position.CalculateIndex(qcurr.get(0), qcurr.get(1)),
                Position.CalculateIndex(qnew.get(0), qnew.get(1)),
                Position.CalculateIndex(arrow.get(0), arrow.get(1)));
        current_board_state.MakeMove(move, true);
        PruneGameTree();
    }

    // todo (3): verify this needs to return a copy of the state, (find usages) -> (do usages modify internal state)
    private static synchronized LocalState GetStateCopy() {
        return new LocalState(current_board_state);
    }
}
