package tools;

import algorithms.analysis.MonteCarlo;
import structures.BoardPiece;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomGen extends Random {

    public List<Integer> GetSequenceShuffled(int min, int max, int N){
        return ints(min,max).distinct()
                .limit(N).boxed().collect(Collectors.toList());
    }

    public ArrayList<Integer> GetRandomState(){
        ArrayList<Integer> arr = new ArrayList<>(121);
        double threshold = nextDouble() * 0.75;
        for(int i = 0; i < 121; ++i){
            if(nextDouble() < threshold){
                int v = nextInt();
                if(v == 1 || v == 2){
                    arr.add(3);
                } else {
                    arr.add(v);
                }
            } else {
                arr.add(0);
            }
        }
        return arr;
    }

    public ArrayList<Integer> GetRandomState(double threshold){
        ArrayList<Integer> arr = new ArrayList<>(121);
        for(int i = 0; i < 121; ++i){
            if(nextDouble() < threshold){
                int v = nextInt();
                if(v == 1 || v == 2){
                    arr.add(3);
                } else {
                    arr.add(v);
                }
            } else {
                arr.add(0);
            }
        }
        return arr;
    }

    public BoardPiece[] GetRandomBoardPieces(){
        final int N = 4;
        BoardPiece[] positions = new BoardPiece[N];
        var X = GetSequenceShuffled(1,11,N);
        var Y = GetSequenceShuffled(1,11,N);
        for(int i = 0; i < N; ++i){
            positions[i] = new BoardPiece(X.get(i), Y.get(i),1);
        }
        return positions;
    }

    public MonteCarlo.TreePolicy.policy_type get_random_policy(){
        // todo (2): tune this function
        if (nextDouble() < 0.5) {
            // p = 0.5 ?
            return MonteCarlo.TreePolicy.policy_type.FIRST_DEGREE_MOVES;
        } else if (nextDouble() < 0.5) {
            // p = 0.25 ?
            return MonteCarlo.TreePolicy.policy_type.COUNT_HEURISTIC;
        } else if (nextDouble() < 0.5) {
            // p = 0.125 ?
            return MonteCarlo.TreePolicy.policy_type.TERRITORY;
        } else if (nextDouble() < 0.5){
            // p = 0.0625 ?
            return MonteCarlo.TreePolicy.policy_type.ALL_HEURISTICS;
        } else {
            // p = 0.0625 ?
            return MonteCarlo.TreePolicy.policy_type.DO_NOTHING;
        }
    }
}
