package main;

import algorithms.exploration.Explorer;
import data.pod.Move;
import data.structures.GameTree;
import data.parallel.GameTreeNode;

import java.util.concurrent.CountDownLatch;

public class AIController extends GameController {
    AIController(Game game, int turn_num) {
        super(game, turn_num);
    }

    @Override
    public boolean hasLost() {
        return false;
    }

    @Override
    public boolean hasMove(Move move) {
        return true;
    }

    @Override
    public Move getMove() {
        GameTreeNode node = getBestNode();
        if(node != null) {
            return node.move.get();
        }
        return null;
    }

    @Override
    public boolean takeTurn() throws InterruptedException {
        GameTreeNode root = GameTree.get(state);
        signal = new CountDownLatch(1);
        Explorer e = new Explorer(root, signal);
        e.start();
        signal.await();
        e.stop();
        return super.takeTurn();
    }

    @Override
    public GameTreeNode getBestNode() {
        return null;
    }
}
