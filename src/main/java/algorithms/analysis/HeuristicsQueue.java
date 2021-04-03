package algorithms.analysis;

import data.Heuristic;
import data.structures.GameState;
import data.structures.GameTreeNode;
import tools.Debug;
import tools.Maths;
import tools.Tuner;
import ubc.cosc322.AICore;

import java.util.concurrent.ConcurrentLinkedDeque;

public class HeuristicsQueue {
	private static final ConcurrentLinkedDeque<GameTreeNode> queue = new ConcurrentLinkedDeque<>();

	public static void add(GameTreeNode job) {
		if (Tuner.use_heuristic_queue) {
			int this_move_num = job.state_after_move.get().GetMoveNumber() - 1;
			int current_move_num = AICore.GetCurrentMoveNumber();
			if (this_move_num > current_move_num) {
				queue.add(job);
			} else if (this_move_num == current_move_num) {
				queue.push(job);
			}
		}
	}

	public static void push(GameTreeNode job) {
		if (Tuner.use_heuristic_queue) {
			queue.push(job);
		}
	}

	public static void ProcessQueue() {
		while(true) {
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
						if (AICore.GetCurrentMoveNumber() - 1 >= board.GetMoveNumber()) {
							continue;
						}
						CalculateHeuristicsAll(board, node, false);
						i = Math.max(0, i - 1);
					} else {
						Thread.sleep(++i * 1500);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			return;
		}
	}

	public static boolean isProcessing() {
		return !queue.isEmpty();
	}

	public static void CalculateHeuristicsAll(GameState board, GameTreeNode node, boolean skip_propagation) {
		Heuristic h = node.heuristic;
		FillWinner(board, h);
		FillMobility(board, h);
		FillTerritory(board, h);
		FillAmazongs(board, h);
		if (!h.is_ready.get()) {
			h.is_ready.set(true);
			double term1 = 0;
			double term2 = 0;
			double w = 1;
			if (Tuner.use_amazongs_heuristic) {
				term1 = h.amazongs.get();
			}
			if (Tuner.use_territory_heuristic) {
				term2 += h.territory.get();
			}
			if (Tuner.use_mobility_heuristic) {
				term2 += h.mobility.get();
			}
			if (Tuner.use_winner_heuristic) {
				w = h.winner.get();
			}
			double value = Maths.h(term1, term2, w);
			h.value.set(value);
			if (!skip_propagation) {
				node.propagate();
			} else {
				node.one_node_aggregation();
			}
		}
	}

	public static void FillWinner(GameState board, Heuristic h) {
		if (Tuner.use_winner_heuristic) {
			if (!h.has_winner.get()) {
				h.has_winner.set(true);
				h.winner.set(Winner.CalculateHeuristic(board));
			}
		}
	}

	public static void FillMobility(GameState board, Heuristic h) {
		if (Tuner.use_mobility_heuristic) {
			if (!h.has_mobility.get()) {
				h.has_mobility.set(true);
				h.mobility.set(Mobility.CalculateHeuristic(board));
			}
		}
	}

	public static void FillTerritory(GameState board, Heuristic h) {
		if (Tuner.use_territory_heuristic) {
			if (!h.has_territory.get()) {
				h.has_territory.set(true);
				h.territory.set(Territory.CalculateHeuristic(board));
			}
		}
	}

	public static void FillAmazongs(GameState board, Heuristic h) {
		if (Tuner.use_amazongs_heuristic) {
			if (!h.has_amazongs.get()) {
				h.has_amazongs.set(true);
				h.amazongs.set(Amazongs.CalculateHeuristic(board));
			}
		}
	}
}
