package algorithms;

import structures.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class Analysis {
    static private ExecutorService thread_manager = Executors.newCachedThreadPool();

    // To find all of one player's move options you calculate `pieces x positions x arrows` = 4*40*40 = 6400 max options/operations
    public static ArrayList<Move> GetMoveList(LocalState board, BoardPiece[] player_pieces){
        //todo: Can probably find a faster average way to pre-allocate
        ArrayList<Move> all_moves = new ArrayList<>(6400);
        int[] piece_indices = new int[4];
        for(int i = 0; i < piece_indices.length; ++i){
            piece_indices[i] = player_pieces[i].pos.CalculateIndex();
        }
        // Getting the available positions for a piece to move to
        int [][] open_piece_positions = GetOpenPositions(board,piece_indices); //values of -1 are invalid elements, to be ignored
        for(int piece_i = 0; piece_i < piece_indices.length; ++piece_i){
            // Getting the available positions for an arrow from all the positions a piece could shoot from
            int[][] all_arrow_positions = GetOpenPositions(board,open_piece_positions[piece_i]);
            //Time to construct Moves
            //but first, check that the open positions for this piece isn't null/empty
            if(open_piece_positions[piece_i] != null) {
                //start iterating position/arrow combinations
                for (int position_j = 0; position_j < open_piece_positions[piece_i].length; ++position_j) {
                    for (int arrow_k = 0; arrow_k < all_arrow_positions[position_j].length; ++arrow_k) {
                        //check that the arrow array for this position isn't null/empty
                        if (all_arrow_positions[position_j] != null) {
                            all_moves.add(new Move(piece_indices[piece_i], open_piece_positions[piece_i][position_j], all_arrow_positions[position_j][arrow_k]));
                        }
                    }
                }
            }
        }
        return all_moves;
    }

    //40 bottom level operations for every starting position.
    public static int[][] GetOpenPositions(LocalState board, int[] starting_positions){
        if(starting_positions != null) {
            int[][] all_moves = new int[starting_positions.length][];
            for (int i = 0; i < starting_positions.length; ++i) {
                if (starting_positions[i] < 0) {
                    break; // -1 marks the end of valid values
                }
                all_moves[i] = ScanAllDirections(board, starting_positions[i]);
            }
            // all the null arrays are at the end of our array [of arrays]
            return all_moves;
        }
        // the array we received was null
        return null;
    }

    ///Helper functions

    //this will always be faster than a parallel version for the board size we have
    protected static int[] ScanAllDirections(LocalState board, int index){
        int[] moves = new int[40];
        Position pos = new Position(index); //JVM should optimize this to be in the stack memory

        int starting_index = 0;
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,0);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,1,0);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,0,1);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,0,-1);

        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,1,1);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,-1);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,1,-1);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,1);
        if(starting_index < 40){
            moves[starting_index] = -1; //consider this to be null termination of the array
        }

        return moves;
    }

    //this has been optimized to death
    protected static int ScanDirection(int[] moves, int start_index, LocalState board, int x, int y, int xi, int yi){
        x += xi;
        y += yi;
        int i = start_index;
        Function<Integer, Boolean> check_in_range = (v) -> {
            return (v < 11 && v > 0);
        }; //JVM should inline this =)
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
// This is not faster, but the code is useful for syntax lookup purposes
//    public static ArrayList<Integer>[] GetMoveListThreaded(structures.LocalState board, structures.BoardPiece[] player_pieces) throws ExecutionException, InterruptedException {
//        ArrayList<Integer>[] all_moves = new ArrayList[4];
//        Future<ArrayList<Integer>>[] ret_values = new Future[4];
//        for(int i = 0; i < 4; ++i){
//            structures.BoardPiece piece = player_pieces[i];
//            ret_values[i] = thread_manager.submit(() -> ScanMoves(board,piece.pos));
//        }
//        for(int i = 0; i < 4; ++i){
//            all_moves[i] = ret_values[i].get();
//        }
//        return all_moves;
//    }
