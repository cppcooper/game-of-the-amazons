package algorithms.search;

import data.pod.BoardPiece;
import data.pod.Move;
import data.pod.Position;
import data.structures.GameState;
import data.structures.MovePool;
import main.Game;
import tools.Tuner;

import java.util.*;
import java.util.function.*;

//todo: reduce
public class MoveCompiler {
    // To find all of one player's move options you calculate `pieces x positions x arrows` = 4*40*40 = 6400 max options/operations
    public static ArrayList<Move> compileList(GameState board, boolean use_pooling){
        return GetMoveList(board, Game.Get().getTurnPieces(board.getRoundNum()), use_pooling,true);
    }

    public static ArrayList<Move> GetMoveList(GameState board, BoardPiece[] player_pieces, boolean use_pooling){
        return GetMoveList(board,player_pieces,use_pooling,true);
    }

    public static ArrayList<Move> GetMoveList(GameState board, int[] piece_indices, boolean use_pooling){
        return GetMoveList(board,piece_indices,use_pooling,true);
    }

    public static ArrayList<Move> GetMoveList(GameState board, BoardPiece[] player_pieces, boolean use_pooling, boolean use_interrupts) {
        int[] piece_indices = new int[4];
        for (int i = 0; i < piece_indices.length; ++i) {
            piece_indices[i] = player_pieces[i].getIndex();
        }
        return GetMoveList(board,piece_indices,use_pooling,use_interrupts);
    }

    public static ArrayList<Move> GetMoveList(GameState board, int[] piece_indices, boolean use_pooling, boolean use_interrupts){
        ArrayList<Move> all_moves = new ArrayList<>(5000);
        // Getting the available positions for a piece to move to
        int [][] open_piece_positions = GetOpenPositions(board,piece_indices,use_interrupts); //values of -1 are invalid elements, to be ignored
        if(use_interrupts && Thread.currentThread().isInterrupted()) {
            return null; // there are no moves to return yet
        }
        for(int piece_i = 0; piece_i < piece_indices.length; ++piece_i){
            // Getting the available positions for an arrow from all the positions a piece could shoot from
            GameState copy = new GameState(board);
            copy.setTile(piece_indices[piece_i],0);
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
    public static int[][] GetOpenPositions(GameState board, int[] starting_positions, boolean use_interrupts){
        if(starting_positions != null) {
            int[][] open_positions = new int[starting_positions.length][];
            for (int i = 0; i < starting_positions.length; ++i) {
                if (starting_positions[i] < 0 || (use_interrupts && Thread.currentThread().isInterrupted())){
                    break; // -1 marks the end of valid values
                }
                open_positions[i] = ScanAllDirections(board, starting_positions[i], use_interrupts);
            }
            // all the null arrays are at the end of our array [of arrays]
            return open_positions;
        }
        // the array we received was null
        return null;
    }

    //this will always be faster than a parallel version for the board size we have
    public static int[] ScanAllDirections(GameState board, int index, boolean use_interrupts){
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
    public static int ScanDirection(int[] moves, int start_index, GameState board, int x, int y, int xi, int yi, boolean use_interrupts){
        if(use_interrupts && Thread.currentThread().isInterrupted()){
            return 0;
        }

        x += xi;
        y += yi;
        int i = start_index;
        Function<Integer, Boolean> check_in_range = (v) -> (v <= Tuner.coord_max && v >= Tuner.coord_min); //JVM should inline this =)
        while(check_in_range.apply(x) && check_in_range.apply(y)){
            if(use_interrupts && Thread.currentThread().isInterrupted()){
                break;
            }
            int index = Position.calculateIndex(x,y);
            if(board.readTile(index) != 0){
                break;
            }
            moves[i++] = index;
            x += xi;
            y += yi;
        }
        return i;
    }

    public static Position[] GetNeighbours(int index){
        if(index >= 0 && index < Tuner.state_size) {
            Position[] neighbours = new Position[8];
            neighbours[0] = new Position(index - 1);
            neighbours[1] = new Position(index + 1);
            neighbours[2] = new Position(index - Tuner.coord_upper - 1);
            neighbours[3] = new Position(index - Tuner.coord_upper);
            neighbours[4] = new Position(index - Tuner.coord_upper + 1);
            neighbours[5] = new Position(index + Tuner.coord_upper - 1);
            neighbours[6] = new Position(index + Tuner.coord_upper);
            neighbours[7] = new Position(index + Tuner.coord_upper + 1);
            return neighbours;
        }
        return null;
    }

    public static int[] ConvertPositions(Position[] positions){
        int valid_count = 0;
        for(Position p : positions){
            if(p.isValid()){
                valid_count++;
            }
        }
        int[] converted = new int[valid_count];
        int i = 0;
        for(Position p : positions){
            if(p.isValid()){
                converted[i++] = p.getIndex();
            }
        }
        return converted;
    }

    public static int[] GetAllValidPositions() {
        int[] positions = new int[100];
        int j = 0;
        for (int y = Tuner.coord_min; y <= Tuner.coord_max; ++y) {
            for (int x = Tuner.coord_min; x <= Tuner.coord_max; ++x) {
                Position p = new Position(x, y);
                if (p.isValid()) {
                    positions[j++] = p.getIndex();
                }
            }
        }
        return positions;
    }

    public static int[] GetEmptyTiles(GameState board){
        int[] valid_tiles = GetAllValidPositions();
        for(int tile = 0; tile < valid_tiles.length; ++tile){
            if(board.readTile(tile) != 0){
                valid_tiles[tile] = -1;
            }
        }
        return Arrays.stream(valid_tiles).filter((tile)->tile >= 0).toArray();
    }
}