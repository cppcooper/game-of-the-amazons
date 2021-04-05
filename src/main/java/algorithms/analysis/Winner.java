package algorithms.analysis;

import algorithms.search.MoveCompiler;
import data.BoardPiece;
import data.structures.GameState;
import data.Position;
import tools.Tuner;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class Winner {
    private static class CountingAlgorithmData {
        public int[] visited = new int[Tuner.state_size];
        public Queue<Integer> blankspace = new LinkedList<>();
    }

    public static double CalculateHeuristic(GameState board) {
        if(Tuner.alter_winner_heuristic) {
            int p1 = count_accessible_positions(board, Tuner.our_player_num);
            int p2 = count_accessible_positions(board, 3 - Tuner.our_player_num);
            return p1 > p2 ? p1 - p2 : 0;
        } else {
            int winner = calculate_winner(board);
            if(winner != 0){
                return 2;
            }
            return 1;
        }
    }

    private static int calculate_winner(GameState board) {
        int p1 = count_accessible_positions(board, 1);
        int p2 = count_accessible_positions(board, 2);
        return p1 == p2 ? 0 : (p1 > p2 ? 1 : 2);
    }

    private static int count_accessible_positions(GameState board, int player) {
        int count = 0;
        CountingAlgorithmData data = new CountingAlgorithmData();
        for (BoardPiece p : Objects.requireNonNull(board.GetPlayerPieces(player))) {
            enqueue_neighbours(data, p.CalculateIndex(), board);
        }

        while (!data.blankspace.isEmpty()) {
            int value = data.blankspace.poll();
            count++;
            enqueue_neighbours(data, value, board);
        }

        return count;
    }

    private static void enqueue_neighbours(CountingAlgorithmData data, int index, GameState board) {
        Position[] neighbours = MoveCompiler.GetNeighbours(index);
        for (Position p : neighbours) {
            if (p.IsValid()) {
                int neighbour = p.CalculateIndex();
                if (data.visited[neighbour] > 0) {
                    continue;
                }
                data.visited[neighbour] = neighbour;
                if (board.ReadTile(neighbour) == 0) {
                    data.blankspace.offer(neighbour);
                }
            }
        }
    }
}
