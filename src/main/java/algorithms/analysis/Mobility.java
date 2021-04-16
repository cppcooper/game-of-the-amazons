package algorithms.analysis;

import algorithms.search.MoveCompiler;
import data.pod.BoardPiece;
import data.structures.GameState;
import tools.Maths;

public class Mobility {
    private static final int max_moves = 4*35*35;

    public static double CalculateHeuristic(GameState board){
        double r = CalculateReductionHeuristic(board);
        double f = CalculateFreedomHeuristic(board);
        double p = board.GetMoveNumber() / 92.0;
        r = (Maths.lerp(r,f,1-p) + r) / 2.0;
        f = Maths.lerp(f,r,p);
        return r+f;
    }

    public static double CalculateFreedomHeuristic(GameState board){
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
