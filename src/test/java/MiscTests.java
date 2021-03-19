import algorithms.analysis.MoveCompiler;
import org.junit.jupiter.api.Test;
import structures.LocalState;
import structures.Position;
import tools.RandomGen;

import java.util.ArrayList;
import java.util.Arrays;

public class MiscTests {
    @Test
    void find_first_degree_range() {
        RandomGen rng = new RandomGen();
        Integer[] state = {
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        LocalState board = new LocalState(new ArrayList<Integer>(Arrays.asList(state)),false,true);
        int[] positions = new int[100];
        int index = 0;
        for(int x = 1; x < 11; ++x){
            for(int y = 1; y < 11; ++y){
                Position p = new Position(x,y);
                if(p.IsValid()){
                    positions[index++] = p.CalculateIndex();
                }
            }
        }
        int[][] first_degree_territory = MoveCompiler.GetOpenPositions(board,positions);
        int max = 0;
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < first_degree_territory.length; ++i) {
            int first_degree = 0;
            for (int x : first_degree_territory[i]) {
                if (x < 0) {
                    break;
                }
                first_degree++;
            }
            if(first_degree > max){
                max = first_degree;
            }
            if(first_degree < min){
                min = first_degree;
            }
        }
        System.out.printf("min: %d\nmax: %d\n",min,max);
    }
}
