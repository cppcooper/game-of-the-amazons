package controllers;

import data.pod.BoardPiece;
import data.pod.Move;
import data.structures.GameState;
import data.parallel.GameTreeNode;
import ygraph.ai.smartfox.games.amazons.OurGameGUI;

import java.util.concurrent.CountDownLatch;

public abstract class GameController {
    private final int turn_order;
    protected boolean is_my_turn;
    protected final GameState state;
    protected final OurGameGUI gui;
    protected final BoardPiece[] pieces;
    protected CountDownLatch signal;

    protected GameController(GameState state, OurGameGUI gui, int turn_order){
        this.gui = gui;
        this.state = state;
        pieces = state.findPieces(turn_order);
        is_my_turn = turn_order == 1;
        this.turn_order = turn_order;
        //game.addNewTurnHandler(this::handleNewTurn);
    }

    public GameState getBoardState() { return state; }
    public int getPlayerNumber() { return turn_order; }
    public boolean isMyTurn() { return is_my_turn; }

    public abstract boolean hasMove(Move move);
    public abstract Move getMove() throws InterruptedException;
    public abstract GameTreeNode getBestNode() throws InterruptedException;
    public abstract boolean takeTurn() throws InterruptedException;
    public BoardPiece[] getPlayerPieces() {
        return pieces;
    }
}
