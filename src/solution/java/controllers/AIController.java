package controllers;

import algorithms.exploration.Explorer;
import algorithms.search.BestNode;
import data.pod.BoardPiece;
import data.pod.Move;
import data.structures.GameState;
import data.structures.GameTree;
import data.parallel.GameTreeNode;
import main.Game;
import tools.Debug;
import ygraph.ai.smartfox.games.amazons.OurGameGUI;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;

public class AIController extends GameController {
    public AIController(GameState state, OurGameGUI gui, int turn_num) {
        super(state, gui, turn_num);
    }

    @Override
    public boolean hasMove(Move move) {
        return !GameTree.get(state).isTerminal();
    }

    @Override
    public Move getMove() throws InterruptedException {
        GameTreeNode node = getBestNode();
        return Objects.requireNonNull(node).move.get();
    }

    @Override
    public boolean takeTurn() throws InterruptedException {
        Debug.RunInfoL1DebugCode(()-> System.out.println("AI taking turn"));
        is_my_turn = true;
        GameTreeNode root = GameTree.get(state);
        signal = new CountDownLatch(100); // number of expandTree calls (between multiple GameExplorers) 1 for the root node, then x for children
        Explorer e = new Explorer(root, signal);
        e.start();
        Debug.RunInfoL1DebugCode(()-> System.out.println("Now exploring game tree"));
        Move move = getMove(); // waits until the explorer has done a finite amount of work, then goes about returning a move to make
        e.stop();
        Debug.RunInfoL1DebugCode(()-> System.out.println("Found a move"));
        if (isMyTurn() && hasMove(move)) {
            Game.Get().apply(move);
            for(BoardPiece p : pieces){
                if(p.getIndex() == move.start){
                    p.moveTo(move.next);
                    break;
                }
            }
            is_my_turn = false;
            return true;
        }
        return false;
    }

    @Override
    public GameTreeNode getBestNode() throws InterruptedException  {
        signal.await();
        return BestNode.Get(GameTree.get(state));
    }
}
