package tools;

import algorithms.analysis.MonteCarlo;
import structures.BoardPiece;
import structures.GameState;
import structures.Position;

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
                int v = nextInt(4);
                if(v == 1 || v == 2){
                    arr.add(3);
                } else {
                    arr.add(v);
                }
            } else {
                arr.add(0);
            }
        }
        var pieces = GetRandomPositions(8);
        int count = 0;
        int player = 1;
        for(Position p : pieces){
            if(count++ == 4){
                player = 2;
            }
            arr.set(p.CalculateIndex(),player);
        }
        return arr;
    }

    public ArrayList<Integer> GetRandomState(double threshold){
        ArrayList<Integer> arr = new ArrayList<>(121);
        for(int i = 0; i < 121; ++i){
            if(nextDouble() < threshold){
                arr.add(3);
            } else {
                arr.add(0);
            }
        }
        var pieces = GetRandomPositions(8);
        int count = 0;
        int player = 1;
        for(Position p : pieces){
            if(count++ == 4){
                player = 2;
            }
            arr.set(p.CalculateIndex(),player);
        }
        return arr;
    }

    public GameState GetRandomBoard(){
        return new GameState(GetRandomState(),true,true);
    }

    public GameState GetRandomBoard(double threshold){
        return new GameState(GetRandomState(threshold),true,true);
    }

    public Position[] GetRandomPositions(int N){
        int i = 0;
        Position[] positions = new Position[N];
        var indices = GetDistinctSequenceShuffled(0,120,120);
        for(int index : indices){
            Position p = new Position(index);
            if(p.IsValid()){
                positions[i++] = p;
            }
            if(i == N){
                break;
            }
        }
        return positions;
    }

    public BoardPiece[] GetRandomBoardPieces(int player){
        final int N = 4;
        int i = 0;
        BoardPiece[] pieces = new BoardPiece[N];
        var positions = GetDistinctSequenceShuffled(0,120,120);
        for(int index : positions){
            Position p = new Position(index);
            if(p.IsValid()){
                pieces[i++] = new BoardPiece(index,player);
            }
        }
        return pieces;
    }

    public MonteCarlo.TreePolicy.policy_type get_random_policy(int move_num){
        // todo (tuning): improve ability to aid in pruning moves
        double progression = move_num / 100.0;
        double p1 = (0.9 - progression/2) * (1 - progression);
        double p2 = 0.95 * (1 - p1);
        double p3 = 0.5 * (1 - (p1 + p2));
        assert (p1+p2+p3) <= 1;
        double x = nextDouble();
        if(x < p1){
            return MonteCarlo.TreePolicy.policy_type.MOBILITY;
        } else if (x < p1+p2) {
            return MonteCarlo.TreePolicy.policy_type.WINNER_LOSER;
        } else if (x < p1+p2+p3) {
            return MonteCarlo.TreePolicy.policy_type.TERRITORY;
        } else {
            return MonteCarlo.TreePolicy.policy_type.ALL_HEURISTICS;
        }
    }/**/
}
