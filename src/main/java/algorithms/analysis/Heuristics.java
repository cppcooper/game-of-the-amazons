package algorithms.analysis;

import data.*;
import tools.Debug;
import tools.Maths;
import ubc.cosc322.AICore;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

public class Heuristics {
	private static final ConcurrentLinkedDeque<GameTreeNode> queue = new ConcurrentLinkedDeque<>();

	public static final AtomicBoolean first_depth_processed = new AtomicBoolean(false);

	public static void enqueue(GameTreeNode job) {
		queue.push(job);
	}

	public static void ProcessQueue() {
		try {
			Debug.PrintThreadID("ProcessQueue");
			int i = 0;
			while (!Thread.currentThread().isInterrupted()) {
				GameTreeNode node = queue.poll();
				if (node != null) {
					GameState board = node.state_after_move.get();
					if (board == null || node.move.get() == null) {
						continue;
					}
					if (AICore.GetCurrentTurnNumber() > board.GetMoveNumber()) {
						continue;
					}
					CalculateHeuristicsAll(board, node);
					i = Math.max(0, i - 1);
				} else {
					if(BreadFirstSearch.first_depth_done.get()){
						first_depth_processed.set(true);
					}
					Thread.sleep(++i * 50);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void CalculateHeuristicsAll(GameState board, GameTreeNode node) {
		boolean changed = false;
		Heuristic h = node.heuristic;
		if (!h.has_winner.get()) {
			h.has_winner.set(true);
			h.winner.set(Winner.CalculateHeuristic(board));
			changed = true;
		}
		if(!h.has_mobility.get()){
			h.has_mobility.set(true);
			h.mobility.set(Mobility.CalculateHeuristic(board));
			changed = true;
		}
		if (!h.has_territory.get()) {
			h.has_territory.set(true);
			h.territory.set(Territory.CalculateHeuristic(board));
			changed = true;
		}
		if(changed){
			double value = h.winner.get() * h.mobility.get() * h.territory.get();
			h.value.set(value);
			node.propagate();
		}
		h.is_ready.set(true);
	}

	public static void SetWinner(GameState board, GameTreeNode node) {
		Heuristic h = node.heuristic;
		if (!h.has_winner.get()) {
			h.has_winner.set(true);
			h.winner.set(Winner.CalculateHeuristic(board));
		}
		h.value.set(h.winner.get());
	}

	public static void SetMobility(GameState board, GameTreeNode node) {
		Heuristic h = node.heuristic;
		if(!h.has_mobility.get()){
			h.has_mobility.set(true);
			h.mobility.set(Mobility.CalculateHeuristic(board));
		}
		h.value.set(h.mobility.get());
	}

	public static void SetTerritory(GameState board, GameTreeNode node) {
		Heuristic h = node.heuristic;
		if (!h.has_territory.get()) {
			h.has_territory.set(true);
			h.territory.set(Territory.CalculateHeuristic(board));
		}
		h.value.set(h.territory.get());
	}

	////////////////////
	/// Nuts and Bolts

	public static class Winner {
		private static class CountingAlgorithmData {
			public int[] visited = new int[121];
			public Queue<Integer> blankspace = new LinkedList<>();
		}

		// todo (refactor heuristics): use to nullify losing moves and double winning moves
		public static int CalculateHeuristic(GameState board){
			int winner = calculate_winner(board);
			if(winner == 0){
				return 1;
			}
			return winner != board.GetPlayerTurn() ? 2 : 0;
		}

		private static int calculate_winner(GameState board) {
			int p1 = count_accessible_positions(board,1);
			int p2 = count_accessible_positions(board,2);
			return p1 == p2 ? 0 : (p1 > p2 ? 1 : 2);
		}

		private static int count_accessible_positions(GameState board, int player) {
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

	public static class Mobility {
		public static class MobilityData {
			//todo: if we only need more than one of the king distances, then move the arrays inside distance data (the algo will only need to run once)
			public DistanceData[] ours;
			public DistanceData[] theirs;
			public DistanceData our_best = new DistanceData();
			public DistanceData their_best = new DistanceData();

			MobilityData(DistanceData[] ours, DistanceData[] theirs) {
				this.ours = ours;
				this.theirs = theirs;
				for (int piece = 0; piece < ours.length; ++piece) {
					for (int tile = 0; tile < our_best.king_distances.length; ++tile) {
						if (our_best.king_distances[tile] > ours[piece].king_distances[tile] || our_best.king_distances[tile] == 0) {
							our_best.king_distances[tile] = ours[piece].king_distances[tile];
						}
						if (our_best.queen_distances[tile] > ours[piece].queen_distances[tile] || our_best.queen_distances[tile] == 0) {
							our_best.queen_distances[tile] = ours[piece].queen_distances[tile];
						}
						if (their_best.king_distances[tile] > theirs[piece].king_distances[tile] || their_best.king_distances[tile] == 0) {
							their_best.king_distances[tile] = theirs[piece].king_distances[tile];
						}
						if (their_best.queen_distances[tile] > theirs[piece].queen_distances[tile] || their_best.queen_distances[tile] == 0) {
							their_best.queen_distances[tile] = theirs[piece].queen_distances[tile];
						}
					}
				}
			}
		}

		private static class DistanceData {
			public int[] queen_distances = new int[121];
			public int[] king_distances = new int[121];
		}

		public static double CalculateHeuristic(GameState board) {
			MobilityData data = get_territories(board);
			final int num_pieces = data.ours.length;
			int[] valid_tiles = MoveCompiler.GetAllValidPositions();
			double c1 = 0;
			double c2 = 0;
			double w = 0;
			double t1 = 0;
			double t2 = 0;
			for (int tile : valid_tiles) {
				c1 += Math.pow(2, -data.our_best.queen_distances[tile]) - Math.pow(2, -data.their_best.queen_distances[tile]);
				int temp1 = data.their_best.king_distances[tile] - data.our_best.king_distances[tile];
				c2 += Math.min(1, Math.max(-1, temp1 / 6.0));
				int temp2 = data.our_best.queen_distances[tile] - data.their_best.queen_distances[tile];
				if(data.our_best.queen_distances[tile] != 0 && data.their_best.queen_distances[tile] != 0) {
					w += Math.pow(2, -Math.abs(temp2));
				}
				t1 += temp2;
				t2 += data.our_best.king_distances[tile] - data.their_best.king_distances[tile];
			}
			c1 *= 2;
			double t = Maths.f1(w)*t1 + Maths.f2(w)*c1 + Maths.f3(w)*c2 + Maths.f4(w)*t2;
			double[] p1_a = new double[num_pieces];
			double[] p2_a = new double[num_pieces];
			for(int piece = 0; piece < num_pieces; ++piece) {
				p1_a[piece] = 0;
				p2_a[piece] = 0;
				for (int tile : valid_tiles) {
					int N_b = count_neighbours(board, tile);
					if (data.ours[piece].queen_distances[tile] == 1) {
						p1_a[piece] += Math.pow(2, -data.ours[piece].king_distances[tile]) * N_b;
					}
					if (data.theirs[piece].queen_distances[tile] == 1) {
						p2_a[piece] += Math.pow(2, -data.theirs[piece].king_distances[tile]) * N_b;
					}
				}
			}
			double m = Maths.sumf(w, p2_a) - Maths.sumf(w, p1_a);
			double h = t + m;
			return h;
		}

		private static MobilityData get_territories(GameState board) {
			BoardPiece[] ours = board.GetPrevTurnPieces();
			BoardPiece[] theirs = board.GetTurnPieces();
			return new MobilityData(calculate_territory(board, ours), calculate_territory(board, theirs));
		}

		private static DistanceData[] calculate_territory(GameState board, BoardPiece[] pieces) {
			DistanceData[] territory = new DistanceData[pieces.length];
			int[] position = new int[1];
			for (int i = 0; i < territory.length; ++i) {
				territory[i] = new DistanceData();
				position[0] = pieces[i].CalculateIndex();
				find_best_king_distances(board, position, territory[i].king_distances, 1);
				find_best_queen_distances(board, position, territory[i].queen_distances, 1);
			}
			return territory;
		}

		private static void find_best_king_distances(GameState board, int[] starting_positions, int[] distance_map, int distance) {
			int[] next_positions = new int[121];
			for(int index : starting_positions) {
				if(index != 0) {
					Position[] neighbours = MoveCompiler.GetNeighbours(index);
					if(neighbours != null) {
						for (Position n : neighbours) {
							int n_index = n.CalculateIndex();
							if (n.IsValid() && board.ReadTile(n_index) == 0 && (distance_map[n_index] > distance || distance_map[n_index] == 0)) {
								next_positions[n.CalculateIndex()] = n.CalculateIndex();
								distance_map[n_index] = distance;
							}
						}
					}
				}
			}
			if(distance < 100) {
				find_best_king_distances(board, next_positions, distance_map, distance + 1);
			}
		}

		private static void find_best_queen_distances(GameState board, int[] starting_positions, int[] distance_map, int distance) {
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
					find_best_queen_distances(board, prune_positions(position_list, distance_map, distance), distance_map, distance + 1);
				}
			}
		}

		private static int[] prune_positions(int[] positions, int[] distance_map, int distance) {
			int[] pruned_positions = new int[positions.length];
			int i = 0;
			for (int index : positions) {
				if(index < 0){
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

		private static int count_neighbours(GameState board, int tile){
			Position[] neighbours = MoveCompiler.GetNeighbours(tile);
			int count = 0;
			for(Position n : neighbours){
				if(n.IsValid() && board.ReadTile(n.CalculateIndex()) == 0){
					count++;
				}
			}
			return count;
		}
	}

	public static class Territory {
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
			double heuristic;
			heuristic = counts.ours / total;
			heuristic = Math.pow(4*heuristic, 2);
			heuristic /= 16;
			return heuristic;
		}

		private static TerritoryData calculate_territories(GameState board) {
			int[] our_distance_map = null;
			int[] their_distance_map = null;
			our_distance_map = calculate_distance_map(board, board.GetPrevTurnPieces());
			their_distance_map = calculate_distance_map(board, board.GetTurnPieces());
			double our_territory_count = 0;
			double their_territory_count = 0;
			for (int i = 0; i < our_distance_map.length; i++) {
				if (our_distance_map[i] < their_distance_map[i]) {
					our_territory_count += Math.pow(2, their_distance_map[i] - our_distance_map[i] - 1);
				} else if (our_distance_map[i] > their_distance_map[i]) {
					their_territory_count += Math.pow(2, our_distance_map[i] - their_distance_map[i] - 1);
				}
			}
			return new TerritoryData(our_territory_count, their_territory_count);
		}

		private static int[] calculate_distance_map(GameState board, BoardPiece[] pieces) {
			int[] distance_map = new int[121];
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
				if(index < 0){
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
}
