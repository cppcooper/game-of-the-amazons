package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomGen extends Random {

    public List<Integer> GetSequenceShuffled(int min, int max, int N){
        // todo (10): benchmark versus simpler sequence generator/shuffler. This isn't useful to the AI, so this is purely after the fact for funsies
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
}
