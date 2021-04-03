package ubc.cosc322;

import algorithms.search.BreadFirstSearch;
import algorithms.search.MoveCompiler;
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
import java.util.concurrent.atomic.AtomicReference;

public class AICore {
    private static GameState current_board_state = null;
    private static AIPlayer player = null;
    private static Thread search_thread0 = null;
    private static Thread search_thread1 = null;
    private static Thread search_thread2 = null;
    private static Thread heuristics_thread = null;
    private static Thread move_sender_orphan = null;
    private static final AtomicBoolean terminate_threads = new AtomicBoolean(false);
    private static final AtomicBoolean game_tree_is_explored = new AtomicBoolean(false);
    private static final AtomicBoolean is_searching = new AtomicBoolean(false);
    private static AtomicReference<GameTreeNode> root = new AtomicReference<>();

    public static void main(String[] args) {
        try {
            assert Tuner.use_amazongs_heuristic || Tuner.use_winner_heuristic || Tuner.use_territory_heuristic || Tuner.use_mobility_heuristic;
            MovePool.generate_pool();
            RandomGen rng = new RandomGen();
            player = new AIPlayer("coopstar" + rng.nextInt(4488), "secure_password");
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    player.Go();
                }
            });
            search_thread0 = Thread.currentThread();
            while(player.isRunning()){
                if(is_searching.get()){
                    MonteCarloTreeSearch();
                }
                Thread.sleep(2500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void TerminateThreads() {
        terminate_threads.set(true);
        if (search_thread1 != null && search_thread1.isAlive()) {
            search_thread1.interrupt();
        }
        if (search_thread2 != null && search_thread2.isAlive()) {
            search_thread2.interrupt();
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
                    (search_thread1 != null && search_thread1.isAlive())
                    || (search_thread2 != null && search_thread2.isAlive())
                    || (heuristics_thread != null && heuristics_thread.isAlive())
            ) {
                Thread.sleep(100);
            }
            search_thread1 = null;
            search_thread2 = null;
            heuristics_thread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        terminate_threads.set(false);
        is_searching.set(false);
    }

    public static void InterruptSimulations(){
        search_thread1.interrupt();
        search_thread2.interrupt();
    }

    public static void LaunchThreads(){
        if(search_thread1 != null && search_thread1.isAlive() && !search_thread1.isInterrupted()){
            search_thread1.interrupt();
        }
        if(search_thread2 != null && search_thread2.isAlive() && !search_thread2.isInterrupted()){
            search_thread2.interrupt();
        }
        search_thread1 = new Thread(AICore::ExhaustiveSearch);
        search_thread2 = new Thread(AICore::MonteCarloTreeSearch);
        if(Tuner.use_heuristic_queue) {
            if (heuristics_thread == null) {
                heuristics_thread = new Thread(HeuristicsQueue::ProcessQueue);
                heuristics_thread.start();
            }
        }

        search_thread1.start();
        search_thread2.start();
        is_searching.set(true);
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
        final int initial_branches = 500;
        final float initial_binc = 17.f;
        float branches = initial_branches;
        float binc = initial_binc;
        GameState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && copy.CanGameContinue() && !terminate_threads.get()) {
            float p = copy.GetMoveNumber() / 92.0f;
            float d = Math.abs(0.5f - p) / 0.5f;
            if(MonteCarlo.RunSimulation(copy, root.get(), new MonteCarlo.SimPolicy((int)branches,3))){
                branches += binc;
            } else {
                branches = Math.max(initial_branches, p * initial_branches * initial_branches);
                binc = (p * initial_binc) * initial_binc;
            }
            if(copy.GetMoveNumber() != GetState().GetMoveNumber()) {
                copy = GetStateCopy();
            }
        }
    }

    public static void SendDelayedMessage() {
        if(move_sender_orphan == null || !move_sender_orphan.isAlive()) {
            move_sender_orphan = new Thread(() -> {
                try {
                    System.out.println("SendDelayedMessage: now waiting..");
                    if (!game_tree_is_explored.get() || HeuristicsQueue.isProcessing()) {
                        Thread.sleep(Tuner.send_delay);
                    }
                    if (!Thread.currentThread().isInterrupted()) {
                        GameTreeNode node = GetBestNode();
                        Move move = null;
                        if (node != null) {
                            move = node.move.get();
                            if (move == null) {
                                throw new IllegalStateException("We found a node with a null move. This shouldn't even be possible.");
                            }
                        } else {
                            GameState copy = GetStateCopy();
                            ArrayList<Move> options = MoveCompiler.GetMoveList(copy, copy.GetTurnPieces(), true);
                            for (Move m : options) {
                                if (m.IsValidFor(copy)) {
                                    move = m;
                                    break;
                                }
                            }
                            if (options.size() == 0 || move == null) {
                                throw new IllegalStateException("We cannot find any moves for some reason. Even the MoveCompiler can't find a valid move. If the game is over, this thread should have been terminated.");
                            }
                        }
                        if(!Thread.currentThread().isInterrupted()) {
                            current_board_state.MakeMove(move, true, true);
                            InterruptSimulations();
                            var msg = MakeMessage(move);
                            player.makeMove(msg);
                            player.getGameClient().sendMoveMessage(msg);
                            System.out.println("Move sent to server.");
                            Debug.RunInfoL1DebugCode(()->{
                                PrintChoice(node);
                            });
                            PruneGameTree();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            move_sender_orphan.start();
        } else {
            System.out.println("We are Desynchronized for some reason. SendDelayedMessage is already running.");
        }
    }

    private static GameTreeNode GetBestNode() throws Exception {
        double best_low;
        double best_high;
        int index;
        GameTreeNode best_low_node = null;
        GameTreeNode best_high_node = null;
        int bad_loop_count = 0;
        Benchmarker B = new Benchmarker();
        B.Start();
        do {
            best_low = Double.POSITIVE_INFINITY;//Double.NEGATIVE_INFINITY;
            best_high = Double.NEGATIVE_INFINITY;
            /* Control Structure
             * check that we have a root to search from
             * check that the root has edges
             * compare edges
             * */
            GameTreeNode root = AICore.root.get();
            if (root == null) {
                Debug.NoParentNodeFound.set(true);
                System.out.println("GetBestNode: GameTree can't find the state");
                throw new IllegalStateException("There absolutely should be a root node, and we can't find it.");
            } else if (root.edges() == 0) {
                Debug.ZeroEdgesDetected.set(true);
                System.out.println("GetBestNode: Zero edges");
                throw new IllegalStateException(
                        String.format("This should mean we have lost the game, in which case this thread should have been terminated.\n" +
                                "[game can continue: %B]", GetState().CanGameContinue()));
            } else {
                Debug.RunInfoL2DebugCode(() -> System.out.printf("GetBestNode: our root node has %d edges, now to find the best edge\n", root.edges()));
                for (int i = 0; i < root.edges(); ++i) {
                    final int edge = i;
                    GameTreeNode sub_node = root.get(i);
                    if (!sub_node.heuristic.is_ready.get() && B.Elapsed() < Tuner.max_wait_time) {
                        Debug.RunVerboseL1DebugCode(() -> System.out.printf("GetBestNode: node not ready. [Node: %s]\n", sub_node));
                        HeuristicsQueue.CalculateHeuristicsAll(sub_node.state_after_move.get(), sub_node, true);
                    }
                    Debug.RunVerboseL1DebugCode(() -> System.out.printf("GetBestNode: node %d\n%s", edge, sub_node));

                    // need to check if this node is better than previous nodes
                    if (Tuner.find_best_aggregate) {
                        double heuristic = sub_node.heuristic.aggregate.get();
                        if (Tuner.use_lowest_heuristic && heuristic < best_low) {
                            Debug.RunInfoL2DebugCode(() -> System.out.printf("GetBestNode: new lowest node\n%s", sub_node));
                            best_low = heuristic;
                            best_low_node = sub_node;
                        }
                        if (Tuner.use_highest_heuristic && heuristic > best_high) {
                            Debug.RunInfoL2DebugCode(() -> System.out.printf("GetBestNode: new high node\n%s", sub_node));
                            best_high = heuristic;
                            best_high_node = sub_node;
                        }
                    } else {
                        double heuristic = sub_node.heuristic.value.get();
                        if (Tuner.use_lowest_heuristic && heuristic < best_low) {
                            Debug.RunInfoL2DebugCode(() -> System.out.printf("GetBestNode: new lowest node\n%s", sub_node));
                            best_low = heuristic;
                            best_low_node = sub_node;
                        }
                        if (Tuner.use_highest_heuristic && heuristic > best_high) {
                            Debug.RunInfoL2DebugCode(() -> System.out.printf("GetBestNode: new high node\n%s", sub_node));
                            best_high = heuristic;
                            best_high_node = sub_node;
                        }
                    }
                }
                // We've found our best nodes, now we need to return
                Heuristic h_low = null;
                if (best_low_node != null && best_high_node != null) {
                    System.out.println("GetBestNode: found two");
                    Heuristic h1 = best_low_node.heuristic;
                    Heuristic h2 = best_high_node.heuristic;
                    if(h1.has_winner.get() && h2.has_winner.get()) {
                        if (h1.winner.get() > h2.winner.get()) {
                            return best_low_node;
                        } else if (h2.winner.get() > h1.winner.get()) {
                            return best_high_node;
                        }
                    }
                    if(h1.has_mobility.get() && h2.has_mobility.get()){
                        if (h1.mobility.get() > h2.mobility.get()) {
                            return best_low_node;
                        } else if (h2.mobility.get() > h1.mobility.get()) {
                            return best_high_node;
                        }
                    }
                    if(h1.has_territory.get() && h2.has_territory.get()){
                        if (h1.territory.get() > h2.territory.get()) {
                            return best_low_node;
                        } else if (h2.territory.get() > h1.territory.get()) {
                            return best_high_node;
                        }
                    }
                } else if (best_high_node != null) {
                    System.out.println("GetBestNode: found one");
                    return best_high_node;
                } else if (best_low_node != null){
                    System.out.println("GetBestNode: found one");
                    return best_low_node;
                } else {
                    System.out.println("GetBestNode: Could not find a good node.. try again?");
                }
            }
        } while (B.Elapsed() < Tuner.max_wait_time);
        return null;
    }

    private static Map<String, Object> MakeMessage(Move move) {
        if (move != null) {
            Position start = new Position(move.start);
            Position next = new Position(move.next);
            Position arrow = new Position(move.arrow);
            // coords are (row, col) and not (x, y)
            ArrayList<Integer> msg_start = new ArrayList<>(Arrays.asList(start.row(), start.col()));
            ArrayList<Integer> msg_next = new ArrayList<>(Arrays.asList(next.row(), next.col()));
            ArrayList<Integer> msg_arrow = new ArrayList<>(Arrays.asList(arrow.row(), arrow.col()));
            Map<String, Object> msg = new HashMap<>();
            msg.put("queen-position-current", msg_start);
            msg.put("queen-position-next", msg_next);
            msg.put("arrow-position", msg_arrow);
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
        root.set(new GameTreeNode(null,null, current_board_state));
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
        GameTreeNode parent = root.get();
        if(!current_board_state.MakeMove(move, true, false)){
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
        root.set(child);
        GameTreeNode finalChild = child;
        Debug.RunInfoL1DebugCode(()->{
            if(!finalChild.heuristic.is_ready.get()){
                HeuristicsQueue.CalculateHeuristicsAll(copy, finalChild, true);
            }
            PrintChoice(finalChild);
        });
    }

    private static void PrintChoice(GameTreeNode node){
        if(node != null) {
            node.state_after_move.get().DebugPrint();
            System.out.printf("\n========\nNode chosen\n%s\n", node);
        } else {
            System.out.println("\n\n=========\nNULL NODE\n=========\n\n");
        }
    }

    private static synchronized GameState GetState() {
        return current_board_state;
    }

    private static synchronized GameState GetStateCopy() {
        return new GameState(current_board_state);
    }
}
