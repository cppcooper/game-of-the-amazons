package main;

import algorithms.search.BestMove;
import algorithms.search.MoveCompiler;
import algorithms.search.BreadthFirst;
import algorithms.search.MonteCarlo;
import algorithms.analysis.HeuristicsQueue;
import data.pod.Move;
import data.pod.Position;
import data.structures.GameState;
import data.structures.GameTree;
import data.structures.GameTreeNode;
import data.structures.MovePool;
import tools.Debug;
import tools.RandomGen;
import tools.Tuner;
import ygraph.ai.smartfox.games.amazons.BaseGameGUI;
import ygraph.ai.smartfox.games.amazons.OurGameGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AICore {
    private static OurGameGUI game_gui;
    private static GameState current_board_state = new GameState();
    private static Thread exploration_thread0 = null;
    private static Thread exploration_thread1 = null;
    private static Thread exploration_thread2 = null;
    private static Thread heuristics_thread = null;
    private static Thread move_sender_orphan = null;
    private static final AtomicBoolean threads_terminating = new AtomicBoolean(false);
    private static final AtomicBoolean game_tree_is_explored = new AtomicBoolean(false);
    private static final AtomicBoolean is_searching = new AtomicBoolean(false);
    private static AtomicReference<GameTreeNode> root = new AtomicReference<>();

    public static void main(String[] args) {
        try {
            assert Tuner.use_amazongs_heuristic || Tuner.use_winner_heuristic || Tuner.use_territory_heuristic || Tuner.use_mobility_heuristic;
            MovePool.generate_pool();
            RandomGen rng = new RandomGen();
            //player = new AIPlayer("coopstar" + rng.nextInt(4488), "secure_password");
            BaseGameGUI.sys_setup();
            game_gui = new OurGameGUI();
            exploration_thread0 = Thread.currentThread();
            // todo: select colour
            /*LaunchThreads();
            while(!game_gui.is_closed.get()){
                if(is_searching.get()){
                    BreadthFirst_exhaustive();
                }
                try {
                    Thread.sleep(2500);
                } catch (Exception e){}
            }*/
            //TerminateThreads(); is called in gui closing event
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void TerminateThreads() {
        threads_terminating.set(true);
        if (exploration_thread0 != null && exploration_thread0.isAlive()) {
            exploration_thread0.interrupt();
        }
        if (exploration_thread1 != null && exploration_thread1.isAlive()) {
            exploration_thread1.interrupt();
        }
        if (exploration_thread2 != null && exploration_thread2.isAlive()) {
            exploration_thread2.interrupt();
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
                    (exploration_thread1 != null && exploration_thread1.isAlive())
                    || (exploration_thread2 != null && exploration_thread2.isAlive())
                    || (heuristics_thread != null && heuristics_thread.isAlive())
            ) {
                Thread.sleep(100);
            }
            exploration_thread1 = null;
            exploration_thread2 = null;
            heuristics_thread = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        threads_terminating.set(false);
        is_searching.set(false);
    }

    public static void InterruptSimulations(){
        exploration_thread0.interrupt();
        exploration_thread1.interrupt();
        exploration_thread2.interrupt();
    }

    public static void LaunchThreads(){
        if(exploration_thread1 != null && exploration_thread1.isAlive() && !exploration_thread1.isInterrupted()){
            exploration_thread1.interrupt();
        }
        if(exploration_thread2 != null && exploration_thread2.isAlive() && !exploration_thread2.isInterrupted()){
            exploration_thread2.interrupt();
        }
        exploration_thread1 = new Thread(AICore::MonteCarloTreeSearch_breadthfirst);
        exploration_thread2 = new Thread(AICore::MonteCarloTreeSearch_depthfirst);
        if(Tuner.use_heuristic_queue) {
            if (heuristics_thread == null) {
                heuristics_thread = new Thread(HeuristicsQueue::ProcessQueue);
                heuristics_thread.start();
            }
        }

        exploration_thread1.start();
        exploration_thread2.start();
        is_searching.set(true);
    }

    private static void BreadthFirst_exhaustive() {
        Debug.PrintThreadID("ExhaustiveSearch");
        GameState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && copy.CanGameContinue() && !threads_terminating.get()) {
            if(BreadthFirst.ExploreGameTree(copy)){
                game_tree_is_explored.set(true);
                System.out.println("\nGAME TREE IS NOW FULLY EXPLORED.\n");
                return;
            }
            copy = GetStateCopy();
        }
    }

    private static void MonteCarloTreeSearch_breadthfirst(){
        Debug.PrintThreadID("MonteCarloSearch");
        GameState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && copy.CanGameContinue() && !threads_terminating.get()) {
            MonteCarlo.RunSimulation(copy, root.get(), true);
            if(copy.GetMoveNumber() != GetState().GetMoveNumber()) {
                copy = GetStateCopy();
            }
        }
    }

    private static void MonteCarloTreeSearch_depthfirst(){
        Debug.PrintThreadID("MonteCarloSearch");
        GameState copy = GetStateCopy();
        while (!game_tree_is_explored.get() && copy.CanGameContinue() && !threads_terminating.get()) {
            MonteCarlo.RunSimulation(copy, root.get(), false);
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
                        Move move = BestMove.Get(root.get());
                        if (move == null){
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
                            //player.makeMove(msg);
                            //player.getGameClient().sendMoveMessage(msg);
                            //System.out.println("Move sent to server.");
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
        GameTree.prune(prev_turn_num);
    }

    public static synchronized int GetCurrentMoveNumber(){
        return current_board_state.GetMoveNumber();
    }

    public static synchronized void SetState(GameState board){
        current_board_state = board;
        root.set(new GameTreeNode(null,null, current_board_state));
        game_tree_is_explored.set(false);
        current_board_state.DebugPrint();
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
            //player.kill();
            game_gui.dispose();
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
