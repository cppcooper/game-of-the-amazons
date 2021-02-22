package ubc.cosc322;

import java.util.ArrayList;
import java.util.function.*;

public class Analysis {
    public static ArrayList<Integer>[] GetMoveList(LocalState board, BoardPiece[] player_pieces){
        //todo: type needs to change (I think)
        ArrayList<Integer>[] all_moves = new ArrayList[4];
        //todo: parallelize below this using a sync-list. At this level, just refactor and pass the element(?) pass by value.. will it work?
        for(int i = 0; i < 4; ++i){
            all_moves[i] = ScanMoves(board,player_pieces[i]);
        }
        return all_moves;
    }

    protected static ArrayList<Integer> ScanMoves(LocalState board, BoardPiece piece){
        ArrayList<Integer> moves = new ArrayList<>();
        int xi = 0;
        int yi = 0;

        //todo: launch all of these as threads
        xi = 1; yi = 1;
        moves.addAll(ScanMoves(board,piece,xi,yi));
        xi = -1; yi = -1;
        moves.addAll(ScanMoves(board,piece,xi,yi));

        xi = 1; yi = 0;
        moves.addAll(ScanMoves(board,piece,xi,yi));
        xi = -1; yi = 0;
        moves.addAll(ScanMoves(board,piece,xi,yi));

        xi = 0; yi = 1;
        moves.addAll(ScanMoves(board,piece,xi,yi));
        xi = 0; yi = -1;
        moves.addAll(ScanMoves(board,piece,xi,yi));

        xi = 1; yi = -1;
        moves.addAll(ScanMoves(board,piece,xi,yi));
        xi = -1; yi = 1;
        moves.addAll(ScanMoves(board,piece,xi,yi));

        return moves;
    }

    protected static ArrayList<Integer> ScanMoves(LocalState board, BoardPiece piece, int xi, int yi){
        ArrayList<Integer> moves = new ArrayList<>();
        int x = piece.x + xi;
        int y = piece.y + yi;
        Function<Integer, Boolean> check_in_range = (v) -> {
            return (v < 11 && v > 0);
        };
        while(check_in_range.apply(x) && check_in_range.apply(y)){
            if(board.ReadTile(x,y) != 0){
                break;
            }
            moves.add((x*11)+y);
            x += xi;
            y += yi;
        }
        return moves;
    }
}
