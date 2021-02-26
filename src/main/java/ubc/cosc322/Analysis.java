package ubc.cosc322;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;

public class Analysis {
    static private ExecutorService thread_manager = Executors.newCachedThreadPool();

    //320 bottom level operations -> new positions to look at
    public static ArrayList<Integer>[] GetMoveList(LocalState board, BoardPiece[] player_pieces){
        ArrayList<Integer>[] all_moves = new ArrayList[4];
        for(int i = 0; i < 4; ++i){
            all_moves[i] = ScanMoves(board,player_pieces[i].pos);
        }
        return all_moves;
    }

    //25600 bottom level operations if used on the 320 new positions from above
    //This should hopefully be adequate for parallelization I think splitting into 8 sections should be adequate work for each thread based on benchmarking, but thorough testing is required
    public static ArrayList<Integer>[] GetOpenPositions(LocalState board, ArrayList<Integer> starting_positions){
        ArrayList<Integer>[] all_moves = new ArrayList[starting_positions.size()];
        for(int i = 0; i < starting_positions.size(); ++i){
            all_moves[i] = ScanMoves(board,starting_positions.get(i));
        }
        return all_moves;
    }

    // This is not faster, but the code is useful for syntax lookup purposes
//    public static ArrayList<Integer>[] GetMoveListThreaded(LocalState board, BoardPiece[] player_pieces) throws ExecutionException, InterruptedException {
//        ArrayList<Integer>[] all_moves = new ArrayList[4];
//        Future<ArrayList<Integer>>[] ret_values = new Future[4];
//        for(int i = 0; i < 4; ++i){
//            BoardPiece piece = player_pieces[i];
//            ret_values[i] = thread_manager.submit(() -> ScanMoves(board,piece.pos));
//        }
//        for(int i = 0; i < 4; ++i){
//            all_moves[i] = ret_values[i].get();
//        }
//        return all_moves;
//    }

    ///Helper functions

    protected static ArrayList<Integer> ScanMoves(LocalState board, int index){
        return ScanMoves(board,new Position(index));
    }

    //this will always be faster than a parallel version for the board size we have
    protected static ArrayList<Integer> ScanMoves(LocalState board, Position pos){
        Integer[] moves = new Integer[40];

        int starting_index = 0;
        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,0);
        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,1,0);
        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,0,1);
        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,0,-1);

        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,1,1);
        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,-1);
        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,1,-1);
        starting_index += ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,1);
        for(int i = starting_index; i < 40; ++i){
            moves[i] = -1;
        }

        return new ArrayList<>(Arrays.asList(moves));
    }

    //this has been optimized to death
    protected static int ScanDirection(Integer[] moves, int start_index, LocalState board, int x, int y, int xi, int yi){
        x += xi;
        y += yi;
        int i = start_index;
        Function<Integer, Boolean> check_in_range = (v) -> {
            return (v < 11 && v > 0);
        }; //hoping the JVM is gonna inline this =)
        while(check_in_range.apply(x) && check_in_range.apply(y)){
            int index = (x*11)+y;
            if(board.ReadTile(index) != 0){
                break;
            }
            moves[i++] = index;
            x += xi;
            y += yi;
        }
        return i;
    }
}
