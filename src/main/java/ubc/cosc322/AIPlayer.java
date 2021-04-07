package ubc.cosc322;


import java.util.ArrayList;
import java.util.Map;

import tools.Tuner;
import ygraph.ai.smartfox.games.*;

/**
 * An example illustrating how to implement a GamePlayer
 * @author Yong Gao (yong.gao@ubc.ca)
 * Jan 5, 2021
 *
 */
public class AIPlayer extends GamePlayer{

    private OurGameClient gameClient = null;
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

    public boolean isRunning(){
    	return !gamegui.is_closed.get();
	}

    @Override
	public void onLogin() {
		userName = gameClient.getUserName();
		if(gamegui != null) {
			gamegui.setRoomInformation(gameClient.getRoomList());
		}
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
			// open up a thread to send a delayed message
			AICore.SendDelayedMessage();
			AICore.PruneGameTree(); // last thing for this thread to do is clean up the Game Tree

		} else if (messageType.equals(GameMessage.GAME_STATE_BOARD)) {
			ArrayList<Integer> state = (ArrayList<Integer>) msgDetails.get("game-state");
			//ArrayList<Integer> state = new ArrayList<>(Arrays.asList(LocalState.late_state));
			gamegui.setGameState(state); //doesn't save the state reference
			AICore.SetState(state); //saves the state reference

			// we have a board, doesn't matter if the game has started.. we can start the monte carlo simulations
			AICore.TerminateThreads();
			AICore.LaunchThreads();
		} else if (messageType.equals(GameMessage.GAME_ACTION_START)) {
			if(userName.equals(msgDetails.get("player-black"))){
				Tuner.our_player_num = 1;
				Tuner.other_player_num = 2;
				System.out.printf("black: %s\n", msgDetails.get("player-black"));
				System.out.printf("white: %s\n", msgDetails.get("player-white"));
				AICore.SendDelayedMessage();
			} else {
				Tuner.our_player_num = 2;
				Tuner.other_player_num = 1;
				System.out.printf("white: %s\n", msgDetails.get("player-white"));
				System.out.printf("black: %s\n", msgDetails.get("player-black"));
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
    	gameClient = new OurGameClient(userName, passwd, this);
	}

	public void kill(){
    	gamegui.dispose();
	}

}//end of class
