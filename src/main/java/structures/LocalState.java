package structures;

import java.util.ArrayList;

public class LocalState {
	private ArrayList<Integer> board;
	private BoardPiece[] player1 = new BoardPiece[4];
	private BoardPiece[] player2 = new BoardPiece[4];

	public LocalState(ArrayList<Integer> state, boolean find_pieces) throws Exception {
		if(state == null){
			throw new Exception("What did you do!");
		}
		board = state;
		if(find_pieces) {
			FindPieces();
		}
	}
	public void FindPieces(){
		int p1 = 0;
		int p2 = 0;
		int index = 0;
		for (Integer tile : board) {
			switch (tile) {
				case 1:
					player1[p1++] = new BoardPiece(index,1);
					break;
				case 2:
					player2[p2++] = new BoardPiece(index,2);
					break;
			}
			index++;
		}
	}
	public final BoardPiece[] GetP1Pieces(){
		return player1;
	}
	public final BoardPiece[] GetP2Pieces(){
		return player2;
	}
	public int ReadTile(int index){
		return board.get(index);
	}
	public int ReadTile(int x, int y){
		int index = (x*11)+y;
		return ReadTile(index);
		/* in case it happens again
		try {
		} catch (Exception e) {
			System.out.println("x: " + x + ", y: " + y + ", index: " + index);
		} finally {
			return -1;
		}*/
	}
	public int ReadTile(Position pos){
		return ReadTile(pos.x,pos.y);
	}
	public void SetTile(int index, int value){
		board.set(index,value);
	}
	public void SetTile(int x, int y, int value){
		int index = (x*11)+y;
		SetTile(index,value);
	}
	public void SetTile(Position pos, int value){
		SetTile(pos.x,pos.y,value);
	}
	public void MakeMove(BoardPiece piece, Position new_pos, Position arrow_pos) throws Exception {
		if(ReadTile(piece.pos) != piece.player || ReadTile(new_pos) != 0 || ReadTile(arrow_pos) != 0){
			throw new Exception("Invalid move");
		}
		SetTile(piece.pos,0);
		SetTile(new_pos,piece.player);
		SetTile(arrow_pos,3);
	}
}
