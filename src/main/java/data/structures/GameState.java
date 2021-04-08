package data.structures;

import algorithms.search.MoveCompiler;
import data.BoardPiece;
import data.Move;
import data.Position;
import tools.Tuner;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.function.Function;

public class GameState {
	private static HashSet<Integer> always_empty = null;
	private ArrayList<Integer> board;
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
	private static int[] game_start = { //this is upside down compared to the GUI
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 2, 0, 0, 2, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0};

	private void PopulateSet(){
		if (always_empty == null) {
			always_empty = new HashSet<>();
			if(Tuner.state_size > 100) {
				for (int x = 0; x < 11; ++x) {
					for (int y = 0; y < 11; ++y) {
						if (x == 0 || y == 0) {
							always_empty.add(Position.CalculateIndex(x, y));
						}
					}
				}
			}
		}
	}
	public GameState(){
		this(game_start);
		FindPieces();
	}
	public GameState(int[] state){
		board = new ArrayList<>(Tuner.state_size);
		if(state.length > Tuner.state_size) {
			Function<Integer,Integer> convert = (index) -> {
				int y = index / 11;
				int x = index - (y * 11);
				y--;x--;
				if(x < 0 || y < 0){
					return -1;
				}
				return Position.CalculateIndex(x,y);
			};
			for (int i = 0; i < state.length; ++i) {
				int index = convert.apply(i);
				if(index >= 0) {
					board.add(state[i]);
				}
			}
		} else {
			for (int i = 0; i < state.length; ++i) {
				board.add(state[i]);
			}
		}
		TransformState();
	}
	public GameState(GameState other){
		board = new ArrayList<>(other.board);
		if(other.player1 != null && other.player1[0] != null) {
			for (int i = 0; i < player1.length; ++i) {
				player1[i] = new BoardPiece(other.player1[i]);
				player2[i] = new BoardPiece(other.player2[i]);
			}
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
	public GameState(ArrayList<Integer> state, boolean find_pieces, boolean copy_state) {
		if(state.size() == Tuner.state_size) {
			if (copy_state) {
				board = new ArrayList<>(state);
			} else {
				board = state;
			}
		} else {
			Function<Integer,Integer> convert = (index) -> {
				int y = index / 11;
				int x = index - (y * 11);
				y--;x--;
				if(x < 0 || y < 0){
					return -1;
				}
				return Position.CalculateIndex(x,y);
			};
			board = new ArrayList<>(Tuner.state_size);
			for (int i = 0; i < state.size(); ++i) {
				int index = convert.apply(i);
				if(index >= 0) {
					board.add(state.get(i));
				}
			}
		}
		TransformState();
		if(find_pieces) {
			FindPieces();
		}
	}
	private void TransformState(){
		ArrayList<Integer> transformed_state = new ArrayList<>(board.size());
		for(int y = Tuner.coord_max; y >= Tuner.coord_min; --y){
			for(int x = Tuner.coord_min; x <= Tuner.coord_max; ++x){
				transformed_state.add(board.get(Position.CalculateIndex(x,y)));
			}
		}
		board = transformed_state;
		PopulateSet();
	}

	public void DebugPrint(){
		final char[] col = {' ','a','b','c','d','e','f','g','h','i','j'};
		System.out.print("    ");
		for(int i = 0; i < 10; ++i){
			System.out.printf("%2d ", i);
		}
		System.out.print("\n    ------------------------------\n");
		for(int y = 0; y <= Tuner.coord_upper; ++y){
			for(int x = -Tuner.coord_offset; x <= Tuner.coord_max; ++x){
				if(y != 10){
					if(x != -Tuner.coord_offset){
						if(x == Tuner.coord_max){
							System.out.printf("%2d | %2d", board.get(Position.CalculateIndex(x,y)), Tuner.coord_upper - y);
						} else {
							System.out.printf("%2d ", board.get(Position.CalculateIndex(x,y)));
						}
					} else {
						System.out.printf("%2d |", y);
					}
				} else {
					if(x == -Tuner.coord_offset){
						System.out.print("    ------------------------------\n    ");
					} else {
						System.out.printf(" %c ", col[x+Tuner.coord_offset]);
					}
				}
			}
			System.out.println();
		}
		System.out.println();
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
		var pieces = player_num == 1 ? player1 : player2;
		for (BoardPiece piece : pieces) {
			Position[] neighbours = MoveCompiler.GetNeighbours(piece.CalculateIndex());
			for(Position n : neighbours){
				if(n.IsValid() && ReadTile(n.CalculateIndex()) == 0){
					switch (player_num) {
						case 1:
							p1_has_moves = true;
							p1_state_analyzed = true;
							break;
						case 2:
							p2_has_moves = true;
							p2_state_analyzed = true;
							break;
					}
					return true;
				}
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

	public boolean CanGameContinue(){
		return PlayerHasMoves(player_turn);
	}

	public boolean MakeMove(Move move, boolean update_pieces, boolean print_move_num) {
		//if the move doesn't include invalid indices, check if the move is valid for this state
		if (!always_empty.contains(move.start) && !always_empty.contains(move.next) && !always_empty.contains(move.arrow)) {
			if (move.IsValidFor(this)) {
				int player = ReadTile(move.start);
				if(this.player_turn != player){
					return false;
				}
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
							return false;
					}
					for (BoardPiece p : arr) {
						if (p.CalculateIndex() == move.start) {
							p.UpdatePosition(move.next);
						}
					}
				}
				SetTile(move.next, player);
				SetTile(move.start, 0);
				SetTile(move.arrow, 3);
				if(print_move_num){
					System.out.printf("==Move Number %d==\n", move_number);
				}
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
				return true;
			}
		}
		return false;
	}

	public void SetMoveNumber(int move_number) {
		player_turn = move_number % 2 == 0 ? 2 : 1;
		this.move_number = move_number;
	}

	public int GetMoveNumber(){
		return move_number;
	}

	public int GetPlayerTurn(){
		return player_turn;
	}

	public int GetNextPlayerTurn(){
		return player_turn == 1 ? 2 : 1;
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

	public final BoardPiece[] GetPlayerPieces(int player){
		switch(player){
			case 1:
				return player1;
			case 2:
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
		GameState that = (GameState) o;
		return board.equals(that.board) && ((last_move == null && that.last_move == null) || (last_move != null && last_move.equals(that.last_move)));
	}
}
