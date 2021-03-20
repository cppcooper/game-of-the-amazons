package tools;

import algorithms.analysis.MonteCarlo;
import structures.BoardPiece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomGen extends Random {

    public List<Integer> DEPRECATED_GetDistinctSequenceShuffled(int min, int max, int N){
        assert (++max-min) >= N;
        return ints(min,max).distinct()
                .limit(N).boxed().collect(Collectors.toList());
    }

    public List<Integer> GetNumberSequence(int min, int max){
        assert min < max;
        ArrayList<Integer> seq = new ArrayList<>(max-min+1);
        for(int x = min; x <= max; ++x){
            seq.add(x);
        }
        return seq;
    }

    public List<Integer> GetRandomSequenceShuffled(int min, int max, int N){
        ArrayList<Integer> seq = new ArrayList<>(N);
        for(int i = 0; i < N; ++i){
            seq.add(nextInt(max-min)+min);
        }
        Collections.shuffle(seq);
        return seq;
    }

    public List<Integer> GetDistinctSequenceShuffled(int min, int max, int N){
        assert min < max;
        List<Integer> valid_numbers = GetNumberSequence(min,max);
        ArrayList<Integer> seq = new ArrayList<>(N);
        int i = 0;
        while(i < N) {
            Collections.shuffle(valid_numbers);
            for (int j = 0; i < N && j < valid_numbers.size(); ++j, ++i) {
                seq.add(valid_numbers.get(j));
            }
        }
        return seq;
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
        var X = GetDistinctSequenceShuffled(1,11,N);
        var Y = GetDistinctSequenceShuffled(1,11,N);
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
