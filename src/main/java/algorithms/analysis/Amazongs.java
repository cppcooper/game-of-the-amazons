package algorithms.analysis;

import algorithms.search.MoveCompiler;
import data.BoardPiece;
import data.structures.GameState;
import data.Position;
import org.junit.jupiter.api.Test;
import tools.Maths;
import tools.RandomGen;
import tools.Tuner;

import java.util.Arrays;

public class Amazongs {
    static class UberDistanceData {
        public DistanceData[] p1;
        public DistanceData[] p2;
        public DistanceData p1_best;
        public DistanceData p2_best;

        UberDistanceData(DistanceData[] p1, DistanceData[] p2) {
            this.p1 = p1;
            this.p2 = p2;
            p1_best = new DistanceData();
            p2_best = new DistanceData();
            for (int piece = 0; piece < p1.length; ++piece) {
                for (int tile = 0; tile < Tuner.state_size; ++tile) {
                    double p1b1 = p1_best.queen_distances[tile];
                    double p2b1 = p2_best.queen_distances[tile];
                    double p1b2 = p1_best.king_distances[tile];
                    double p2b2 = p2_best.king_distances[tile];
                    double p11 = p1[piece].queen_distances[tile];
                    double p21 = p2[piece].queen_distances[tile];
                    double p12 = p1[piece].king_distances[tile];
                    double p22 = p2[piece].king_distances[tile];
                    if (p11 < p1b1) {
                        p1_best.queen_distances[tile] = p11;
                    }
                    if (p12 < p1b2) {
                        p1_best.king_distances[tile] = p12;
                    }
                    if (p21 < p2b1) {
                        p2_best.queen_distances[tile] = p21;
                    }
                    if (p22 < p2b2) {
                        p2_best.king_distances[tile] = p22;
                    }
                }
            }
        }
    }

    static class DistanceData {
        public double[] queen_distances = new double[Tuner.state_size];
        public double[] king_distances = new double[Tuner.state_size];
        public DistanceData(){
            Arrays.fill(queen_distances, Double.POSITIVE_INFINITY);
            Arrays.fill(king_distances, Double.POSITIVE_INFINITY);
        }
    }

    public static double CalculateHeuristic(GameState board) {
        UberDistanceData data = calculate_all_distances(board);
        final int num_pieces = data.p1.length;
        int[] valid_tiles = MoveCompiler.GetAllValidPositions();
        double c1 = 0;
        double c2 = 0;
        double w = 0;
        double t1 = 0;
        double t2 = 0;
        for (int tile : valid_tiles) {
            double p1_d1 = data.p1_best.queen_distances[tile];
            double p2_d1 = data.p2_best.queen_distances[tile];
            double dd1 = data.p1_best.queen_distances[tile] - data.p2_best.queen_distances[tile];
            double dd2 = p1_d1 - p2_d1;
            t1 += dd1;
            c1 += Math.pow(2, -p1_d1) - Math.pow(2, -p2_d1);
            c2 += Math.min(1, Math.max(-1, dd2 / -6.0));
            if (Double.isFinite(dd1)) {
                w += Math.pow(2, -Math.abs(dd1));
            }
            t2 += dd2;
        }
        c1 *= 2;
        double term1 = Maths.f1(w) * t1;
        double term2 = Maths.f2(w) * c1;
        double term3 = Maths.f3(w) * c2;
        double term4 = Maths.f4(w) * t2;
        double t = term1 + term2 + term3 + term4;
        double[] p1_a = new double[num_pieces];
        double[] p2_a = new double[num_pieces];
        for (int piece = 0; piece < num_pieces; ++piece) {
            p1_a[piece] = 0;
            p2_a[piece] = 0;
            for (int tile : valid_tiles) {
                int N_b = count_neighbours(board, tile);
                if (data.p1[piece].queen_distances[tile] == 1 && data.p2_best.queen_distances[tile] != 0) {
                    p1_a[piece] += Math.pow(2, -data.p1[piece].king_distances[tile]) * N_b;
                }
                if (data.p2[piece].queen_distances[tile] == 1 && data.p1_best.queen_distances[tile] != 0) {
                    p2_a[piece] += Math.pow(2, -data.p2[piece].king_distances[tile]) * N_b;
                }
            }
        }
        double p1a = 0;
        double p2a = 0;
        for (int i = 0; i < num_pieces; ++i) {
            p1a += p1_a[i];
            p2a += p2_a[i];
        }
        double m = Maths.sumf(w, p2_a) - Maths.sumf(w, p1_a);
        double h = t + m;
        return h;
    }

