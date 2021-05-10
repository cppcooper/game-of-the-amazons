package controllers;

import algorithms.search.MoveCompiler;
import algorithms.search.MoveValidator;
import data.pod.BoardPiece;
import data.pod.Move;
import data.parallel.GameTreeNode;
import data.structures.GameState;
import main.Game;
import ygraph.ai.smartfox.games.amazons.OurGameGUI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CountDownLatch;

public class HumanController extends GameController {
    public HumanController(GameState state, OurGameGUI gui, int turn_num) {
        super(state, gui, turn_num);
        for(int idx : MoveCompiler.GetAllValidPositions()){
            HumanMoveHandler moveHandler = new HumanMoveHandler(idx, this);
            gui.setTHandler(idx, moveHandler);
        }
    }

    @Override
    public boolean hasMove(Move move) {
        return MoveValidator.verify(move, this);
    }

    @Override
    public Move getMove() throws InterruptedException {
        return HumanMoveHandler.move;
    }

    @Override
    public GameTreeNode getBestNode() throws InterruptedException  {
        return null;
    }

    @Override
    public boolean takeTurn() throws InterruptedException {
        is_my_turn = true;
        signal = new CountDownLatch(1);
        signal.await();
        Move move = getMove();
        if (isMyTurn() && hasMove(move)) {
            is_my_turn = false; //disallow more user input
            Game.Get().apply(move);
            for(BoardPiece p : pieces){
                if(p.getIndex() == move.start){
                    p.moveTo(move.next);
                    break;
                }
            }
            return true;
        }
        return false;
    }

    private static class HumanMoveHandler extends MouseAdapter {
        static Move move = new Move();
        static int count = 0;
        private int index;
        private HumanController pc;

        HumanMoveHandler(int index, HumanController pc) {
            this.index = index;
            this.pc = pc;
        }

        public void mousePressed(MouseEvent e) {
            if(pc.isMyTurn()) {
                if (count == 0 && pc.state.readTile(index) == pc.getPlayerNumber()) {
                    move = new Move();
                    move.start = index;
                    if(MoveValidator.verify(move, pc)){
                        count++;
                    } else {
                        System.out.println("Invalid selection");
                    }
                } else if (count == 1 && index != move.start) {
                    move.next = index;
                    if(MoveValidator.verify(move, pc)){
                        count++;
                    } else {
                        System.out.println("Invalid selection");
                    }
                } else if (count == 2 && index != move.next) {
                    move.arrow = index;
                    if(MoveValidator.verify(move, pc)){
                        count++;
                    } else {
                        System.out.println("Invalid selection");
                    }
                }

                if (count == 3) {
                    count = 0;
                    if(pc.signal != null) {
                        pc.signal.countDown();
                    }
                }
            }
        }
    }
}
