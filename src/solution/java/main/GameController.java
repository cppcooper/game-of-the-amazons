package main;

import data.pod.BoardPiece;
import data.pod.Move;
import data.structures.GameState;
import data.parallel.GameTreeNode;
import ygraph.ai.smartfox.games.amazons.OurGameGUI;

import java.util.concurrent.CountDownLatch;

public abstract class GameController {
    private final int turn_order;
    private boolean is_my_turn;
    protected final GameState state;
    protected final OurGameGUI gui;
    protected final BoardPiece[] pieces;
    protected CountDownLatch signal;

    public GameController(Game game, int turn_order){
        gui = game.gui;
        state = game.state;
        pieces = state.findPieces(turn_order);
        is_my_turn = turn_order == 1;
        this.turn_order = turn_order;
        //game.addNewTurnHandler(this::handleNewTurn);
    }

    public GameState getBoardState() { return state; }
    public int getPlayerNumber() { return turn_order; }
    public boolean isMyTurn() { return is_my_turn; }
    public abstract boolean hasLost();
    public abstract boolean hasMove(Move move);
    public abstract Move getMove();
    public boolean takeTurn() throws InterruptedException {
        is_my_turn = true;
        signal = new CountDownLatch(1);
        signal.await();
        Move move = getMove();
        if (isMyTurn() && hasMove(move)) {
            Game.Get().apply(move);
            is_my_turn = false;
            for(BoardPiece p : pieces){
                if(p.getIndex() == move.start){
                    p.moveTo(move.start);
                    break;
                }
            }
            return true;
        }
        return false;
    }
    public abstract GameTreeNode getBestNode();
    public BoardPiece[] getPlayerPieces() {
        return pieces;
    }
}
