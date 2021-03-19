package algorithms.analysis;

import algorithms.analysis.Heuristics;
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
        LocalState board = new LocalState(new int[121]);
        int[] positions = new int[100];
        int j = 0;
        for(int x = 1; x < 11; ++x){
            for(int y = 1; y < 11; ++y){
                Position p = new Position(x,y);
                if(p.IsValid()){
                    positions[j++] = p.CalculateIndex();
                }
            }
        }
        int[][] first_degree_territory = MoveCompiler.GetOpenPositions(board,positions);
        double max_count_heuristic = 0;
        for(int index : positions){
            double value = Heuristics.GetCount(board,index).empty_heuristic;
            if(value > max_count_heuristic){
                max_count_heuristic = value;
            }
        }
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
        System.out.printf("max empty_heuristic: %f", max_count_heuristic);
    }
}