    static UberDistanceData calculate_all_distances(GameState board) {
        BoardPiece[] p1 = board.GetPlayerPieces(1);
        BoardPiece[] p2 = board.GetPlayerPieces(2);
        return new UberDistanceData(calculate_distances(board, p1), calculate_distances(board, p2));
    }


    private static DistanceData[] calculate_distances(GameState board, BoardPiece[] pieces) {
        DistanceData[] territory = new DistanceData[pieces.length];
        int[] position = new int[1];
        for (int i = 0; i < territory.length; ++i) {
            territory[i] = new DistanceData();
            position[0] = pieces[i].CalculateIndex();
            territory[i].king_distances[position[0]] = 0;
            territory[i].queen_distances[position[0]] = 0;
            find_best_king_distances(board, position, territory[i].king_distances, 1);
            find_best_queen_distances(board, position, territory[i].queen_distances, 1);
        }
        return territory;
    }

    private static void find_best_king_distances(GameState board, int[] starting_positions, double[] distance_map, int distance) {
        int[] next_positions = new int[Tuner.state_size];
        Arrays.fill(next_positions, -1);
        for (int index : starting_positions) {
            if (index < 0) {
                continue;
            }
            Position[] neighbours = MoveCompiler.GetNeighbours(index);
            if (neighbours != null) {
                for (Position n : neighbours) {
                    int n_index = n.CalculateIndex();
                    if (n.IsValid() && board.ReadTile(n_index) == 0 && distance < distance_map[n_index]) {
                        next_positions[n.CalculateIndex()] = n.CalculateIndex();
                        distance_map[n_index] = distance;
                    }
                }
            }
        }
        if (distance < 20) {
            find_best_king_distances(board, next_positions, distance_map, distance + 1);
        }
    }

    private static void find_best_queen_distances(GameState board, int[] starting_positions, double[] distance_map, int distance) {
        int[][] new_positions = MoveCompiler.GetOpenPositions(board, starting_positions, false); //[starting index][index of open positions]
        for (int[] position_list : new_positions) {
            if (position_list != null) {
                for (int index : position_list) {
                    if (index == -1) {
                        break;
                    }
                    if (distance < distance_map[index]) {
                        distance_map[index] = distance;
                    }
                }
            }
        }

        for (int[] position_list : new_positions) {
            if (position_list != null) {
                find_best_queen_distances(board, prune_positions(position_list, distance_map, distance), distance_map, distance + 1);
            }
        }
    }

    private static int[] prune_positions(int[] positions, double[] distance_map, int distance) {
        int[] pruned_positions = new int[positions.length];
        int i = 0;
        for (int index : positions) {
            if (index < 0) {
                break;
            }
            if (distance < distance_map[index]) {
                pruned_positions[i++] = index;
            }
        }
        if (i < pruned_positions.length) {
            pruned_positions[i] = -1;
        }
        return pruned_positions;
    }

    private static int count_neighbours(GameState board, int tile) {
        Position[] neighbours = MoveCompiler.GetNeighbours(tile);
        int count = 0;
        for (Position n : neighbours) {
            if (n.IsValid() && board.ReadTile(n.CalculateIndex()) == 0) {
                count++;
            }
        }
        return count;
    }

    @Test // 0..75 (roughly)
    void w_values() {
        final RandomGen rng = new RandomGen();
        final int trials = 1000000;
        double max = Double.NEGATIVE_INFINITY;
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < trials; ++i) {
            GameState board;
            if (i == 0) {
                board = new GameState();
            } else {
                board = rng.GetRandomBoard(0.45);
            }
            UberDistanceData data = Amazongs.calculate_all_distances(board);
            int[] valid_tiles = MoveCompiler.GetAllValidPositions();
            double w = 0;
            for (int tile : valid_tiles) {
                double p1_d1 = data.p1_best.queen_distances[tile];
                double p2_d1 = data.p2_best.queen_distances[tile];
                double dd1 = p1_d1 - p2_d1;
                if (Double.isFinite(dd1)) {
                    w += Math.pow(2, -Math.abs(dd1));
                }
            }

            boolean new_value = false;
            if (w > max) {
                new_value = true;
                max = w;
            }
            if (w < min) {
                new_value = true;
                min = w;
            }
            if (new_value) {
                board.DebugPrint();
                System.out.printf("--------\nnew min: %.3f\nnew max: %.3f\n", min, max);
            }
        }
        System.out.printf("min: %.3f\nmax: %.3f\n", min, max);
    }
}
