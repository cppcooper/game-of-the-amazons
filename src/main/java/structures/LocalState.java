package structures;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

public class LocalState {
	private ArrayList<Integer> board;
	private static HashSet<Integer> always_empty = null;
	private BoardPiece[] player1 = new BoardPiece[4];
	private BoardPiece[] player2 = new BoardPiece[4];

	public LocalState(LocalState other){
		board = new ArrayList<>(other.board);
		for(int i = 0; i < player1.length; ++i){
			player1[i] = new BoardPiece(other.player1[i]);
			player2[i] = new BoardPiece(other.player2[i]);
		}
	}
	public LocalState(ArrayList<Integer> state, boolean find_pieces) throws Exception {
		if(state == null){
			throw new Exception("What did you do!");
		}
		if (always_empty == null) {
			always_empty = new HashSet<>();
			for(int x = 0; x < 11; ++x){
				for(int y = 0; y < 11; ++y){
					if(x == 0 || y == 0){
						always_empty.add(Position.CalculateIndex(x,y));
					}
				}
			}
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
		return ReadTile(Position.CalculateIndex(x,y));
	}
	public int ReadTile(Position pos){
		return ReadTile(pos.x,pos.y);
	}
	public void SetTile(int index, int value){
		board.set(index,value);
	}
	public void SetTile(int x, int y, int value){
		SetTile(Position.CalculateIndex(x,y),value);
	}
	public void SetTile(Position pos, int value){
		SetTile(pos.x,pos.y,value);
	}
	public void MakeMoves(Move move, boolean update_pieces) {
		if(move.IsValidFor(this)) {
			int player = ReadTile(move.start);
			if (update_pieces) {
				BoardPiece[] arr = null;
				if (player == 1) {
					arr = player1;
				} else if (player == 2) {
					arr = player2;
				}
				for (BoardPiece p : arr) {
					if (p.pos.CalculateIndex() == move.start) {
						p.pos = new Position(move.piece);
					}
				}
			}
			SetTile(move.piece, player);
			SetTile(move.start, 0);
			SetTile(move.arrow, 3);
		}
	}

	public int hashCode(){
		int hash = 0;
		int hash_mask = ~((256-1) << 24);
		BitSet hasher = new BitSet(200);
		int index_count = 0;
		for(int index = 0; index < board.size(); ++index){
			if(!always_empty.contains(index)) {
				for (int bit = 0; bit < 2; ++bit) {
					boolean x = (board.get(index).intValue() & bit) != 0;
					hasher.set((index_count++ * 2) + bit, x);
				}
			}
		}
		hash = (hasher.cardinality() << 24) & (hasher.hashCode() & hash_mask);
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LocalState that = (LocalState) o;
		return board.equals(that.board);
	}
}
