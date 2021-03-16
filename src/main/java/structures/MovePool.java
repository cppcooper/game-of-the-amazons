package structures;

import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.HashMap;

public class MovePool {
    private static HashMap<ImmutableTriple<Integer, Integer, Integer>, Move> pool
            = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Move>(64000, 3.f);

    private static ImmutableTriple<Integer, Integer, Integer> make_key(int start, int next, int arrow){
        return new ImmutableTriple<>(start, next, arrow);
    }
    public static Move get(int start, int next, int arrow) {
        return pool.get(make_key(start, next, arrow));
    }

    public static void put(int start, int next, int arrow, Move move) {
        pool.put(make_key(start, next, arrow), move);
    }
}
