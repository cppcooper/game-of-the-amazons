package algorithms.analysis;

import structures.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

public class MoveCompiler {
    // To find all of one player's move options you calculate `pieces x positions x arrows` = 4*40*40 = 6400 max options/operations
    public static ArrayList<Move> GetMoveList(LocalState board, BoardPiece[] player_pieces, boolean use_pooling){
        return GetMoveList(board,player_pieces,use_pooling,true);
    }

    public static ArrayList<Move> GetMoveList(LocalState board, int[] piece_indices, boolean use_pooling){
        return GetMoveList(board,piece_indices,use_pooling,true);
    }

    public static ArrayList<Move> GetMoveList(LocalState board, BoardPiece[] player_pieces, boolean use_pooling, boolean use_interrupts) {
        int[] piece_indices = new int[4];
        for (int i = 0; i < piece_indices.length; ++i) {
            piece_indices[i] = player_pieces[i].CalculateIndex();
        }
        return GetMoveList(board,piece_indices,use_pooling,use_interrupts);
    }

    public static ArrayList<Move> GetMoveList(LocalState board, int[] piece_indices, boolean use_pooling, boolean use_interrupts){
        ArrayList<Move> all_moves = new ArrayList<>(5000);
        // Getting the available positions for a piece to move to
        int [][] open_piece_positions = GetOpenPositions(board,piece_indices,use_interrupts); //values of -1 are invalid elements, to be ignored
        if(use_interrupts && Thread.currentThread().isInterrupted()) {
            return null; // there are no moves to return yet
        }
        for(int piece_i = 0; piece_i < piece_indices.length; ++piece_i){
            // Getting the available positions for an arrow from all the positions a piece could shoot from
            LocalState copy = new LocalState(board);
            copy.SetTile(piece_indices[piece_i],0);
            int[][] all_arrow_positions = GetOpenPositions(copy,open_piece_positions[piece_i],use_interrupts);
            if(use_interrupts && Thread.currentThread().isInterrupted()){
                return null; // there are no moves to return yet
            }
            //Time to construct Moves
            //but first, check that the open positions for this piece isn't null/empty
            if(open_piece_positions[piece_i] != null) {
                //start iterating position/arrow combinations
                for (int position_j = 0; position_j < open_piece_positions[piece_i].length; ++position_j) {
                    if(open_piece_positions[piece_i][position_j] < 0){
                        break;
                    }
                    for (int arrow_k = 0; arrow_k < all_arrow_positions[position_j].length; ++arrow_k) {
                        if(all_arrow_positions[position_j][arrow_k] < 0){
                            break;
                        }
                        if(use_interrupts && Thread.currentThread().isInterrupted()){
                            return null; // the caller is not going to be doing anything with the moves anyway
                        }
                        //check that the arrow array for this position isn't null/empty
                        if (all_arrow_positions[position_j] != null) {
                            int start = piece_indices[piece_i];
                            int next = open_piece_positions[piece_i][position_j];
                            int arrow = all_arrow_positions[position_j][arrow_k];
                            if(use_pooling) {
                                all_moves.add(MovePool.get(start, next, arrow));
                            } else {
                                all_moves.add(new Move(piece_indices[piece_i], open_piece_positions[piece_i][position_j], all_arrow_positions[position_j][arrow_k]));
                            }
                        }
                    }
                }
            }
        }
        return all_moves;
    }

    //40 bottom level operations for every starting position.
    public static int[][] GetOpenPositions(LocalState board, int[] starting_positions, boolean use_interrupts){
        if(starting_positions != null) {
            int[][] all_moves = new int[starting_positions.length][];
            for (int i = 0; i < starting_positions.length; ++i) {
                if (starting_positions[i] < 0 || (use_interrupts && Thread.currentThread().isInterrupted())){
                    break; // -1 marks the end of valid values
                }
                all_moves[i] = ScanAllDirections(board, starting_positions[i],use_interrupts);
            }
            // all the null arrays are at the end of our array [of arrays]
            return all_moves;
        }
        // the array we received was null
        return null;
    }

    //this will always be faster than a parallel version for the board size we have
    public static int[] ScanAllDirections(LocalState board, int index, boolean use_interrupts){
        int[] moves = new int[40];
        Position pos = new Position(index); //JVM should optimize this to be in the stack memory

        int starting_index = 0;
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,0,use_interrupts);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,1,0,use_interrupts);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,0,1,use_interrupts);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,0,-1,use_interrupts);

        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,1,1,use_interrupts);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,-1,use_interrupts);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,1,-1,use_interrupts);
        starting_index = ScanDirection(moves,starting_index,board,pos.x,pos.y,-1,1,use_interrupts);
        if(starting_index < 40){
            moves[starting_index] = -1; //consider this to be null termination of the array
        }

        return moves;
    }

    //this has been optimized to death
    public static int ScanDirection(int[] moves, int start_index, LocalState board, int x, int y, int xi, int yi, boolean use_interrupts){
        if(use_interrupts && Thread.currentThread().isInterrupted()){
            return 0;
        }

        x += xi;
        y += yi;
        int i = start_index;
        Function<Integer, Boolean> check_in_range = (v) -> (v < 11 && v > 0); //JVM should inline this =)
        while(check_in_range.apply(x) && check_in_range.apply(y)){
            if(use_interrupts && Thread.currentThread().isInterrupted()){
                break;
            }
            int index = Position.CalculateIndex(x,y);
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