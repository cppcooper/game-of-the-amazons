package algorithms.analysis;

import org.junit.jupiter.api.Test;
import structures.LocalState;
import structures.Position;
import tools.RandomGen;
import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void find_max_territory_count(){
        RandomGen rng = new RandomGen();
        find_max_territory_count(rng,1000);
    }

    void find_max_territory_count(RandomGen rng, int trials){
        int max_ours = 0;
        int max_theirs = 0;
        for(int i = 0; i < trials; ++i){
            LocalState board = new LocalState(new int[121]);/*new int[121]);/**/
            var positions = rng.GetRandomPositions(8);
            int count = 0;
            int player = 1;
            for(Position p : positions){
                if(count++ == 4){
                    player = 2;
                }
                board.SetTile(p.CalculateIndex(),player);
            }
            board.FindPieces();
            var counts = Heuristics.GetTerritoryCount(board);
            if(counts.ours > max_ours){
                max_ours = counts.ours;
            }
            if(counts.theirs > max_theirs){
                max_theirs = counts.theirs;
            }
        }
        System.out.printf("max ours:   %d\nmax theirs: %d\n", max_ours,max_theirs);
    }

    @Test
    void testing_infinite(){
        assertEquals(Double.NEGATIVE_INFINITY < 0, true);
    }
}
