package algorithms.analysis;

import org.apache.commons.math3.util.Pair;
import structures.*;
import tools.ASingleMaths;
import ubc.cosc322.AICore;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Heuristics {
	private static final ConcurrentLinkedDeque<Pair<GameState, GameTreeNode>> queue = new ConcurrentLinkedDeque<>();

	public static void enqueue(Pair<GameState, GameTreeNode> job) {
		queue.push(job);
	}

	public static void ProcessQueue() {
		try {
			Debug.PrintThreadID("ProcessQueue");
			int i = 0;
			while (!Thread.currentThread().isInterrupted()) {
				var pair = queue.poll();
				if (pair != null) {
					GameState board = pair.getFirst();
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

	public static void CalculateHeuristicsAll(GameState board, GameTreeNode node) {
		boolean changed = false;
		GameTreeNode.Heuristic h = node.heuristic;
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
			h.aggregate.set(h.winner.get() * (h.mobility.get() + h.territory.get()));
			node.propagate();
		}
	}

	public static void SetWinner(GameState board, GameTreeNode node) {
		GameTreeNode.Heuristic h = node.heuristic;
		if (!h.has_winner.get()) {
			h.has_winner.set(true);
			h.winner.set(Winner.CalculateHeuristic(board));
		}
	}

	public static void SetMobility(GameState board, GameTreeNode node) {
		GameTreeNode.Heuristic h = node.heuristic;
		if(!h.has_mobility.get()){
			h.has_mobility.set(true);
			h.mobility.set(Mobility.CalculateHeuristic(board));
		}
	}

	public static void SetTerritory(GameState board, GameTreeNode node) {
		GameTreeNode.Heuristic h = node.heuristic;
		if (!h.has_territory.get()) {
			h.has_territory.set(true);
			h.territory.set(Territory.CalculateHeuristic(board));
		}
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
			return winner != board.GetPlayerTurn() ? 2 : -1;
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
		// todo: improve mobility heuristic. Needs to consider pieces that are cut off
		//  Maybe for each piece that is cut off count a block heuristic
		//  then use the block heuristic to reduce the mobility heuristic
		//  or if against the enemy, then to increase the mobility heuristic
		//  division and multiplication?
		//  maybe just grabbing the first degree positions and using that to multiply the as-is mobility heuristic is enough?

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

	public static class Territory {
		private static class TerritoryCounts {
			int ours = 0;
			int theirs = 0;

			TerritoryCounts(int ours, int theirs) {
				this.ours = ours;
				this.theirs = theirs;
			}
		}

		public static double CalculateHeuristic(GameState board) {
			var counts = calculate_territories(board);
			int total = counts.ours + counts.theirs;
			double heuristic;
			// todo: figure it out part 2?
			//  Maybe this is the part that needs to be flipped?
			if(counts.ours > counts.theirs){
				heuristic = 1-((double) counts.theirs / total);
			} else {
				heuristic = ((double) counts.ours / total);
			}
			heuristic = Math.pow(4*heuristic, 2);
			heuristic /= 16;
			return ASingleMaths.clamp(heuristic,0,1);
		}

		private static TerritoryCounts calculate_territories(GameState board) {
			int[] our_degree_map = null;
			int[] their_degree_map = null;
			switch(board.GetPlayerTurn()){
				// todo: figure it out
				//  Not sure why this isn't flipped.. I think it should be flipped.. but the game is way smarter this way
				//  very confused why this happens.
				//  IT IS BECAUSE THE EDGES PROVIDE THE MOST TERRITORY!
				case 1:
					our_degree_map = calculate_degree_map(board,1);
					their_degree_map = calculate_degree_map(board,2);
					break;
				case 2:
					our_degree_map = calculate_degree_map(board,2);
					their_degree_map = calculate_degree_map(board,1);
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

		private static int[] calculate_degree_map(GameState board, int player) {
			int[] degree_map = new int[121];
			find_lowest_degrees(board, BoardPiece.GetIndices(Objects.requireNonNull(board.GetPlayerPieces(player))), degree_map, 1);
			return degree_map;
		}

		private static void find_lowest_degrees(GameState board, int[] starting_positions, int[] degree_mapping, int degree) {
			int[][] new_positions = MoveCompiler.GetOpenPositions(board, starting_positions, false); //[starting index][index of open positions]
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
				if(index < 0){
					break;
				}
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
