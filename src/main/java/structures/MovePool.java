package structures;

import algorithms.analysis.MoveCompiler;

import java.util.ArrayList;
import java.util.HashMap;

public class MovePool {
    private static HashMap<Integer, Move> pool = new HashMap<>(64000, 3.f);

    public static void generate_pool(){
        BoardPiece[] all_positions = new BoardPiece[100];
        int i = 0;
        for(int x = 1; x < 11; ++x){
            for(int y = 1; y < 11; ++y){
                Position p = new Position(x,y);
                if(p.IsValid()){
                    all_positions[i++] = new BoardPiece(p.x,p.y,1);
                }
            }
        }
        ArrayList<Integer> blank_state = new ArrayList<Integer>(121);
        for(int j = 0; j < 121; ++j){
            blank_state.add(0);
        }
        MoveCompiler.GetMoveList(new LocalState(blank_state,false,true),all_positions,true);
    }

    private static int make_key(int start, int next, int arrow){
        final int bits = 8;
        return (start << (bits << 1)) & (next << bits) & arrow;
    }
    
    public static Move get(int start, int next, int arrow) {
        int key = make_key(start, next, arrow);
        Move move = pool.get(key);
        if(move == null){
            move = new Move(start,next,arrow);
            pool.put(key,move);
        }
        return move;
    }

    public static void put(int start, int next, int arrow, Move move) {
        pool.put(make_key(start, next, arrow), move);
    }
}
