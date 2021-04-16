package algorithms.analysis;

import data.structures.GameState;
import data.structures.GameTreeNode;
import tools.Debug;
import tools.Tuner;
import main.AICore;

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
				while (!Thread.currentThread().isInterrupted()) { // we only interrupt this thread when it is time to stop
					GameTreeNode node = queue.poll();
					if (node != null) {
						GameState board = node.state_after_move.get();
						if (board == null || node.move.get() == null) {
							continue;
						}
						if (AICore.GetCurrentMoveNumber() - 1 >= board.GetMoveNumber()) {
							continue;
						}
						node.calculate_heuristics(false);
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

}
