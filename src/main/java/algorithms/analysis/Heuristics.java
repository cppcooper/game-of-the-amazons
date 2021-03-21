package algorithms.analysis;

import org.apache.commons.math3.util.Pair;
import structures.BoardPiece;
import structures.GameTreeNode;
import structures.LocalState;
import structures.Position;
import ubc.cosc322.AICore;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.BiFunction;

public class Heuristics {
	private static ConcurrentLinkedDeque<Pair<LocalState, GameTreeNode>> queue = new ConcurrentLinkedDeque();

	public static void enqueue(Pair<LocalState,GameTreeNode> job){
		queue.push(job);
	}

	public static void ProcessQueue(){
		while(!Thread.currentThread().isInterrupted()){
			var pair = queue.poll();
			if(pair != null) {
				LocalState board = pair.getFirst();
				GameTreeNode node = pair.getSecond();
				if(board == null || node == null || node.move == null){
					continue;
				}
				if(AICore.GetCurrentTurnNumber() > board.GetMoveNumber()){
					continue;
				}
				int N = 0;
				double original = node.get_heuristic();;
				double heuristic = 0;
				if(!node.has_first_degree.get()) {
					N++;
					heuristic += Heuristics.GetFirstDegreeMoveHeuristic(board);
					node.has_first_degree.set(true);
				}
				if(!node.has_count.get()) {
					N++;
					heuristic += Heuristics.GetCountHeuristic(board);
					node.has_count.set(true);
				}
				if(!node.has_territory.get()) {
					N++;
					heuristic += Heuristics.GetCountHeuristic(board);
					node.has_territory.set(true);
				}
				// if N == 0, then we do nothing cause it's already done
				if(N > 0) {
					// if original == 0, then N == 3
					if(original > 0){
						// if original > 0 then N != 3
						switch(N){
							case 1:
								heuristic = original + (heuristic - original)/3;
								break;
							case 2:
								heuristic = heuristic + (original - heuristic)/3;
								break;
						}
					}
					node.set_heuristic(heuristic,3);
				}
			}
		}
	}

	public static double GetFirstDegreeMoveHeuristic(LocalState board){
		int value = GetFirstDegreeMoveCount(board);
		return (double)value / (4*35);
	}

	public static int GetFirstDegreeMoveCount(LocalState board){
		BoardPiece[] pieces = board.GetPrevTurnPieces(); // we'll calculate heuristics for the player who got us here
		int[] positions = new int[pieces.length];
		for (int i = 0; i < 4; ++i) {
			int index = pieces[i].pos.CalculateIndex();
			positions[i] = index;
		}
		int first_degree = 0;
		int[][] first_degree_territory = MoveCompiler.GetOpenPositions(board,positions);
		for (int[] scan_direction : first_degree_territory) {
			for (int tile_index : scan_direction) {
				if (tile_index < 0) {
					break;
				}
				first_degree++;
			}
		}
		return first_degree;
	}

	public static double GetCountHeuristic(LocalState board){
		BoardPiece[] pieces = board.GetPrevTurnPieces(); // we'll calculate heuristics for the player who got us here
		CountData total = new CountData();
		for (int i = 0; i < 4; ++i) {
			int index = pieces[i].pos.CalculateIndex();
			total.add(GetCount(board,index));
		}
		total.empty_heuristic /= (4*161);
		total.nonempty_heuristic /= (4*80);
		return total.empty_heuristic - total.nonempty_heuristic;
	}

	static CountData GetCount(LocalState board, int startingPos) {
		CountData counts = new CountData();
		CountingAlgorithmData data = new CountingAlgorithmData();
		Position start = new Position(startingPos);

		data.visited[startingPos] = startingPos;
		QueueNeighbours(data, startingPos, board);
		BiFunction<Position,Integer,Boolean> is_first_degree = (s, index) -> {
			Position p = new Position(index);
			int dx = Math.abs(p.x - s.x);
			int dy = Math.abs(p.y - s.y);
			return dx == 0 || dy == 0 || dx == dy;
		};

		if (!data.blankspace.isEmpty()) {
			while (!data.blankspace.isEmpty()) {
				int value = data.blankspace.poll();
				counts.empty++;
				QueueNeighbours(data, value, board);

				// todo (debug): tune this weighting
				counts.empty_heuristic += is_first_degree.apply(start,value) ? 1.5 : 1;
			}
		}

		if (!data.blockedspace.isEmpty()) {
			while (!data.blockedspace.isEmpty()) {
				int value = data.blockedspace.poll();
				Position current = new Position(value);
				counts.nonempty++;

				//calculate simple distance
				int max = Math.max(Math.abs(start.x - current.x),  Math.abs(start.y - current.y));
				counts.nonempty_heuristic += 10.0/Math.pow(10,max-1);
			}
		}
		return counts;
	}

