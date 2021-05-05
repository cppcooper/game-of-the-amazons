package main;

import algorithms.search.MoveCompiler;
import algorithms.search.MoveValidator;
import data.pod.Move;
import data.parallel.GameTreeNode;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HumanController extends GameController{
    HumanController(Game game, int turn_num) {
        super(game, turn_num);
        for(int idx : MoveCompiler.GetAllValidPositions()){
            HumanMoveHandler moveHandler = new HumanMoveHandler(idx, this);
            gui.setTHandler(idx, moveHandler);
        }
    }

    @Override
    public boolean hasLost() {
        return false;
    }

    @Override
    public boolean hasMove(Move move) {
        return MoveValidator.verify(move, this);
    }

    @Override
    public Move getMove() {
        return HumanMoveHandler.move;
    }

    @Override
    public GameTreeNode getBestNode() {
        return null;
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
                    move.start = index;
                    if(MoveValidator.verify(move, pc)){
                        count++;
                    }
                } else if (count == 1 && index != move.start) {
                    move.next = index;
                    if(MoveValidator.verify(move, pc)){
                        count++;
                    }
                } else if (count == 2 && index != move.next) {
                    move.arrow = index;
                    if(MoveValidator.verify(move, pc)){
                        count++;
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
