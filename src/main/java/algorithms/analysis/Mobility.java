package algorithms.analysis;

import algorithms.search.MoveCompiler;
import data.BoardPiece;
import data.structures.GameState;

public class Mobility {
    private static final int max_moves = 4*35*35;

    public static double CalculateHeuristic(GameState board){
        return (CalculateMobilityHeuristic(board) + CalculateReductionHeuristic(board)) / 2.0;
    }

    public static double CalculateMobilityHeuristic(GameState board){
        return (double)count_first_degree_moves(board, board.GetPrevTurnPieces()) / max_moves;
    }

    public static double CalculateReductionHeuristic(GameState board){
        return 1 - ((double)count_first_degree_moves(board, board.GetTurnPieces()) / max_moves);
    }

    private static int count_first_degree_positions(GameState board, BoardPiece[] pieces) {
        int[] positions = new int[pieces.length];
        for (int i = 0; i < 4; ++i) {
            int index = pieces[i].CalculateIndex();
            positions[i] = index;
        }
        int moves = 0;
        int[][] first_degree_positions = MoveCompiler.GetOpenPositions(board, positions,false);
        for (int[] scan_direction : first_degree_positions) {
            if (scan_direction != null) {
                for (int tile_index : scan_direction) {
                    if (tile_index < 0) {
                        break;
                    }
                    moves++;
                }
            }
        }
        return moves;
    }

    private static int count_first_degree_moves(GameState board, BoardPiece[] pieces) {
        return MoveCompiler.GetMoveList(board, pieces,true, false).size();
    }
}
