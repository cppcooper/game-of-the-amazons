package algorithms.exploration;

import algorithms.search.MoveCompiler;
import data.pod.Move;
import data.structures.GameState;
import data.structures.GameTree;
import data.parallel.GameTreeNode;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public abstract class GameExplorer {
    private CountDownLatch signal;
    protected Deque<GameTreeNode> job_queue = new LinkedList<>();
    protected GameExplorer(CountDownLatch signal){
        this.signal = signal;
    }

    public abstract boolean explore(GameTreeNode root);
    protected abstract void explore(GameTreeNode root, boolean is_root_invocation);
    protected abstract void evaluate(GameTreeNode node);

    protected LinkedList<GameTreeNode> expandTree(GameTreeNode root){
        LinkedList<GameTreeNode> nodes = new LinkedList<>();
        GameState board = root.state_after_move.get();
        if(!Thread.currentThread().isInterrupted()) {
            ArrayList<Move> moves = MoveCompiler.compileList(board, true);
            if(moves.isEmpty()){
                identifyTerminalState(root);
                return null;
            }
            for (Move move : moves) {
                if (Thread.currentThread().isInterrupted()) {
                    return null;
                }
                GameState new_state = new GameState(board);
                if (new_state.apply(move)) {
                    GameTreeNode node = GameTree.get(new_state); // GameTreeNode might already exist for this state [original_state + move]
                    if (node == null) {
                        // GameState is a new position
                        node = new GameTreeNode(move, root, new_state);
                        root.adopt(node);
                        GameTree.put(node);
                    } else {
                        // This should represent branches merging.. or redundant work
                        if(root.adopt(node)){
                            node.propagateTo(root);
                        }
                    }
                    evaluate(node);
                    nodes.add(node);
                }
            }
        }
        signal.countDown();
        return nodes;
    }

    protected void processJobs(){
        while (!job_queue.isEmpty()) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            GameTreeNode node = job_queue.poll();
            explore(node, false);
        }
    }

    private void identifyTerminalState(GameTreeNode node){
        node.identifyTerminalState();
        node.propagate();
    }
}
