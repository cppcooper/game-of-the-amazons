package ubc.cosc322;

import java.util.ArrayList;
import java.util.function.*;

public class Analysis {
    public static ArrayList<Integer>[] GetMoveList(LocalState board, BoardPiece[] player_pieces){
        ArrayList<Integer>[] all_moves = new ArrayList[4];
        for(int i = 0; i < 4; ++i){
            all_moves[i] = ScanMoves(board,player_pieces[i]);
        }
        return all_moves;
    }

    protected static ArrayList<Integer> ScanMoves(LocalState board, BoardPiece piece){
        ArrayList<Integer> moves = new ArrayList<>();
        int xi = 0;
        int yi = 0;

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
        int x = piece.x;
        int y = piece.y;
        int index;

        while(x == 1000){
            x += xi;
            y += yi;
        }
        return moves;
    }
}
