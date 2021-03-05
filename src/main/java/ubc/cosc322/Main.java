package ubc.cosc322;

import structures.Move;
import structures.Position;
import ygraph.ai.smartfox.games.BaseGameGUI;

public class Game {
    private static Player player = null;

    public static void main(String[] args){
        if(args.length >= 2) {
            player = new Player(args[0], args[1]);
            BaseGameGUI.sys_setup();
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    player.Go();
                }
            });
            // todo: launch AI thread to constantly generate and explore move options
            // todo: write game loop
            /* While our turn //todo: write code to signal/detect player turn
             * wait 60 seconds (maybe 55 to be safe)
             * then GetBestMove() and send message to server
             * wait for our turn
            * */
            //player.getGameClient().sendMoveMessage(...);
        } else {
            System.out.println("Command line arguments missing.");
        }
    }
}
