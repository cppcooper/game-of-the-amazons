package algorithms.analysis;

import org.apache.commons.math3.util.Pair;
import structures.BoardPiece;
import structures.GameTreeNode;
import structures.LocalState;
import structures.Position;
import tools.ASingleMaths;
import ubc.cosc322.AICore;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Heuristics {
	private static final ConcurrentLinkedDeque<Pair<LocalState, GameTreeNode>> queue = new ConcurrentLinkedDeque<>();

	public static void enqueue(Pair<LocalState, GameTreeNode> job) {
		queue.push(job);
	}

	public static void ProcessQueue() {
		try {
			int i = 0;
			while (!Thread.currentThread().isInterrupted()) {
				var pair = queue.poll();
				if (pair != null) {
					LocalState board = pair.getFirst();
					GameTreeNode node = pair.getSecond();
					if (board == null || node == null || node.move.get() == null) {
						continue;
					}
					if (AICore.GetCurrentTurnNumber() > board.GetMoveNumber()) {
						continue;
					}
					CalculateHeuristicsAll(board, node);
					i = Math.max(0, i - 1);
				} else {
					Thread.sleep(++i * 50);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void CalculateHeuristicsAll(LocalState board, GameTreeNode node) {
		final int total_heuristics = 2;
		int N = 0;
		double original = node.get_heuristic();
		;
		double heuristic = 0;
		if (!node.has_first_degree.get()) {
			N++;
			heuristic += Heuristics.Mobility.CalculateHeuristic(board);
			node.has_first_degree.set(true);
		}
		if (!node.has_territory.get()) {
			N++;
			heuristic += Heuristics.Territory.CalculateHeuristic(board);
			node.has_territory.set(true);
		}/**/
		// if N == 0, then we do nothing cause it's already done
		if (N > 0) {
			node.set_heuristic(original + heuristic, total_heuristics);
		}
	}

	public static void CalculateHeuristicFirstDegree(LocalState board, GameTreeNode node) {
		if (!node.has_first_degree.get()) {
			node.add_heuristic(Heuristics.Mobility.CalculateHeuristic(board));
			node.has_first_degree.set(true);
		}
	}

	public static void CalculateHeuristicTerritory(LocalState board, GameTreeNode node) {
		// this is probably the most valuable (single) heuristic for pruning moves. It might also be the most expensive
		if (!node.has_territory.get()) {
			node.add_heuristic(Heuristics.Territory.CalculateHeuristic(board));
			node.has_territory.set(true);
		}/**/
	}

	////////////////////
	/// Nuts and Bolts

	public static class Winning {
		private static class CountingAlgorithmData {
			public int[] visited = new int[121];
			public Queue<Integer> blankspace = new LinkedList<>();
		}

		// todo (refactor heuristics): use to nullify losing moves and double winning moves
		public static double CalculateHeuristic(LocalState board){
			int winner = calculate_winner(board);
			if(winner == 0){
				return 1.0;
			}
			return winner != board.GetPlayerTurn() ? 2.0 : 0.0;
		}

		private static int calculate_winner(LocalState board) {
			int p1 = count_accessible_positions(board,1);
			int p2 = count_accessible_positions(board,2);
			return p1 == p2 ? 0 : (p1 > p2 ? 1 : 2);
		}

		private static int count_accessible_positions(LocalState board, int player) {
			int count = 0;
			CountingAlgorithmData data = new CountingAlgorithmData();
			for(BoardPiece p : Objects.requireNonNull(board.GetPlayerPieces(player))) {
				enqueue_neighbours(data, p.CalculateIndex(), board);
			}

			while (!data.blankspace.isEmpty()) {
				int value = data.blankspace.poll();
				count++;
				enqueue_neighbours(data, value, board);
			}

			return count;
		}

		private static void enqueue_neighbours(CountingAlgorithmData data, int index, LocalState board) {
			Position[] neighbours = new Position[8];
			neighbours[0] = new Position(index - 1);
			neighbours[1] = new Position(index + 1);
			neighbours[2] = new Position(index - 12);
			neighbours[3] = new Position(index - 11);
			neighbours[4] = new Position(index - 10);
			neighbours[5] = new Position(index + 10);
			neighbours[6] = new Position(index + 11);
			neighbours[7] = new Position(index + 12);

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

	public static class Mobility {
		private static final int max_moves = 4*35*35;

		public static double CalculateHeuristic(LocalState board){
			return (CalculateMobilityHeuristic(board) + CalculateReductionHeuristic(board)) / 2.0;
		}

		public static double CalculateMobilityHeuristic(LocalState board){
			return (double)count_first_degree_moves(board, board.GetPrevTurnPieces()) / max_moves;
		}

		public static double CalculateReductionHeuristic(LocalState board){
			return 1 - ((double)count_first_degree_moves(board, board.GetTurnPieces()) / max_moves);
		}

		private static int count_first_degree_positions(LocalState board, BoardPiece[] pieces) {
			int[] positions = new int[pieces.length];
			for (int i = 0; i < 4; ++i) {
				int index = pieces[i].CalculateIndex();
				positions[i] = index;
			}
			int moves = 0;
			int[][] first_degree_positions = MoveCompiler.GetOpenPositions(board, positions);
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

		private static int count_first_degree_moves(LocalState board, BoardPiece[] pieces) {
			return MoveCompiler.GetMoveList(board, pieces,true).size();
		}
	}

	public static class Territory {
		private static class TerritoryCounts {
			int ours = 0;
			int theirs = 0;

			TerritoryCounts(int ours, int theirs) {
				this.ours = ours;
				this.theirs = theirs;
			}
		}

		public static double CalculateHeuristic(LocalState board) {
			var counts = calculate_territories(board);
			int total = counts.ours + counts.theirs;
			double heuristic;
			if(counts.ours > counts.theirs){
				heuristic = 1-((double) counts.theirs / total);
			} else {
				heuristic = ((double) counts.ours / total);
			}
			heuristic = Math.pow(4*heuristic, 2);
			heuristic /= 16;
			return ASingleMaths.clamp(heuristic,0,1);
		}

		private static TerritoryCounts calculate_territories(LocalState board) {
			int[] our_degree_map = null;
			int[] their_degree_map = null;
			switch(board.GetPlayerTurn()){
				case 1:
					our_degree_map = calculate_degree_map(board,2);
					their_degree_map = calculate_degree_map(board,1);
					break;
				case 2:
					our_degree_map = calculate_degree_map(board,1);
					their_degree_map = calculate_degree_map(board,2);
					break;
				default: // this will never happen
					our_degree_map = new int[121];
					their_degree_map = new int[121];
			}
			int our_territory_count = 0;
			int their_territory_count = 0;
			for (int i = 0; i < our_degree_map.length; i++) {
				if (our_degree_map[i] < their_degree_map[i]) {
					our_territory_count++;
				} else if (our_degree_map[i] > their_degree_map[i]) {
					their_territory_count++;
				}
			}
			return new TerritoryCounts(our_territory_count, their_territory_count);
		}

		private static int[] calculate_degree_map(LocalState board, int player) {
			int[] degree_map = new int[121];
			find_lowest_degrees(board, BoardPiece.GetIndices(Objects.requireNonNull(board.GetPlayerPieces(player))), degree_map, 1);
			return degree_map;
		}

		private static void find_lowest_degrees(LocalState board, int[] starting_positions, int[] degree_mapping, int degree) {
			int[][] new_positions = MoveCompiler.GetOpenPositions(board, starting_positions); //[starting index][index of open positions]
			for (int[] position_list : new_positions) {
				if (position_list != null) {
					for (int index : position_list) {
						if (index == -1) {
							break;
						}
						if (degree_mapping[index] == 0 || degree_mapping[index] > degree) {
							degree_mapping[index] = degree;
						}
					}
				}
			}

			for (int[] position_list : new_positions) {
				if (position_list != null) {
					find_lowest_degrees(board, prune_positions(degree_mapping, position_list, degree), degree_mapping, degree + 1);
				}
			}
		}

		private static int[] prune_positions(int[] degree_mapping, int[] positions, int degree) {
			int[] pruned_positions = new int[positions.length];
			int i = 0;
			for (int index : positions) {
				if (degree_mapping[index] > degree) {
					pruned_positions[i++] = index;
				}
			}
			if (i < pruned_positions.length) {
				pruned_positions[i] = -1;
			}
			return pruned_positions;
		}
	}
}
