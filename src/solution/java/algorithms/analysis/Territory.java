package algorithms.analysis;

import algorithms.search.MoveCompiler;
import data.pod.BoardPiece;
import data.structures.GameState;
import main.Game;
import tools.Tuner;

public class Territory {

    private static class TerritoryData {
        double ours = 0;
        double theirs = 0;

        TerritoryData(double ours, double theirs) {
            this.ours = ours;
            this.theirs = theirs;
        }

    }

    public static double CalculateHeuristic(GameState board) {
        var counts = calculate_territories(board);
        double total = counts.ours + counts.theirs;
        double heuristic = counts.ours / total;
        //heuristic = Maths.remap_value(heuristic,0, 1,0.07,5);
        return heuristic;
    }

    private static TerritoryData calculate_territories(GameState board) {
        BoardPiece[] pa;
        BoardPiece[] pb;
        pa = board.findPieces(Game.Get().getPrevTurnPlayer(board.getRoundNum()));
        pb = board.findPieces(Game.Get().getTurnPlayer(board.getRoundNum()));
        int[] distance_map_a = calculate_distance_map(board, pa);
        int[] distance_map_b = calculate_distance_map(board, pb);
        double aw = 0;
        double bw = 0;
        for (int tile = 0; tile < distance_map_a.length; tile++) {
            int ad1 = distance_map_a[tile];
            int bd1 = distance_map_b[tile];
            int dd1 = Math.abs(ad1 - bd1);
            if(ad1 > bd1) {
                aw += Math.pow(2, dd1 );
            } else {
                bw += Math.pow(2, dd1 );
            }
        }
        return new TerritoryData(aw, bw);
    }

    private static int[] calculate_distance_map(GameState board, BoardPiece[] pieces) {
        int[] distance_map = new int[Tuner.state_size];
        find_shortest_distances(board, MoveCompiler.ConvertPositions(pieces), distance_map, 1);
        return distance_map;
    }

    private static void find_shortest_distances(GameState board, int[] starting_positions, int[] distance_map, int distance) {
        int[][] new_positions = MoveCompiler.GetOpenPositions(board, starting_positions, false); //[starting index][index of open positions]
        for (int[] position_list : new_positions) {
            if (position_list != null) {
                for (int index : position_list) {
                    if (index == -1) {
                        break;
                    }
                    if (distance_map[index] == 0 || distance_map[index] > distance) {
                        distance_map[index] = distance;
                    }
                }
            }
        }

        for (int[] position_list : new_positions) {
            if (position_list != null) {
                find_shortest_distances(board, prune_positions(distance_map, position_list, distance), distance_map, distance + 1);
            }
        }
    }

    private static int[] prune_positions(int[] distance_map, int[] positions, int distance) {
        int[] pruned_positions = new int[positions.length];
        int i = 0;
        for (int index : positions) {
            if (index < 0) {
                break;
            }
            if (distance_map[index] >= distance) {
                pruned_positions[i++] = index;
            }
        }
        if (i < pruned_positions.length) {
            pruned_positions[i] = -1;
        }
        return pruned_positions;
    }

}
