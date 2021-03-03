package test;

import java.util.LinkedList;
import java.util.Queue;

public class search_test {
	
	// These are the methods that the Heuristics class uses. It's kinda useful if you 
	// want to see what's going on with the board when you use the methods, but delete this if you want.

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// 0 is open space
		// 1 is player 1 pieces
		// 2 is player 2 pieces
		// 3 is blocked space
		
		int[] board = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
				0, 0, 0, 0, 2, 3, 0, 2, 0, 0, 0, 
				0, 3, 0, 0, 0, 3, 0, 0, 0, 0, 0, 
				0, 0, 3, 0, 0, 3, 3, 3, 3, 0, 0, 
				0, 2, 0, 3, 0, 0, 0, 0, 3, 3, 2, 
				0, 0, 0, 3, 0, 0, 0, 0, 0, 3, 3, 
				0, 0, 0, 0, 3, 0, 1, 0, 0, 3, 3, 
				0, 3, 0, 3, 0, 0, 0, 3, 3, 3, 3, 
				0, 1, 3, 3, 0, 0, 0, 3, 0, 0, 1, 
				0, 0, 0, 0, 3, 0, 0, 3, 0, 0, 0, 
				0, 0, 0, 1, 3, 3, 0, 3, 0, 0, 0};

		int count1 = 0;
		for (int val: board) {
			System.out.print(val + " ");
			count1++;
			if (count1 == 11) {
				System.out.println();
				count1 = 0;
			}
		}

		int index = 72; //position 6,6
		int x = index / 11;
		int y = index - (x * 11);

		int blankcount = 0;
		int blockedcount = 0;
		int blockedtotal = 0;

		Queue<Integer> blankspace = new LinkedList<Integer>();
		Queue<Integer> blockedspace = new LinkedList<Integer>();
		int[] visited = new int[121];

		System.out.println("Index: " + index + " Location: " + x + "," + y);

		visited[index] = index;


		GetNearbySpaces(blankspace, blockedspace, visited, board, x, y);


		System.out.println("Open spaces:");
		if (!blankspace.isEmpty()) {
			while (!blankspace.isEmpty()) {
				int value = blankspace.poll();
				int qx = value / 11;
				int qy = value - (qx * 11);
				System.out.println(qx + "," + qy);
				board[value] = 7;
				blankcount++;
				GetNearbySpaces(blankspace, blockedspace, visited, board, qx, qy);
			}
		}else {
			System.out.println("Blankspace Empty!");
			//count = 0;
		}

		System.out.println("Blocked spaces:");
		if (!blockedspace.isEmpty()) {
			while (!blockedspace.isEmpty()) {
				int value = blockedspace.poll();
				int qx = value / 11;
				int qy = value - (qx * 11);
				board[value] = 5;
				blockedcount++;

				int dx = Math.abs(x - qx);
				int dy = Math.abs(y - qy);
				int max = Math.max(dx,  dy);
				System.out.println(qx + "," + qy + " difference: (" + dx + "," + dy + ")");

				if (max == 1) {
					blockedtotal += 100*blockedcount;
				}else if (max == 2){
					blockedtotal += 10*blockedcount;
				}else {
					blockedtotal += 1*blockedcount;
				}

			}
		}else {
			System.out.println("Blockedspace Empty!");
			//count = 0;
		}

		System.out.println("Blank Count: " + blankcount);
		System.out.println("Blocked Count: " + blockedcount);
		System.out.println("Total Blocked Count: " + blockedtotal);


		count1 = 0;
		for (int val: board) {
			System.out.print(val + " ");
			count1++;
			if (count1 == 11) {
				System.out.println();
				count1 = 0;
			}
		}

	}

	public static void GetNearbySpaces(Queue<Integer> blankspace, Queue<Integer> blockedspace, int[] visited, int[] board, int x, int y){

		int value = 0;

		// North
		if (x-1 > 0 && y > 0 && x-1 < 11 && y < 11) {
			value = (x-1)*11+y;
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}
		// Northeast
		if (x-1 > 0 && y+1 > 0 && x-1 < 11 && y+1 < 11) {
			value = (x-1)*11+(y+1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// East
		if (x > 0 && y+1 > 0 && x < 11 && y+1 < 11) {
			value = x*11+(y+1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// Southeast
		if (x+1 > 0 && y+1 > 0 && x+1 < 11 && y+1 < 11) {
			value = (x+1)*11+(y+1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// South
		if (x+1 > 0 && y > 0 && x+1 < 11 && y < 11) {
			value = (x+1)*11+y;
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// Southwest
		if (x+1 > 0 && y-1 > 0 && x+1 < 11 && y-1 < 11) {
			value = (x+1)*11+(y-1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// West
		if(x > 0 && y-1 > 0 && x < 11 && y-1 < 11) {
			value = x*11+(y-1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}

		// NorthWest
		if (x-1 > 0 && y-1 > 0 && x-1 < 11 && y-1 < 11) {
			value = (x-1)*11+(y-1);
			AddToQueue(blankspace, blockedspace, visited, board, value);
		}
	}



	public static void AddToQueue(Queue<Integer> blankspace, Queue<Integer> blockedspace, int[] visited, int[] board, int index) {
		if (visited[index] == 0) {
			visited[index] = index;
			if(board[index] == 0) {
				blankspace.offer(index);
			}else {
				blockedspace.offer(index);
			}
		}
	}

}
