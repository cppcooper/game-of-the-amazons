package structures;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.function.Function;

public class LocalState {
	private static HashSet<Integer> always_empty = null;
	private final ArrayList<Integer> board;
	private final BoardPiece[] player1 = new BoardPiece[4];
	private final BoardPiece[] player2 = new BoardPiece[4];
	private Move last_move = null;
	private boolean p1_state_analyzed = false;
	private boolean p2_state_analyzed = false;
	private boolean p1_has_moves = true;
	private boolean p2_has_moves = true;
	private int move_number = 1;
	private int player_turn = 1;
	private int hash = -1;
	private boolean valid_hash = false;

	private void PopulateSet(){
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
	}
	public LocalState(int[] state){
		assert state.length >= 121;
		board = new ArrayList<>(121);
		for(int i = 0; i < 121; ++i){
			board.add(state[i]);
		}
	}
	public LocalState(LocalState other){
		board = new ArrayList<>(other.board);
		for(int i = 0; i < player1.length; ++i){
			player1[i] = new BoardPiece(other.player1[i]);
			player2[i] = new BoardPiece(other.player2[i]);
		}
		last_move = other.last_move;
		p1_state_analyzed = other.p1_state_analyzed;
		p2_state_analyzed = other.p2_state_analyzed;
		p1_has_moves = other.p1_has_moves;
		p2_has_moves = other.p2_has_moves;
		move_number = other.move_number;
		player_turn = other.player_turn;
		hash = other.hash;
		valid_hash = other.valid_hash;
	}
	public LocalState(ArrayList<Integer> state, boolean find_pieces, boolean copy_state) {
		PopulateSet();
		if(copy_state && state != null) {
			board = new ArrayList<>(state);
		} else {
			board = state;
		}
		if(find_pieces) {
			FindPieces();
		}
	}

	public void DebugPrint(){
		for(int i = 0; i < board.size(); ++i){
			System.out.printf("%2d ", board.get(i));
			if((i+1) % 11 == 0){
				System.out.println();
			}
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
		valid_hash = board.set(index, value) == value && valid_hash; // last == new && valid_hash
		// ie. true if already true AND the new value for the tile is the same as what was already there, otherwise false. IDE suggested this form so figured I'd explain it
	}
	public void SetTile(int x, int y, int value){
		SetTile(Position.CalculateIndex(x,y),value);
	}
	public void SetTile(Position pos, int value){
		SetTile(pos.x,pos.y,value);
	}

	public boolean PlayerHasMoves(int player_num){
		switch (player_num){
			case 1:
				if(p1_state_analyzed)
					return p1_has_moves;
				break;
			case 2:
				if(p2_state_analyzed)
					return p2_has_moves;
				break;
		}
		Function<Integer, Boolean> has_a_move = index -> {
			Position[] neighbours = new Position[8];
			neighbours[0] = new Position(index - 1);
			neighbours[1] = new Position(index + 1);
			neighbours[2] = new Position(index - 12);
			neighbours[3] = new Position(index - 11);
			neighbours[4] = new Position(index - 10);
			neighbours[5] = new Position(index + 10);
			neighbours[6] = new Position(index + 11);
			neighbours[7] = new Position(index + 12);

			for(Position p : neighbours){
				if(p.IsValid() && ReadTile(p.CalculateIndex()) == 0){
					return true;
				}
			}
			return false;
		};
		var pieces = player_num == 1 ? player1 : player2;
		for (BoardPiece piece : pieces) {
			if (has_a_move.apply(piece.pos.CalculateIndex())) {
				switch (player_num) {
					case 1:
						p1_has_moves = true;
						p1_state_analyzed = true;
						break;
					case 2:
						p2_has_moves = true;
						p2_state_analyzed = true;
				}
				return true;
			}
		}
		switch(player_num){
			case 1:
				p1_has_moves = false;
				p1_state_analyzed = true;
				break;
			case 2:
				p2_has_moves = false;
				p2_state_analyzed = true;
		}
		return false;
	}

	public boolean IsGameOver(){
		return !PlayerHasMoves(1) || !PlayerHasMoves(2);
	}

	public void MakeMove(Move move, boolean update_pieces) {
		//if the move doesn't include invalid indices, check if the move is valid for this state
		if (!always_empty.contains(move.start) && !always_empty.contains(move.next) && !always_empty.contains(move.arrow)) {
			if (move.IsValidFor(this)) {
				int player = ReadTile(move.start);
				if (update_pieces) {
					BoardPiece[] arr = null;
					switch (player) {
						case 1:
							arr = player1;
							break;
						case 2:
							arr = player2;
							break;
						default:
							return;
					}
					for (BoardPiece p : arr) {
						if (p.pos.CalculateIndex() == move.start) {
							p.pos = new Position(move.next);
						}
					}
				}
				SetTile(move.next, player);
				SetTile(move.start, 0);
				SetTile(move.arrow, 3);
				move_number++;
				last_move = move;
				switch (player_turn){
					case 1:
						p1_state_analyzed = false;
						break;
					case 2:
						p2_state_analyzed = false;
						break;
				}
				player_turn = player_turn == 1 ? 2 : 1;
				valid_hash = false;

			}
		}
	}

	public int GetMoveNumber(){
		return move_number;
	}

	public int GetPlayerTurn(){
		return player_turn;
	}

	public BoardPiece[] GetTurnPieces(){
		switch(player_turn){
			case 1:
				return player1;
			case 2:
				return player2;
		}
		return null;
	}

	public BoardPiece[] GetPrevTurnPieces(){
		switch(player_turn){
			case 2:
				return player1;
			case 1:
				return player2;
		}
		return null;
	}

	public int hashCode(){
		if(valid_hash){
			return hash;
		}
		BitSet hasher = new BitSet(200);
		int index_count = 0;
		for(int index = 0; index < board.size(); ++index){
			if(!always_empty.contains(index)) {
				for (int bit = 0; bit < 2; ++bit) {
					boolean x = (board.get(index) & bit) != 0;
					hasher.set((index_count++ * 2) + bit, x);
				}
			}
		}
		hash = hasher.hashCode();
		valid_hash = true;
		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		LocalState that = (LocalState) o;
		return board.equals(that.board) && ((last_move == null && that.last_move == null) || (last_move != null && last_move.equals(that.last_move)));
	}
}
