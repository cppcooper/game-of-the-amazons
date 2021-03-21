package ubc.cosc322;


import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import ygraph.ai.smartfox.games.*;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class AIPlayer extends GamePlayer{

    private GameClient gameClient = null; 
    private OurGameGUI gamegui = null;

	
    private String userName = null;
    private String passwd = null;

    /**
     * Any name and passwd 
     * @param userName
      * @param passwd
     */
    public AIPlayer(String userName, String passwd) {
    	this.userName = userName;
    	this.passwd = passwd;
    	
    	//To make a GUI-based player, create an instance of BaseGameGUI
    	//and implement the method getGameGUI() accordingly
    	this.gamegui = new OurGameGUI(this);
    }

    @Override
    public void onLogin() {
    	System.out.println("Congratualations!!! "
    			+ "I am called because the server indicated that the login is successfully");
    	System.out.println("The next step is to find a room and join it: "
    			+ "the gameClient instance created in my constructor knows how!");
		//System.out.println(gameClient.getRoomList().toString());
		gameClient.joinRoom("Shannon Lake");
    }

    public void makeMove(Map<String, Object> msgDetails){
		gamegui.updateGameState(msgDetails);
	}

    @Override
    public boolean handleGameMessage(String messageType, Map<String, Object> msgDetails) {
		System.out.printf("message type: %s\n",messageType);
		if (messageType.equals(GameMessage.GAME_ACTION_MOVE)) {
			System.out.println("Move received from server.");
			gamegui.updateGameState(msgDetails);
			AICore.UpdateState(msgDetails);

			// interrupt the monte carlo algorithms to restart from our current state
			AICore.InterruptSimulations();

			// queue sending a move (this new thread will wait for 29.96 seconds and then send a move to the server)
			Thread move_sender_orphan = new Thread(AICore::SendDelayedMessage);
			move_sender_orphan.start(); //orphan will clean itself up (as a good orphan should) when execution is done, no joining or stopping necessary

			AICore.PruneGameTree(); // last thing for this thread to do is clean up the Game Tree

		} else if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {
			ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get("game-state");
			gamegui.setGameState(state); //doesn't save the state reference
			AICore.SetState(state); //saves the state reference

			// we have a board, doesn't matter if the game has started.. we can start the monte carlo simulations
			AICore.TerminateThreads();
			AICore.LaunchThreads();
		} else if (messageType.equals(GameMessage.GAME_ACTION_START)) {
			if(userName.equals(msgDetails.get("player-black"))){
				System.out.printf("black: %s\n", msgDetails.get("player-black"));
				Thread move_sender_orphan = new Thread(AICore::SendDelayedMessage);
				move_sender_orphan.start(); //orphan will clean itself up (as a good orphan should) when execution is done, no joining or stopping necessary
			}
		} else if (messageType.equals(GameMessage.GAME_STATE_PLAYER_LOST)) {
			AICore.TerminateThreads();
		} else if (messageType.equals("user-count-change")) {
			gamegui.setRoomInformation(this.gameClient.getRoomList());
		}
		return true;
	}
    
    
    @Override
    public String userName() {
    	return userName;
    }

	@Override
	public GameClient getGameClient() {
		return this.gameClient;
	}

	@Override
	public BaseGameGUI getGameGUI() {
		return gamegui;
	}

	@Override
	public void connect() {
    	gameClient = new GameClient(userName, passwd, this);
	}

}//end of class
