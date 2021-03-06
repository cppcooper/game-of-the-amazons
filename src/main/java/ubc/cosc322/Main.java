package ubc.cosc322;

import structures.Move;
import ygraph.ai.smartfox.games.BaseGameGUI;

public class Main {
    private static Player player = null;

    public static void main(String[] args){
        if(args.length >= 2) {
            try {
                player = new Player(args[0], args[1]);
                BaseGameGUI.sys_setup();
                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        player.Go();
                    }
                });
                Thread ai_thread = new Thread(() -> AICore.run());
                ai_thread.run();

                // todo: write game loop
                //while(...)
                // todo: determine game over condition/event/etc.
                // todo: figure out how to wait/start new game after game over
                while(player.our_turn.get()){
                    //todo: sleep up to 29.5s
                    Move m = AICore.GetBestMove();
                    //todo: 1) construct move message. 2) send move message
                    //player.getGameClient().sendMoveMessage(...);
                }

                ai_thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Command line arguments missing.");
        }
    }
}
