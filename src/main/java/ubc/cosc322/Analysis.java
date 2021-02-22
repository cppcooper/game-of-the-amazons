package ubc.cosc322;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class Analysis {
    static private ExecutorService thread_manager = Executors.newCachedThreadPool(); //Supposedly good for short lived concurrent tasks

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

        //todo: launch all of these as threads
        xi = 1; yi = 1;
        ScanMoves(moves,board,piece,xi,yi);
        xi = -1; yi = -1;
        ScanMoves(moves,board,piece,xi,yi);

        xi = 1; yi = 0;
        ScanMoves(moves,board,piece,xi,yi);
        xi = -1; yi = 0;
        ScanMoves(moves,board,piece,xi,yi);

        xi = 0; yi = 1;
        ScanMoves(moves,board,piece,xi,yi);
        xi = 0; yi = -1;
        ScanMoves(moves,board,piece,xi,yi);

        xi = 1; yi = -1;
        ScanMoves(moves,board,piece,xi,yi);
        xi = -1; yi = 1;
        ScanMoves(moves,board,piece,xi,yi);

        return moves;
    }

    protected static void ScanMoves(ArrayList<Integer> moves, LocalState board, BoardPiece piece, int xi, int yi){
        int x = piece.x + xi;
        int y = piece.y + yi;
        Function<Integer, Boolean> check_in_range = (v) -> {
            return (v < 11 && v > 0);
        }; //hoping the JVM is gonna inline this =)
        while(check_in_range.apply(x) && check_in_range.apply(y)){
            if(board.ReadTile(x,y) != 0){
                break;
            }
            moves.add((x*11)+y);
            x += xi;
            y += yi;
        }
    }

    ///Threaded variants
    //todo: benchmark threaded vs non-threaded

    public static ArrayList<Integer>[] GetMoveListThreaded(LocalState board, BoardPiece[] player_pieces) throws ExecutionException, InterruptedException {
        ArrayList<Integer>[] all_moves = new ArrayList[4];
        Future<ArrayList<Integer>>[] ret_values = new Future[4];
        for(int i = 0; i < 4; ++i){
            BoardPiece piece = player_pieces[i];
            ret_values[i] = thread_manager.submit(() -> ScanMovesThreaded(board,piece));
        }
        for(int i = 0; i < 4; ++i){
            all_moves[i] = ret_values[i].get();
        }
        return all_moves;
    }

    protected static ArrayList<Integer> ScanMovesThreaded(LocalState board, BoardPiece piece){
        ArrayList<Integer> moves = new ArrayList<>();
        ArrayList<Future<ArrayList<Integer>>> ret_values = new ArrayList<>(8);

        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,1,1)));
        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,-1,-1)));

        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,1,0)));
        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,-1,0)));

        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,0,1)));
        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,0,-1)));

        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,1,-1)));
        ret_values.add(thread_manager.submit(() -> ScanMoves4Threads(board,piece,-1,1)));

        ret_values.stream().parallel()
            .map(f -> {
                try {
                    return f.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            })
            .map(c -> (List<Integer>)c)
            .reduce(Collections.synchronizedList(moves),
                (v1,v2) -> {
                    v1.addAll(v2);
                    return v1;
            });
        return moves;
    }

    protected static ArrayList<Integer> ScanMoves4Threads(LocalState board, BoardPiece piece, int xi, int yi){
        ArrayList<Integer> moves = new ArrayList<>();
        int x = piece.x + xi;
        int y = piece.y + yi;
        Function<Integer, Boolean> check_in_range = (v) -> {
            return (v < 11 && v > 0);
        }; //hoping the JVM is gonna inline this =)
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
