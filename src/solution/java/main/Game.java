package main;

import data.pod.BoardPiece;
import data.pod.Move;
import data.structures.GameState;
import data.structures.GameTree;
import controllers.AIController;
import controllers.GameController;
import controllers.HumanController;
import org.junit.jupiter.api.Test;
import tools.RandomGen;
import ygraph.ai.smartfox.games.amazons.OurGameGUI;

import java.util.ArrayList;

public class Game {
    private final ArrayList<Runnable> callbacks = new ArrayList<>();
    final GameState state = new GameState();
    final OurGameGUI gui = new OurGameGUI();
    private final GameController[] players = new GameController[2];

    public static Game Get(){
        return instance;
    }
    private static Game instance = new Game();
    private Game(){
        RandomGen rng = new RandomGen();
        if(rng.nextBoolean()){
            players[0] = new HumanController(state, gui,1);
            players[1] = new AIController(state, gui,2);
        } else {
            players[0] = new AIController(state, gui,1);
            players[1] = new HumanController(state, gui,2);
        }
    }

    public void play() throws InterruptedException {
        while(!gui.is_closed.get()){
            if(canContinue()){
                for(GameController player : players){
                    if(!canContinue()){
                        break;
                    }
                    player.takeTurn();
                }
            }
        }
    }

    public synchronized void apply(Move move){
        gui.updateGameState(move);
        state.apply(move);
    }

    private boolean canContinue(){
        return !GameTree.get(state).isTerminal();
    }

    public synchronized int getCurrentTurn(){
        return state.getRoundNum();
    }

    public int getTurnPlayer(int round){
        return ((round - 1) % 2) + 1;
    }

    public int getPrevTurnPlayer(int round){
        return (round % 2) + 1;//getTurnPlayer(round-1);
    }

    public BoardPiece[] getTurnPieces(int round){ // -> MoveCompiler.compileList (this dependency is not ideal)
        return players[getTurnPlayer(round) - 1].getPlayerPieces();
    }

    public BoardPiece[] getPrevTurnPieces(int round){
        return players[getPrevTurnPlayer(round) - 1].getPlayerPieces();
    }

    @Test
    void test_turnprev(){
        for(int round = 1; round < 5; ++round){
            System.out.printf("Round: %d\n", round);
            System.out.printf("  turn player: %d\n", getTurnPlayer(round));
            System.out.printf("  prev turn player: %d\n", getPrevTurnPlayer(round));
        }
    }
}
