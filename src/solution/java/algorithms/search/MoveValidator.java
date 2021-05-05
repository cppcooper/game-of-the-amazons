package algorithms.search;

import data.pod.Move;
import data.pod.Position;
import controllers.GameController;

public class MoveValidator {
    private GameController controller;
    private MoveValidator(GameController controller){
        this.controller = controller;
    }

    public static boolean verify(Move move, GameController controller){
        return new MoveValidator(controller).validate(move);
    }

    private boolean validate(Move move){
        boolean valid = false;
        for(Position p : controller.getPlayerPieces()){
            if(p.getIndex() == move.start){
                valid = true;
                break;
            }
        }
        if(valid) {
            if (move.next < 0) {
                return true;
            }
            if (hasValidNext(move)) {
                if (move.arrow < 0 || move.arrow == move.start) {
                    return true;
                }
                return hasValidArrow(move);
            }
        }
        return false;
    }

    private boolean hasValidNext(Move move){
        if(move.next >= 0){
            Position start = new Position(move.start);
            Position next = new Position(move.next);
            int dx = next.x - start.x;
            int dy = next.y - start.y;
            if(Math.abs(dx) == Math.abs(dy) || (dx == 0 ^ dy == 0)){
                int xi = Math.min(1,Math.max(-1, dx));
                int yi = Math.min(1,Math.max(-1, dy));
                int[] moves = new int[10];
                MoveCompiler.ScanDirection(moves, 0, controller.getBoardState(), start.x, start.y, xi, yi, false);
                for(int index : moves){
                    if(index == move.next){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasValidArrow(Move move){
        if(move.arrow >= 0){
            Position next = new Position(move.next);
            Position arrow = new Position(move.arrow);
            int dx = arrow.x - next.x;
            int dy = arrow.y - next.y;
            if(Math.abs(dx) == Math.abs(dy) || (dx == 0 ^ dy == 0)){
                int xi = Math.min(1,Math.max(-1, dx));
                int yi = Math.min(1,Math.max(-1, dy));
                int[] moves = new int[10];
                MoveCompiler.ScanDirection(moves, 0, controller.getBoardState(), next.x, next.y, xi, yi, false);
                for(int index : moves){
                    if(index == move.arrow){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