	private static void QueueNeighbours(CountingAlgorithmData data, int index, LocalState board){
		Position[] neighbours = new Position[8];
		neighbours[0] = new Position(index - 1);
		neighbours[1] = new Position(index + 1);
		neighbours[2] = new Position(index - 12);
		neighbours[3] = new Position(index - 11);
		neighbours[4] = new Position(index - 10);
		neighbours[5] = new Position(index + 10);
		neighbours[6] = new Position(index + 11);
		neighbours[7] = new Position(index + 12);

		for(Position p : neighbours) {
			if (p.IsValid()) {
				int neighbour = p.CalculateIndex();
				if (data.visited[neighbour] == 0) {
					data.visited[neighbour] = neighbour;
					if (board.ReadTile(neighbour) == 0) {
						data.blankspace.offer(neighbour);
					} else {
						data.blockedspace.offer(neighbour);
					}
				}
			}
		}
	}
	
	public static void Territory(LocalState lc) {
		
		BoardPiece[] oppplayer = lc.GetTurnPieces();
		BoardPiece[] usplayer = lc.GetPrevTurnPieces();
		int[] ourpositions = new int[4];
		int[] otherpositions = new int[4];
		int[] us = new int[121];
		int[] opp = new int[121]; 
		HashSet<Integer> been = new HashSet<Integer>();	
		
		for(int i = 0; i < ourpositions.length; i++) {
			ourpositions[i] = usplayer[i].pos.CalculateIndex();  
			otherpositions[i] = oppplayer[i].pos.CalculateIndex();			//element # is the index
		}      
		
		ter(lc, ourpositions, us, 1, been);
		ter(lc, otherpositions, opp, 1, been);
		
		
		int ourtiles = 0;
		int othertiles = 0;
		
		for(int i = 0; i < us.length; i++) {
			if(us[i] < opp[i]) {
				ourtiles++;
			} else if(us[i] > opp[i]){
				othertiles++;                               /* if we have a lower cost to move set to us, else set to 0 */
			} else if(us[i] == opp[i]) {
				continue;
			}
		}
		
		
	}
	
	private static void ter(LocalState lc, int[] pos, int[] count, int level, HashSet<Integer> visited) {
		
		int[][] moves = MoveCompiler.GetOpenPositions(lc, pos);								//[starting index][index of open positions]	
		
			for(int i = 0; i < moves.length; i++) {
				for(int j = 0; j < moves[i].length; j++) {
					if(moves[i][j] == -1) {
						break;
					}
					if(count[moves[i][j]] == 0) {  
						count[moves[i][j]] = level;
					}
				}
			}
			
		for(int i = 0; i < moves.length; i++) {
			ter(lc, prune_positions(visited, moves[i]), count, level+1, visited);
		}
		
	}
	
	private static int[] prune_positions(HashSet<Integer> scanned, int[] positions){
	    int[] pruned_positions = new int[positions.length];
	    int i = 0;
	    for (int index : positions) {
	        if (!scanned.contains(index)) {
	            pruned_positions[i++] = index;
	            scanned.add(index);
	        }
	    }
	    if(i < pruned_positions.length){
	        pruned_positions[i] = -1;
	    }
	    return pruned_positions;
	}

	public static double GetTerritoryHeuristic(LocalState board){
		final double max_value = -1.0;
		return (double)GetTerritoryCount(board) / max_value;
	}

	public static int GetTerritoryCount(LocalState board){
		return 0;
	}

	private static class CountingAlgorithmData {
		public int[] visited = new int[121];
		public Queue<Integer> blankspace = new LinkedList<>();
		public Queue<Integer> blockedspace = new LinkedList<>();
	}

	public static class CountData {
		public int empty;
		public int nonempty;
		public double empty_heuristic;
		public double nonempty_heuristic;
		void add(CountData other){
			empty += other.empty;
			empty_heuristic += other.empty_heuristic;
			nonempty += other.nonempty;
			nonempty_heuristic += other.nonempty_heuristic;
		}
	}

}
