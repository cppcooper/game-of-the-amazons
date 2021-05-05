package main;

import data.structures.MovePool;

public class Main {
    public static void main(String[] args) {
        try {
            MovePool.generate_pool();
            Game.Get().play();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
